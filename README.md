# GitHub User Explorer

## Overview
GitHub user exploration application which supports:
- Search GitHub users by username
- View user details (avatar, username, full name, followers, following)
- Browse user repositories (non-forked)
- View repository details (name, language, stars, description)
- Open repository in web browser

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
- Coroutine, Flow, Dagger Hilt, Retrofit, Paging, Splash API
- GitHub REST API
- Coil for image loading
- Mockk, Turbine, JUnit for testing

## Demo
[![YouTube Video](https://img.youtube.com/vi/WCRDt1d4MRk/maxresdefault.jpg)](https://youtu.be/WCRDt1d4MRk)

## Setup and Running Instructions

### Prerequisites
- **Java**: Ensure you have Java 17 installed.
- **Android Studio (Meerkat or above) **: Download and install [Android Studio](https://developer.android.com/studio).

### Command
#### To clean and build: `./gradlew clean build`
#### To run the test: `./gradlew test`

## GitHub API Configuration

### Rate Limit
GitHub API has a rate limit of 60 requests per hour for unauthenticated requests. To increase this limit, the application uses a Personal Access Token.

### Setting up Personal Access Token
1. Create a GitHub Personal Access Token from your GitHub account settings
2. Add the token to your `local.properties` file:
   ```
   GITHUB_TOKEN=YOUR_PERSONAL_ACCESS_TOKEN
   ```

## Features

### User Search Screen
- Search bar fixed at the top of the screen
- List of users matching the search query
- Each item displays user avatar and username
- Tap on a user to navigate to their repository screen

### User Repository Screen
- User details section showing avatar, username, full name, followers and following count
- List of non-forked repositories
- Each repository item shows name, language, star count, and description
- Tap on a repository to open its webpage in a browser
