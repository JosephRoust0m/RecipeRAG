"use client"

import * as React from "react"
import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { Field, FieldLabel, FieldGroup } from "@/components/ui/field"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import { cn } from "@/lib/utils"
import { getAllDietTypes, getAllPreferenceTypes, getPreferences, getSession, savePreferences } from "@/lib/api"
import { useNavigate } from "react-router-dom"

// ── Selectable list (toggle chips) ──────────────────────────────────────────

type SelectableListProps = {
  label: string
  options: string[]
  values: string[]
  onChange: (values: string[]) => void
}

function SelectableList({ label, options, values, onChange }: SelectableListProps) {
  const toggle = (item: string) => {
    onChange(values.includes(item) ? values.filter((v) => v !== item) : [...values, item])
  }

  return (
    <Field>
      <FieldLabel>{label}</FieldLabel>
      {options.length === 0 ? (
        <div className="border-border text-muted-foreground rounded-md border border-dashed px-4 py-6 text-center text-sm">
          No options available — these will be loaded from the server.
        </div>
      ) : (
        <div className="flex flex-wrap gap-2 pt-1">
          {options.map((item) => {
            const selected = values.includes(item)
            return (
              <button
                key={item}
                type="button"
                onClick={() => toggle(item)}
                className={cn(
                  "rounded-full border px-3 py-1 text-sm transition-colors",
                  selected
                    ? "border-green-600 bg-green-600 text-white"
                    : "border-border text-foreground hover:border-green-400 hover:bg-green-50"
                )}
              >
                {item}
              </button>
            )
          })}
        </div>
      )}
    </Field>
  )
}

// ── Fallback data ─────────────────────────────────────────────────────────────

const CUISINE_FALLBACK = [
  "Mexican", "Chinese", "French", "Italian", "Japanese", "Indian",
  "Thai", "Greek", "Spanish", "Lebanese", "Korean", "Vietnamese",
  "Turkish", "Moroccan", "Ethiopian", "American", "Brazilian", "Peruvian",
]

const RESTRICTION_FALLBACK = [
  "Vegetarian", "Vegan", "Halal", "Kosher", "Keto", "Paleo",
  "Gluten-Free", "Dairy-Free", "Low-Carb", "Low-Sodium", "Low-Fat",
  "Whole30", "Raw Food", "Pescatarian",
]

// ── Main page ─────────────────────────────────────────────────────────────────

export default function DietaryPreferences() {
  const navigate = useNavigate()
  const [cuisines, setCuisines] = React.useState<string[]>([])
  const [restrictions, setRestrictions] = React.useState<string[]>([])
  const [saving, setSaving] = React.useState(false)
  const [saveError, setSaveError] = React.useState<string | null>(null)
  const [saveSuccess, setSaveSuccess] = React.useState(false)

  const [cuisineOptions, setCuisineOptions] = React.useState<string[]>(CUISINE_FALLBACK)
  const [restrictionOptions, setRestrictionOptions] = React.useState<string[]>(RESTRICTION_FALLBACK)

  React.useEffect(() => {
    getAllPreferenceTypes()
      .then((data) => { if (data.length > 0) setCuisineOptions(data.map((p) => p.name)) })
      .catch(() => {/* keep fallback */})

    getAllDietTypes()
      .then((data) => { if (data.length > 0) setRestrictionOptions(data.map((d) => d.name)) })
      .catch(() => {/* keep fallback */})

    const session = getSession()
    if (session) {
      getPreferences(session.userId)
        .then((prefs) => {
          setCuisines(prefs.preferenceTypeNames)
          setRestrictions(prefs.dietTypeNames)
        })
        .catch(() => {/* no saved prefs yet */})
    }
  }, [])

  const handleSave = async () => {
    const session = getSession()
    if (!session) return
    setSaving(true)
    setSaveError(null)
    setSaveSuccess(false)
    try {
      await savePreferences(session.userId, cuisines, restrictions)
      setSaveSuccess(true)
      setTimeout(() => navigate("/home"), 800)
    } catch (err) {
      setSaveError(err instanceof Error ? err.message : "Failed to save preferences.")
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="bg-background flex min-h-screen items-start justify-center px-4 py-10">
      <div className="w-full max-w-2xl">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-semibold tracking-tight">Dietary Preferences</h1>
          <Button variant="ghost" onClick={() => navigate("/home")}>← Back</Button>
        </div>

        <Card>
          <CardContent>
            <FieldGroup className="gap-6">
              <SelectableList
                label="Preferred Cuisine Types"
                options={cuisineOptions}
                values={cuisines}
                onChange={setCuisines}
              />

              <Separator />

              <SelectableList
                label="Dietary Restrictions"
                options={restrictionOptions}
                values={restrictions}
                onChange={setRestrictions}
              />
            </FieldGroup>
          </CardContent>

          <CardFooter className="border-t flex-col items-stretch gap-2 pt-4">
            {saveError && (
              <p className="text-destructive text-sm">{saveError}</p>
            )}
            {saveSuccess && (
              <p className="text-sm text-green-600">Preferences saved!</p>
            )}
            <div className="flex justify-between">
              {/* <Button variant="ghost" size="sm" className="text-muted-foreground" onClick={() => navigate("/home")}>
                Skip for now
              </Button> */}
              <Button onClick={handleSave} disabled={saving}>
                {saving ? "Saving..." : "Save Preferences"}
              </Button>
            </div>
          </CardFooter>
        </Card>
      </div>
    </div>
  )
}
