#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail
cd "$(dirname "$0")/.."

# Reuse the working Gradle Wrapper from the user's existing VOT project when present.
VOT_WRAPPER="$HOME/VOT-FishAudio-Fix/VOT-Player-Iraqi-Expanded-v5"
if [[ ! -x ./gradlew && -x "$VOT_WRAPPER/gradlew" ]]; then
  cp "$VOT_WRAPPER/gradlew" ./gradlew
  mkdir -p gradle/wrapper
  cp "$VOT_WRAPPER/gradle/wrapper/gradle-wrapper.jar" gradle/wrapper/
  cp "$VOT_WRAPPER/gradle/wrapper/gradle-wrapper.properties" gradle/wrapper/
  chmod +x ./gradlew
fi

if [[ -x ./gradlew ]]; then
  ./gradlew --no-daemon assembleDebug
elif command -v gradle >/dev/null 2>&1; then
  gradle --no-daemon assembleDebug
else
  echo "Gradle غير موجود. ثبّت Gradle أو جهّز Gradle Wrapper من مشروعك الحالي." >&2
  exit 1
fi

APK="app/build/outputs/apk/debug/app-debug.apk"
if [[ ! -f "$APK" ]]; then
  echo "ما ظهر ملف APK بعد البناء." >&2
  exit 1
fi
ls -lh "$APK"
if [[ -d "$HOME/storage/shared/Download" ]]; then
  cp -f "$APK" "$HOME/storage/shared/Download/TubeDubIQ-1.0.0-debug.apk"
  echo "تم نسخ APK إلى Download/TubeDubIQ-1.0.0-debug.apk"
fi
