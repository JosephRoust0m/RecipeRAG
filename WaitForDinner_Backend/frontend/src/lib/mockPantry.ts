export type PantryItem = {
    id: string
    name: string
    quantity: number
    unit: string
    expiryDate: string // ISO date string "YYYY-MM-DD", empty string if none
}

const STORAGE_KEY = "wfd_pantry"

const DEFAULT_ITEMS: PantryItem[] = [
    { id: "1", name: "Eggs", quantity: 12, unit: "pieces", expiryDate: "2026-03-20" },
    { id: "2", name: "Milk", quantity: 2, unit: "L", expiryDate: "2026-03-15" },
    { id: "3", name: "Flour", quantity: 500, unit: "g", expiryDate: "2026-12-01" },
]

export function getPantryItems(): PantryItem[] {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) return JSON.parse(raw) as PantryItem[]
    localStorage.setItem(STORAGE_KEY, JSON.stringify(DEFAULT_ITEMS))
    return DEFAULT_ITEMS
}

export function savePantryItems(items: PantryItem[]) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items))
}
