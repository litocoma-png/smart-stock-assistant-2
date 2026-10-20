# Smart Stock Assistant for Android

This project packages the store app and the supplied Kareth logo inside an Android APK. It opens from a launcher icon using the Kareth logo and works offline; users do not need to launch a browser. Store records remain in that app's private local storage on each device.

## Build and download from GitHub

1. Upload the contents of the `outputs` folder to the root of a GitHub repository, including the `.github` folder.
2. Open **Actions** in the repository and run **Build Smart Stock Android APK** (or push to `main`).
3. Open the completed workflow run and download the `smart-stock-android-apk` artifact.
4. Transfer `app-debug.apk` to an Android phone, open it, and approve Android's install prompt. The user may need to allow installation from that download source.

To publish an APK as a GitHub Release, push a version tag such as `v1.0.0`. The same workflow builds and attaches the APK to the release.

## Build locally

Open this `android-app` folder in Android Studio, or use JDK 17, Android SDK Platform 36, and Gradle 8.13 to run `gradle assembleDebug`. The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

The build uses Android Gradle Plugin 8.13.2, AndroidX WebKit's `WebViewAssetLoader`, and AndroidX Core's `FileProvider`. Minimum supported Android API is 23. Camera permission is requested only when the barcode scanner is used. Product photo selection/capture is handled by Android's camera and file picker.

## App data

The app keeps data locally on each phone. It does not sync automatically with the web app or other devices. Use its backup and restore features to move data. An update retains local data only when signed with the same signing key as the installed version. GitHub Actions' debug APK is a quick install for trying the app; keep an app backup before replacing or reinstalling it. Production updates need one persistent private signing key.

The included APK build is a debug-signed sideload package, not a Play Store release. For store distribution, register/sign it under the developer identity required by Android.
