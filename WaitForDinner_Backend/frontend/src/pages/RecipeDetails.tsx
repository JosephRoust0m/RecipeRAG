import { useState, useEffect } from "react"
import { Link, useNavigate, useParams } from "react-router-dom"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"

import {
    getRecipeDetail,
    getMissingIngredients,
    saveRecipe,
    unsaveRecipe,
    cookRecipe,
    getSession,
    type RecipeDetail,
} from "@/lib/api"

export default function RecipeDetails() {
    const { id } = useParams<{ id: string }>()
    const navigate = useNavigate()
    const [recipe, setRecipe] = useState<RecipeDetail | null>(null)
    const [missingIngredients, setMissingIngredients] = useState<string[]>([])
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [cooking, setCooking] = useState(false)
    const [cookSuccess, setCookSuccess] = useState(false)
    const [cookError, setCookError] = useState("")
    const [saveError, setSaveError] = useState("")
    const [error, setError] = useState("")

    const recipeId = parseInt(id ?? "0", 10)

    useEffect(() => {
        const session = getSession()
        if (!session) {
            navigate("/login")
            return
        }
        if (!recipeId) {
            navigate("/recipes")
            return
        }

        Promise.all([
            getRecipeDetail(recipeId),
            getMissingIngredients(recipeId),
        ])
            .then(([detail, missing]) => {
                setRecipe(detail)
                setMissingIngredients(missing)
            })
            .catch(() => setError("Failed to load recipe."))
            .finally(() => setLoading(false))
    }, [recipeId, navigate])

    const onToggleSave = async () => {
        if (!recipe) return
        setSaving(true)
        setSaveError("")
        try {
            if (recipe.saved) {
                await unsaveRecipe(recipeId)
                setRecipe({ ...recipe, saved: false })
            } else {
                await saveRecipe(recipeId)
                setRecipe({ ...recipe, saved: true })
            }
        } catch {
            setSaveError(recipe.saved ? "Failed to unsave recipe." : "Failed to save recipe.")
        } finally {
            setSaving(false)
        }
    }

    const onCook = async () => {
        setCooking(true)
        setCookError("")
        try {
            await cookRecipe(recipeId)
            setCookSuccess(true)
            setTimeout(() => setCookSuccess(false), 3000)
        } catch (err: unknown) {
            setCookError(err instanceof Error ? err.message : "Failed to mark as cooked.")
        } finally {
            setCooking(false)
        }
    }

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-orange-50 via-white to-amber-50">
                <p className="text-muted-foreground">Loading recipe...</p>
            </div>
        )
    }

    if (error || !recipe) {
        return (
            <div className="min-h-screen flex items-center justify-center flex-col gap-4 bg-gradient-to-br from-orange-50 via-white to-amber-50">
                <div className="rounded-lg bg-red-50 border border-red-200 px-6 py-4 text-red-600 text-sm">
                    {error || "Recipe not found."}
                </div>
                <Button asChild variant="ghost">
                    <Link to="/recipes">← Back to Recipes</Link>
                </Button>
            </div>
        )
    }

    const canCook = missingIngredients.length === 0

    return (
        <div className="min-h-screen w-full bg-gradient-to-br from-orange-50 via-white to-amber-50 flex items-start justify-center p-8">
            <div className="w-full max-w-3xl space-y-6">

                {/* Header */}
                <div className="flex items-center justify-between">
                    <div>
                        <h1 className="text-3xl font-bold text-gray-800">{recipe.name}</h1>
                        <p className="text-gray-500 text-sm mt-1">Recipe details</p>
                    </div>
                    <Button variant="ghost" asChild className="text-gray-500 hover:text-orange-600">
                        <Link to="/recipes">← Back</Link>
                    </Button>
                </div>

                {/* Meta badges */}
                <div className="flex flex-wrap gap-2">
                    {recipe.difficulty && (
                        <span className={`text-xs px-3 py-1 rounded-full font-medium border ${
                            recipe.difficulty.toLowerCase() === "easy"
                                ? "bg-green-100 text-green-700 border-green-200"
                                : recipe.difficulty.toLowerCase() === "medium"
                                ? "bg-yellow-100 text-yellow-700 border-yellow-200"
                                : "bg-red-100 text-red-700 border-red-200"
                        }`}>
                            {recipe.difficulty}
                        </span>
                    )}
                    {recipe.time > 0 && (
                        <span className="text-xs px-3 py-1 rounded-full bg-orange-100 text-orange-700 border border-orange-200 font-medium">
                            ⏱ {recipe.time} min
                        </span>
                    )}
                    {recipe.portions > 0 && (
                        <span className="text-xs px-3 py-1 rounded-full bg-blue-100 text-blue-700 border border-blue-200 font-medium">
                            🍽 {recipe.portions} servings
                        </span>
                    )}
                    {recipe.dietaryTags.map(tag => (
                        <span key={tag} className="text-xs px-3 py-1 rounded-full bg-green-100 text-green-700 border border-green-200 font-medium">
                            🥗 {tag}
                        </span>
                    ))}
                    {recipe.cuisines.map(c => (
                        <span key={c} className="text-xs px-3 py-1 rounded-full bg-violet-100 text-violet-700 border border-violet-200 font-medium">
                            🌍 {c}
                        </span>
                    ))}
                </div>

                {/* Action buttons */}
                <div className="flex flex-col gap-3">
                    <div className="flex gap-3 flex-wrap">
                        <Button
                            variant={recipe.saved ? "outline" : "default"}
                            onClick={onToggleSave}
                            disabled={saving}
                            className={recipe.saved
                                ? "border-indigo-300 text-indigo-600 hover:bg-indigo-50"
                                : "bg-indigo-500 hover:bg-indigo-600 text-white"
                            }
                        >
                            {saving ? "..." : recipe.saved ? "🔖 Unsave" : "🔖 Save Recipe"}
                        </Button>
                        <Button
                            onClick={onCook}
                            disabled={cooking || !canCook}
                            className={canCook
                                ? "bg-green-500 hover:bg-green-600 text-white"
                                : "opacity-50 cursor-not-allowed"
                            }
                        >
                            {cooking ? "Marking..." : "✅ Mark as Cooked"}
                        </Button>
                        {cookSuccess && (
                            <span className="self-center text-green-600 text-sm font-medium bg-green-50 border border-green-200 px-3 py-1 rounded-full">
                                Marked as cooked!
                            </span>
                        )}
                    </div>
                    {!canCook && (
                        <p className="text-sm text-red-500 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
                            ⚠️ You're missing {missingIngredients.length} ingredient{missingIngredients.length !== 1 ? "s" : ""} — add them to your pantry to cook this recipe.
                        </p>
                    )}
                    {cookError && (
                        <p className="text-sm text-red-500 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
                            {cookError}
                        </p>
                    )}
                    {saveError && (
                        <p className="text-sm text-red-500 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
                            {saveError}
                        </p>
                    )}
                </div>

                {/* Ingredients */}
                <Card className="border-0 shadow-md">
                    <CardHeader className="pb-3 border-b">
                        <div className="flex items-center justify-between">
                            <div>
                                <CardTitle className="text-orange-800">Ingredients</CardTitle>
                                <CardDescription>
                                    {missingIngredients.length === 0
                                        ? "✅ You have all the ingredients!"
                                        : `⚠️ Missing ${missingIngredients.length} ingredient${missingIngredients.length !== 1 ? "s" : ""}`}
                                </CardDescription>
                            </div>
                        </div>
                    </CardHeader>
                    <CardContent className="pt-4">
                        <ul className="space-y-2">
                            {recipe.ingredients.map(ing => {
                                const isMissing = missingIngredients.includes(ing)
                                return (
                                    <li key={ing} className={`text-sm flex items-center gap-2 px-3 py-2 rounded-lg ${
                                        isMissing
                                            ? "bg-red-50 text-red-600 border border-red-100"
                                            : "bg-green-50 text-green-700 border border-green-100"
                                    }`}>
                                        <span>{isMissing ? "✗" : "✓"}</span>
                                        <span>{ing}</span>
                                    </li>
                                )
                            })}
                        </ul>
                    </CardContent>
                </Card>

                {/* Steps */}
                {recipe.steps && (
                    <Card className="border-0 shadow-md">
                        <CardHeader className="pb-3 border-b">
                            <CardTitle className="text-violet-800">Instructions</CardTitle>
                        </CardHeader>
                        <CardContent className="pt-4">
                            <ol className="space-y-3">
                                {recipe.steps.split(",").map((step, i) => (
                                    <li key={i} className="text-sm leading-relaxed flex gap-3">
                                        <span className="flex-shrink-0 w-6 h-6 rounded-full bg-violet-100 text-violet-700 text-xs font-bold flex items-center justify-center">
                                            {i + 1}
                                        </span>
                                        <span className="text-gray-700 pt-0.5">{step.trim()}</span>
                                    </li>
                                ))}
                            </ol>
                        </CardContent>
                    </Card>
                )}
            </div>
        </div>
    )
}
