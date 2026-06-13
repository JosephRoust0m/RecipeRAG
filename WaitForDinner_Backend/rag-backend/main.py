"""
WaitForDinner RAG Pipeline Backend
  Query Phase: User query → ANN search → Top 5 recipe names → Fetch from Spring Boot → Return
"""

from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import chromadb
from openai import OpenAI
import json
import os
import re
import httpx
from dotenv import load_dotenv

load_dotenv()

# ── State ────────────────────────────────────────────────────────────────────
SPRING_BOOT_URL = "http://localhost:8080"

chroma_client = chromadb.PersistentClient(path="./chroma_db")
collection = None
openai_client = None

# ── Startup: auto-initialise from env ────────────────────────────────────────
@asynccontextmanager
async def lifespan(app: FastAPI):
    global openai_client, collection
    api_key = os.getenv("OPENAI_API_KEY")
    if api_key:
        try:
            openai_client = OpenAI(api_key=api_key)
            openai_client.models.list()
            collection = get_collection()
            print("✓ OpenAI and ChromaDB initialised from environment")
        except Exception as e:
            print(f"⚠ Auto-init failed: {e}")
    else:
        print("⚠ OPENAI_API_KEY not set in .env — chatbot will not work")
    yield

app = FastAPI(title="WaitForDinner RAG API", lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# ── Pydantic models ──────────────────────────────────────────────────────────
class ChatRequest(BaseModel):
    query: str
    top_k: int = 5

# ── Helpers ──────────────────────────────────────────────────────────────────
def get_embedding(text: str) -> list:
    resp = openai_client.embeddings.create(input=text, model="text-embedding-3-small")
    return resp.data[0].embedding

def filter_with_llm(query: str, recipes: list) -> list:
    recipes_text = ""
    for i, r in enumerate(recipes, 1):
        ingredients = ", ".join(re.sub(r'^\d+\s+\w+\s+', '', ing) for ing in r["ingredients"])
        recipes_text += (
            f"{i}. {r['name']}\n"
            f"   Cuisine: {', '.join(r['cuisine_types'])} | Diet: {', '.join(r['diet_types']) if r['diet_types'] else 'None'}\n"
            f"   Time: {r['time']} min | Difficulty: {r['difficulty']}\n"
            f"   Ingredients: {ingredients}\n\n"
        )

    prompt = (
        "You are a recipe filter. A user made a request and the system retrieved candidate recipes.\n\n"
        f"User request: \"{query}\"\n\n"
        f"Candidate recipes:\n{recipes_text}"
        "Return a JSON array of the numbers (1-based) of recipes that genuinely match the request. "
        "If none match, return an empty array []. "
        "Return ONLY the JSON array, nothing else. Example: [1, 3] or []"
    )

    response = openai_client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[{"role": "user", "content": prompt}],
        temperature=0,
        max_tokens=50
    )

    raw = response.choices[0].message.content.strip()
    indices = json.loads(raw)
    return [recipes[i - 1] for i in indices if 1 <= i <= len(recipes)]

def get_collection():
    return chroma_client.get_or_create_collection(
        name="waitfordinner_recipes",
        metadata={"hnsw:space": "cosine"}
    )

# ── Routes ────────────────────────────────────────────────────────────────────

@app.get("/health")
def health():
    return {
        "status": "ok",
        "chroma_ready": collection is not None,
        "openai_ready": openai_client is not None,
        "recipes_indexed": collection.count() if collection else 0
    }

@app.post("/api/chat")
def chat(req: ChatRequest):
    if openai_client is None:
        raise HTTPException(503, "The recommendation service is not available. Please try again later.")
    if collection is None or collection.count() == 0:
        raise HTTPException(503, "The recommendation service is not available. Please try again later.")

    query_embedding = get_embedding(req.query)

    results = collection.query(
        query_embeddings=[query_embedding],
        n_results=min(req.top_k, 5),
        include=["metadatas", "distances"]
    )

    matched_names = [m["name"] for m in results["metadatas"][0]]
    distances = results["distances"][0]

    retrieved = []
    for name, dist in zip(matched_names, distances):
        try:
            resp = httpx.get(
                f"{SPRING_BOOT_URL}/api/recipe/byName",
                params={"name": name},
                timeout=5.0
            )
            if resp.status_code == 200:
                data = resp.json()
                retrieved.append({
                    "id": data.get("recipeId", 0),
                    "name": data.get("name", name),
                    "cuisine_types": data.get("cuisines", []),
                    "diet_types": data.get("dietaryTags", []),
                    "difficulty": data.get("difficulty", ""),
                    "time": data.get("time", 0),
                    "portions": data.get("portions", 0),
                    "ingredients": data.get("ingredients", []),
                    "steps": data.get("steps", []),
                    "similarity_score": round(1 - dist, 4),
                })
        except Exception:
            pass

    if not retrieved:
        return {"query": req.query, "results": [], "total_found": 0, "message": "Could not fetch recipe details from backend."}

    filtered = filter_with_llm(req.query, retrieved)

    if not filtered:
        return {"query": req.query, "results": [], "total_found": 0, "message": "No matching recipes found"}

    return {"query": req.query, "results": filtered, "total_found": len(filtered)}
