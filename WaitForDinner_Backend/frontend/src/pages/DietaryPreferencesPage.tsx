import { useEffect, useRef, useState, type KeyboardEvent } from "react"

import { Button } from "@/components/ui/button"
import {
    Combobox,
    ComboboxInput,
    ComboboxContent,
    ComboboxList,
    ComboboxItem,
    ComboboxEmpty,
} from "@/components/ui/combobox"
import { cn } from "@/lib/utils"
import {
    getAllDiets,
    getCurrentDiets,
    adoptDiets,
    quitDiets,
    getAllPreferences,
    getCurrentPreferences,
    addPreferences,
    removePreferences,
    type DietTypesResponse,
    type PreferenceTypesResponse,
} from "@/services/api"

type Option = { id: number; name: string }

function zipOptions(names: string[], ids: number[]): Option[] {
    return names.map((name, i) => ({ name, id: ids[i] }))
}

// ── Backend-driven searchable chip combobox ───────────────────────────────────

function ChipSelect({
    label,
    options,
    selected,
    onChange,
}: {
    label: string
    options: Option[]
    selected: number[]
    onChange: (ids: number[]) => void
}) {
    const getName = (id: number) => options.find(o => o.id === id)?.name ?? String(id)

    return (
        <div className="space-y-2">
            <p className="text-sm font-medium">{label}</p>
            <Combobox
                multiple
                value={selected}
                onValueChange={(val) => onChange(val as number[])}
            >
                <ComboboxInput placeholder="Search..." showClear={false} />
                <ComboboxContent>
                    <ComboboxList>
                        <ComboboxEmpty>No results found.</ComboboxEmpty>
                        {options.map(opt => (
                            <ComboboxItem key={opt.id} value={opt.id}>
                                {opt.name}
                            </ComboboxItem>
                        ))}
                    </ComboboxList>
                </ComboboxContent>
            </Combobox>
            {selected.length > 0 && (
                <div className="flex flex-wrap gap-1.5">
                    {selected.map(id => (
                        <span
                            key={id}
                            className="bg-muted text-foreground flex h-[calc(--spacing(5.5))] items-center gap-1 rounded-sm px-1.5 text-xs font-medium whitespace-nowrap"
                        >
                            {getName(id)}
                            <button
                                type="button"
                                onClick={() => onChange(selected.filter(s => s !== id))}
                                className="opacity-50 hover:opacity-100 ml-0.5"
                            >
                                ✕
                            </button>
                        </span>
                    ))}
                </div>
            )}
        </div>
    )
}

// ── Free-text chip input ──────────────────────────────────────────────────────

function FreeTextChips({
    label,
    values,
    onChange,
}: {
    label: string
    values: string[]
    onChange: (values: string[]) => void
}) {
    const [input, setInput] = useState("")

    const commit = () => {
        const trimmed = input.trim()
        if (trimmed && !values.includes(trimmed)) {
            onChange([...values, trimmed])
        }
        setInput("")
    }

    const onKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
        if (e.key === "Enter") {
            e.preventDefault()
            commit()
        } else if (e.key === "Backspace" && input === "" && values.length > 0) {
            onChange(values.slice(0, -1))
        }
    }

    const remove = (val: string) => onChange(values.filter(v => v !== val))

    return (
        <div className="space-y-2">
            <p className="text-sm font-medium">{label}</p>
            <div className={cn(
                "dark:bg-input/30 border-input focus-within:border-ring focus-within:ring-ring/50",
                "flex min-h-9 flex-wrap items-center gap-1.5 rounded-md border bg-transparent",
                "px-2.5 py-1.5 text-sm shadow-xs transition-[color,box-shadow] focus-within:ring-3"
            )}>
                {values.map(val => (
                    <span
                        key={val}
                        className="bg-muted text-foreground flex h-[calc(--spacing(5.5))] items-center gap-1 rounded-sm px-1.5 text-xs font-medium whitespace-nowrap"
                    >
                        {val}
                        <button
                            type="button"
                            onClick={() => remove(val)}
                            className="opacity-50 hover:opacity-100 ml-0.5"
                            aria-label={`Remove ${val}`}
                        >
                            ✕
                        </button>
                    </span>
                ))}
                <input
                    className="min-w-16 flex-1 bg-transparent outline-none text-sm placeholder:text-muted-foreground"
                    placeholder="Type and press Enter..."
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={onKeyDown}
                    onBlur={commit}
                />
            </div>
        </div>
    )
}

// ── Page ─────────────────────────────────────────────────────────────────────

export default function DietaryPreferencesPage() {
    const [cuisineOptions, setCuisineOptions] = useState<Option[]>([])
    const [dietOptions, setDietOptions] = useState<Option[]>([])

    const [selectedCuisines, setSelectedCuisines] = useState<number[]>([])
    const [allergies, setAllergies] = useState<string[]>([])
    const [selectedDiets, setSelectedDiets] = useState<number[]>([])

    const origCuisines = useRef<number[]>([])
    const origDiets = useRef<number[]>([])

    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState("")
    const [successMsg, setSuccessMsg] = useState("")

    useEffect(() => {
        Promise.all([
            getAllPreferences(),
            getCurrentPreferences(),
            getAllDiets(),
            getCurrentDiets(),
        ])
            .then(([allPrefs, curPrefs, allDiets, curDiets]) => {
                setCuisineOptions(zipOptions(
                    (allPrefs as PreferenceTypesResponse).names,
                    (allPrefs as PreferenceTypesResponse).ids
                ))
                setDietOptions(zipOptions(
                    (allDiets as DietTypesResponse).dietNames,
                    (allDiets as DietTypesResponse).dietIds
                ))

                const curCuisineIds = curPrefs.preferenceIds ?? []
                const curDietIds = curDiets.dietTypeIds ?? []

                setSelectedCuisines(curCuisineIds)
                setSelectedDiets(curDietIds)
                origCuisines.current = curCuisineIds
                origDiets.current = curDietIds
            })
            .catch(() => setError("Failed to load preferences. Please try again."))
            .finally(() => setLoading(false))
    }, [])

    const onSave = async () => {
        setSaving(true)
        setError("")
        setSuccessMsg("")
        try {
            const toAddCuisines = selectedCuisines.filter(id => !origCuisines.current.includes(id))
            const toRemoveCuisines = origCuisines.current.filter(id => !selectedCuisines.includes(id))
            const toAdoptDiets = selectedDiets.filter(id => !origDiets.current.includes(id))
            const toQuitDiets = origDiets.current.filter(id => !selectedDiets.includes(id))

            await Promise.all([
                toAddCuisines.length > 0 ? addPreferences(toAddCuisines) : Promise.resolve(),
                toRemoveCuisines.length > 0 ? removePreferences(toRemoveCuisines) : Promise.resolve(),
                toAdoptDiets.length > 0 ? adoptDiets(toAdoptDiets) : Promise.resolve(),
                toQuitDiets.length > 0 ? quitDiets(toQuitDiets) : Promise.resolve(),
            ])

            origCuisines.current = [...selectedCuisines]
            origDiets.current = [...selectedDiets]
            setSuccessMsg("Preferences saved successfully.")
        } catch {
            setError("Failed to save preferences. Please try again.")
        } finally {
            setSaving(false)
        }
    }

    return (
        <div className="min-h-screen w-full flex flex-col items-start justify-start p-8">
            <h1 className="text-2xl font-bold mb-6">Dietary Preferences</h1>

            {loading ? (
                <p className="text-sm text-muted-foreground">Loading...</p>
            ) : (
                <>
                    <div className="w-full max-w-2xl border rounded-2xl p-8 space-y-8">
                        <ChipSelect
                            label="Preferred Cuisine Types"
                            options={cuisineOptions}
                            selected={selectedCuisines}
                            onChange={setSelectedCuisines}
                        />
                        <FreeTextChips
                            label="Allergies"
                            values={allergies}
                            onChange={setAllergies}
                        />
                        <ChipSelect
                            label="Dietary Restrictions (Halal, Keto, Vegetarian, etc)"
                            options={dietOptions}
                            selected={selectedDiets}
                            onChange={setSelectedDiets}
                        />
                    </div>

                    {error && <p className="mt-4 text-sm text-red-500">{error}</p>}
                    {successMsg && <p className="mt-4 text-sm text-green-600">{successMsg}</p>}

                    <div className="mt-6 w-full max-w-2xl flex justify-between">
                        <Button onClick={onSave} disabled={saving}>
                            {saving ? "Saving..." : "Save"}
                        </Button>
                        {/* <Button variant="outline" asChild>
                            <Link to="/home">Skip for now</Link>
                        </Button> */}
                    </div>
                </>
            )}
        </div>
    )
}
