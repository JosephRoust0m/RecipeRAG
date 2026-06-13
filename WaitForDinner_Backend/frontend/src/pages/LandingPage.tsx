import { useEffect } from "react"
import { Link, useNavigate } from "react-router-dom"
import { getSession } from "@/lib/api"

export default function LandingPage() {
    const navigate = useNavigate()

    useEffect(() => {
        if (getSession()) navigate("/home")
    }, [navigate])

    return (
        <div className="min-h-screen w-full bg-gradient-to-br from-orange-50 via-white to-amber-50 flex flex-col items-center justify-center p-8">

            {/* Logo / branding */}
            <div className="flex flex-col items-center gap-3 mb-10">
                <span className="text-6xl">🍽</span>
                <h1 className="text-5xl font-bold tracking-tight text-gray-800">WaitForDinner</h1>
                <p className="text-gray-500 text-lg text-center max-w-sm">
                    Your personal recipe assistant — cook what you already have.
                </p>
            </div>

            {/* CTA buttons */}
            <div className="flex flex-col gap-3 w-full max-w-xs">
                <Link
                    to="/login"
                    className="w-full h-14 rounded-xl bg-orange-500 text-white text-lg font-semibold shadow-md hover:bg-orange-600 transition-colors flex items-center justify-center"
                >
                    Sign In
                </Link>
                <Link
                    to="/signup"
                    className="w-full h-14 rounded-xl border-2 border-orange-400 text-orange-600 text-lg font-semibold hover:bg-orange-50 transition-colors flex items-center justify-center"
                >
                    Create Account
                </Link>
            </div>

            <p className="mt-10 text-xs text-gray-400">Find dinner with what's already in your pantry.</p>
        </div>
    )
}
