package app.callnotes.feature.feed

import app.callnotes.core.domain.repository.FeedFilter
import app.callnotes.core.domain.repository.RecordingRepository
import app.callnotes.core.model.ProcessingStage
import app.callnotes.core.model.Recording
import app.callnotes.core.model.RecordingListItem
import app.callnotes.core.model.RecordingType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `le fil affiche les enregistrements du repository`() = runTest(dispatcher) {
        val repository = FakeRecordingRepository(listOf(item("a", RecordingType.CALL)))
        val viewModel = FeedViewModel(repository)
        backgroundScope.launch { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(FeedUiState.Ready::class.java)
        assertThat((state as FeedUiState.Ready).items.map { it.id }).containsExactly("a")
    }

    @Test
    fun `le filtre par type ne garde que les reunions`() = runTest(dispatcher) {
        val repository = FakeRecordingRepository(
            listOf(item("call", RecordingType.CALL), item("meeting", RecordingType.MEETING)),
        )
        val viewModel = FeedViewModel(repository)
        backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onTypeFilterChange(RecordingType.MEETING)

        val state = viewModel.uiState.value as FeedUiState.Ready
        assertThat(state.typeFilter).isEqualTo(RecordingType.MEETING)
        assertThat(state.items.map { it.id }).containsExactly("meeting")
    }

    private fun item(id: String, type: RecordingType) = RecordingListItem(
        id = id,
        type = type,
        title = null,
        startedAt = Instant.EPOCH,
        duration = Duration.ofMinutes(1),
        direction = null,
        counterpartNumber = null,
        contactId = null,
        contactName = null,
        contactPhotoUri = null,
        oneLineSummary = null,
        stage = ProcessingStage.DONE,
        openTodoCount = 0,
    )

    /** Faux repository en mémoire : applique le filtre par type. */
    private class FakeRecordingRepository(items: List<RecordingListItem>) : RecordingRepository {
        private val all = MutableStateFlow(items)

        override fun observeFeed(filter: FeedFilter): Flow<List<RecordingListItem>> =
            all.map { list -> list.filter { filter.type == null || it.type == filter.type } }

        override fun observeRecording(id: String): Flow<Recording?> = flowOf(null)

        override suspend fun upsert(recording: Recording) = Unit

        override suspend fun delete(id: String) = Unit
    }
}
