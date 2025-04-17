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

# Latest Major Update: **"Cache me if you can!" (v1.5)**

### What's New?

In the **v1.5** update, we focused on improving the app’s performance and reliability with a major overhaul of the storage and caching mechanisms. Here's what's been added and improved:

- **Enhanced caching system**: Satellite data and user settings are now stored and cached more efficiently, reducing the need for frequent network requests and ensuring a smoother experience.
- **User location storage**: The app now caches the user's location and retrieves it even when GPS is off, ensuring faster startup times and more reliable location handling.
- **Stale data handling**: Improved detection and management of stale data, ensuring that users always see the most relevant and up-to-date satellite information.
- **Optimized satellite data storage**: Satellite information is stored in a more compact format, reducing app data usage and ensuring quicker access to the necessary data.

### Why This Update Matters

With an ever-growing dataset and frequent API calls, managing storage and reducing unnecessary network usage is essential for providing a seamless user experience. This update addresses performance bottlenecks and ensures that users have a smooth, responsive app even when they’re not connected to the internet or have limited network access. These changes make the app faster, more reliable, and ready for future enhancements.
