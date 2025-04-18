# SatFinder

**SatFinder** is a mobile application designed to provide real-time satellite tracking and information. It allows users to view satellite positions, upcoming ISS passes, and track their own custom list of satellites. Built with a sleek and minimalist interface, SatFinder is perfect for satellite enthusiasts, astronomers, or anyone interested in tracking objects in the sky. This app leverages the N2YO API for satellite data, providing users with the latest satellite positions and pass predictions.

## Why I'm Making This

As a **Computer Science student** in my final year, I wanted to create a project that would be both challenging and practical, showcasing my skills while being something that excites me personally. This app is a **school project** for my final year, and it aims to combine my interests in space technology and software development.

I’ve been inspired by the **ISS Detector** app, which already exists as a great tool for tracking the ISS. SatFinder is, in a sense, a **love letter to ISS Detector**, trying to capture the same essence of satellite tracking but with additional features and a focus on performance and user experience. While ISS Detector focuses mainly on the ISS, I wanted to extend the functionality to include tracking of a variety of satellites and improving the overall experience with smarter caching and better data handling.

## Features

- **Real-time satellite tracking**: See the exact position of satellites in the sky at any given time.
- **ISS pass details**: Get detailed predictions for when the ISS will be visible from your location.
- **Satellite list management**: Add and track specific satellites by their IDs and save them for future reference.
- **Minimalist design**: A simple, modern UI focused on providing clear, accessible information without unnecessary clutter.
- **Notifications and alarms**: Get notified when a tracked satellite is overhead.

---

# Latest Major Update: **"Dust-Off" (v1.6)**

### What's New?

The **v1.6** update is all about tightening up the codebase, cleaning behind the scenes, and preparing the foundation for future features. While it might not look flashy on the surface, this version brings major under-the-hood improvements that developers (and power users) will appreciate:

- **Separation of concerns**: Refactored the codebase to split responsibilities between services, utilities, UI logic, and data layers—making everything cleaner, more modular, and easier to maintain.
- **Utility functions**: Introduced new helper methods and reusable utilities to reduce code duplication and increase clarity throughout the app.
- **Centralized string resources**: All static texts are now pulled from string resource files, ensuring better localization support and consistency across screens.
- **Bug fixing**: Addressed lingering silent failure states and logic bugs since the last release cycle.
- **Layout and logic cleanup**: Simplified fragment navigation logic, standardized API handling, and removed deprecated or redundant code.

### Why This Update Matters

"Dust-Off" might not come with shiny new features, but it's a crucial part of keeping SatFinder stable and future-ready. By organizing and optimizing the internals, we’re paving the way for faster development, easier bug fixes, and cleaner feature rollouts down the line. Less spaghetti, more clarity.

---

## 📦 Changelog
See [CHANGELOG.md](./CHANGELOG.md) for a full list of updates.
