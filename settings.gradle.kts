// =============================================================================
// CallNotes — déclaration des modules Gradle.
// Voir docs/architecture.md § 3 pour le rôle de chaque module et l'étape
// du projet qui le crée.
// =============================================================================

pluginManagement {
    repositories {
        // Le dépôt Google n'est interrogé que pour les groupes Android/Google :
        // la résolution est plus rapide et plus sûre.
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // Interdit aux modules de déclarer leurs propres dépôts : tout passe ici.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CallNotes"

// Application (point d'entrée, navigation, injection de dépendances)
include(":app")

// Socle
include(":core:model")          // Kotlin pur : modèles du domaine
include(":core:domain")         // Kotlin pur : contrats (repositories) et logique métier
include(":core:database")       // Room + SQLCipher : schéma, DAO
include(":core:data")           // Implémentations des repositories
include(":core:security")       // Android Keystore : clé de la base chiffrée
include(":core:designsystem")   // Thème Material 3, composants partagés

// Écrans
include(":feature:feed")        // Onglet « Fil »
