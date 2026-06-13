import { useEffect, useState } from "react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
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

function zip(names: string[], ids: number[]): Option[] {
    return names.map((name, i) => ({ name, id: ids[i] }))
}

type Props = {
    onClose?: () => void
}

export default function DietaryPreferences({ onClose }: Props) {
    const [diets, setDiets] = useState<Option[]>([])
    const [preferences, setPreferences] = useState<Option[]>([])
    const [selectedDietIds, setSelectedDietIds] = useState<Set<number>>(new Set())
    const [selectedPrefIds, setSelectedPrefIds] = useState<Set<number>>(new Set())
    const [originalDietIds, setOriginalDietIds] = useState<Set<number>>(new Set())
    const [originalPrefIds, setOriginalPrefIds] = useState<Set<number>>(new Set())

    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState("")
    const [successMsg, setSuccessMsg] = useState("")

    useEffect(() => {
        Promise.all([
            getAllDiets(),
            getCurrentDiets(),
            getAllPreferences(),
            getCurrentPreferences(),
        ])
            .then(([allDiets, curDiets, allPrefs, curPrefs]) => {
                const dietOptions = zip(
                    (allDiets as DietTypesResponse).dietNames,
                    (allDiets as DietTypesResponse).dietIds
                )
                const prefOptions = zip(
                    (allPrefs as PreferenceTypesResponse).names,
                    (allPrefs as PreferenceTypesResponse).ids
                )
                setDiets(dietOptions)
                setPreferences(prefOptions)

                const curDietSet = new Set(curDiets.dietTypeIds ?? [])
                const curPrefSet = new Set(curPrefs.preferenceIds ?? [])
                setSelectedDietIds(curDietSet)
                setOriginalDietIds(curDietSet)
                setSelectedPrefIds(curPrefSet)
                setOriginalPrefIds(curPrefSet)
            })
            .catch(() => setError("Failed to load preferences. Please try again."))
            .finally(() => setLoading(false))
    }, [])

    const toggle = (id: number, set: Set<number>, setter: (s: Set<number>) => void) => {
        const next = new Set(set)
        if (next.has(id)) next.delete(id)
        else next.add(id)
        setter(next)
        setSuccessMsg("")
    }

    const onSave = async () => {
        setSaving(true)
        setError("")
        setSuccessMsg("")
        try {
            const toAdopt = [...selectedDietIds].filter(id => !originalDietIds.has(id))
            const toQuit = [...originalDietIds].filter(id => !selectedDietIds.has(id))
            const toAdd = [...selectedPrefIds].filter(id => !originalPrefIds.has(id))
            const toRemove = [...originalPrefIds].filter(id => !selectedPrefIds.has(id))

            await Promise.all([
                toAdopt.length > 0 ? adoptDiets(toAdopt) : Promise.resolve(),
                toQuit.length > 0 ? quitDiets(toQuit) : Promise.resolve(),
                toAdd.length > 0 ? addPreferences(toAdd) : Promise.resolve(),
                toRemove.length > 0 ? removePreferences(toRemove) : Promise.resolve(),
            ])

            setOriginalDietIds(new Set(selectedDietIds))
            setOriginalPrefIds(new Set(selectedPrefIds))
            setSuccessMsg("Preferences saved successfully.")
        } catch {
            setError("Failed to save preferences. Please try again.")
        } finally {
            setSaving(false)
        }
    }

    return (
        <Card className="w-full" data-slot="dietary-preferences">
            <CardHeader className="space-y-1">
                <div className="flex items-center justify-between">
                    <div>
                        <CardTitle>Dietary Preferences</CardTitle>
                        <CardDescription>Select your dietary restrictions and cuisine types.</CardDescription>
                    </div>
                    {onClose && (
                        <Button variant="ghost" size="sm" onClick={onClose}>
                            ✕
                        </Button>
                    )}
                </div>
            </CardHeader>

            <CardContent className="space-y-6">
                {loading ? (
                    <p className="text-sm text-muted-foreground">Loading...</p>
                ) : (
                    <>
                        {/* Dietary Restrictions */}
                        <div className="space-y-2">
                            <p className="text-sm font-medium">Dietary Restrictions</p>
                            {diets.length === 0 ? (
                                <p className="text-sm text-muted-foreground">No dietary options available.</p>
                            ) : (
                                <div className="flex flex-wrap gap-2">
                                    {diets.map(diet => (
                                        <button
                                            key={diet.id}
                                            type="button"
                                            onClick={() => toggle(diet.id, selectedDietIds, setSelectedDietIds)}
                                        >
                                            <Badge
                                                variant={selectedDietIds.has(diet.id) ? "default" : "outline"}
                                                className={cn(
                                                    "cursor-pointer select-none px-3 py-1 text-sm h-auto",
                                                    !selectedDietIds.has(diet.id) && "hover:bg-muted"
                                                )}
                                            >
                                                {diet.name}
                                            </Badge>
                                        </button>
                                    ))}
                                </div>
                            )}
                        </div>

                        {/* Cuisine Types */}
                        <div className="space-y-2">
                            <p className="text-sm font-medium">Cuisine Types</p>
                            {preferences.length === 0 ? (
                                <p className="text-sm text-muted-foreground">No cuisine options available.</p>
                            ) : (
                                <div className="flex flex-wrap gap-2">
                                    {preferences.map(pref => (
                                        <button
                                            key={pref.id}
                                            type="button"
                                            onClick={() => toggle(pref.id, selectedPrefIds, setSelectedPrefIds)}
                                        >
                                            <Badge
                                                variant={selectedPrefIds.has(pref.id) ? "default" : "outline"}
                                                className={cn(
                                                    "cursor-pointer select-none px-3 py-1 text-sm h-auto",
                                                    !selectedPrefIds.has(pref.id) && "hover:bg-muted"
                                                )}
                                            >
                                                {pref.name}
                                            </Badge>
                                        </button>
                                    ))}
                                </div>
                            )}
                        </div>

                        {error && <p className="text-sm text-red-500">{error}</p>}
                        {successMsg && <p className="text-sm text-green-600">{successMsg}</p>}

                        <Button className="w-full" onClick={onSave} disabled={saving}>
                            {saving ? "Saving..." : "Save Preferences"}
                        </Button>
                    </>
                )}
            </CardContent>
        </Card>
    )
}
