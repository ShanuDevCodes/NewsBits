# 📰 NewsBits – Modern News App

&#x20;  &#x20;

**NewsBits** is a fast, full-featured, and beautifully designed **full stack news reader** built with **Jetpack Compose**, powered by a **Firebase** backend, and enhanced with **Algolia Search**, **Gemini AI**, **real-time analytics**, and **offline caching**.

> 🚀 Built with scalability, modern Android architecture, and real-world use-cases in mind.

---

## 📚 Table of Contents

1. [Features](#-features)
2. [Architecture](#-architecture)
3. [UI Showcase](#-ui-showcase)
4. [Firebase Setup](#-firebase-setup)
5. [Algolia Search Setup](#-algolia-search-setup)
6. [Gemini API Setup](#-gemini-api-setup)
7. [API Key / ](#-api-key--localproperties-setup)[`local.properties`](#-api-key--localproperties-setup)[ Setup](#-api-key--localproperties-setup)
8. [Getting Started](#-getting-started)
9. [Troubleshooting](#-troubleshooting)
10. [Roadmap](#-roadmap)
11. [Author](#-author)
12. [License](#-license)

---

## ✨ Features

### 🔥 Core Highlights

- 🌐 **Real-time News Sync**\
  • Fetches from NewsData.io every **7.5 min** via Firebase Cloud Functions\
  • Stores parsed data in Firestore\
  • Mirrors to Algolia for blazing-fast search

- 🔍 **Powerful Algolia Search**\
  • Typo-tolerant, blazing-fast search\
  • Search bar in Explore screen\
  • Room database stores local search history

- 📦 **Offline-First Architecture**\
  • Room DB for bookmarks\
  • Firestore SDK with offline cache support

- 💬 **Gemini AI Integration**\
  • Summarizes top 10 headlines in concise or detailed format\
  • Offers AI-powered **Bit Digest** (Daily/Weekly summaries - WIP)

### 🧠 User Experience

- 🎢 **Onboarding Flow with Auth Options**\
  • Modern onboarding screens\
  • Choose topics of interest (personalization)\
  • Continue as **Guest** or login using **Email/Password** or **Google Sign-In**\
  • **Email Verification** required post-authentication

- 📰 **"For You" Page**\
  • Personalized full-screen **vertical carousel** (like Instagram Reels)\
  • Doom-scroll meaningful news based on selected preferences

- 🧱 **Explore Page**\
  • Horizontal carousel for top 10 headlines\
  • Infinite scroll of all news sorted by publish date\
  • Gemini AI summary of top stories (Concise/Detailed options)

- 🔖 **Bookmarks Page**\
  • Locally stored using Room\
  • Instant access to saved news

- 👤 **Profile Page**\
  • View profile info (photo, email, username)\
  • Edit Profile screen\
  • Settings, Rate Us, Share, Help Center, About, GitHub\
  • Logout/Login controls

- 📊 **Category-Wise Analytics**\
  • Tracked via **Firebase Analytics**

---

## 🧱 Architecture

- **MVVM** Pattern
- **Clean Architecture (Room Data Flow)**
- **Jetpack Compose + Material 3 Expressive**
- **Navigation Component**
- **Kotlin Coroutines & Flow**
- **Room Database**
- **Algolia + Firestore for Sync/Search**
- **Ktor Client (for Gemini AI integration)**
- **Firebase Cloud Functions (News fetching & indexing)**

---

## 🖼 UI Showcase

(Screenshots section to be added manually with image URLs)

---

## 🔥 Firebase Setup

> ⚠️ **Required** — The app will **not build** without Firebase.

### Required Services

- Firebase Authentication
- Firebase Firestore
- Firebase Analytics
- Firebase Cloud Functions
- (Optional) Firebase Cloud Messaging

### Authentication Setup

- Enable **Email/Password** and **Google Sign-In** under Firebase Auth
- Add SHA1/SHA256 keys to Firebase Console for Google Sign-In
- Email verification is **mandatory** before user is granted access to the app

> 🔐 Supports **Continue as Guest** and switching to login later.

### Cloud Functions

See included `functions/` directory. Deploy using:

```bash
cd functions
npm install
firebase deploy --only functions
```

---

## 🔍 Algolia Search Setup

1. Create account on [Algolia](https://www.algolia.com)
2. Create index (e.g., `newsbits_articles`)
3. Configure searchable attributes: `title`, `description`, `category`, `source`
4. Add credentials to `local.properties`
5. Firestore → Algolia sync handled via Cloud Functions

---

## 🌟 Gemini API Setup (AI Summaries)

### Prerequisites

- Get an API Key from [Google AI Studio](https://makersuite.google.com/app/apikey)

### Add to `local.properties`

```properties
Gemini_API_Key=YOUR_API_KEY
```

### Expose in `build.gradle.kts`

```kotlin
buildConfigField("String", "Gemini_API_Key", "\"${localProps["Gemini_API_Key"]}\"")
```

> ⚠️ Uses **Ktor HTTP Client** (SDK not used due to dependency conflicts)

---

## 🔑 API Key / `local.properties` Setup

```properties
# Algolia
ALGOLIA_APP_ID=YOUR_APP_ID
ALGOLIA_SEARCH_KEY=YOUR_SEARCH_KEY
ALGOLIA_INDEX=newsbits_articles

# Gemini
Gemini_API_Key=YOUR_API_KEY
```

Add to `build.gradle.kts`:

```kotlin
buildConfigField("String", "ALGOLIA_APP_ID", "\"${localProps["ALGOLIA_APP_ID"]}\"")
buildConfigField("String", "ALGOLIA_SEARCH_KEY", "\"${localProps["ALGOLIA_SEARCH_KEY"]}\"")
buildConfigField("String", "ALGOLIA_INDEX", "\"${localProps["ALGOLIA_INDEX"]}\"")
buildConfigField("String", "Gemini_API_Key", "\"${localProps["Gemini_API_Key"]}\"")
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog+
- JDK 17+
- Firebase + Algolia account
- NewsData.io API key
- Gemini API key

### Steps

```bash
git clone https://github.com/ShanuDevCodes/NewsBits.git
cd NewsBits
```

1. Add `google-services.json` to `app/`
2. Setup `local.properties` with required keys
3. Sync project with Gradle
4. Run on emulator/device

---

## 🧰 Troubleshooting

| Issue                            | Fix                                                            |
| -------------------------------- | -------------------------------------------------------------- |
| `google-services.json` not found | Ensure it's in `app/` directory                                |
| Missing `ALGOLIA_APP_ID`         | Add keys to `local.properties` and re-sync                     |
| Blank AI summary                 | Check Gemini API key and internet connection                   |
| News not syncing                 | Ensure Cloud Function is deployed and Firestore is initialized |

---

## 🔮 Roadmap

- ✅ Firebase Auth (Guest, Google, Email/Password)
- ✅ Email verification & profile editing
- ✅ Cloud Functions with Firestore & Algolia
- ✅ Gemini AI summaries (concise/detailed)
- ✅ Personalized "For You" vertical carousel
- ✅ Explore page with carousel + search
- ✅ Offline Bookmarks (Room DB)
- ✅ Clean Arch (Room Data Flow)
- ⚠️ Bit Digest AI (Daily/Weekly summary)
- ⏳ Web/iOS Support

---

## 🧑‍💻 Author

Made with ❤️ by [**ShanuDevCodes**](https://github.com/ShanuDevCodes).\
Like this project? ⭐️ it or share it with your network!

---

## 📄 License

MIT License — See [`LICENSE`](LICENSE) for details.
