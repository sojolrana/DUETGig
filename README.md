# DUETGig — Student Freelancing & Peer Service Platform 🚀

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Language](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com/)
[![Backend](https://img.shields.io/badge/Backend-Firebase-ffca28.svg)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**DUETGig** is a dedicated peer-to-peer marketplace mobile platform designed exclusively for students at Dhaka University of Engineering & Technology (DUET). It enables skilled engineering students to showcase their practical expertise, accept project requests, build professional portfolios, and earn income while pursuing their academic degrees.

---

## 📌 Features

- **Student Authentication:** Secure onboarding verified for university students.
- **Skill Profiles:** Custom portfolio pages showcasing technical skills (Programming, CAD, Web Dev, Robotics, Tutoring).
- **Service Marketplace:** Browse and offer specialized services and peer assistance.
- **Project Request Board:** Post project requirements and accept gig proposals.
- **Real-Time Messaging:** Direct chat system connecting service providers and clients.
- **Ratings & Reviews:** Transparency and trust building through peer reviews.
- **Earnings Dashboard:** Track completed gigs and earned income.
- **Push Notifications:** Instant updates for project requests and chat messages via Firebase Cloud Messaging.

---

## 🛠 Tech Stack

* **Frontend:** Android Native (Java/XML) - *Migration to Jetpack Compose in progress*
* **Backend:** Firebase (Auth, Firestore, Storage, Messaging)
* **Build System:** Kotlin DSL with Version Catalog (`libs.versions.toml`)
* **Architecture:** MVP / Modular Android Architecture

---

## 🚀 Getting Started

### Prerequisites

* **Android Studio:** Ladybug (2024.2.1) or newer
* **Firebase Setup**: Create a project in the [Firebase Console](https://console.firebase.google.com/).
* **Configuration File**: Download the `google-services.json` file and place it in the `app/` directory.
* **Local Properties**: Ensure you have a `local.properties` file with your Android SDK path.

### Installation & Setup

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/sojolrana/DUETGig.git
   cd DUETGig
   ```
2. **Open the project in Android Studio.**
3. **Add your `google-services.json` to the `app/` folder.**
4. **Build and run the app.**

---

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
