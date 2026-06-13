import { useState } from "react"
import { useNavigate, Link } from "react-router-dom"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { signup, setSession } from "@/lib/api"

type Errors = Partial<Record<"username" | "email" | "password" | "confirmPassword", string>>

export default function SignUp() {
  const navigate = useNavigate()

  const [username, setUsername] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [errors, setErrors] = useState<Errors>({})
  const [serverError, setServerError] = useState("")
  const [submitting, setSubmitting] = useState(false)

  const clearFieldError = (field: keyof Errors) =>
    setErrors(prev => ({ ...prev, [field]: undefined }))

  const validate = (): Errors => {
    const e: Errors = {}
    if (!username.trim()) e.username = "Username is required."
    if (!email.trim()) {
      e.email = "Email is required."
    } else if (!/^\S+@\S+\.\S+$/.test(email.trim())) {
      e.email = "Invalid email format."
    }
    if (!password.trim()) {
      e.password = "Password is required."
    } else if (password.length < 6) {
      e.password = "Password must be at least 6 characters."
    }
    if (password !== confirmPassword) {
      e.confirmPassword = "Passwords do not match."
    }
    return e
  }

  const onSubmit = async () => {
    setServerError("")
    const e = validate()
    setErrors(e)
    if (Object.keys(e).length) return

    setSubmitting(true)
    try {
      const res = await signup({
        username: username.trim(),
        email: email.trim(),
        password,
      })
      setSession({ userId: res.userId, username: res.username || (res as any).name || '', email: res.email })
      navigate("/home")
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : ""
      if (msg.includes("409") || msg.includes("already") || msg.includes("exists")) {
        setServerError("An account with that email or username already exists.")
      } else if (msg.includes("400")) {
        setServerError("Invalid details. Please check your information and try again.")
      } else if (msg.includes("500")) {
        setServerError("Server error. Please try again later.")
      } else {
        setServerError("Sign up failed. Please try again.")
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
          <CardTitle className="text-2xl font-bold text-gray-800">Create an account</CardTitle>
          <CardDescription>Join WaitForDinner and start cooking smarter</CardDescription>
        </CardHeader>

        <CardContent className="space-y-5 pt-6">

          {/* Server error banner */}
          {serverError && (
            <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-600 flex items-start gap-2">
              <span className="mt-0.5">⚠️</span>
              <span>{serverError}</span>
            </div>
          )}

          {/* Username */}
          <div className="space-y-1.5">
            <Label htmlFor="username" className="text-gray-700 font-medium">Username</Label>
            <Input
              id="username"
              value={username}
              onChange={(e) => { setUsername(e.target.value); clearFieldError("username") }}
              onKeyDown={e => { if (e.key === "Enter") onSubmit() }}
              placeholder="Choose a username"
              className={errors.username ? "border-red-400 focus:border-red-400" : ""}
            />
            {errors.username && <p className="text-xs text-red-500">{errors.username}</p>}
          </div>

          {/* Email */}
          <div className="space-y-1.5">
            <Label htmlFor="email" className="text-gray-700 font-medium">Email</Label>
            <Input
              id="email"
              type="email"
              value={email}
              onChange={(e) => { setEmail(e.target.value); clearFieldError("email") }}
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
              onChange={(e) => { setPassword(e.target.value); clearFieldError("password") }}
              onKeyDown={e => { if (e.key === "Enter") onSubmit() }}
              placeholder="At least 6 characters"
              className={errors.password ? "border-red-400 focus:border-red-400" : ""}
            />
            {errors.password && <p className="text-xs text-red-500">{errors.password}</p>}
          </div>

          {/* Confirm Password */}
          <div className="space-y-1.5">
            <Label htmlFor="confirmPassword" className="text-gray-700 font-medium">Confirm Password</Label>
            <Input
              id="confirmPassword"
              type="password"
              value={confirmPassword}
              onChange={(e) => { setConfirmPassword(e.target.value); clearFieldError("confirmPassword") }}
              onKeyDown={e => { if (e.key === "Enter") onSubmit() }}
              placeholder="Repeat your password"
              className={errors.confirmPassword ? "border-red-400 focus:border-red-400" : ""}
            />
            {errors.confirmPassword && <p className="text-xs text-red-500">{errors.confirmPassword}</p>}
          </div>

          {/* Submit */}
          <Button
            className="w-full bg-orange-500 hover:bg-orange-600 text-white font-semibold h-11"
            onClick={onSubmit}
            disabled={submitting}
          >
            {submitting ? "Creating account..." : "Create Account"}
          </Button>

          <p className="text-center text-sm text-gray-500">
            Already have an account?{" "}
            <Link to="/login" className="text-orange-600 font-medium hover:underline">
              Sign in
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
