import { useState, useEffect, useRef } from "react"
import { Link, useNavigate } from "react-router-dom"
import { getSession, clearSession, logout } from "@/lib/api"

const RAG_API = "http://localhost:8000"
const PIC_KEY = "wfd_profile_pic"

type Recipe = {
  id: number
  name: string
  cuisine_types: string[]
  diet_types: string[]
  difficulty: string
  time: number
  portions: number
  ingredients: string[]
  steps: string[]
  similarity_score: number
}

type Message =
  | { id: number; type: "user"; content: string }
  | { id: number; type: "loading" }
  | { id: number; type: "results"; results: Recipe[] }
  | { id: number; type: "error"; content: string }

function RecipeCard({ recipe, rank }: { recipe: Recipe; rank: number }) {
  const [expanded, setExpanded] = useState(false)
  const difficultyColor =
    recipe.difficulty === "Easy"
      ? "text-emerald-600 bg-emerald-50"
      : recipe.difficulty === "Medium"
      ? "text-amber-600 bg-amber-50"
      : "text-red-600 bg-red-50"

  return (
    <div className="border border-orange-100 rounded-xl bg-white shadow-sm mb-3 overflow-hidden hover:border-orange-300 transition-colors">
      <div
        className="flex items-center justify-between px-4 py-3 cursor-pointer select-none"
        onClick={() => setExpanded(!expanded)}
      >
        <div className="flex items-center gap-3 min-w-0">
          <div className="w-7 h-7 rounded-lg bg-orange-100 flex items-center justify-center text-xs font-bold text-orange-600 flex-shrink-0">
            #{rank}
          </div>
          <div className="min-w-0">
            <div className="font-semibold text-gray-800 text-sm truncate">{recipe.name}</div>
            <div className="flex gap-1.5 mt-1 flex-wrap">
              {recipe.cuisine_types.map(c => (
                <span key={c} className="text-xs bg-indigo-50 text-indigo-600 px-2 py-0.5 rounded-full">{c}</span>
              ))}
              {recipe.diet_types.slice(0, 2).map(d => (
                <span key={d} className="text-xs bg-emerald-50 text-emerald-600 px-2 py-0.5 rounded-full">{d}</span>
              ))}
            </div>
          </div>
        </div>
        <div className="flex items-center gap-3 flex-shrink-0 ml-3">
          <div className="hidden sm:flex gap-3 text-xs text-gray-500">
            <span>⏱ {recipe.time}m</span>
            <span>👥 {recipe.portions}</span>
          </div>
          <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${difficultyColor}`}>
            {recipe.difficulty}
          </span>
          <span className={`text-xs transition-transform duration-200 text-gray-400 ${expanded ? "rotate-180" : ""}`}>▼</span>
        </div>
      </div>

      {expanded && (
        <div className="border-t border-orange-100 px-4 py-4 bg-orange-50/30">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <div className="text-xs font-semibold text-orange-600 uppercase tracking-wide mb-2">Ingredients</div>
              <ul className="space-y-1">
                {recipe.ingredients.map((ing, i) => (
                  <li key={i} className="text-xs text-gray-600 flex gap-1.5">
                    <span className="text-orange-400 flex-shrink-0">•</span>{ing}
                  </li>
                ))}
              </ul>
            </div>
            <div>
              <div className="text-xs font-semibold text-indigo-600 uppercase tracking-wide mb-2">Steps</div>
              <ol className="space-y-1.5">
                {recipe.steps.map((step, i) => (
                  <li key={i} className="text-xs text-gray-600 flex gap-1.5">
                    <span className="text-indigo-500 font-medium min-w-[16px] flex-shrink-0">{i + 1}.</span>{step}
                  </li>
                ))}
              </ol>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

function ChatMessage({ msg }: { msg: Message }) {
  if (msg.type === "user") {
    return (
      <div className="flex justify-end mb-4">
        <div className="bg-gradient-to-br from-orange-400 to-orange-600 text-white rounded-2xl rounded-tr-sm px-4 py-2.5 max-w-[70%] text-sm font-medium shadow-sm">
          {msg.content}
        </div>
      </div>
    )
  }

  if (msg.type === "loading") {
    return (
      <div className="flex items-center gap-2 mb-4 py-2">
        <div className="flex gap-1">
          {[0, 1, 2].map(i => (
            <div
              key={i}
              className="w-2 h-2 rounded-full bg-orange-400"
              style={{ animation: `wfd-dot-bounce 1.2s ${i * 0.2}s ease-in-out infinite` }}
            />
          ))}
        </div>
        <span className="text-xs text-gray-400">Finding recipes...</span>
      </div>
    )
  }

  if (msg.type === "error") {
    return (
      <div className="mb-4 bg-red-50 border border-red-200 rounded-xl px-4 py-3 text-sm text-red-600">
        {msg.content}
      </div>
    )
  }

  if (msg.type === "results") {
    if (msg.results.length === 0) {
      return (
        <div className="mb-4 text-sm text-gray-500 italic bg-white border border-gray-100 rounded-xl px-4 py-3 shadow-sm">
          No recipes found for that request. Try describing what you're in the mood for differently!
        </div>
      )
    }
    return (
      <div className="mb-4">
        <div className="text-xs text-gray-400 mb-2">
          Found <span className="text-orange-500 font-semibold">{msg.results.length}</span> recipe{msg.results.length !== 1 ? "s" : ""} for you
        </div>
        {msg.results.map((r, i) => (
          <RecipeCard key={r.id} recipe={r} rank={i + 1} />
        ))}
      </div>
    )
  }

  return null
}

const SUGGESTIONS = [
  "Quick vegan dinner under 30 minutes",
  "Gluten-free Asian dishes with chicken",
  "Something with salmon and lemon",
  "Keto friendly meal with beef",
]

export default function Chatbot() {
  const navigate = useNavigate()
  const session = getSession()
  const [profilePicUrl, setProfilePicUrl] = useState("")
  const [messages, setMessages] = useState<Message[]>([])
  const [query, setQuery] = useState("")
  const [sending, setSending] = useState(false)
  const chatRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (!session) { navigate("/login"); return }
    setProfilePicUrl(localStorage.getItem(PIC_KEY) ?? "")
  }, [navigate])

  useEffect(() => {
    if (chatRef.current) chatRef.current.scrollTop = chatRef.current.scrollHeight
  }, [messages])

  const sendQuery = async (q: string) => {
    if (!q.trim() || sending) return
    setQuery("")
    setSending(true)

    setMessages(prev => [
      ...prev,
      { type: "user", content: q, id: Date.now() },
      { type: "loading", id: Date.now() + 1 },
    ])

    try {
      const r = await fetch(`${RAG_API}/api/chat`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query: q, top_k: 5 }),
      })
      const d = await r.json()
      setMessages(prev => {
        const without = prev.filter(m => m.type !== "loading")
        if (!r.ok) return [...without, { type: "error", content: "Something went wrong. Please try again.", id: Date.now() }]
        return [...without, { type: "results", results: d.results, id: Date.now() }]
      })
    } catch {
      setMessages(prev =>
        prev.filter(m => m.type !== "loading").concat({
          type: "error",
          content: "Could not reach the recommendation service. Please make sure it's running.",
          id: Date.now(),
        })
      )
    }
    setSending(false)
  }

  return (
    <div className="min-h-screen w-full bg-gradient-to-br from-orange-50 via-white to-amber-50 flex flex-col">

      {/* Nav bar */}
      <header className="w-full bg-white border-b shadow-sm px-6 py-3 flex items-center justify-between">
        <h1 className="text-xl font-bold tracking-tight text-orange-600">🍽 WaitForDinner</h1>

        <nav className="flex items-center gap-1">
          <Link to="/home" className="px-3 py-1.5 text-sm text-gray-500 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-colors">
            Home
          </Link>
          <Link to="/recipes" className="px-3 py-1.5 text-sm text-gray-500 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-colors">
            Recipes
          </Link>
          <Link to="/chatbot" className="px-3 py-1.5 text-sm font-semibold text-orange-600 bg-orange-50 border border-orange-200 rounded-lg">
            🤖 AI Recommendation
          </Link>
        </nav>

        <div className="flex items-center gap-3">
          <Link to="/profile-settings" className="flex items-center gap-2 px-3 py-1.5 rounded-lg hover:bg-orange-50 transition-colors">
            {profilePicUrl ? (
              <img src={profilePicUrl} className="h-8 w-8 rounded-full object-cover border-2 border-orange-200" alt="Profile" />
            ) : (
              <div className="h-8 w-8 rounded-full bg-orange-100 border-2 border-orange-200 flex items-center justify-center text-sm font-bold text-orange-600">
                {session?.username?.charAt(0)?.toUpperCase() ?? "?"}
              </div>
            )}
            <span className="text-sm font-medium text-gray-700">{session?.username ?? "Profile"}</span>
          </Link>
          <button
            onClick={async () => {
              try { await logout() } catch { /* ignore */ }
              clearSession()
              navigate("/")
            }}
            className="px-3 py-1.5 rounded-lg text-sm font-medium text-gray-500 hover:bg-red-50 hover:text-red-600 transition-colors border border-gray-200"
          >
            Logout
          </button>
        </div>
      </header>

      {/* Chat area */}
      <div className="flex-1 flex flex-col overflow-hidden" style={{ height: "calc(100vh - 57px)" }}>

        {/* Messages */}
        <div ref={chatRef} className="flex-1 overflow-y-auto px-8 py-6 max-w-4xl w-full mx-auto">
          {messages.length === 0 && (
            <div className="flex flex-col items-center justify-center h-full text-center">
              <div className="text-5xl mb-4">🤖</div>
              <h2 className="text-2xl font-bold text-gray-800 mb-2">What are you craving?</h2>
              <p className="text-sm text-gray-400 mb-8">
                Describe what you feel like eating and we'll find the best matching recipes for you
              </p>
              <div className="flex flex-wrap gap-2 justify-center max-w-lg">
                {SUGGESTIONS.map(s => (
                  <button
                    key={s}
                    onClick={() => sendQuery(s)}
                    className="bg-white border border-orange-100 text-gray-600 text-sm px-4 py-2 rounded-full hover:border-orange-400 hover:text-orange-600 transition-colors shadow-sm"
                  >
                    {s}
                  </button>
                ))}
              </div>
            </div>
          )}
          {messages.map(msg => <ChatMessage key={msg.id} msg={msg} />)}
        </div>

        {/* Input bar */}
        <div className="bg-white border-t border-orange-100 px-8 py-4">
          <div className="max-w-4xl mx-auto">
            <div className="flex gap-3 items-center bg-orange-50 border border-orange-200 rounded-xl px-4 py-2.5 focus-within:ring-2 focus-within:ring-orange-400 focus-within:border-orange-400 transition-all">
              <input
                value={query}
                onChange={e => setQuery(e.target.value)}
                onKeyDown={e => e.key === "Enter" && !e.shiftKey && sendQuery(query)}
                placeholder="Describe what you feel like eating..."
                className="flex-1 bg-transparent text-sm text-gray-800 placeholder:text-gray-400 outline-none"
              />
              <button
                onClick={() => sendQuery(query)}
                disabled={!query.trim() || sending}
                className="bg-gradient-to-br from-orange-400 to-orange-600 hover:from-orange-500 hover:to-orange-700 disabled:opacity-40 disabled:cursor-not-allowed text-white rounded-lg w-9 h-9 flex items-center justify-center transition-all shadow-sm flex-shrink-0"
              >
                {sending ? (
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                ) : (
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M22 2L11 13M22 2L15 22 11 13 2 9l20-7z" />
                  </svg>
                )}
              </button>
            </div>
            <p className="text-xs text-gray-400 mt-1.5 text-center">Press Enter to send · Click a recipe to see details</p>
          </div>
        </div>

      </div>
    </div>
  )
}
