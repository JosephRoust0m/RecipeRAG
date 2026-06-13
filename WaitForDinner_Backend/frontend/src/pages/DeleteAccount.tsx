import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Link, useNavigate } from "react-router-dom"

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog"
import { deleteAccount, getSession, clearSession } from "@/lib/api"

export function DeleteAccount() {
  const navigate = useNavigate()
  const [serverError, setServerError] = useState("")
  const [deleting, setDeleting] = useState(false)

  async function handleDelete() {
    const session = getSession()
    if (!session) { navigate("/login"); return }

    setDeleting(true)
    setServerError("")
    try {
      await deleteAccount(session.userId)
      clearSession()
      navigate("/login")
    } catch (err: unknown) {
      setServerError(err instanceof Error ? err.message : "Delete failed.")
    } finally {
      setDeleting(false)
    }
  }

  return (
    <div className="min-h-screen w-full bg-background text-foreground flex items-center justify-center p-8">
      <div className="w-full max-w-md space-y-6">

        {/* Back button - top right */}
       <div className="flex items-center justify-end">
          <Button variant="ghost" asChild>
            <Link to="/profile-settings">← Back</Link>
          </Button>
        </div>

        <Card className="w-full max-w-md">
          <CardHeader className="text-center">
            <CardTitle className="text-2xl font-bold text-destructive">
              Delete Account
            </CardTitle>
            <CardDescription>
              This action is permanent and cannot be undone. All your data,
              including saved recipes, preferences, and pantry items, will be
              permanently deleted.
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="rounded-lg border border-destructive/20 bg-destructive/5 p-4 text-sm text-destructive">
              <strong>Warning:</strong> Deleting your account will permanently
              remove all of your data. This cannot be reversed.
            </div>

            {serverError && <p className="text-sm text-red-500">{serverError}</p>}

            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button variant="destructive" className="w-full" disabled={deleting}>
                  {deleting ? "Deleting..." : "Delete My Account"}
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                  <AlertDialogDescription>
                    This will permanently delete your account and all associated
                    data. This action cannot be undone.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Cancel</AlertDialogCancel>
                  <AlertDialogAction variant="destructive" onClick={handleDelete}>
                    Yes, delete my account
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

export default DeleteAccount
