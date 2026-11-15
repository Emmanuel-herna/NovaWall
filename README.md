# NovaWall

Android live wallpaper app with dark mode, categories, Google Sign-In, and saved wallpapers.

## Features
- Live / animated wallpaper service
- Dark Material 3 UI
- Google Sign-In (Credential Manager)
- Save wallpapers per signed-in account (local on device for now)

## Google Sign-In setup
1. Open [Google Cloud Console](https://console.cloud.google.com/)
2. Create a project (or use an existing one)
3. Configure OAuth consent screen
4. Create an **OAuth client ID** of type **Web application**
5. Put that client ID into `app/src/main/res/values/strings.xml` as `default_web_client_id`
6. Also create an Android OAuth client with package `com.novawall.app` and your debug/release SHA-1 if prompted

Until a real Web client ID is set, tapping **Sign in with Google** shows a configuration message instead of opening the account picker.

## Build
Push to `main` runs GitHub Actions and uploads a debug APK artifact.

Or open the project in Android Studio and run on a device.
