# Spenderman - Expense Tracker App

Spenderman is a multiplatform expense tracking application built with Kotlin Multiplatform and Jetpack Compose. It allows users to track their expenses, manage budgets, and gain insights into their spending habits.

## Features

- **Expense Tracking**: Add, edit, and delete expenses with categories, tags, and payment methods
- **Budget Management**: Create and monitor budgets for different categories
- **Dashboard**: View summaries of income, expenses, and balance
- **Categories**: Organize expenses with customizable categories
- **Reports**: Analyze spending patterns with visual reports
- **Multi-platform**: Works on Android, iOS, and Web

## Technology Stack

- **Kotlin Multiplatform**: For sharing code across platforms
- **Jetpack Compose**: For building the UI
- **Koin**: For dependency injection
- **Multiplatform Settings**: For data persistence
- **Kotlinx Serialization**: For JSON serialization
- **Kotlinx Coroutines**: For asynchronous programming
- **Kotlinx DateTime**: For date and time handling

## Project Structure

- **data/model**: Data classes representing the domain models
- **data/repository**: Interfaces defining the data access layer
- **data/repository/impl**: Implementations of the repository interfaces
- **data/source**: Data sources for storing and retrieving data
- **di**: Dependency injection modules
- **ui/components**: Reusable UI components
- **ui/screens**: Screen composables
- **ui/theme**: Theme definitions
- **ui/viewmodel**: ViewModels for managing UI state
- **util**: Utility classes

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Kotlin 1.8.0 or later
- Gradle 7.0 or later

### Running the App

#### Web

```bash
./gradlew :composeApp:wasmJsRun
```

#### Android

```bash
./gradlew :composeApp:installDebug
```

#### iOS

Open the Xcode project in the `iosApp` directory and run the app.

## Screenshots

(Screenshots will be added here)

## License

This project is licensed under the MIT License - see the LICENSE file for details.
