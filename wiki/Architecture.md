# Module Receipts

# Architecture

Receipts follows modern Android development practices using a Clean Architecture approach with MVVM.

## 🏗️ Layers
- **Data Layer:** Room for local storage, DataStore for preferences.
- **Domain Layer:** Business logic and use cases.
- **UI Layer:** Jetpack Compose for a modern, reactive interface.

## 💉 Dependency Injection
We use **Hilt** (Dagger) for managing dependencies across the app.

## 🤖 AI Layer
Integration with **Google Gemini SDK** provides intelligent coaching in Littles Mode, offering advice on communication and relationship building.

## 🎥 Media Handling
- **Media3 (ExoPlayer):** For seamless video playback.
- **FFmpeg:** For complex video operations like the "Expose" compilation videos.
