import { useEffect, useMemo, useState } from "react"
import { Link, useNavigate } from "react-router-dom"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { getSession, setSession, updateProfile } from "@/lib/api"

type Errors = Partial<Record<"username" | "email" | "password" | "confirmPassword" | "picture", string>>

const PIC_KEY = "wfd_profile_pic"

export default function ProfileSettings() {
    const navigate = useNavigate()
    const [loaded, setLoaded] = useState(false)

    const [username, setUsername] = useState("")
    const [email, setEmail] = useState("")
    const [profilePictureUrl, setProfilePictureUrl] = useState("")
    const [selectedFile, setSelectedFile] = useState<File | null>(null)
    const [password, setPassword] = useState("")
    const [confirmPassword, setConfirmPassword] = useState("")

    const [errors, setErrors] = useState<Errors>({})
    const [successMsg, setSuccessMsg] = useState("")
    const [serverError, setServerError] = useState("")
    const [saving, setSaving] = useState(false)

    useEffect(() => {
        const session = getSession()
        if (!session) {
            navigate("/login")
            return
        }
        setUsername(session.username)
        setEmail(session.email)
        const savedPic = localStorage.getItem(PIC_KEY) ?? ""
        setProfilePictureUrl(savedPic)
        setLoaded(true)
    }, [navigate])

    const filePreviewUrl = useMemo(() => {
        if (!selectedFile) return ""
        return URL.createObjectURL(selectedFile)
    }, [selectedFile])

    useEffect(() => {
        return () => {
            if (filePreviewUrl) URL.revokeObjectURL(filePreviewUrl)
        }
    }, [filePreviewUrl])

    const validate = (): Errors => {
        const e: Errors = {}

        if (!username.trim()) e.username = "Name is required."
        if (!email.trim()) e.email = "Email is required."

        if (password.trim()) {
            if (password.length < 6) e.password = "Password must be at least 6 characters."
            if (password !== confirmPassword) e.confirmPassword = "Passwords do not match."
        }

        const emailRegex = /^\S+@\S+\.\S+$/
        if (email.trim() && !emailRegex.test(email.trim())) {
            e.email = "Invalid email format."
        }

        if (selectedFile) {
            if (!selectedFile.type.startsWith("image/")) {
                e.picture = "Selected file must be an image."
            }
            const maxBytes = 5 * 1024 * 1024
            if (selectedFile.size > maxBytes) {
                e.picture = "Image must be 5MB or smaller."
            }
        }

        return e
    }

    const onSave = async () => {
        setSuccessMsg("")
        setServerError("")
        const e = validate()
        setErrors(e)
        if (Object.keys(e).length) return

        const session = getSession()
        if (!session) { navigate("/login"); return }

        setSaving(true)
        try {
            const payload: { username?: string; email?: string; password?: string } = {
                username: username.trim(),
                email: email.trim(),
            }
            if (password.trim()) payload.password = password

            const res = await updateProfile(session.userId, payload)

            // Update stored session with new name/email
            setSession({ userId: res.userId || session.userId, username: res.username || (res as any).name || username, email: res.email })

            // Save profile picture locally
            if (selectedFile) {
                localStorage.setItem(PIC_KEY, filePreviewUrl)
                setProfilePictureUrl(filePreviewUrl)
            }

            setPassword("")
            setConfirmPassword("")
            setSelectedFile(null)
            setSuccessMsg("Profile updated successfully.")
        } catch (err: unknown) {
            setServerError(err instanceof Error ? err.message : "Update failed.")
        } finally {
            setSaving(false)
        }
    }

    if (!loaded) return null

    const shownPicture = selectedFile ? filePreviewUrl : profilePictureUrl

    return (
        <div className="min-h-screen w-full flex items-start justify-center p-8">
            <div className="w-full max-w-6xl space-y-6">

                {/* Page title */}
                <div className="flex items-center justify-between">
                    <h1 className="text-2xl font-bold">Profile Settings</h1>

                    <Button variant="ghost" asChild>
                        <Link to="/home">← Back</Link>
                    </Button>
                </div>

                {/* 2-column layout: left edit card, right action button */}
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-10 items-start">

                    {/* LEFT: Edit card */}
                    <Card className="w-full max-w-xl">
                        <CardHeader className="space-y-1">
                            <CardTitle>Edit</CardTitle>
                            <CardDescription>Update your profile information.</CardDescription>
                        </CardHeader>

                        <CardContent className="space-y-5">
                            {/* current profile picture displayed */}
                            {shownPicture && (
                                <div className="flex flex-col items-center gap-3">
                                    <img
                                        src={shownPicture}
                                        alt="Profile"
                                        className="h-24 w-24 rounded-full object-cover border"
                                    />
                                    <p className="text-xs text-muted-foreground">Current profile picture</p>
                                </div>
                            )}

                            {/* name */}
                            <div className="space-y-2">
                                <Label htmlFor="username">Username</Label>
                                <Input
                                    id="username"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    placeholder="Enter your username"
                                />
                                {errors.username && <p className="text-sm text-red-500">{errors.username}</p>}
                            </div>

                            {/* email */}
                            <div className="space-y-2">
                                <Label htmlFor="email">Email</Label>
                                <Input
                                    id="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    placeholder="Enter your email"
                                />
                                {errors.email && <p className="text-sm text-red-500">{errors.email}</p>}
                            </div>

                            {/* password */}
                            <div className="space-y-2">
                                <Label htmlFor="password">Password</Label>
                                <Input
                                    id="password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder="Enter a new password (optional)"
                                />
                                {errors.password && <p className="text-sm text-red-500">{errors.password}</p>}
                            </div>

                            {/* confirm password */}
                            <div className="space-y-2">
                                <Label htmlFor="confirmPassword">Confirm password</Label>
                                <Input
                                    id="confirmPassword"
                                    type="password"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    placeholder="Confirm your new password"
                                />
                                {errors.confirmPassword && <p className="text-sm text-red-500">{errors.confirmPassword}</p>}
                            </div>

                            {/* upload picture */}
                            <div className="space-y-2">
                                <Label htmlFor="upload">Upload new profile picture</Label>
                                <Input
                                    id="upload"
                                    type="file"
                                    accept="image/*"
                                    onChange={(e) => setSelectedFile(e.target.files?.[0] ?? null)}
                                />
                                {errors.picture && <p className="text-sm text-red-500">{errors.picture}</p>}
                            </div>

                            {successMsg && <p className="text-sm text-green-600">{successMsg}</p>}
                            {serverError && <p className="text-sm text-red-500">{serverError}</p>}

                            <Button className="w-full" onClick={onSave} disabled={saving}>
                                {saving ? "Saving..." : "Save Changes"}
                            </Button>
                        </CardContent>
                    </Card>

                    {/* RIGHT: Action buttons */}
                    <div className="w-full flex flex-col items-center lg:items-start pt-6 gap-4">
                        {/* Dietary preferences button */}
                        <button
                            className="w-full max-w-sm h-24 rounded-2xl bg-primary text-primary-foreground text-xl font-semibold shadow-md hover:opacity-90 transition"
                            onClick={() => navigate("/dietary-preferences")}
                        >
                            <div className="flex flex-col items-center">
                                <span>Update Dietary Preferences</span>
                                <span className="text-sm font-normal opacity-80">
                                    Manage your dietary settings
                                </span>
                            </div>
                        </button>

                        {/* Delete account button */}
                        <Link
                            to="/delete-account"
                            className="w-full max-w-sm h-24 rounded-2xl bg-primary text-primary-foreground text-xl font-semibold shadow-md hover:opacity-90 transition flex items-center justify-center"
                        >
                            <div className="flex flex-col items-center">
                                <span>Delete Account</span>
                                <span className="text-sm font-normal opacity-80">
                                    Permanently remove your account
                                </span>
                            </div>
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    )
}
