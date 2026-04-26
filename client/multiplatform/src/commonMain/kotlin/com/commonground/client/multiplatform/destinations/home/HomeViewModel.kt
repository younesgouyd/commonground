package com.commonground.client.multiplatform.destinations.home

class HomeViewModel {
    val events = listOf(
        Event(1, "Chess Tournament", "A competitive open-bracket chess tournament.", "Central Park", "2026-05-15", false, "5h", false),
        Event(2, "Tech Meetup", "Developers discussing Kotlin Multiplatform.", "Tech Hub Office", "2026-05-20", true, "3h", false),
        Event(3, "Beach Volleyball", "Friendly 4v4 matches on the sand.", "Sunny Beach", "2026-06-01", false, "4h", false),
        Event(4, "Book Swapping", "Bring a book, take a book.", "City Library", "2026-06-05", false, "2h", false),
        Event(5, "House Warming", "Celebrating our new apartment.", "Private Residence", "2026-06-12", true, "6h", false),
        Event(6, "Marathon Training", "Group run for marathon prep.", "Forest Trail", "2026-06-15", false, "2h", false),
        Event(7, "Coding Workshop", "Hands-on Spring Boot tutorial.", "University Lab", "2026-06-22", true, "4h", true),
        Event(8, "Jazz Night", "Live local jazz quartet performance.", "Blue Note Cafe", "2026-06-25", false, "3h", true),
        Event(9, "Photography Walk", "Capturing urban landscapes at sunset.", "Downtown Square", "2026-07-02", false, "2h", false),
        Event(10, "Board Game Night", "Playing Catan and Ticket to Ride.", "Alex's Home", "2026-07-05", true, "5h", false),
        Event(11, "Yoga in the Park", "Morning vinyasa flow session.", "Green Garden", "2026-07-10", false, "1h", false),
        Event(12, "Art Exhibition", "Showcasing local student paintings.", "Modern Gallery", "2026-07-15", false, "8h", false),
        Event(13, "Cooking Class", "Learning to make authentic pasta.", "Culinary School", "2026-07-20", true, "3h", true),
        Event(14, "Movie Night", "Outdoor screening of a classic film.", "Community Park", "2026-07-28", false, "2.5h", false),
        Event(15, "Networking Mixer", "Professional gathering for startup founders.", "Skyline Lounge", "2026-08-01", true, "3h", true),
        Event(16, "Charity Auction", "Raising funds for local animal shelters.", "Grand Hotel", "2026-08-05", false, "4h", true),
        Event(17, "Hiking Adventure", "Challenging trek to the mountain summit.", "Pine Peak", "2026-08-12", false, "7h", false),
        Event(18, "Language Exchange", "Practice French and Arabic with locals.", "Harmony Cafe", "2026-08-18", false, "2h", false),
        Event(19, "Potluck Dinner", "Everyone brings a dish to share.", "Sarah's Backyard", "2026-08-22", true, "4h", false),
        Event(20, "E-sports Viewing", "Watching the world championships live.", "Gaming Arena", "2026-08-30", false, "6h", true)
    )

    fun search(query: String) {
        // TODO
    }
}