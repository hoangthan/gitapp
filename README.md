# Note App

## Overview
Note taking application which support user: 
- Create New Note
- Delete Note
- Update Note
- View Note
- Mark Note As Completed.

## Project architecture
By applying the idea of Clean Architecture, the project is separate into 3 main parts:

- **Data**: The layer will be responsible for data storage and retrieval.
- **Domain**: The layer will be responsible for business logic.
- **Features**: The layer will be responsible for user interface.

From real-world application, 3 layers be modularized to:
- `libraries` module: which would be combined from `Domain` and `Data` layer. It will provide usecase to take input and return output.
- `features` module: which will use `libraries` to provide the UI as the SDK.

Pros of this approach:
1. Highly reusable
2. Easy to test
3. Easy to maintain
4. Speed up the build process since the modules can be built parallelly.
5. Easy to isolate as separate SDK.

## Technologies
- Kotlin, Jetpack Compose
- Coroutine, Flow, Dagger Hilt, Room Database, Paging, Splash API
- Mockk, Turbine, JUnit.

## Setup and Running Instructions

### Prerequisites
- **Java**: Ensure you have Java 17 installed.
- **Android Studio (Meerkat or above) **: Download and install [Android Studio](https://developer.android.com/studio).

### Command
#### To clean and buid: `./gradlew clean build`
#### To run the test: `./gradlew test`
