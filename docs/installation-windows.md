# Installer CallNotes sur ton téléphone depuis un PC Windows

Deux méthodes :

- **A. Avec Android Studio** (recommandée) : tu compiles l'app toi-même, tu peux la modifier et la relancer en un clic.
- **B. Sans rien installer sur le PC** : tu télécharges l'APK compilé par GitHub et tu l'installes directement sur le téléphone.

---

## A. Avec Android Studio

### 1. Prérequis

- Windows 10 ou 11 (64 bits), 16 Go de RAM conseillés (8 Go minimum), environ 20 Go d'espace disque libre.
- Un câble USB qui transmet des données (pas seulement la charge), ou le téléphone et le PC sur le même Wi-Fi.
- Ton téléphone sous Android 13 ou plus récent.

### 2. Installer Android Studio

1. Télécharge Android Studio sur <https://developer.android.com/studio> (bouton *Download Android Studio*).
2. Lance l'installateur et garde les options par défaut (*Android Virtual Device* peut être décoché : on utilisera ton vrai téléphone).
3. Au premier démarrage, choisis l'installation **Standard** et accepte les licences : Android Studio télécharge le SDK Android (plusieurs Go, patience).
4. Mets Android Studio à jour : menu **Help › Check for Updates**. Le projet utilise des versions récentes (Android Gradle Plugin 9.4) ; une version trop ancienne affiche « *incompatible version of the Android Gradle plugin* ».

### 3. Récupérer le projet

1. Sur l'écran d'accueil d'Android Studio : **Clone Repository** (ou menu **File › New › Project from Version Control**).
2. URL : `https://github.com/sisi45451/Free-sacha-.git`
   Si Android Studio demande Git, clique sur *Download and Install*. Pour un dépôt privé, connecte ton compte GitHub quand c'est proposé.
3. Choisis un dossier **sans espaces ni accents**, par exemple `C:\dev\CallNotes` (cela évite des erreurs de compilation sous Windows).
4. Clique sur **Clone**, puis **Trust Project**.
5. Pour tester une étape pas encore fusionnée : en bas à droite, clique sur le nom de la branche (*main*) et choisis la branche de l'étape (par exemple `origin/claude/callnotes-android-app-ynhe7i`) › **Checkout**.

### 4. Première compilation

1. Android Studio lance automatiquement une **synchronisation Gradle** (barre de progression en bas). La première fois, elle télécharge Gradle et les bibliothèques : comptez 5 à 15 minutes.
2. Si la plateforme Android 17 (API 37) manque, Android Studio propose un lien *Install missing SDK package* : clique dessus. Sinon : **Tools › SDK Manager › SDK Platforms**, coche **Android 17 (API 37)** › **Apply**.
3. Vérifie le JDK utilisé par Gradle : **File › Settings › Build, Execution, Deployment › Build Tools › Gradle › Gradle JDK** doit indiquer le JDK intégré à Android Studio (*jbr-21* ou plus récent).
4. Choisis la variante : menu **View › Tool Windows › Build Variants**, puis pour le module `:app` sélectionne **persoDebug** (la variante personnelle, avec le journal d'appels).

### 5. Préparer le téléphone (exemple Samsung)

1. **Paramètres › À propos du téléphone › Informations sur le logiciel** : appuie 7 fois sur **Numéro de version**. Le message « Mode développeur activé » s'affiche.
2. **Paramètres › Options de développement** : active **Débogage USB**.
3. **Samsung (One UI 6 et +)** : **Paramètres › Sécurité et confidentialité › Blocage automatique** (*Auto Blocker*) doit être **désactivé**, sinon il bloque les commandes USB et l'installation d'apps hors Galaxy Store / Play Store. Tu pourras le réactiver après l'installation.
4. Branche le téléphone en USB. Sur le téléphone, accepte **Autoriser le débogage USB** (coche *Toujours autoriser depuis cet ordinateur*).
5. Si le téléphone n'apparaît pas dans Android Studio : installe le pilote USB du fabricant (Samsung : *Samsung Android USB Driver for Windows*, sur le site développeurs de Samsung), puis débranche/rebranche.

**Sans câble (Wi-Fi)** : sur le téléphone, **Options de développement › Débogage sans fil** ; dans Android Studio, menu déroulant des appareils › **Pair Devices Using Wi-Fi**, puis scanne le QR code avec le téléphone.

### 6. Installer et lancer

1. En haut d'Android Studio, sélectionne ton téléphone dans la liste des appareils.
2. Clique sur **Run ▶** (ou `Maj + F10`).
3. L'app **CallNotes** s'installe et s'ouvre sur le téléphone. Elle reste installée même une fois le câble débranché.

Pour mettre à jour après une nouvelle étape : **Git › Pull** (ou `Ctrl + T`), puis **Run ▶** à nouveau. Les données de l'app sont conservées.

---

## B. Sans Android Studio : l'APK compilé par GitHub

À chaque modification, GitHub compile l'app (voir l'onglet **Actions** du dépôt).

1. Sur le PC ou directement sur le téléphone, ouvre <https://github.com/sisi45451/Free-sacha-/actions> (connecté à ton compte GitHub).
2. Clique sur la dernière exécution réussie (coche verte) de **Android CI** pour la branche voulue.
3. En bas de la page, section **Artifacts** : télécharge **callnotes-perso-debug** (un fichier `.zip`).
4. Décompresse-le : il contient `app-perso-debug.apk`.
5. Copie l'APK sur le téléphone (câble, Google Drive…) et ouvre-le avec l'app **Mes fichiers**.
6. Android demande d'autoriser l'installation depuis cette source : accepte (**Paramètres › Installer des applis inconnues**). Sur Samsung, le **Blocage automatique** doit être désactivé (voir A.5).

> Les méthodes A et B produisent des APK signés avec la même clé de debug (fichier `app/debug.keystore` du dépôt) : tu peux passer de l'une à l'autre, la nouvelle version s'installe par-dessus l'ancienne en gardant tes données.

---

## Dépannage

| Problème | Solution |
|---|---|
| « SDK location not found » | Ouvre le projet avec Android Studio (il crée `local.properties` tout seul) au lieu de lancer Gradle à la main. |
| « Unsupported class file major version » ou erreur de JDK | Gradle JDK : choisis le JDK intégré d'Android Studio (A.4, point 3). |
| « incompatible version of the Android Gradle plugin » | Mets Android Studio à jour (A.2, point 4). |
| Erreurs avec des chemins très longs | Clone le projet dans un dossier court, par exemple `C:\dev\CallNotes`. |
| Le téléphone est « unauthorized » | Débranche, rebranche et accepte la demande de débogage USB sur l'écran du téléphone. |
| « INSTALL_FAILED_UPDATE_INCOMPATIBLE » | Une version signée différemment est déjà installée : désinstalle CallNotes du téléphone puis relance. |
| La synchronisation Gradle échoue sur un téléchargement | Vérifie la connexion (proxy d'entreprise, pare-feu), puis **File › Sync Project with Gradle Files**. |
