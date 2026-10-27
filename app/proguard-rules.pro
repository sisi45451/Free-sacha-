# Règles R8 de la version « release » (réduction et obfuscation du code).

# SQLCipher : la bibliothèque native appelle ces classes par JNI.
-keep,includedescriptorclasses class net.zetetic.database.** { *; }
-keep,includedescriptorclasses interface net.zetetic.database.** { *; }
