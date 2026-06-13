const BASE = "/api"

// ── Ingredients (Allergies) ───────────────────────────────────────────────────

export type IngredientsResponse = {
    ingredientNames: string[]
    ids: number[]
}

export type IngredientUserResponse = {
    ingredientNames: string[]
    ingredientIds: number[]
    statuses: string[]
}

export const getAllIngredients = (): Promise<IngredientsResponse> =>
    fetch(`${BASE}/ingredient`).then(json)

export const getCurrentIngredients = (): Promise<IngredientUserResponse> =>
    fetch(`${BASE}/ingredient/user/current`).then(json)

export const addIngredientAllergy = (ingredientId: number, ingredientName: string): Promise<IngredientUserResponse> =>
    post(`${BASE}/ingredient/user/add`, { ingredientId, ingredientName, status: "Allergic" })

export const removeIngredientAllergy = (ingredientId: number, ingredientName: string): Promise<IngredientUserResponse> =>
    post(`${BASE}/ingredient/user/remove`, { ingredientId, ingredientName, status: "Allergic" })

const json = (res: Response) => {
    if (!res.ok) throw new Error(`${res.status} ${res.statusText}`)
    return res.json()
}

const post = (url: string, body: unknown) =>
    fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
    }).then(json)

// ── Diet types ────────────────────────────────────────────────────────────────

export type DietTypesResponse = {
    dietNames: string[]
    dietIds: number[]
}

export type DietUserResponse = {
    dietNames: string[]
    dietTypeIds: number[]
}

export const getAllDiets = (): Promise<DietTypesResponse> =>
    fetch(`${BASE}/diet`).then(json)

export const getCurrentDiets = (): Promise<DietUserResponse> =>
    fetch(`${BASE}/diet/current`).then(json)

export const adoptDiets = (dietTypeIds: number[]): Promise<DietUserResponse> =>
    post(`${BASE}/diet/adopt`, { dietTypeIds })

export const quitDiets = (dietTypeIds: number[]): Promise<DietUserResponse> =>
    post(`${BASE}/diet/quit`, { dietTypeIds })

// ── Preference (cuisine) types ────────────────────────────────────────────────

export type PreferenceTypesResponse = {
    names: string[]
    ids: number[]
}

export type PreferenceUserResponse = {
    preferenceNames: string[]
    preferenceIds: number[]
}

export const getAllPreferences = (): Promise<PreferenceTypesResponse> =>
    fetch(`${BASE}/preference`).then(json)

export const getCurrentPreferences = (): Promise<PreferenceUserResponse> =>
    fetch(`${BASE}/preference/current`).then(json)

export const addPreferences = (preferenceIds: number[]): Promise<PreferenceUserResponse> =>
    post(`${BASE}/preference/add`, { preferenceIds })

export const removePreferences = (preferenceIds: number[]): Promise<PreferenceUserResponse> =>
    post(`${BASE}/preference/remove`, { preferenceIds })
