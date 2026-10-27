// =============================================================================
// Fichier de build racine : déclare les plugins (sans les appliquer).
// Chaque module applique ensuite ceux dont il a besoin.
//
// Note AGP 9 : le support Kotlin est intégré au plugin Android (« built-in
// Kotlin »). Il ne faut PLUS appliquer org.jetbrains.kotlin.android dans les
// modules Android. Déclarer ici les plugins Kotlin (compose, serialization,
// jvm) en version 2.4.x fait aussi monter la version du compilateur Kotlin
// utilisé par AGP.
// =============================================================================

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
}
