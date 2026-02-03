# You Must Pray

Android app that locks the phone during Islamic prayer times in Jordan and requires a prayer mat photo to unlock.

## What it does
- Detects the user location (with fallback to Amman).
- Calculates prayer times using the Adhan library.
- Shows the next prayer and today’s schedule.
- Starts a prayer lock screen that can only be unlocked by capturing a prayer mat photo.

## Build the APK
Use Android Studio or Gradle:

```bash
./gradlew assembleDebug
```

The debug APK will be generated under:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Required permissions
- Location (to calculate prayer times for the user’s area)
- Camera (to capture the prayer mat photo)

## Notes
- Full device-lock enforcement requires device owner / kiosk setup on Android. The current lock activity demonstrates the flow without device admin enrollment.
