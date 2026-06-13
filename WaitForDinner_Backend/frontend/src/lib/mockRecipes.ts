export type Recipe = { id: string; title: string; description: string }

export function getSavedRecipes(): Recipe[] {
    return [
        {
            id: "1",
            title: "Spaghetti Carbonara",
            description: "A classic Italian pasta dish made with eggs, cheese, pancetta, and pepper."
        },
        {
            id: "2",
            title: "Chicken Tikka Masala",
            description: "A popular Indian curry dish made with marinated chicken pieces cooked in a creamy tomato sauce."
        },
        {
            id: "3",
            title: "Vegetable Stir-Fry",
            description: "A quick and healthy dish made with a mix of fresh vegetables sautéed in a savory sauce."
        }
    ]
}

export function getCookedRecipes(): Recipe[] {
    return [
        {
            id: "4",
            title: "Beef Tacos",
            description: "Soft tortillas filled with seasoned ground beef, topped with lettuce, cheese, and salsa."
        },
        {
            id: "5",
            title: "Margherita Pizza",
            description: "A simple yet delicious pizza topped with fresh tomatoes, mozzarella cheese, and basil leaves."
        }
    ]
}