package com.reelsapp.ui.home

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.reelsapp.reels.repository.ReelsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class HomeViewModel @AssistedInject constructor(
    @Assisted initialState: HomeState
) : MavericksViewModel<HomeState>(initialState) {

    private val repository = ReelsRepository()

    init {
        loadReels()
    }

    fun loadReels(query: String = "sports") {
        withState { currentState ->
            viewModelScope.launch {
                setState { copy(isLoadingReels = true) }
                val items = repository.getReels(apiKey = currentState.pexelsApiKey, query = query)
                setState { copy(reels = items, isLoadingReels = false) }
            }
        }
    }

    /** Set Pexels API Key dynamically */
    fun setPexelsApiKey(key: String) {
        setState { copy(pexelsApiKey = key) }
        loadReels()
    }

    /** Show the "Open Reels" animated dialog */
    fun showReelsDialog() = setState { copy(isReelsDialogVisible = true) }

    /** Dismiss the dialog */
    fun dismissReelsDialog() = setState { copy(isReelsDialogVisible = false) }

    /** Launch full-screen Reels feed */
    fun launchReelsFeed() = setState { copy(isReelsDialogVisible = false, isReelsFeedActive = true) }

    /** Close Reels feed */
    fun closeReelsFeed() = setState { copy(isReelsFeedActive = false) }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HomeViewModel, HomeState> {
        override fun create(state: HomeState): HomeViewModel
    }

    companion object : MavericksViewModelFactory<HomeViewModel, HomeState> by hiltMavericksViewModelFactory()
}
