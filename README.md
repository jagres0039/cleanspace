# CleanSpace 🧹

All-in-One Android storage & media cleaner. Fokus ke biang kerok memori penuh sebenarnya: **media (foto/video), media WhatsApp, duplikat, dan file besar** — bukan klaim bombastis "hapus cache semua app".

> Posisi produk: **Storage & Media Manager**, bukan "magic booster". Lebih jujur + lebih aman di review Google Play.

## Tech Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **MVVM + Clean Architecture** (data / domain / presentation)
- **Hilt** (DI), **Room** (cache), **WorkManager** (background scan)
- **Coroutines + Flow**

## Prasyarat

- Android Studio Ladybug (2024.2+)
- JDK 17
- minSdk 26 / targetSdk 35

## Cara jalanin

1. `git clone https://github.com/jagres0039/cleanspace.git`
2. Buka folder di **Android Studio**.
3. Karena Gradle wrapper binary (`gradle-wrapper.jar`) tidak di-commit, generate dulu:
   ```bash
   gradle wrapper --gradle-version 8.11.1
   ```
   atau cukup buka project di Android Studio — IDE akan otomatis men-setup wrapper.
4. **Sync** project, lalu **Run** ▶️.

## Catatan: GitHub Actions CI

File workflow `.github/workflows/android.yml` belum ke-commit karena keterbatasan izin push otomatis. Tambahin manual dengan isi berikut:

```yaml
name: Android CI
on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v4
      - name: Grant execute permission
        run: chmod +x gradlew
      - name: Build debug
        run: ./gradlew assembleDebug --stacktrace
      - name: Run unit tests
        run: ./gradlew testDebugUnitTest --stacktrace
      - name: Lint
        run: ./gradlew lintDebug --stacktrace
```

## Status

- [x] **Sprint 0** — Project setup (boilerplate) ✅
- [ ] **Sprint 1** — Storage Overview + Largest Files + Duplicate Finder
- [ ] **Sprint 2** — WhatsApp Media Cleaner + delete flow
- [ ] **Sprint 3** — Screenshot/downloads cleaner + polish UI
- [ ] **Sprint 4** — Internal testing + Play Console setup
- [ ] **Sprint 5** — AI photo cleaner (on-device ML)

## Lisensi

TBD
