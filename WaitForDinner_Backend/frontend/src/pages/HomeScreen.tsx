import { useEffect, useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"

import { getSession, clearSession, logout, getSavedRecipes, getCookedRecipes, type SavedRecipe, type CookedRecipe } from "@/lib/api"

const PIC_KEY = "wfd_profile_pic"

export default function HomeScreen() {
    const navigate = useNavigate()
    const [savedRecipes, setSavedRecipes] = useState<SavedRecipe[]>([])
    const [cookedRecipes, setCookedRecipes] = useState<CookedRecipe[]>([])
    const [profilePicUrl, setProfilePicUrl] = useState("")
    const [savedError, setSavedError] = useState("")
    const [cookedError, setCookedError] = useState("")

    useEffect(() => {
        const session = getSession()
        if (!session) {
            navigate("/login")
            return
        }

        setProfilePicUrl(localStorage.getItem(PIC_KEY) ?? "")

        getSavedRecipes()
            .then(setSavedRecipes)
            .catch(() => setSavedError("Failed to load saved recipes."))

        getCookedRecipes()
            .then(setCookedRecipes)
            .catch(() => setCookedError("Failed to load cooked recipes."))
    }, [navigate])

    const session = getSession()

    return (
        <div className="min-h-screen w-full bg-gradient-to-br from-orange-50 via-white to-amber-50 text-foreground flex flex-col">

            {/* Top nav bar */}
            <header className="w-full bg-white border-b shadow-sm px-6 py-3 flex items-center justify-between">
                <h1 className="text-xl font-bold tracking-tight text-orange-600">
                    🍽 WaitForDinner
                </h1>
                <nav className="flex items-center gap-1">
                    <Link
                        to="/home"
                        className="px-3 py-1.5 text-sm font-semibold text-orange-600 bg-orange-50 border border-orange-200 rounded-lg"
                    >
                        Home
                    </Link>
                    <Link
                        to="/recipes"
                        className="px-3 py-1.5 text-sm text-gray-500 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-colors"
                    >
                        Recipes
                    </Link>
                    <Link
                        to="/chatbot"
                        className="px-3 py-1.5 text-sm text-gray-500 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-colors"
                    >
                        🤖 AI Recommendation
                    </Link>
                </nav>
                <div className="flex items-center gap-3">
                    <Link
                        to="/profile-settings"
                        className="flex items-center gap-2 px-3 py-1.5 rounded-lg hover:bg-orange-50 transition-colors"
                    >
                        {profilePicUrl ? (
                            <img
                                src={profilePicUrl}
                                className="h-8 w-8 rounded-full object-cover border-2 border-orange-200"
                                alt="Profile"
                            />
                        ) : (
                            <div className="h-8 w-8 rounded-full bg-orange-100 border-2 border-orange-200 flex items-center justify-center text-sm font-bold text-orange-600">
                                {session?.username?.charAt(0)?.toUpperCase() ?? "?"}
                            </div>
                        )}
                        <span className="text-sm font-medium text-gray-700">
                            {session?.username ?? "Profile"}
                        </span>
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

            <main className="mx-auto max-w-6xl px-6 py-10 w-full flex-1">

                <div className="mb-8">
                    <h2 className="text-3xl font-bold text-gray-800">
                        Good evening, {session?.username ?? "Chef"} 👋
                    </h2>
                    <p className="text-gray-500 mt-1">What are we cooking tonight?</p>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start">

                    {/* LEFT: Saved recipes */}
                    <Card className="lg:col-span-1 border-0 shadow-md bg-indigo-50/40">
                        <CardHeader className="pb-3 border-b border-indigo-100">
                            <div className="flex items-center gap-2">
                                <span className="text-xl">🔖</span>
                                <div>
                                    <CardTitle className="text-base text-indigo-800">Saved Recipes</CardTitle>
                                    <CardDescription>Your bookmarked meals</CardDescription>
                                </div>
                            </div>
                        </CardHeader>
                        <CardContent className="pt-4">
                            {savedError ? (
                                <p className="text-sm text-red-500">{savedError}</p>
                            ) : (
                                <div className="max-h-[420px] overflow-y-auto space-y-2 pr-1">
                                    {savedRecipes.length === 0 ? (
                                        <p className="text-sm text-muted-foreground italic">No saved recipes yet.</p>
                                    ) : (
                                        savedRecipes.map((r) => (
                                            <Link key={r.recipeId} to={`/recipe/${r.recipeId}`}>
                                                <div className="border border-indigo-100 bg-white/70 rounded-lg px-3 py-2 text-sm hover:bg-indigo-100 hover:border-indigo-300 cursor-pointer transition-colors">
                                                    <span className="font-medium text-gray-800">{r.name}</span>
                                                    {r.cuisine && (
                                                        <span className="text-indigo-500 ml-2 text-xs">· {r.cuisine}</span>
                                                    )}
                                                </div>
                                            </Link>
                                        ))
                                    )}
                                </div>
                            )}
                        </CardContent>
                    </Card>

                    {/* MIDDLE: Action buttons */}
                    <div className="lg:col-span-1 flex flex-col justify-center space-y-4">
                        <Link
                            to="/recipes"
                            className="w-full h-24 rounded-2xl bg-gradient-to-br from-orange-400 to-orange-600 text-white text-xl font-semibold shadow-lg hover:shadow-orange-200 hover:scale-[1.02] transition-all flex items-center justify-center gap-3"
                        >
                            <span className="text-3xl">🍳</span>
                            <div className="flex flex-col">
                                <span>Get Dinner</span>
                                <span className="text-sm font-normal opacity-80">Browse & search recipes</span>
                            </div>
                        </Link>

                        <Link
                            to="/pantry"
                            className="w-full h-24 rounded-2xl bg-gradient-to-br from-green-400 to-emerald-600 text-white text-xl font-semibold shadow-lg hover:shadow-green-200 hover:scale-[1.02] transition-all flex items-center justify-center gap-3"
                        >
                            <span className="text-3xl">🛒</span>
                            <div className="flex flex-col">
                                <span>Update Pantry</span>
                                <span className="text-sm font-normal opacity-80">Manage your ingredients</span>
                            </div>
                        </Link>

                        <Link
                            to="/dietary-preferences"
                            className="w-full h-24 rounded-2xl bg-gradient-to-br from-violet-400 to-purple-600 text-white text-xl font-semibold shadow-lg hover:shadow-purple-200 hover:scale-[1.02] transition-all flex items-center justify-center gap-3"
                        >
                            <span className="text-3xl">🥗</span>
                            <div className="flex flex-col">
                                <span>Dietary Preferences</span>
                                <span className="text-sm font-normal opacity-80">Set your diet & cuisine</span>
                            </div>
                        </Link>
                    </div>

                    {/* RIGHT: Cooked recipes */}
                    <Card className="lg:col-span-1 border-0 shadow-md bg-emerald-50/40">
                        <CardHeader className="pb-3 border-b border-emerald-100">
                            <div className="flex items-center gap-2">
                                <span className="text-xl">✅</span>
                                <div>
                                    <CardTitle className="text-base text-emerald-800">Cooked Recipes</CardTitle>
                                    <CardDescription>Your cooking history</CardDescription>
                                </div>
                            </div>
                        </CardHeader>
                        <CardContent className="pt-4">
                            {cookedError ? (
                                <p className="text-sm text-red-500">{cookedError}</p>
                            ) : (
                                <div className="max-h-[420px] overflow-y-auto space-y-2 pr-1">
                                    {cookedRecipes.length === 0 ? (
                                        <p className="text-sm text-muted-foreground italic">No cooked recipes yet.</p>
                                    ) : (
                                        cookedRecipes.map((r, idx) => (
                                            <Link key={`${r.recipeId}-${idx}`} to={`/recipe/${r.recipeId}`}>
                                                <div className="border border-emerald-100 bg-white/70 rounded-lg px-3 py-2 text-sm hover:bg-emerald-100 hover:border-emerald-300 cursor-pointer transition-colors">
                                                    <span className="font-medium text-gray-800">{r.name}</span>
                                                    {r.cookedAt && (
                                                        <span className="text-emerald-600 ml-2 text-xs">
                                                            · {new Date(r.cookedAt).toLocaleDateString()}
                                                        </span>
                                                    )}
                                                </div>
                                            </Link>
                                        ))
                                    )}
                                </div>
                            )}
                        </CardContent>
                    </Card>

                </div>
            </main>
        </div>
    )
}
