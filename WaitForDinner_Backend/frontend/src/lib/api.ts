const API_BASE = "http://localhost:8080/api";

function fetchOpts(method: string, body?: unknown): RequestInit {
  return {
    method,
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
  };
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let msg = `HTTP ${res.status}`;
    try {
      const body = await res.json();
      msg = body.message || body.error || msg;
    } catch {
      // ignore parse error
    }
    throw new Error(msg);
  }
  const text = await res.text();
  if (!text) return undefined as unknown as T;
  return JSON.parse(text) as T;
}

// ---------- Session helpers ----------

export type UserSession = {
  userId: number;
  username: string;
  email: string;
};

const SESSION_KEY = "wfd_user";

export function getSession(): UserSession | null {
  const raw = localStorage.getItem(SESSION_KEY);
  return raw ? (JSON.parse(raw) as UserSession) : null;
}

export function setSession(user: UserSession) {
  localStorage.setItem(SESSION_KEY, JSON.stringify(user));
}

export function clearSession() {
  localStorage.removeItem(SESSION_KEY);
}

// ---------- Auth ----------

export type SignupRequest = {
  username: string;
  email: string;
  password: string;
};

export type AuthResponse = {
  userId: number;
  username: string;
  name: string;
  email: string;
};

export async function signup(data: SignupRequest): Promise<AuthResponse> {
  const res = await fetch(`${API_BASE}/auth/signup`, fetchOpts("POST", data));
  return handleResponse<AuthResponse>(res);
}

export type LoginRequest = {
  email: string;
  password: string;
};

export async function login(data: LoginRequest): Promise<AuthResponse> {
  const res = await fetch(`${API_BASE}/auth/login`, fetchOpts("POST", data));
  return handleResponse<AuthResponse>(res);
}

export async function logout(): Promise<void> {
  const res = await fetch(`${API_BASE}/auth/logout`, fetchOpts("POST"));
  await handleResponse<void>(res);
}

export async function forgotPassword(email: string): Promise<void> {
  const res = await fetch(`${API_BASE}/auth/forgot-password`, fetchOpts("POST", { email }));
  await handleResponse<void>(res);
}

export async function resetPassword(email: string, password: string): Promise<void> {
  const res = await fetch(`${API_BASE}/auth/reset-password`, fetchOpts("POST", { email, password }));
  await handleResponse<void>(res);
}

// ---------- Profile ----------

export type UpdateProfileRequest = {
  username?: string;
  email?: string;
  password?: string;
};

export type UpdateProfileResponse = {
  userId: number;
  username: string;
  name: string;
  email: string;
};

export async function updateProfile(
  _userId: number,
  data: UpdateProfileRequest
): Promise<UpdateProfileResponse> {
  const payload = {
    name: data.username,
    username: data.username,
    email: data.email,
    password: data.password,
  };
  const res = await fetch(`${API_BASE}/profile/update`, fetchOpts("POST", payload));
  return handleResponse<UpdateProfileResponse>(res);
}

export async function deleteAccount(_userId: number): Promise<void> {
  const res = await fetch(`${API_BASE}/auth/delete-account`, fetchOpts("DELETE"));
  await handleResponse<void>(res);
}

// ---------- Preferences ----------

export type UserPreferences = {
  preferenceTypeNames: string[];
  dietTypeNames: string[];
};

export async function getPreferences(userId: number): Promise<UserPreferences> {
  const res = await fetch(`${API_BASE}/users/${userId}/preferences`, {
    credentials: "include",
  });
  return handleResponse<UserPreferences>(res);
}

export async function savePreferences(
  userId: number,
  preferenceTypeNames: string[],
  dietTypeNames: string[]
): Promise<void> {
  const res = await fetch(
    `${API_BASE}/users/${userId}/preferences`,
    fetchOpts("PUT", { preferenceTypeNames, dietTypeNames })
  );
  await handleResponse<void>(res);
}

// ---------- All diet/preference types ----------

export async function getAllDietTypes(): Promise<{ name: string }[]> {
  const res = await fetch(`${API_BASE}/diet`, { credentials: "include" });
  if (!res.ok) return [];
  try {
    const data = await res.json();
    // Backend returns { dietName: string[], dietIds: int[] }
    const names: string[] = data.dietTypes || data.dietName || data.dietNames || [];
    return names.map((n: string) => ({ name: n }));
  } catch {
    return [];
  }
}

export async function getAllPreferenceTypes(): Promise<{ name: string }[]> {
  const res = await fetch(`${API_BASE}/preference`, { credentials: "include" });
  if (!res.ok) return [];
  try {
    const data = await res.json();
    // Backend returns { name: string[], ids: int[] }
    const names: string[] = data.preferenceTypes || data.name || data.names || [];
    return names.map((n: string) => ({ name: n }));
  } catch {
    return [];
  }
}

// ---------- Recipes ----------

export type RecipeRankItem = {
  name: string;
  matchCount: number;
  fullyCookable: boolean;
  dietaryMatch: boolean;
};

export type RecipeSearchItem = {
  recipeId: number;
  name: string;
  dietaryTag: string;
  cuisine: string;
  time: number;
  difficulty: string;
  matchCount: number;
  fullyCookable: boolean;
};

export type RecipeDetail = {
  recipeId: number;
  name: string;
  steps: string;
  difficulty: string;
  time: number;
  portions: number;
  ingredients: string[];
  dietaryTags: string[];
  cuisines: string[];
  saved: boolean;
};

export type SavedRecipe = {
  recipeId: number;
  name: string;
  dietaryTag: string;
  cuisine: string;
};

export type CookedRecipe = {
  recipeId: number;
  name: string;
  cookedAt: string;
};

export async function getRankedRecipes(): Promise<RecipeSearchItem[]> {
  const res = await fetch(`${API_BASE}/recipe/ranked`, { credentials: "include" });
  if (!res.ok) return [];
  const data = await res.json();
  return data.rankedRecipes || [];
}

export async function getValidRecipes(): Promise<RecipeSearchItem[]> {
  const res = await fetch(`${API_BASE}/recipe/valid`, { credentials: "include" });
  if (!res.ok) return [];
  const data = await res.json();
  return data.results || [];
}

export async function searchRecipes(
  keyword?: string,
  cuisine?: string,
  dietaryTag?: string
): Promise<RecipeSearchItem[]> {
  const params = new URLSearchParams();
  if (keyword) params.append("keyword", keyword);
  if (cuisine) params.append("cuisine", cuisine);
  if (dietaryTag) params.append("dietaryTag", dietaryTag);
  const res = await fetch(`${API_BASE}/recipe/search?${params}`, { credentials: "include" });
  if (!res.ok) return [];
  const data = await res.json();
  return data.results || [];
}

export async function getRecipeDetail(recipeId: number): Promise<RecipeDetail | null> {
  const res = await fetch(`${API_BASE}/recipe/${recipeId}`, { credentials: "include" });
  if (!res.ok) return null;
  return res.json();
}

export async function getMissingIngredients(recipeId: number): Promise<string[]> {
  const res = await fetch(`${API_BASE}/recipe/${recipeId}/missing-ingredients`, {
    credentials: "include",
  });
  if (!res.ok) return [];
  const data = await res.json();
  return data.missingIngredients || [];
}

export async function saveRecipe(recipeId: number): Promise<void> {
  await fetch(`${API_BASE}/recipe/${recipeId}/save`, fetchOpts("POST"));
}

export async function unsaveRecipe(recipeId: number): Promise<void> {
  await fetch(`${API_BASE}/recipe/${recipeId}/save`, fetchOpts("DELETE"));
}

export async function getSavedRecipes(): Promise<SavedRecipe[]> {
  const res = await fetch(`${API_BASE}/recipe/saved`, { credentials: "include" });
  if (!res.ok) return [];
  const data = await res.json();
  return data.savedRecipes || [];
}

export async function cookRecipe(recipeId: number): Promise<void> {
  await fetch(`${API_BASE}/recipe/${recipeId}/cook`, fetchOpts("POST"));
}

export async function getCookedRecipes(): Promise<CookedRecipe[]> {
  const res = await fetch(`${API_BASE}/recipe/cooked`, { credentials: "include" });
  if (!res.ok) return [];
  const data = await res.json();
  return data.cookedRecipes || [];
}

// ---------- Ingredients ----------

export async function getAllIngredientNames(): Promise<string[]> {
  const res = await fetch(`${API_BASE}/ingredient`, { credentials: "include" });
  if (!res.ok) return [];
  const data = await res.json();
  return data.ingredientNames || [];
}

// ---------- Pantry ----------

export type PantryItem = {
  name: string;
  quantity: number;
  unit: string;
};

export async function getPantryItems(): Promise<PantryItem[]> {
  const res = await fetch(`${API_BASE}/pantry/items`, { credentials: "include" });
  if (!res.ok) return [];
  const data = await res.json();
  return data.items || [];
}

export async function upsertPantryItem(name: string, quantity: number): Promise<PantryItem[]> {
  const res = await fetch(`${API_BASE}/pantry/item`, fetchOpts("POST", { name, quantity }));
  return handleResponse<{ items: PantryItem[] }>(res).then(data => data.items || []);
}

export async function deletePantryItem(name: string): Promise<void> {
  const res = await fetch(
    `${API_BASE}/pantry/items/${encodeURIComponent(name)}`,
    fetchOpts("DELETE")
  );
  await handleResponse<void>(res);
}
