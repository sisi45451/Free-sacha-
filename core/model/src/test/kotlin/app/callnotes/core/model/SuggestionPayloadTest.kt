package app.callnotes.core.model

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.junit.Test

class SuggestionPayloadTest {

    @Test
    fun `une proposition survit a un aller-retour JSON`() {
        val payload: SuggestionPayload = SuggestionPayload.UpdateContactField(
            contactId = "c1",
            field = ContactField.EMAIL,
            value = "martin@exemple.fr",
            evidence = "Mon adresse c'est martin@exemple.fr",
        )

        val json = Json.encodeToString(SuggestionPayload.serializer(), payload)
        val decoded = Json.decodeFromString(SuggestionPayload.serializer(), json)

        assertThat(json).contains("\"type\":\"update_contact_field\"")
        assertThat(decoded).isEqualTo(payload)
        assertThat(decoded.kind).isEqualTo(SuggestionKind.UPDATE_CONTACT_FIELD)
    }
}
