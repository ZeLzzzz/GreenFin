# Finance Audit App

A simple and intuitive finance audit application built with Jetpack Compose and Firebase. This app helps users track their financial transactions, manage wallets, and gain insights into their spending habits.

## Features

*   **User Authentication:** Secure sign-in with Google.
*   **Wallet Management:** Create and manage multiple wallets.
*   **Transaction Tracking:** Add, view, and categorize income and expenses.
*   **AI-Powered Insights:** (Assumed based on Groq API) Get smart financial advice or automatic transaction categorization.
*   **Clean & Modern UI:** A beautiful and user-friendly interface built with Jetpack Compose.

## Tech Stack & Architecture

This project follows the principles of Clean Architecture, promoting a separation of concerns and making the codebase more scalable and maintainable.

*   **UI (Presentation Layer):**
    *   [Jetpack Compose](https://developer.android.com/jetpack/compose) for building the UI.
    *   [Compose Navigation](https://developer.android.com/jetpack/compose/navigation) for handling in-app navigation.
    *   [Coil](https://coil-kt.github.io/coil/compose/) for image loading.
*   **State Management:**
    *   [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) to store and manage UI-related data.
    *   Kotlin Flows and StateFlow for reactive data streams.
*   **Dependency Injection:**
    *   [Hilt](https://dagger.dev/hilt/) for managing dependencies throughout the app.
*   **Data Layer:**
    *   [Firebase Firestore](https://firebase.google.com/docs/firestore) as the primary database.
    *   [Firebase Authentication](https://firebase.google.com/docs/auth) for user authentication.
    *   [Retrofit](https://square.github.io/retrofit/) for networking with the Groq API.
*   **Architecture:**
    *   **MVVM (Model-View-ViewModel):** The presentation layer follows the MVVM pattern.
    *   **Clean Architecture:** The app is divided into three main layers:
        *   `presentation`: UI and ViewModels.
        *   `domain`: Use cases and business logic.
        *   `data`: Repositories and data sources (Firebase, APIs).

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

*   Android Studio Iguana | 2023.2.1 or later.
*   A Google account for Firebase.

### Setup

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/your_username/financeAudit.git
    ```
2.  **Firebase Setup:**
    *   Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
    *   Add an Android app to your Firebase project with the package name `com.example.financeaudit`.
    *   Download the `google-services.json` file and place it in the `app/` directory.
    *   In the Firebase Console, enable **Google Sign-In** in the Authentication section.
    *   Set up **Firestore Database** and configure the security rules.
3.  **Groq API (Optional):**
    *   Get your API key from [Groq](https://groq.com/).
    *   Open `app/src/main/java/com/example/financeaudit/presentation/viewmodel/ScanViewModel.kt`.
    *   Add your API key to the `GROQ_API_KEY` variable on line 55.
4.  **Build and Run:**
    *   Open the project in Android Studio.
    *   Let Gradle sync the dependencies.
    *   Run the app on an emulator or a physical device.

## Project Structure

```
financeAudit/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/financeaudit/
│   │   │   │   ├── data/        # Repositories, data sources (Firebase, API), models
│   │   │   │   ├── domain/      # Use cases, business logic, domain models
│   │   │   │   ├── presentation/ # UI (Compose screens), ViewModels
│   │   │   │   ├── di/          # Hilt dependency injection modules
│   │   │   │   └── BaseApplication.kt # Application class
│   │   │   └── res/           # XML resources (drawables, layouts, etc.)
│   └── build.gradle.kts     # App-level Gradle build script
├── build.gradle.kts         # Project-level Gradle build script
└── README.md
```
