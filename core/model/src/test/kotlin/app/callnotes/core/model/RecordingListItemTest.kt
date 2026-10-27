package app.callnotes.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Duration
import java.time.Instant

class RecordingListItemTest {

    private fun item(
        type: RecordingType,
        title: String? = null,
        contactName: String? = null,
        number: String? = null,
    ) = RecordingListItem(
        id = "id",
        type = type,
        title = title,
        startedAt = Instant.EPOCH,
        duration = Duration.ofMinutes(3),
        direction = null,
        counterpartNumber = number?.let { PhoneNumber(normalized = it, raw = it) },
        contactId = null,
        contactName = contactName,
        contactPhotoUri = null,
        oneLineSummary = null,
        stage = ProcessingStage.DONE,
        openTodoCount = 0,
    )

    @Test
    fun `un appel affiche le nom du contact en priorite`() {
        val call = item(RecordingType.CALL, title = "Titre", contactName = "Martin", number = "0612345678")
        assertThat(call.headline).isEqualTo("Martin")
    }

    @Test
    fun `un appel sans contact affiche le numero`() {
        val call = item(RecordingType.CALL, number = "06 12 34 56 78")
        assertThat(call.headline).isEqualTo("06 12 34 56 78")
    }

    @Test
    fun `une reunion affiche son titre en priorite`() {
        val meeting = item(RecordingType.MEETING, title = "Point chantier", contactName = "Martin")
        assertThat(meeting.headline).isEqualTo("Point chantier")
    }

    @Test
    fun `sans aucune information le libelle est nul`() {
        assertThat(item(RecordingType.MEMO).headline).isNull()
    }
}
