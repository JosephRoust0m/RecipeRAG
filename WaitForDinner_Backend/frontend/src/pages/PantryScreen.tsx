import { useState, useEffect, useRef } from "react"
import { Link } from "react-router-dom"

import { Button } from "@/components/ui/button"
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"

import { getPantryItems, upsertPantryItem, deletePantryItem, getAllIngredientNames, type PantryItem } from "@/lib/api"

const UNITS = ["pieces", "g", "kg", "ml", "L", "cups", "tbsp", "tsp", "oz", "lbs"]

type LocalPantryItem = {
    name: string
    quantity: number
    unit: string
}

type FormErrors = Partial<Record<"name" | "quantity" | "unit" | "server", string>>

export default function PantryScreen() {
    const [items, setItems] = useState<LocalPantryItem[]>([])
    const [loading, setLoading] = useState(true)
    const [allIngredients, setAllIngredients] = useState<string[]>([])

    // Add form state
    const [name, setName] = useState("")
    const [quantity, setQuantity] = useState("")
    const [unit, setUnit] = useState("pieces")
    const [errors, setErrors] = useState<FormErrors>({})
    const [showSuggestions, setShowSuggestions] = useState(false)
    const addNameRef = useRef<HTMLDivElement>(null)

    // Edit modal state
    const [editingItem, setEditingItem] = useState<LocalPantryItem | null>(null)
    const [editName, setEditName] = useState("")
    const [editQuantity, setEditQuantity] = useState("")
    const [editUnit, setEditUnit] = useState("pieces")
    const [editErrors, setEditErrors] = useState<FormErrors>({})
    const [showEditSuggestions, setShowEditSuggestions] = useState(false)
    const editNameRef = useRef<HTMLDivElement>(null)

    // Delete dialog state
    const [deletingItem, setDeletingItem] = useState<LocalPantryItem | null>(null)

    useEffect(() => {
        getPantryItems()
            .then((apiItems: PantryItem[]) => {
                setItems(apiItems.map(i => ({ name: i.name, quantity: i.quantity, unit: i.unit })))
            })
            .catch(() => setItems([]))
            .finally(() => setLoading(false))

        getAllIngredientNames().then(setAllIngredients).catch(() => {})
    }, [])

    // Close dropdowns on outside click
    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (!addNameRef.current?.contains(e.target as Node)) setShowSuggestions(false)
            if (!editNameRef.current?.contains(e.target as Node)) setShowEditSuggestions(false)
        }
        document.addEventListener("mousedown", handler)
        return () => document.removeEventListener("mousedown", handler)
    }, [])

    const filteredSuggestions = (query: string) =>
        query.trim()
            ? allIngredients.filter(n => n.toLowerCase().includes(query.trim().toLowerCase())).slice(0, 8)
            : []

    const validate = (n: string, q: string, u: string): FormErrors => {
        const e: FormErrors = {}
        if (!n.trim()) e.name = "Name is required."
        const qty = Number(q)
        if (!q.trim() || isNaN(qty) || qty <= 0 || !Number.isInteger(qty)) e.quantity = "Quantity must be a whole number."
        if (!u) e.unit = "Select a unit."
        return e
    }

    const onAdd = async () => {
        const e = validate(name, quantity, unit)
        setErrors(e)
        if (Object.keys(e).length) return

        try {
            const updated = await upsertPantryItem(name.trim(), Number(quantity))
            setItems(updated.map(i => ({ name: i.name, quantity: i.quantity, unit: i.unit })))
            setName("")
            setQuantity("")
            setUnit("pieces")
            setErrors({})
        } catch (err: unknown) {
            setErrors({ server: err instanceof Error ? err.message : "Failed to add item." })
        }
    }

    const openEdit = (item: LocalPantryItem) => {
        setEditingItem(item)
        setEditName(item.name)
        setEditQuantity(String(item.quantity))
        setEditUnit(item.unit)
        setEditErrors({})
    }

    const onSaveEdit = async () => {
        const e = validate(editName, editQuantity, editUnit)
        setEditErrors(e)
        if (Object.keys(e).length) return

        try {
            const updated = await upsertPantryItem(editName.trim(), Number(editQuantity))
            setItems(updated.map(i => ({ name: i.name, quantity: i.quantity, unit: i.unit })))
            setEditingItem(null)
        } catch (err: unknown) {
            setEditErrors({ server: err instanceof Error ? err.message : "Failed to update item." })
        }
    }

    const onConfirmDelete = async () => {
        if (!deletingItem) return
        try {
            await deletePantryItem(deletingItem.name)
            setItems(prev => prev.filter(i => i.name !== deletingItem.name))
        } catch {
            setErrors({ server: `Failed to delete ${deletingItem.name}. Please try again.` })
        }
        setDeletingItem(null)
    }

    return (
        <div className="min-h-screen w-full bg-gradient-to-br from-orange-50 via-white to-amber-50 flex items-start justify-center p-8">
            <div className="w-full max-w-6xl space-y-6">

                {/* Page title */}
                <div className="flex items-center justify-between">
                    <div>
                        <h1 className="text-3xl font-bold text-gray-800">🛒 My Pantry</h1>
                        <p className="text-gray-500 text-sm mt-1">Manage your available ingredients</p>
                    </div>
                    <Button variant="ghost" asChild className="text-gray-500 hover:text-orange-600">
                        <Link to="/home">← Back</Link>
                    </Button>
                </div>

                {/* 2-column layout: add form (left) + item list (right) */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-10 items-start">

                    {/* LEFT: Add item card */}
                    <Card className="w-full max-w-xl border-0 shadow-md">
                        <CardHeader className="space-y-1 border-b">
                            <CardTitle className="text-green-800">Add Ingredient</CardTitle>
                            <CardDescription>Add a new ingredient to your pantry.</CardDescription>
                        </CardHeader>
                        <CardContent className="space-y-5">

                            {/* name with autocomplete */}
                            <div className="space-y-2">
                                <Label htmlFor="name">Ingredient name</Label>
                                <div ref={addNameRef} className="relative">
                                    <Input
                                        id="name"
                                        value={name}
                                        onChange={e => { setName(e.target.value); setShowSuggestions(true) }}
                                        onFocus={() => setShowSuggestions(true)}
                                        placeholder="e.g. Eggs"
                                        autoComplete="off"
                                    />
                                    {showSuggestions && filteredSuggestions(name).length > 0 && (
                                        <div className="absolute z-50 mt-1 w-full rounded-md border bg-popover text-popover-foreground shadow-md">
                                            {filteredSuggestions(name).map(s => (
                                                <button
                                                    key={s}
                                                    type="button"
                                                    onMouseDown={e => { e.preventDefault(); setName(s); setShowSuggestions(false) }}
                                                    className="w-full px-3 py-1.5 text-left text-sm hover:bg-accent hover:text-accent-foreground"
                                                >
                                                    {s}
                                                </button>
                                            ))}
                                        </div>
                                    )}
                                </div>
                                {errors.name && <p className="text-sm text-red-500">{errors.name}</p>}
                            </div>

                            {/* quantity + unit */}
                            <div className="grid grid-cols-2 gap-3">
                                <div className="space-y-2">
                                    <Label htmlFor="quantity">Quantity</Label>
                                    <Input
                                        id="quantity"
                                        type="number"
                                        min="1"
                                        value={quantity}
                                        onChange={e => setQuantity(e.target.value)}
                                        placeholder="e.g. 2"
                                    />
                                    {errors.quantity && <p className="text-sm text-red-500">{errors.quantity}</p>}
                                </div>
                                <div className="space-y-2">
                                    <Label>Unit</Label>
                                    <Select value={unit} onValueChange={setUnit}>
                                        <SelectTrigger className="w-full">
                                            <SelectValue placeholder="Select unit" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {UNITS.map(u => (
                                                <SelectItem key={u} value={u}>{u}</SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                    {errors.unit && <p className="text-sm text-red-500">{errors.unit}</p>}
                                </div>
                            </div>

                            {errors.server && (
                                <p className="text-sm text-red-500 bg-red-50 border border-red-200 rounded-lg px-3 py-2">{errors.server}</p>
                            )}
                            <Button className="w-full bg-green-500 hover:bg-green-600 text-white" onClick={onAdd}>
                                + Add to Pantry
                            </Button>
                        </CardContent>
                    </Card>

                    {/* RIGHT: Current items list */}
                    <Card className="w-full border-0 shadow-md">
                        <CardHeader className="text-center space-y-1 border-b">
                            <CardTitle className="text-green-800">Current Ingredients</CardTitle>
                            <CardDescription>
                                {!loading && items.length > 0 ? `${items.length} item${items.length !== 1 ? "s" : ""} in your pantry` : "Ingredients in your pantry"}
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="pt-4">
                            <div className="max-h-[520px] overflow-y-auto space-y-2 pr-1">
                                {loading ? (
                                    <p className="text-sm text-muted-foreground italic">Loading...</p>
                                ) : items.length === 0 ? (
                                    <p className="text-sm text-muted-foreground italic">No items in your pantry yet.</p>
                                ) : (
                                    items.map(item => (
                                        <div key={item.name} className="border border-green-100 rounded-lg px-3 py-2.5 text-sm flex items-center justify-between gap-2 bg-white hover:bg-green-50 transition-colors">
                                            <div className="flex-1 min-w-0">
                                                <span className="font-medium text-gray-800">{item.name}</span>
                                                <span className="text-emerald-600 ml-2 text-xs font-medium bg-emerald-50 px-2 py-0.5 rounded-full">{item.quantity} {item.unit}</span>
                                            </div>
                                            <div className="flex gap-1 shrink-0">
                                                <Button variant="ghost" size="sm" className="text-blue-500 hover:text-blue-700 hover:bg-blue-50" onClick={() => openEdit(item)}>
                                                    Edit
                                                </Button>
                                                <Button
                                                    variant="ghost"
                                                    size="sm"
                                                    className="text-red-400 hover:text-red-600 hover:bg-red-50"
                                                    onClick={() => setDeletingItem(item)}
                                                >
                                                    Delete
                                                </Button>
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        </CardContent>
                    </Card>

                </div>
            </div>

            {/* Edit item modal */}
            <AlertDialog open={!!editingItem} onOpenChange={open => { if (!open) setEditingItem(null) }}>
                <AlertDialogContent size="default">
                    <AlertDialogHeader>
                        <AlertDialogTitle>Edit Item</AlertDialogTitle>
                        <AlertDialogDescription>Update the details for this pantry item.</AlertDialogDescription>
                    </AlertDialogHeader>

                    <div className="space-y-4">
                        {/* name with autocomplete */}
                        <div className="space-y-2">
                            <Label htmlFor="edit-name">Ingredient name</Label>
                            <div ref={editNameRef} className="relative">
                                <Input
                                    id="edit-name"
                                    value={editName}
                                    onChange={e => { setEditName(e.target.value); setShowEditSuggestions(true) }}
                                    onFocus={() => setShowEditSuggestions(true)}
                                    placeholder="e.g. Eggs"
                                    autoComplete="off"
                                />
                                {showEditSuggestions && filteredSuggestions(editName).length > 0 && (
                                    <div className="absolute z-50 mt-1 w-full rounded-md border bg-popover text-popover-foreground shadow-md">
                                        {filteredSuggestions(editName).map(s => (
                                            <button
                                                key={s}
                                                type="button"
                                                onMouseDown={e => { e.preventDefault(); setEditName(s); setShowEditSuggestions(false) }}
                                                className="w-full px-3 py-1.5 text-left text-sm hover:bg-accent hover:text-accent-foreground"
                                            >
                                                {s}
                                            </button>
                                        ))}
                                    </div>
                                )}
                            </div>
                            {editErrors.name && <p className="text-sm text-red-500">{editErrors.name}</p>}
                        </div>

                        {/* quantity + unit */}
                        <div className="grid grid-cols-2 gap-3">
                            <div className="space-y-2">
                                <Label htmlFor="edit-quantity">Quantity</Label>
                                <Input
                                    id="edit-quantity"
                                    type="number"
                                    min="1"
                                    step="1"
                                    value={editQuantity}
                                    onChange={e => setEditQuantity(e.target.value)}
                                    placeholder="e.g. 2"
                                />
                                {editErrors.quantity && <p className="text-sm text-red-500">{editErrors.quantity}</p>}
                            </div>
                            <div className="space-y-2">
                                <Label>Unit</Label>
                                <Select value={editUnit} onValueChange={setEditUnit}>
                                    <SelectTrigger className="w-full">
                                        <SelectValue placeholder="Select unit" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {UNITS.map(u => (
                                            <SelectItem key={u} value={u}>{u}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                                {editErrors.unit && <p className="text-sm text-red-500">{editErrors.unit}</p>}
                            </div>
                        </div>
                    </div>

                    {editErrors.server && (
                        <p className="text-sm text-red-500 bg-red-50 border border-red-200 rounded-lg px-3 py-2">{editErrors.server}</p>
                    )}
                    <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                        <Button onClick={onSaveEdit}>Save Changes</Button>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>

            {/* Delete confirmation dialog */}
            <AlertDialog open={!!deletingItem} onOpenChange={open => { if (!open) setDeletingItem(null) }}>
                <AlertDialogContent size="sm">
                    <AlertDialogHeader>
                        <AlertDialogTitle>Delete Item</AlertDialogTitle>
                        <AlertDialogDescription>
                            Are you sure you want to remove <strong>{deletingItem?.name}</strong> from your pantry? This action cannot be undone.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                        <AlertDialogAction variant="destructive" onClick={onConfirmDelete}>
                            Delete
                        </AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    )
}
