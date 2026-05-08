# 🇩🇪 Feiertage DE — German Holidays Android App

A **premium, fully offline** Android app for tracking all German public holidays across all 16 Bundesländer. Built with Java, Material Design 3, and zero network dependencies.

---

## ✨ Features

| Feature | Details |
|---|---|
| **All 16 Bundesländer** | Complete state-by-state holiday data |
| **Years 2025–2027** | Pre-loaded JSON databases bundled in assets |
| **Offline-first** | Zero internet required — ever |
| **Next Holiday card** | Hero card with countdown to your next holiday |
| **Bridge Day Detector** | Analyses Brückentag opportunities automatically |
| **Favorites** | Mark holidays with a heart, persisted via SharedPreferences |
| **Calendar View** | Month grid with red/gold holiday dot indicators |
| **Search** | Real-time name search across all holidays |
| **State Filter** | Filter by any of the 16 states or view all |
| **Year Selector** | Switch between 2025, 2026, 2027 |
| **Dark Mode** | Full Material You dark theme |
| **Notifications** | AlarmManager reminders 1–7 days before any holiday |
| **Boot Resilience** | Notifications rescheduled automatically after reboot |
| **Smooth Animations** | Fade transitions, card ripples, slider interactions |

---

## 📁 Project Structure

```
app/src/main/
├── java/com/feiertage/deutschland/
│   ├── activities/
│   │   ├── SplashActivity.java          ← Entry point, SplashScreen API
│   │   ├── MainActivity.java            ← Bottom-nav host (4 tabs)
│   │   ├── HolidayDetailActivity.java   ← Full holiday detail
│   │   ├── StateSelectionActivity.java  ← State picker
│   │   ├── SettingsActivity.java        ← Standalone settings
│   │   └── ThemeHelper.java             ← Dark/light mode utility
│   ├── adapters/
│   │   ├── HolidayAdapter.java          ← RecyclerView holiday list
│   │   ├── StateAdapter.java            ← State picker list
│   │   └── CalendarDayAdapter.java      ← 7-column calendar grid
│   ├── fragments/
│   │   ├── HomeFragment.java            ← Main home screen
│   │   ├── CalendarFragment.java        ← Monthly calendar
│   │   ├── StatesFragment.java          ← States + preview
│   │   └── SettingsFragment.java        ← Settings tab
│   ├── models/
│   │   ├── Holiday.java                 ← Holiday data model
│   │   ├── GermanState.java             ← State model
│   │   ├── HolidayDatabase.java         ← JSON root wrapper
│   │   └── LongWeekend.java             ← Bridge day opportunity
│   ├── receivers/
│   │   ├── NotificationReceiver.java    ← AlarmManager receiver
│   │   └── BootReceiver.java            ← Post-reboot reschedule
│   └── utils/
│       ├── HolidayRepository.java       ← Data loading + filtering
│       ├── DateUtils.java               ← German date formatting
│       ├── StateManager.java            ← 16-state registry
│       ├── PreferenceManager.java       ← SharedPreferences wrapper
│       ├── NotificationHelper.java      ← Alarm scheduling
│       └── LongWeekendDetector.java     ← Brückentag analysis
├── res/
│   ├── layout/                          ← All XML layouts
│   ├── drawable/                        ← Shapes, dots, vector icons
│   ├── values/                          ← Colors, strings, themes, styles
│   ├── values-night/                    ← Dark mode theme overrides
│   ├── color/                           ← State-list selectors
│   ├── menu/                            ← Bottom nav menu
│   ├── font/                            ← Hanken Grotesk font files
│   └── xml/                             ← Backup rules, file paths
└── assets/holidays/
    ├── holidays_2025.json               ← 19 holidays, all states
    ├── holidays_2026.json               ← 19 holidays, all states
    └── holidays_2027.json               ← 19 holidays, all states
```

---

## 🚀 Setup Instructions

### 1. Open in Android Studio
```
File → Open → Select this folder (Feiertage DE)
```

### 2. Add Hanken Grotesk Fonts
Download from [Google Fonts](https://fonts.google.com/specimen/Hanken+Grotesk) and place in:
```
app/src/main/res/font/
  hanken_grotesk.ttf          ← Regular (400)
  hanken_grotesk_semibold.ttf ← SemiBold (600)
  hanken_grotesk_bold.ttf     ← Bold (700)
```

> **Quick alternative:** Rename any standard font (e.g. Roboto) temporarily to get the project building, then replace with the correct files.

### 3. Sync Gradle
```
File → Sync Project with Gradle Files
```

### 4. Run
Connect a device or start an emulator (API 24+), then click **Run ▶**

---

## 🏗️ Architecture

```
┌─────────────────────────────────┐
│         UI Layer                │
│  Activities / Fragments         │
│  RecyclerView Adapters          │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│         Data Layer              │
│  HolidayRepository (Singleton)  │
│  ┌─────────────────────────┐    │
│  │  assets/holidays/*.json │    │
│  │  Gson deserialization   │    │
│  │  In-memory year cache   │    │
│  └─────────────────────────┘    │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│       Persistence Layer         │
│  PreferenceManager              │
│  (state, year, dark mode,       │
│   favorites, notif settings)    │
└─────────────────────────────────┘
```

---

## 🎨 Design System

| Token | Value |
|---|---|
| **Primary** | `#A8000A` Signal Red |
| **Tertiary** | `#FFCE00` Gold Accent |
| **Surface** | `#F9F9F9` Cool Light Gray |
| **On-Surface** | `#1A1C1C` Rich Black |
| **Font** | Hanken Grotesk (400 / 600 / 700) |
| **Card Radius** | 24dp |
| **Button Radius** | 16dp |
| **Badge** | Fully pill-shaped |
| **Grid** | 8dp baseline |

---

## 📱 Screens

| Screen | Description |
|---|---|
| **Splash** | German flag stripes, animated logo, 1.8s delay |
| **Home** | Hero next-holiday card, bridge-day tile, holiday list |
| **Calendar** | Month grid, holiday dots, BottomSheet day detail |
| **States** | All 16 Bundesländer list + inline holiday preview |
| **Settings** | Dark mode, notification toggle, advance-day slider |
| **Detail** | Full holiday info, state chips, favorite FAB |
| **State Picker** | Searchable state list with All-states option |

---

## 🔔 Notifications

- Uses `AlarmManager.setExactAndAllowWhileIdle()` on Android 12+
- Falls back to `setAndAllowWhileIdle()` on older versions
- Fire at **9:00 AM** on the configured number of days before each holiday
- Automatically rescheduled on device reboot via `BootReceiver`
- Configurable lead time: **1–7 days** via the Settings slider

---

## 📊 Holiday Database

Each JSON file follows this schema:
```json
{
  "version": "1.0",
  "lastUpdated": "YYYY-MM-DD",
  "holidays": [
    {
      "id": "YYYY_holiday_id",
      "name": "German Name",
      "nameEn": "English Name",
      "date": "YYYY-MM-DD",
      "isNational": true,
      "states": ["BW", "BY", "..."],
      "description": "Holiday description in German.",
      "type": "national | state | regional",
      "year": 2026
    }
  ]
}
```

### State Codes
| Code | State | Capital |
|---|---|---|
| BW | Baden-Württemberg | Stuttgart |
| BY | Bayern | München |
| BE | Berlin | Berlin |
| BB | Brandenburg | Potsdam |
| HB | Bremen | Bremen |
| HH | Hamburg | Hamburg |
| HE | Hessen | Wiesbaden |
| MV | Mecklenburg-Vorpommern | Schwerin |
| NI | Niedersachsen | Hannover |
| NW | Nordrhein-Westfalen | Düsseldorf |
| RP | Rheinland-Pfalz | Mainz |
| SL | Saarland | Saarbrücken |
| SN | Sachsen | Dresden |
| ST | Sachsen-Anhalt | Magdeburg |
| SH | Schleswig-Holstein | Kiel |
| TH | Thüringen | Erfurt |

---

## ⚙️ Tech Stack

| Component | Library / API |
|---|---|
| Language | Java |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |
| UI | Material Design 3 |
| JSON | Gson 2.10.1 |
| Notifications | AlarmManager |
| Preferences | SharedPreferences |
| Navigation | Fragment transactions |
| Build | Gradle 8.6 |

---

## 🔧 Extending Holiday Data

To add more years, create `assets/holidays/holidays_YYYY.json` following the schema above. The `HolidayRepository` auto-discovers files by year number — no code changes needed.

---

*Developed with ❤️ for Germany — Feiertage DE v1.0*
