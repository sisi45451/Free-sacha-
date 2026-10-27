# CallNotes

Journal de mes appels, réunions et mémos vocaux, avec transcription, analyse IA et un assistant pour interroger le tout.

App Android native (Kotlin, Jetpack Compose, Material 3) — Android 13 minimum, optimisée pour Android 17. Version web en option (React + Vite, synchronisée par Supabase).

- **Appels** : l'enregistreur natif du téléphone dépose un fichier, CallNotes le détecte, identifie l'interlocuteur (journal d'appels, contacts), transcrit, analyse et notifie, sans aucune action de ta part.
- **Réunions et mémos** : enregistrés directement par l'app.
- **Analyse IA** : résumé, to-dos, points clés, questions en suspens, tags, dossier et mises à jour de fiche contact proposés.
- **Assistant** « Demande à tes appels » : questions en langage naturel, réponses sourcées.
- **Confidentialité** : base chiffrée, rien dans le cloud sans activation explicite.

## Documentation

- [Architecture](docs/architecture.md) : modèle de données, modules, flux, sécurité, décisions à valider.
- [Installation sur le téléphone depuis Windows](docs/installation-windows.md)

## Avancement

| Étape | Contenu | État |
|---|---|---|
| 1 | Architecture, squelette, base chiffrée, navigation | ✅ à valider |
| 2 | MVP appels : détection, identification, transcription, historique | |
| 3 | Analyse IA, to-dos, assistant | |
| 4 | Dossiers et fiches contacts enrichies | |
| 5 | Réunions et mémos vocaux | |
| 6 | Synchronisation et version web | |
| 7 | Gemini, widget, biométrie, RGPD | |
| 8 | Préparation Google Play | |

## Compiler

```bash
./gradlew assemblePersoDebug   # APK : app/build/outputs/apk/perso/debug/
./gradlew test                 # tests unitaires
```

Variantes : `perso` (usage personnel, journal d'appels) et `play` (future version Google Play, sans journal d'appels).
