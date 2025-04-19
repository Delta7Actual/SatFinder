# 📦 SatFinder — Changelog

> All notable changes to the project will be documented in this file.  
> Follows semantic versioning: `MAJOR.MINOR.PATCH`  
> Current version: **1.5.7**

---

### [1.6.4] - 2025-04-18
- 🚀 Added new custom functionality to notifications
- 💥 Reworked how the alarm and notification system works
- 🎨 Code cleanup and documentation
- 🎨 Cleaned up some user actions

### [1.6.3] - 2025-04-18
- 🎨 Improved documentation and cleaned up the code
- 🔧 Improved and refactored the handling of alarms and notifications

### [1.6.2] - 2025-04-18
- 🔧 Improved and refactored fragments
- 🔧 Improved and refactored adapters
- 🎨 Minor UI changes

### [1.6.1] - 2025-04-18  
- 🎨 Added comments, documentations and guidelines
- 🐛 Patched minor bugs
- 🔧 Improved and refactored activities

### [1.6.0] - 2025-04-18 - "The Dust-Off Update"
- 🔧 Separated UI, service, and data logic for better maintainability  
- 🔧 Standardized navigation and layout structure
- 🚀 Added utility methods to streamline repetitive code  
- 💥 Centralized all string literals into string resources  
- 💥 Removed deprecated code and unused layout elements  
- 🐛 Fixed multiple small UI and silent failure states  

---

### [1.5.7] - 2025-04-18
- 🐛 Fixed bug in user deletion process
- 🎨 Improved UI and UX
- 🎨 Unified app design

### [1.5.6] - 2025-04-17  
- 🐛 Minor layout bug fixes  
- 🔧 Improved responsiveness of bottom navigation  
- 🔧 Final pre-release cleanup  

### [1.5.5] - 2025-04-16  
- 🚀 Added persistent caching and storage manager  
- 🔧 Finalized logic for saving passes and favorites  
- 🎨 UI polish: smooth fragment transitions and loading indicators  

### [1.5.4] - 2025-04-10  
- 🐛 Fixed compass orientation issue on certain devices
- 🐛 Fixed issue where invalid cache would be read  
- 🔧 Refined compass needle animation timing

### [1.5.3] - 2025-04-08  
- 🚀 Visual compass direction logic implemented  
- 🔧 Improved azimuth accuracy  
- 🎨 Cardinal direction labels styled and positioned  

### [1.5.2] - 2025-03-23  
- 🚀 Offline support added for saved passes  
- 🧪 Added experimental offline TLE parsing logic  
- 🔧 RecyclerView performance improvements
- 🎨 Improved offline location tracking

### [1.5.1] - 2025-03-19  
- 🔧 Refactored API logic into service classes  
- 🔧 Introduced threading for background tasks  
- 🔧 Added local storage structure for TLE/pass caching  
- 🎨 Updated search results and detail layout  

### [1.5.0] - 2025-03-15 - "The Cache Me If You Can Update"
- 💥 **Satellite tracking optimization**: Introduced advanced tracking algorithms for real-time satellite position calculation using TLE data.
- 🚀 Caching mechanism for frequently used satellite data to improve load times and reduce server dependency.
- 🔧 Refined the app's data flow, optimizing how satellite information is fetched and stored.
- 🎨Reduced API calls through local caching and offloaded heavy computations for improved app performance.


---

### [1.4.3] - 2025-03-12  
- 🚀 User preferences persist in Settings  
- 🚀 Notification toggle implemented  
- 🔧 AlarmManager setup for pass alerts  

### [1.4.2] - 2025-03-10  
- 🐛 Fixed issue with saving favorites  
- 🎨 Corrected theme color bug in dark mode  

### [1.4.1] - 2025-03-09  
- 🚀 Favorites functionality for satellites  
- 🔧 Updated card layout on home screen  
- 🔧 Improved network error handling  

### [1.4.0] - 2025-03-06 - "The Right Direction Update"
- 🚀 Compass view UI added  
- 🔧 Stubbed needle orientation logic  
- 🎨 Refined icon set  

---

### [1.3.2] - 2025-03-03  
- 🚀 Local TLE parsing logic added  
- 🔧 Optimized API request frequency  
- 🔧 Improved splash-to-home transition speed  

### [1.3.1] - 2025-03-01  
- 🐛 Fixed crash for invalid satellite ID  
- 🔧 Improved DetailsFragment stability  

### [1.3.0] - 2025-02-28 "The Search Party Update"
- 🚀 Satellite search by ID  
- 🔧 Hidden fragment container for result display  
- 🔧 Layout bug fixes  

---

### [1.2.0] - 2025-02-27 - "The AP(I)-ocalypse Update"
- 🚀 Integrated N2YO API (positions, passes, TLE)  
- 🚀 Added satellite details fragment  
- 🎨 Styled satellite list with highlight colors  
- 🔧 Refactored layout and data separation  

---

### [1.1.0] - 2025-02-25 - "The Pass Me By Update"
- 🚀 ISS pass layout and live data setup  
- 🚀 Satellite list with scrollable view  
- 🔧 New icon assets and padding fixes  

---

### [1.0.0] - 2025-02-24 - "The 1.0 Update"
🎉 Initial public release  
- 🚀 Splash, login, sign-up, and home screens  
- 🚀 Firebase authentication integration  
- 🔧 Toolbar and bottom navigation fragments  
- 🔧 Base layout structure established  

---

### [0.4.0] - 2025-02-20 - "The (Fire)-Base Of Operations Update"
- 🚀 Complete Firebase integration  
- 🔧 Auth logic finalized with Firestore support  
- 🔧 Theme color system introduced  

### [0.3.0] - 2025-02-17 - "The U(I)-topia Update"
- 🚀 Basic UI setup: toolbar, bottom nav, fragments  
- 🔧 Stubbed API services for satellite and ISS data  
- 🎨 Early styling pass for main screens  

### [0.2.0] - 2025-02-12 - "The Setup Update"
- 🚀 Layout planning and mock fragment structure  
- 🔧 Login/signup fragment logic prototyped  
- 🔧 Firebase dependency setup  

### [0.1.0] - 2025-02-08 - "The Humble Beginnnings Update"
🧪 Project initialized  
- 🛠️ Basic folder structure  
- 🔧 Gradle setup and dependency stubs  
- 🎨 Theme baseline created  
