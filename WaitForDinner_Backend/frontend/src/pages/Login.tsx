import { useState } from "react"
import { useNavigate, Link } from "react-router-dom"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { login, setSession } from "@/lib/api"

type Errors = Partial<Record<"email" | "password", string>>

export default function Login() {
  const navigate = useNavigate()

  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [errors, setErrors] = useState<Errors>({})
  const [serverError, setServerError] = useState("")
  const [submitting, setSubmitting] = useState(false)

  const validate = (): Errors => {
    const e: Errors = {}
    if (!email.trim()) e.email = "Email is required."
    if (!password.trim()) e.password = "Password is required."
    return e
  }

  const onSubmit = async () => {
    setServerError("")
    const e = validate()
    setErrors(e)
    if (Object.keys(e).length) return

    setSubmitting(true)
    try {
      const res = await login({ email: email.trim(), password })
      setSession({ userId: res.userId, username: res.username || (res as any).name || '', email: res.email })
      navigate("/home")
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : ""
      if (msg.includes("403") || msg.includes("401") || msg.includes("invalid") || msg.includes("credentials")) {
        setServerError("Login failed. Wrong email or password.")
      } else if (msg.includes("404")) {
        setServerError("No account found with that email.")
      } else if (msg.includes("500")) {
        setServerError("Server error. Please try again later.")
      } else {
        setServerError("Login failed. Please try again.")
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="min-h-screen w-full bg-gradient-to-br from-orange-50 via-white to-amber-50 flex flex-col items-center justify-center p-8">

      {/* Branding */}
      <Link to="/" className="flex flex-col items-center gap-1 mb-8 hover:opacity-80 transition-opacity">
        <span className="text-4xl">🍽</span>
        <span className="text-xl font-bold text-orange-600">WaitForDinner</span>
      </Link>

      <Card className="w-full max-w-md border-0 shadow-lg">
        <CardHeader className="text-center pb-4 border-b">
          <CardTitle className="text-2xl font-bold text-gray-800">Welcome back</CardTitle>
          <CardDescription>Sign in to your account</CardDescription>
        </CardHeader>

        <CardContent className="space-y-5 pt-6">

          {/* Server error banner */}
          {serverError && (
            <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-600 flex items-start gap-2">
              <span className="mt-0.5">⚠️</span>
              <span>{serverError}</span>
            </div>
          )}

          {/* Email */}
          <div className="space-y-1.5">
            <Label htmlFor="email" className="text-gray-700 font-medium">Email</Label>
            <Input
              id="email"
              type="email"
              value={email}
              onChange={(e) => { setEmail(e.target.value); setErrors(prev => ({ ...prev, email: undefined })) }}
              onKeyDown={e => { if (e.key === "Enter") onSubmit() }}
              placeholder="you@example.com"
              className={errors.email ? "border-red-400 focus:border-red-400" : ""}
            />
            {errors.email && <p className="text-xs text-red-500">{errors.email}</p>}
          </div>

          {/* Password */}
          <div className="space-y-1.5">
            <Label htmlFor="password" className="text-gray-700 font-medium">Password</Label>
            <Input
              id="password"
              type="password"
              value={password}
              onChange={(e) => { setPassword(e.target.value); setErrors(prev => ({ ...prev, password: undefined })) }}
              onKeyDown={e => { if (e.key === "Enter") onSubmit() }}
              placeholder="Enter your password"
              className={errors.password ? "border-red-400 focus:border-red-400" : ""}
            />
            {errors.password && <p className="text-xs text-red-500">{errors.password}</p>}
          </div>

          {/* Submit */}
          <Button
            className="w-full bg-orange-500 hover:bg-orange-600 text-white font-semibold h-11"
            onClick={onSubmit}
            disabled={submitting}
          >
            {submitting ? "Signing in..." : "Sign In"}
          </Button>

          <p className="text-center text-sm text-gray-500">
            Don't have an account?{" "}
            <Link to="/signup" className="text-orange-600 font-medium hover:underline">
              Sign up
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
