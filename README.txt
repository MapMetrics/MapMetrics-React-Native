# MapLibre React Native Project Documentation

## Overview
This project is a React Native implementation using MapLibre for map visualization. It provides a cross-platform solution for displaying maps on both iOS and Android devices, with support for custom map styles and tile sources.

## Project Structure
```
maplibre-react-native/
├── android/                 # Android-specific native code
├── examples/               # Example applications
│   ├── react-native-app/   # Main React Native example app
│   └── shared/            # Shared components and utilities
├── ios/                    # iOS-specific native code
└── src/                    # Core library source code
```

## Prerequisites
- Node.js (Latest LTS version recommended)
- React Native CLI
- Android Studio (for Android development)
- Xcode (for iOS development, macOS only)
- CocoaPods (for iOS dependencies)
- JDK 11 or later

## Installation

### 1. Clone the Repository
```bash
git clone [repository-url]
cd maplibre-react-native
```

### 2. Install Dependencies
```bash
# Install root dependencies
yarn install

# Install example app dependencies
cd examples/react-native-app
yarn install
```

### 3. iOS Setup (macOS only)
```bash
cd ios
pod install
cd ..
```

## Running the Application

### Android
```bash
# Start Metro bundler
cd examples/react-native-app
yarn start

# In a new terminal, run the Android app
yarn android
```

### iOS (macOS only)
```bash
# Start Metro bundler
cd examples/react-native-app
yarn start

# In a new terminal, run the iOS app
yarn ios
```

## Map Configuration

### Default Style URL
The project uses a default MapMetrics style URL:
```
https://gateway.mapmetrics.org/styles/?fileName=91cf50f5-e3cb-45d3-a1ab-f2f575f6c9b2/urbcalm.json&token=[YOUR_TOKEN]
```

### Using Custom Styles
To use a custom style, pass the style URL to the MapView component:
```javascript
import { MapView } from "@maplibre/maplibre-react-native";

export function ShowMap() {
  return (
    <MapView 
      style={sheet.matchParent} 
      mapStyle="YOUR_STYLE_URL"
    />
  );
}
```

## Event Handling
The MapView component supports various events for debugging and monitoring:

```javascript
<MapView 
  onWillStartLoadingMap={() => console.log('Will start loading map')}
  onDidFinishLoadingMap={() => console.log('Did finish loading map')}
  onDidFailLoadingMap={() => console.log('Did fail loading map')}
  onDidFinishLoadingStyle={() => console.log('Did finish loading style')}
/>
```

## Troubleshooting

### Common Issues

1. Port 8081 Already in Use
   - Error: `listen EADDRINUSE: address already in use :::8081`
   - Solution: Kill the existing Metro process or use a different port

2. Map Loading Issues
   - Check network connectivity
   - Verify style URL accessibility
   - Ensure proper token configuration
   - Check console logs for specific error messages

3. iOS Build Issues
   - Ensure CocoaPods is properly installed
   - Run `pod install` in the ios directory
   - Clean build folder in Xcode

4. Android Build Issues
   - Ensure Android SDK is properly configured
   - Check local.properties file
   - Clean project in Android Studio

## Debugging

### Android Logs
```bash
adb logcat -v time -s MapLibre:D Mbgl-HttpRequest:D
```

### iOS Logs
Use Xcode console or:
```bash
xcrun simctl spawn booted log stream --predicate 'subsystem contains "MapLibre"'
```

## Network Configuration
The project uses OkHttp for Android and URLSession for iOS for network requests. Ensure proper network permissions are set in both platforms:

### Android
Add to AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### iOS
Add to Info.plist:
```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
</dict>
```

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
[Specify your license here]

## Support
For issues and feature requests, please use the project's issue tracker. 