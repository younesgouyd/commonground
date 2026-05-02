package com.commonground.client.multiplatform.ui.destinations.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.commonground.core.CategoryId
import com.commonground.core.EventCategory
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class OnboardingState {
    data object Loading : OnboardingState()

    data class Loaded(
        val step: Step,
        val categories: List<EventCategory>,
        val selected: Set<CategoryId>,
        val isSubmitting: Boolean = false,
        val error: String? = null
    ) : OnboardingState() {
        val canContinue: Boolean
            get() = when (step) {
                Step.Welcome -> true
                Step.Categories -> selected.size >= MIN_SELECTED
            }
    }

    data object Error : OnboardingState()

    enum class Step { Welcome, Categories }

    companion object {
        const val MIN_SELECTED = 3
    }
}

/**
 * MVP placeholder: hardcoded category list, no CategoryRepo. Swap [HARDCODED_CATEGORIES]
 * for `categoryRepo.getAllCategories()` and persist [finish]'s selection via
 * `categoryRepo.savePreferredCategories(...)` when the backend is wired up.
 *
 * Icon keys here must match the `iconFor(...)` map in Onboarding.kt.
 */
class OnboardingViewModel(
    private val onFinished: () -> Unit
) : ViewModel() {
    private val logger = KotlinLogging.logger {}

    private val _state: MutableStateFlow<OnboardingState> =
        MutableStateFlow(OnboardingState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Brief delay so the loading state is exercised; remove for an instant load.
            delay(200)
            _state.value = OnboardingState.Loaded(
                step = OnboardingState.Step.Welcome,
                categories = HARDCODED_CATEGORIES,
                selected = emptySet()
            )
        }
    }

    fun toggleCategory(id: CategoryId) {
        _state.update { current ->
            if (current !is OnboardingState.Loaded) return@update current
            val newSelected = if (id in current.selected) current.selected - id
            else current.selected + id
            current.copy(selected = newSelected, error = null)
        }
    }

    fun next() {
        val current = _state.value as? OnboardingState.Loaded ?: return
        if (!current.canContinue || current.isSubmitting) return

        when (current.step) {
            OnboardingState.Step.Welcome ->
                _state.value = current.copy(step = OnboardingState.Step.Categories)
            OnboardingState.Step.Categories -> finish(current)
        }
    }

    fun back() {
        val current = _state.value as? OnboardingState.Loaded ?: return
        if (current.step == OnboardingState.Step.Categories) {
            _state.value = current.copy(step = OnboardingState.Step.Welcome)
        }
    }

    fun skip() {
        val current = _state.value as? OnboardingState.Loaded ?: return
        finish(current.copy(selected = emptySet()))
    }

    private fun finish(current: OnboardingState.Loaded) {
        _state.value = current.copy(isSubmitting = true, error = null)
        viewModelScope.launch {
            delay(500)
            logger.info { "Hardcoded onboarding finish; selected=${current.selected.map { it.value }}" }
            onFinished()
        }
    }

    private companion object {
        val HARDCODED_CATEGORIES = listOf(
            EventCategory(CategoryId("music"),     "Music",          "Concerts, gigs, jam sessions",                "music"),
            EventCategory(CategoryId("sports"),    "Sports",         "Pickup games, tournaments, fitness meetups",  "sports"),
            EventCategory(CategoryId("tech"),      "Tech",           "Hackathons, talks, demo nights",              "tech"),
            EventCategory(CategoryId("food"),      "Food & Drink",   "Tastings, supper clubs, restaurant pop-ups",  "food"),
            EventCategory(CategoryId("art"),       "Art",            "Galleries, exhibitions, art walks",           "art"),
            EventCategory(CategoryId("education"), "Education",      "Workshops, lectures, study groups",           "education"),
            EventCategory(CategoryId("books"),     "Books",          "Book clubs, author talks, poetry nights",     "books"),
            EventCategory(CategoryId("theater"),   "Theater",        "Plays, improv, performances",                 "theater"),
            EventCategory(CategoryId("fitness"),   "Fitness",        "Running clubs, yoga, group workouts",         "fitness"),
            EventCategory(CategoryId("cafe"),      "Coffee Meetups", "Casual cafe gatherings",                      "cafe"),
            EventCategory(CategoryId("design"),    "Design",         "Design critiques, portfolio reviews",         "design"),
            EventCategory(CategoryId("community"), "Community",      "Neighborhood events, volunteering",           null)
        )
    }
}