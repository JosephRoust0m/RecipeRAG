import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LandingPage from './pages/LandingPage'
import HomeScreen from './pages/HomeScreen'
import ProfileSettings from './pages/ProfileSettings'
import DeleteAccount from './pages/DeleteAccount'
import SignUp from './pages/SignUp'
import Login from './pages/Login'
import DietaryPreferences from './pages/DietaryPreferences'
import PantryScreen from './pages/PantryScreen'
import RecipesScreen from './pages/RecipesScreen'
import RecipeDetails from './pages/RecipeDetails'
import Chatbot from './pages/Chatbot'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/login" element={<Login />} />
        <Route path="/home" element={<HomeScreen />} />
        <Route path="/profile-settings" element={<ProfileSettings />} />
        <Route path="/delete-account" element={<DeleteAccount />} />
        <Route path="/dietary-preferences" element={<DietaryPreferences />} />
        <Route path="/pantry" element={<PantryScreen />} />
        <Route path="/recipes" element={<RecipesScreen />} />
        <Route path="/recipe/:id" element={<RecipeDetails />} />
        <Route path="/chatbot" element={<Chatbot />} />
      </Routes>
    </BrowserRouter>
  )
}
