# 📰 NewsBits – Modern News App <br> ![Build](https://img.shields.io/badge/build-passing-brightgreen) ![Platform](https://img.shields.io/badge/platform-Android-blue) ![Firebase](https://img.shields.io/badge/backend-Firebase-orange) ![Algolia](https://img.shields.io/badge/search-Algolia-0D47A1) ![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg) 

**NewsBits** is a fast, full-featured, and beautifully designed **full stack news reader** built with **Jetpack Compose**, powered by a **Firebase** backend, and enhanced with **Algolia Search**, **real-time analytics**, and **offline caching**.  
> 🚀 Built with scalability, modern Android architecture, and real-world use-cases in mind.

---

## 📚 Table of Contents
1. [Features](#-features)  
2. [Architecture](#-architecture)  
3. [UI Showcase](#-ui-showcase)  
4. [Firebase Setup](#-firebase-setup)  
5. [Algolia Search Setup](#-algolia-search-setup)  
6. [API Key / `local.properties` Setup](#-api-key--localproperties-setup)  
7. [Getting Started](#-getting-started)  
8. [Troubleshooting](#-troubleshooting)  
9. [Roadmap](#-roadmap)  
10. [Author](#-author)  
11. [License](#-license)  

---

## ✨ Features
- 🌐 **Real-time News Sync**  
  • Fetches news from NewsData.io API every **7.5 min** via Cloud Functions (200 req/day limit optimized).  
  • Firestore stores parsed articles for fully consistent data across devices.  
- 🔍 **Powerful Algolia Search**  
  • Typo-tolerant, lightning-fast keyword search.  
  • Local search history persisted with Room.  
- 📚 **Offline Read-Later**  
  • Firestore SDK’s offline cache + explicit local bookmarks.  
- 🔔 **Push Notifications** *(under development)*  
  • Breaking news alerts through **Firebase Cloud Messaging**.  
- 🔄 **Paging 3 + Compose**  
  • Infinite scrolling for large datasets.  
- 🧱 **Modern UI/UX**  
  • Material 3, Dynamic Color (Material You Expressive), Dark/Light modes, tablet layouts.  
- 📊 **Category-Wise Analytics**  
  • Insights powered by **Firebase Analytics**.  

---

## 🧱 Architecture
- **MVVM** (ViewModel → Repository → Data sources)  
- **Jetpack Compose** UI + **Navigation Component**  
- **Kotlin Coroutines & Flow** for reactive streams  
- **Firebase Firestore** (offline-first)  
- **Cloud Functions + FCM** *(push notifications WIP)*  
- **Algolia** (search index)  
- **Room** (bookmarks & search history)  
- **Material 3 Expressive** theming  

---

## 🖼 UI Showcase
<p align="center">
  <img src="screenshots/home_light.png"  width="22%"/>
  <img src="screenshots/home_dark.png"  width="22%"/>
  <img src="screenshots/article.png"    width="22%"/>
  <img src="screenshots/search.png"     width="22%"/>
</p>
<p align="center">
  <img src="screenshots/bookmark.png"   width="22%"/>
  <img src="screenshots/categories.png" width="22%"/>
  <img src="screenshots/settings.png"   width="22%"/>
  <img src="screenshots/tablet.png"     width="22%"/>
</p>

---

## 🔥 Firebase Setup
> ⚠️ **Required** – The app will **not compile or run** without a valid Firebase configuration.  
>  
> ✅ Be sure to enable the following services in your Firebase project:
> - **Firestore Database**
> - **Firebase Analytics**
> - **Firebase Cloud Functions**
> - *(Optional)* **Firebase Cloud Messaging** – for push notifications

1. **Create a Firebase project**  
   • Go to the [Firebase Console](https://console.firebase.google.com).  
   • Enable **Firestore Database** and **Firebase Analytics**.  
   • *(Optional)* Enable **Cloud Messaging** if you want push notifications.

2. **Add `google-services.json`**  
   • Firebase Console → ⚙️ **Project Settings** → **General** → **Your Apps**  
   • Register an **Android** app with the package name `com.example.newsbits` (or your own).  
   • Download **`google-services.json`** and place it in **`app/`** (same level as `build.gradle.kts`).  
   • **Never commit** this file; add it to `.gitignore`.

3. **Cloud Functions Backend**  
   The included `functions/` directory contains a ready-to-deploy TypeScript function that:  
   • Fetches headlines from the NewsData.io API every 7.5 minutes (scheduled trigger).  
   • Saves/updates articles in Firestore.  
   • Mirrors new/updated docs to Algolia (see below).

   > 🔔 Note: Cloud Functions with scheduled triggers require Blaze Plan (free if usage is within quota).

   ```
   # From project root
   cd functions
   npm install
   firebase deploy --only functions
   ```

5. **Firestore Security Rules**  
   Update rules to allow authenticated read/write or maintain read-only public access for demo.

---

## 🔍 Algolia Search Setup
1. **Create an Algolia account** → [Algolia Dashboard](https://www.algolia.com).  
2. **Create an Application & Index** (e.g., `newsbits_articles`).  
3. **Grab credentials**  
   • **Application ID**  
   • **Search-Only API Key**  
   • *(Optional but recommended)* **Admin API Key** (only used in Cloud Functions, not the Android client).  
4. **Configure the index**  
   • Searchable attributes: `title`, `description`, `content`, `category`, `source`.  
   • Ranking strategy: `typo`, `geo`, `words`, `proximity`, `attribute`, `exact`, `custom`.  
5. **Sync Firestore → Algolia**  
   The same Cloud Function mentioned above listens for Firestore writes and pushes records to Algolia in real-time.

---

## 🔑 API Key / `local.properties` Setup
Add **all** secrets to `local.properties` (never commit to VCS):

```
# Algolia
ALGOLIA_APP_ID=YOUR_ALGOLIA_APP_ID
ALGOLIA_SEARCH_KEY=YOUR_ALGOLIA_SEARCH_ONLY_API_KEY
ALGOLIA_INDEX=newsbits_articles
```

Expose them in **`app/build.gradle.kts`**:

```
buildConfigField("String", "ALGOLIA_APP_ID", "\"${localProps["ALGOLIA_APP_ID"]}\"")
buildConfigField("String", "ALGOLIA_SEARCH_KEY", "\"${localProps["ALGOLIA_SEARCH_KEY"]}\"")
buildConfigField("String", "ALGOLIA_INDEX", "\"${localProps["ALGOLIA_INDEX"]}\"")
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio Hedgehog (or newer)**  
- **JDK 17+**  
- **Minimum SDK 24 (Android 7.0)**  
- Firebase & Algolia accounts

### 1. Clone the repo
```
git clone https://github.com/ShanuDevCodes/NewsBits.git
cd NewsBits
```

### 2. Configure Firebase
Follow every step in [Firebase Setup](#-firebase-setup) and place `google-services.json` in `app/`.

### 3. Configure Algolia keys
Add them to `local.properties` as shown [above](#-api-key--localproperties-setup).

### 4. Sync & Run
- Open in Android Studio  
- **File ▸ Sync Project with Gradle Files**  
- Select a device/emulator and **Run ▶️**

### 5. (Optional) Deploy Cloud Functions
```
cd functions
firebase deploy --only functions
```

---

## 🧩 Troubleshooting
| Issue | Fix |
|-------|-----|
| `google-services.json` not found | Verify file location: `app/google-services.json` |
| *Could not get unknown property 'ALGOLIA_APP_ID'* | Add keys to `local.properties` **and** re-sync Gradle |
| **403** from NewsData API | You exceeded the 200 req/day quota – wait or upgrade plan |
| Blank search results | Check Algolia index name & credentials; redeploy Cloud Function |

---

## 🔮 Roadmap
- ✅ Firebase backend with scheduled API fetch  
- ✅ Firestore → Algolia sync  
- ✅ Category-wise analytics  
- ✅ Offline bookmarks  
- ✅ Paging 3 integration  
- ❌ Push notifications via FCM *(WIP)*  
- ⏳ iOS & Web clients *(future)*  

---

## 🧑‍💻 Author
Made with ❤️ by **[ShanuDevCodes](https://github.com/ShanuDevCodes)**.  
If you like the project, please ⭐️ **star** the repo or consider a fork!

---

## 📄 License
MIT License — see [`LICENSE`](LICENSE) for full text.
```
