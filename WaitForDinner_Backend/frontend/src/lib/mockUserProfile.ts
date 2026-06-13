export type Profile = {
    name: string
    email: string
    profilePictureUrl: string
}

const STORAGE_KEY = "wfd_profile"

const DEFAULT_PROFILE: Profile = {
    name: "Robert Sabourin",
    email: "rob@sabourin.com",
    profilePictureUrl: "https://via.placeholder.com/150",
}

export function getProfile(): Profile {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) return JSON.parse(raw) as Profile
    localStorage.setItem(STORAGE_KEY, JSON.stringify(DEFAULT_PROFILE))
    return DEFAULT_PROFILE
}

export function saveProfile(profile: Profile) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(profile))
}
