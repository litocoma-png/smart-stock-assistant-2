Smart Stock & Price Assistant — installable web app

Android app (standalone APK): The android-app folder contains a native Android wrapper that bundles the store app for offline launch from an Android home-screen icon. It does not require a browser to open. The included GitHub Actions workflow builds an APK and attaches it to a GitHub Release when you push a version tag (v1.0.0). See android-app/README.md for setup and download steps. No APK is built until these files are uploaded to your GitHub repository and the workflow runs.

Files:
- index.html is the GitHub Pages entry point.
- index1.html is the working app copy.
- manifest.webmanifest and service-worker.js enable installation and offline loading on supported browsers.
- icon-180.png, icon-192.png, icon-512.png, and smart-stock-logo.png are the app/logo images.

Install on Android:
1. Open the HTTPS GitHub Pages address in Chrome.
2. Tap Install app when it appears, or open Chrome’s menu and choose Install app / Add to Home screen.

Install on iPhone or iPad:
1. Open the HTTPS address in Safari.
2. Tap Share, then Add to Home Screen.

Install on desktop:
Open the HTTPS address in a current Chrome or Edge browser and use its Install control when offered.

The app must be hosted over HTTPS for installation and offline caching. Opening the HTML file directly is suitable for local use but does not enable service-worker installation. This is an installable web app (PWA), not a native Android APK; the browser installs it on compatible devices.

GitHub Pages:
The included .github/workflows/deploy-pages.yml publishes the root files whenever changes are pushed to the main branch. In the repository’s Settings > Pages, select GitHub Actions as the source if Pages is not already enabled. After the first deployment, use the Pages URL shown by GitHub.

App notes:
- Data remains in local browser storage on each device; it does not sync automatically between devices.
- Export JSON backups regularly and keep a copy outside the device.
- Credit balances are local records and do not replace formal accounting controls.
- The bundled logo file currently retains its supplied light background.
