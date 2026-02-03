# 📰 NewsBits – Modern Intelligent News App

![NewsBits Banner](https://img.shields.io/badge/Status-Active-success) ![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple) ![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue) ![Firebase](https://img.shields.io/badge/Firebase-Full%20Stack-orange)

**NewsBits** is a high-performance, intelligent news reader application built with **Modern Android Architecture**. It leverages the power of **Jetpack Compose** for UI, **Firebase** for backend services (Auth, Firestore, Cloud Functions), **Algolia** for lightning-fast search, and **Gemini AI** for smart news summarization.

Key focus areas: **Offline-First**, **Real-Time Sync**, **Clean Architecture**, and **Premium UX**.

---

## 📚 Table of Contents

1. [✨ Features](#-features)
2. [🛠 Tech Stack](#-tech-stack)
3. [🧱 Architecture & Design](#-architecture--design)
4. [📂 Project Structure](#-project-structure)
5. [🔄 Data Flow & Sync](#-data-flow--sync)
6. [🔥 Firebase Setup](#-firebase-setup)
7. [🔍 Algolia Search Setup](#-algolia-search-setup)
8. [🧠 Gemini AI Setup](#-gemini-ai-setup)
9. [⚙️ Local Properties Setup](#-local-properties-setup)
10. [🚀 Getting Started](#-getting-started)
11. [🔮 Roadmap](#-roadmap)
12. [🤝 Contributing & License](#-contributing--license)

---

## ✨ Features

### 🔥 Core Highlights
- **Real-time News Sync**: Fetches global headlines every **7.5 minutes** from NewsData.io via **Firebase Cloud Functions**, syncing seamlessly to Firestore and Algolia.
- **Intelligent Search**: Powered by **Algolia**, offering typo-tolerant, instant search results across thousands of articles.
- **AI Summaries**: Integrated **Gemini AI** to generate concise or detailed summaries of top stories on demand.
- **Offline-First**: Built with **Room Database** and **Firestore Offline Persistence** to ensure the app works flawlessly without internet.

### 🧠 User Experience (UX)
- **"For You" Vertical Feed**: A TikTok/Reels-style vertical carousel for immersive news consumption.
- **Smart Onboarding**: Personalized topic selection and smooth authentication flow (Guest, Google, Email/Password).
- **Interactive Explore**: Horizontal scrolling headlines, infinite feed, and category filtering.
- **Bookmarks & History**: Save articles locally and track reading history.
- **Secure Auth**: Email verification enforcement and secure session management using **Credential Manager** and **Google Identity**.

---

## 🛠 Tech Stack

The project uses the latest Android technologies (as of late 2025/2026).

| Category | Libraries / Tools |
|----------|-------------------|
| **Language** | [Kotlin](https://kotlinlang.org/) (v2.0.21) |
| **UI Toolkit** | [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3, Animation, Adaptive) |
| **Architecture** | MVVM, Clean Architecture, Repository Pattern |
| **Backend** | Firebase (Auth, Firestore, Cloud Functions, Analytics) |
| **Search** | [Algolia Search](https://www.algolia.com/) (Client Kotlin v3.x) |
| **AI / ML** | [Gemini API](https://ai.google.dev/) (via Ktor Client) |
| **Local Data** | [Room Database](https://developer.android.com/training/data-storage/room) (KSP, Coroutines support) |
| **Network** | [Ktor Client](https://ktor.io/) (OkHttp engine), NetworkConnectivityObserver |
| **Image Loading** | [Coil Compose](https://coil-kt.github.io/coil/) |
| **Navigation** | [Navigation Compose](https://developer.android.com/guide/navigation) (Type-safe navigation) |
| **Async** | Kotlin Coroutines, Flow |
| **DI / Mgmt** | Manual Dependency Injection (ViewModelFactory pattern) |
| **Build** | Gradle Kotlin DSL (KTS), Version Catalogs (`libs.versions.toml`) |

---

## 🧱 Architecture & Design

The app follows **Clean Architecture** principles enforced with **MVVM (Model-View-ViewModel)**.

### Layers
1. **UI Layer (`ui/`)**:
   - **Activities**: `MainActivity`, `AuthenticationActivity`, `OnboardingActivity`.
   - **Screens**: Composable functions (e.g., `HomeScreen`, `ExploreScreen`, `BookmarksScreen`) located in `ui/screens`.
   - **Theme**: Material 3 implementation (`Theme.kt`, `Color.kt`, `Type.kt`).
2. **ViewModel Layer (`viewmodel/`)**:
   - Manages UI state using `@Stable` data classes and `StateFlow`.
   - Communicates with Repositories.
3. **Data Layer (`data/`)**:
   - **Repositories**: `NewsRepository` (Single source of truth).
   - **Data Sources**:
     - **Remote**: Firebase Firestore, Algolia, Gemini API (Ktor).
     - **Local**: Room Database (`savedarticledb`), DataStore (`DataStoreManager`).
   - **Models**: Data classes (`NewsArticle`, `News`, `GeminiSummaryType`).

---

## 📂 Project Structure

A high-level overview of the key packages in `app/src/main/java/com/shanudevcodes/newsbits/`:

```
com.shanudevcodes.newsbits
├── data
│   ├── firebase             # Firestore & Auth Helpers
│   ├── savedarticledb       # Room Entities & DAO
│   ├── DataStoreManager.kt  # User preferences (Theme, Onboarding status)
│   ├── NewsRepository.kt    # Main data coordinator
│   ├── NewsArticle.kt       # Core data model
│   └── NetworkConnectivityObserver.kt
├── ui
│   ├── screens              # All Composable Screens (Home, Profile, Login...)
│   ├── authentication       # Login/Signup specific UI
│   ├── animation            # Lottie & Custom animations
│   ├── theme                # App styling
│   └── NavigationItems.kt   # Nav routes
├── viewmodel                # ViewModels for screens
├── MainActivity.kt          # Entry point for App flow
├── AuthenticationActivity.kt# Entry point for Auth flow
└── OnboardingActivity.kt    # Entry point for Onboarding
```

---

## 🔄 Data Flow & Sync

1. **Ingestion**: A scheduled **Firebase Cloud Function** fetches news from *NewsData.io* every 7.5 minutes.
2. **Storage**: Data is cleaned and stored in **Firestore**.
3. **Indexing**: An `onCreate` Firestore trigger automatically pushes new articles to an **Algolia Index** for search capability.
4. **Consumption**:
   - The Android app listens to Firestore for real-time feed updates.
   - Search queries are sent directly to Algolia for sub-millisecond responses.
   - User bookmarks are stored in the local **Room Database**.

---

## 🔥 Firebase Setup

> ⚠️ **Critical**: This project relies extensively on Firebase.

1. **Create Project**: Go to [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. **Add Android App**: Download `google-services.json` and place it in the `app/` directory.
3. **Enable Services**:
   - **Authentication**: Email/Password, Google Sign-In. (Add SHA-1 & SHA-256 fingerprints for Google Auth).
   - **Firestore**: Create a database (Production mode).
   - **Storage** (Optional): If profile pillars are implemented.
4. **Deploy Cloud Functions**:
   ```bash
   cd functions
   npm install
   firebase login
   firebase deploy --only functions
   ```
   *Note: Requires the "Blaze" (Pay as you go) plan for external API calls.*

---

## 🔍 Algolia Search Setup

1. Sign up at [Algolia](https://www.algolia.com/).
2. Create an Index named **`newsbits_articles`**.
3. In your Algolia Dashboard, set **Searchable Attributes**:
   - `title`, `description`, `content`, `category`, `source_id`
4. Copy your **App ID** and **Search API Key**.

---

## 🧠 Gemini AI Setup

1. Get an API Key from [Google AI Studio](https://aistudio.google.com/).
2. This key is used by the `NewsRepository` to send article content to Gemini-1.5-flash model for summarization.

---

## ⚙️ Local Properties Setup

Create a `local.properties` file in the project root to secure your keys. **Do not commit this file.**

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk

# Algolia Configuration
ALGOLIA_APP_ID=YOUR_ALGOLIA_APP_ID
ALGOLIA_SEARCH_KEY=YOUR_ALGOLIA_SEARCH_KEY
ALGOLIA_INDEX=newsbits_articles

# Gemini AI
Gemini_API_Key=YOUR_GEMINI_API_KEY
```

These are automatically injected into `BuildConfig` by Gradle.

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer recommended.
- **JDK**: Version 17 or higher (Project uses Kotlin 2.0).
- **Firebase Project**: Configured as above.

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/ShanuDevCodes/NewsBits.git
   cd NewsBits
   ```
2. **Add Secrets**: Create your `local.properties` file with the keys.
3. **Add Firebase Config**: Place `google-services.json` in `app/`.
4. **Sync & Build**:
   - Open in Android Studio.
   - File -> Sync Project with Gradle Files.
   - Run on an Emulator or Physical Device (API 30+ recommended).

---

## 🔮 Roadmap

- [x] **MVP Release**: Core news feed, Auth, Search.
- [x] **AI Integration**: Gemini Summaries.
- [x] **Offline Mode**: Room DB Caching.
- [ ] **Bit Digest**: Daily AI-curated audio briefings.
- [ ] **Social Features**: Commenting and sharing threads.
- [ ] **Wear OS Support**: Quick headlines on browsing watch face.

---

## 🤝 Contributing & License

Contributions are welcome! Please fork the repo and submit a PR.

**Author**: [ShanuDevCodes](https://github.com/ShanuDevCodes)
**License**: MIT License. See [LICENSE](LICENSE) for details.
