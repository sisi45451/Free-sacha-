package app.callnotes.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.callnotes.core.domain.repository.FeedFilter
import app.callnotes.core.domain.repository.RecordingRepository
import app.callnotes.core.model.RecordingListItem
import app.callnotes.core.model.RecordingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** État de l'écran « Fil ». */
sealed interface FeedUiState {
    data object Loading : FeedUiState

    data class Ready(
        /** Filtre par type sélectionné (`null` = tous). */
        val typeFilter: RecordingType?,
        val items: List<RecordingListItem>,
    ) : FeedUiState

    data class Error(val message: String?) : FeedUiState
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val recordingRepository: RecordingRepository,
) : ViewModel() {

    private val typeFilter = MutableStateFlow<RecordingType?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FeedUiState> = typeFilter
        .flatMapLatest { type ->
            recordingRepository.observeFeed(FeedFilter(type = type))
                .map<List<RecordingListItem>, FeedUiState> { items -> FeedUiState.Ready(type, items) }
        }
        .catch { emit(FeedUiState.Error(it.message)) }
        .stateIn(
            scope = viewModelScope,
            // Garde la requête active 5 s après la disparition de l'écran
            // (rotation, retour rapide) pour éviter de la relancer.
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FeedUiState.Loading,
        )

    fun onTypeFilterChange(type: RecordingType?) {
        typeFilter.value = type
    }
}
