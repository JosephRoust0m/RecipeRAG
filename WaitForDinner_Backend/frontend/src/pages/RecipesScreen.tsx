import { useState, useEffect } from "react"
import { Link, useNavigate } from "react-router-dom"

import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"

import { searchRecipes, getSession, getValidRecipes, clearSession, logout, type RecipeSearchItem } from "@/lib/api"

const PIC_KEY = "wfd_profile_pic"

export default function RecipesScreen() {
    const navigate = useNavigate()
    const [recipes, setRecipes] = useState<RecipeSearchItem[]>([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")
    const [keyword, setKeyword] = useState("")
    const [cuisine, setCuisine] = useState("")
    const [dietaryTag, setDietaryTag] = useState("")
    const [searched, setSearched] = useState(false)
    const [profilePicUrl, setProfilePicUrl] = useState("")

    useEffect(() => {
        const session = getSession()
        if (!session) {
            navigate("/login")
            return
        }
        setProfilePicUrl(localStorage.getItem(PIC_KEY) ?? "")
        getAllMatching()
    }, [navigate])

    const getAllMatching = async () => {
        setLoading(true)
        setError("")
        try {
            const results = await getValidRecipes()
            setRecipes(results)
            setSearched(true)
        } catch {
            setRecipes([])
            setError("Failed to load recipes. Please try again.")
        } finally {
            setLoading(false)
        }
    }

    const doSearch = async (kw: string, cuis: string, diet: string) => {
        setLoading(true)
        setError("")
        try {
            const results = await searchRecipes(
                kw || undefined,
                cuis || undefined,
                diet || undefined
            )
            setRecipes(results)
            setSearched(true)
        } catch {
            setRecipes([])
            setError("Search failed. Please try again.")
        } finally {
            setLoading(false)
        }
    }

    const onSearch = () => doSearch(keyword, cuisine, dietaryTag)

    const onClear = () => {
        setKeyword("")
        setCuisine("")
        setDietaryTag("")
        getAllMatching()
    }

    const difficultyColor = (d?: string) => {
        if (!d) return "bg-gray-100 text-gray-600"
        switch (d.toLowerCase()) {
            case "easy": return "bg-green-100 text-green-700"
            case "medium": return "bg-yellow-100 text-yellow-700"
            case "hard": return "bg-red-100 text-red-700"
            default: return "bg-gray-100 text-gray-600"
        }
    }

    const session = getSession()

    return (
        <div className="min-h-screen w-full bg-gradient-to-br from-orange-50 via-white to-amber-50 flex flex-col">

            {/* Nav bar */}
            <header className="w-full bg-white border-b shadow-sm px-6 py-3 flex items-center justify-between">
                <h1 className="text-xl font-bold tracking-tight text-orange-600">🍽 WaitForDinner</h1>

                <nav className="flex items-center gap-1">
                    <Link to="/home" className="px-3 py-1.5 text-sm text-gray-500 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-colors">
                        Home
                    </Link>
                    <Link to="/recipes" className="px-3 py-1.5 text-sm font-semibold text-orange-600 bg-orange-50 border border-orange-200 rounded-lg">
                        Recipes
                    </Link>
                    <Link to="/chatbot" className="px-3 py-1.5 text-sm text-gray-500 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-colors">
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

            <div className="flex-1 flex items-start justify-center p-8">
            <div className="w-full max-w-6xl space-y-6">

                {/* Header */}
                <div>
                    <h1 className="text-3xl font-bold text-gray-800">🍳 Recipes</h1>
                    <p className="text-gray-500 text-sm mt-1">Recipes you can make with what you have</p>
                </div>

                {/* Search filters */}
                <Card className="w-full border-0 shadow-md">
                    <CardHeader className="pb-3 border-b">
                        <CardTitle className="text-orange-700">Search Recipes</CardTitle>
                        <CardDescription>Filter by name, cuisine, or dietary tag</CardDescription>
                    </CardHeader>
                    <CardContent className="pt-5">
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="keyword" className="text-gray-700 font-medium">Keyword</Label>
                                <Input
                                    id="keyword"
                                    value={keyword}
                                    onChange={e => setKeyword(e.target.value)}
                                    placeholder="e.g. pasta, chicken..."
                                    onKeyDown={e => { if (e.key === "Enter") onSearch() }}
                                    className="border-gray-200 focus:border-orange-300 focus:ring-orange-200"
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="cuisine" className="text-gray-700 font-medium">Cuisine</Label>
                                <Input
                                    id="cuisine"
                                    value={cuisine}
                                    onChange={e => setCuisine(e.target.value)}
                                    placeholder="e.g. Italian, Mexican..."
                                    onKeyDown={e => { if (e.key === "Enter") onSearch() }}
                                    className="border-gray-200 focus:border-orange-300 focus:ring-orange-200"
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="dietaryTag" className="text-gray-700 font-medium">Dietary Tag</Label>
                                <Input
                                    id="dietaryTag"
                                    value={dietaryTag}
                                    onChange={e => setDietaryTag(e.target.value)}
                                    placeholder="e.g. Vegan, Gluten-Free..."
                                    onKeyDown={e => { if (e.key === "Enter") onSearch() }}
                                    className="border-gray-200 focus:border-orange-300 focus:ring-orange-200"
                                />
                            </div>
                        </div>
                        <div className="flex gap-2 mt-5">
                            <button
                                onClick={onSearch}
                                disabled={loading}
                                className="bg-orange-500 hover:bg-orange-600 disabled:opacity-50 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
                            >
                                {loading ? "Searching..." : "Search"}
                            </button>
                            <button
                                onClick={onClear}
                                disabled={loading}
                                className="text-gray-500 hover:text-orange-600 disabled:opacity-50 text-sm font-medium px-4 py-2 rounded-lg transition-colors"
                            >
                                Clear
                            </button>
                        </div>
                    </CardContent>
                </Card>

                {/* Error */}
                {error && (
                    <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-600">
                        {error}
                    </div>
                )}

                {/* Results */}
                {searched && !error && (
                    <div className="space-y-3">
                        <p className="text-sm text-gray-500 font-medium">
                            {recipes.length === 0
                                ? "No recipes found matching your pantry."
                                : `✨ ${recipes.length} recipe${recipes.length !== 1 ? "s" : ""} you can make right now`}
                        </p>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                            {recipes.map(recipe => (
                                <Link key={recipe.recipeId} to={`/recipe/${recipe.recipeId}`}>
                                    <Card className="hover:shadow-lg hover:scale-[1.02] transition-all cursor-pointer h-full border-0 shadow-md bg-white">
                                        <CardHeader className="pb-2">
                                            <CardTitle className="text-base text-gray-800">{recipe.name}</CardTitle>
                                            <CardDescription className="flex flex-wrap gap-1 mt-1">
                                                {recipe.cuisine && (
                                                    <span className="inline-block bg-blue-100 text-blue-700 text-xs px-2 py-0.5 rounded-full">
                                                        {recipe.cuisine}
                                                    </span>
                                                )}
                                                {recipe.dietaryTag && recipe.dietaryTag !== "none" && (
                                                    <span className="inline-block bg-green-100 text-green-700 text-xs px-2 py-0.5 rounded-full">
                                                        {recipe.dietaryTag}
                                                    </span>
                                                )}
                                            </CardDescription>
                                        </CardHeader>
                                        <CardContent className="pt-0">
                                            <div className="flex flex-wrap gap-2 text-xs">
                                                {recipe.time > 0 && (
                                                    <span className="bg-orange-50 text-orange-600 px-2 py-0.5 rounded-full border border-orange-100">
                                                        ⏱ {recipe.time} min
                                                    </span>
                                                )}
                                                {recipe.difficulty && (
                                                    <span className={`px-2 py-0.5 rounded-full ${difficultyColor(recipe.difficulty)}`}>
                                                        {recipe.difficulty}
                                                    </span>
                                                )}
                                            </div>
                                        </CardContent>
                                    </Card>
                                </Link>
                            ))}
                        </div>
                    </div>
                )}
            </div>
            </div>
        </div>
    )
}
