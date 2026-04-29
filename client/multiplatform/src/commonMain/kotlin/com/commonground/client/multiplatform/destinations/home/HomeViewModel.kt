package com.commonground.client.multiplatform.destinations.home

import androidx.lifecycle.ViewModel
import com.commonground.core.Event
import com.commonground.core.EventId
import kotlin.time.Duration.Companion.hours

class HomeViewModel : ViewModel() {
    val events = listOf(
        Event(EventId("1"), "Chess Tournament", "A competitive open-bracket chess tournament.", "Central Park", "2026-05-15", false, 5.hours, false),
        Event(EventId("2"), "Tech Meetup", "Developers discussing Kotlin Multiplatform.", "Tech Hub Office", "2026-05-20", true, 3.hours, false),
        Event(EventId("3"), "Beach Volleyball", "Friendly 4v4 matches on the sand.", "Sunny Beach", "2026-06-01", false, 4.hours, false),
        Event(EventId("4"), "Book Swapping", "Bring a book, take a book.", "City Library", "2026-06-05", false, 2.hours, false),
        Event(EventId("5"), "House Warming", "Celebrating our new apartment.", "Private Residence", "2026-06-12", true, 6.hours, false),
        Event(EventId("6"), "Marathon Training", "Group run for marathon prep.", "Forest Trail", "2026-06-15", false, 2.hours, false),
        Event(EventId("7"), "Coding Workshop", "Hands-on Spring Boot tutorial.", "University Lab", "2026-06-22", true, 4.hours, true),
        Event(EventId("8"), "Jazz Night", "Live local jazz quartet performance.", "Blue Note Cafe", "2026-06-25", false, 3.hours, true),
        Event(EventId("9"), "Photography Walk", "Capturing urban landscapes at sunset.", "Downtown Square", "2026-07-02", false, 2.hours, false),
        Event(EventId("10"), "Board Game Night", "Playing Catan and Ticket to Ride.", "Alex's Home", "2026-07-05", true, 5.hours, false),
        Event(EventId("11"), "Yoga in the Park", "Morning vinyasa flow session.", "Green Garden", "2026-07-10", false, 1.hours, false),
        Event(EventId("12"), "Art Exhibition", "Showcasing local student paintings.", "Modern Gallery", "2026-07-15", false, 8.hours, false),
        Event(EventId("13"), "Cooking Class", "Learning to make authentic pasta.", "Culinary School", "2026-07-20", true, 3.hours, true),
        Event(EventId("14"), "Movie Night", "Outdoor screening of a classic film.", "Community Park", "2026-07-28", false, 2.5.hours, false),
        Event(EventId("15"), "Networking Mixer", "Professional gathering for startup founders.", "Skyline Lounge", "2026-08-01", true, 3.hours, true),
        Event(EventId("16"), "Charity Auction", "Raising funds for local animal shelters.", "Grand Hotel", "2026-08-05", false, 4.hours, true),
        Event(EventId("17"), "Hiking Adventure", "Challenging trek to the mountain summit.", "Pine Peak", "2026-08-12", false, 7.hours, false),
        Event(EventId("18"), "Language Exchange", "Practice French and Arabic with locals.", "Harmony Cafe", "2026-08-18", false, 2.hours, false),
        Event(EventId("19"), "Potluck Dinner", "Everyone brings a dish to share.", "Sarah's Backyard", "2026-08-22", true, 4.hours, false),
        Event(EventId("20"), "E-sports Viewing", "Watching the world championships live.", "Gaming Arena", "2026-08-30", false, 6.hours, true)
    )

    fun search(query: String) {
        // TODO
    }
}