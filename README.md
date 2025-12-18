# react-native-decibel

🎤 A high-performance React Native library for real-time audio level monitoring and decibel measurement. Built with [Nitro Modules](https://nitro.margelo.com/) for native performance.

## Features

✨ **Real-time Audio Monitoring** - Continuously measure audio levels in decibels (dB)  
⚡️ **High Performance** - Built with Nitro Modules for optimal native performance  
🎯 **Configurable Intervals** - Customize measurement frequency to suit your needs  
📱 **Cross-platform** - Full support for iOS and Android  
🔒 **Permission Handling** - Built-in microphone permission management  
🪝 **Event-based API** - Simple listener pattern for decibel updates

## Installation

Install the package and its peer dependency:

```sh
npm install react-native-decibel react-native-nitro-modules
```

or with yarn:

```sh
yarn add react-native-decibel react-native-nitro-modules
```

> **Note:** `react-native-nitro-modules` is required as this library is built on [Nitro Modules](https://nitro.margelo.com/).

### iOS Setup

For iOS, install the CocoaPods dependencies:

```sh
cd ios && pod install
```

Add microphone usage description to your `Info.plist`:

```xml
<key>NSMicrophoneUsageDescription</key>
<string>This app requires microphone access to measure audio levels</string>
```

### Android Setup

Add microphone permission to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

For Android 6.0+, the library handles runtime permission requests automatically.

## Usage

### Basic Example

```tsx
import { useEffect, useState } from 'react';
import {
  requestPermission,
  start,
  stop,
  onDecibelUpdate,
  removeDecibelUpdateListener,
} from 'react-native-decibel';

export default function App() {
  const [decibel, setDecibel] = useState<number>(0);
  const [isRecording, setIsRecording] = useState(false);

  useEffect(() => {
    // Request microphone permission
    const setupAudio = async () => {
      const permission = await requestPermission();
      console.log('Permission status:', permission);
    };

    setupAudio();

    // Setup decibel listener
    const listener = (dB: number) => {
      setDecibel(dB);
    };

    onDecibelUpdate(listener);

    // Cleanup
    return () => {
      stop();
      removeDecibelUpdateListener(listener);
    };
  }, []);

  const handleStart = () => {
    start(0.5); // Update every 500ms
    setIsRecording(true);
  };

  const handleStop = () => {
    stop();
    setIsRecording(false);
  };

  return (
    <View>
      <Text>Current Decibel Level: {decibel.toFixed(2)} dB</Text>
      <Button
        title={isRecording ? 'Stop Recording' : 'Start Recording'}
        onPress={isRecording ? handleStop : handleStart}
      />
    </View>
  );
}
```

### Advanced Example with Visual Feedback

```tsx
import { useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import {
  requestPermission,
  start,
  stop,
  onDecibelUpdate,
} from 'react-native-decibel';

export default function DecibelMeter() {
  const [decibel, setDecibel] = useState<number>(-160);
  const [isActive, setIsActive] = useState(false);

  useEffect(() => {
    requestPermission();

    onDecibelUpdate((dB) => {
      setDecibel(dB);
    });

    return () => stop();
  }, []);

  const toggleRecording = () => {
    if (isActive) {
      stop();
      setIsActive(false);
    } else {
      start(0.2); // Update every 200ms for smoother visualization
      setIsActive(true);
    }
  };

  // Normalize dB value (-160 to 0) to percentage (0 to 100)
  const normalizedLevel = Math.max(0, Math.min(100, (decibel + 160) / 1.6));

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Decibel Meter</Text>

      <View style={styles.meterContainer}>
        <View style={[styles.meterBar, { width: `${normalizedLevel}%` }]} />
      </View>

      <Text style={styles.decibelText}>
        {isActive ? `${decibel.toFixed(1)} dB` : '--'}
      </Text>

      <TouchableOpacity
        style={[styles.button, isActive && styles.buttonActive]}
        onPress={toggleRecording}
      >
        <Text style={styles.buttonText}>{isActive ? 'Stop' : 'Start'}</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 40,
  },
  meterContainer: {
    width: '100%',
    height: 40,
    backgroundColor: '#e0e0e0',
    borderRadius: 20,
    overflow: 'hidden',
  },
  meterBar: {
    height: '100%',
    backgroundColor: '#4CAF50',
  },
  decibelText: {
    fontSize: 48,
    fontWeight: 'bold',
    marginVertical: 30,
  },
  button: {
    paddingHorizontal: 40,
    paddingVertical: 15,
    backgroundColor: '#2196F3',
    borderRadius: 25,
  },
  buttonActive: {
    backgroundColor: '#f44336',
  },
  buttonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
});
```

## API Reference

### `requestPermission()`

Request microphone permission from the user.

```typescript
function requestPermission(): Promise<string>;
```

**Returns:** A promise that resolves to a permission status string:

- iOS: `"granted"` | `"not_granted"` | `"undetermined"`
- Android: `"granted"` | `"denied"` | `"never_ask_again"`

**Example:**

```typescript
const status = await requestPermission();
if (status === 'granted') {
  // Permission granted, start recording
}
```

---

### `start(interval?)`

Start measuring audio levels.

```typescript
function start(interval?: number): void;
```

**Parameters:**

- `interval` (optional): Update interval in seconds. Default: `0.2` (200ms)

**Example:**

```typescript
start(0.5); // Update every 500ms
start(1.0); // Update every second
start(); // Use default 200ms interval
```

---

### `stop()`

Stop measuring audio levels.

```typescript
function stop(): void;
```

**Example:**

```typescript
stop();
```

---

### `onDecibelUpdate(listener)`

Register a listener for decibel updates.

```typescript
function onDecibelUpdate(listener: (decibel: number) => void): void;
```

**Parameters:**

- `listener`: Callback function that receives the current decibel level

**Decibel Range:** `-160` to `0` dB

- `-160 dB`: Silence / minimum detectable level
- `-50 to -30 dB`: Quiet environment
- `-30 to -10 dB`: Normal conversation
- `-10 to 0 dB`: Loud environment

**Example:**

```typescript
onDecibelUpdate((dB) => {
  console.log(`Current level: ${dB} dB`);

  if (dB > -30) {
    console.log('Loud!');
  }
});
```

---

### `removeDecibelUpdateListener(listener)`

Remove a previously registered listener.

```typescript
function removeDecibelUpdateListener(listener: (decibel: number) => void): void;
```

**Parameters:**

- `listener`: The callback function to remove

**Example:**

```typescript
const listener = (dB: number) => {
  console.log(dB);
};

onDecibelUpdate(listener);

// Later...
removeDecibelUpdateListener(listener);
```

## Understanding Decibel Values

The library returns values in the range of **-160 dB to 0 dB**:

| dB Range    | Description                          |
| ----------- | ------------------------------------ |
| -160 to -50 | Silence or very quiet                |
| -50 to -30  | Quiet environment (library, bedroom) |
| -30 to -20  | Normal conversation                  |
| -20 to -10  | Busy restaurant, office              |
| -10 to 0    | Loud music, shouting                 |

> **Note:** These are relative values from the device microphone, not absolute SPL (Sound Pressure Level) measurements.

## Platform-Specific Notes

### iOS

- Uses `AVAudioRecorder` for audio level monitoring
- Audio session is configured with `.playAndRecord` category
- Supports background audio with proper configuration

### Android

- Uses `AudioRecord` with `MediaRecorder.AudioSource.MIC`
- Sample rate: 44100 Hz
- Calculates RMS (Root Mean Square) from audio buffer

## Performance Considerations

- **Update Interval:** Lower intervals (e.g., 0.1s) provide smoother updates but use more CPU
- **Recommended Range:** 0.2s - 1.0s for most use cases
- **Battery Impact:** Continuous audio monitoring will impact battery life

## Troubleshooting

### Permission Denied

Make sure you've added the required permissions to your platform-specific configuration files and called `requestPermission()` before starting.

### No Updates Received

Ensure you call `start()` after registering your listener with `onDecibelUpdate()`.

### Build Errors on iOS

Run `cd ios && pod install` to ensure CocoaPods dependencies are installed.

### Build Errors on Android

Make sure you have the latest version of `react-native-nitro-modules` installed.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT © Patrick Abrahamsson

---

Made with ❤️ using [create-react-native-library](https://github.com/callstack/react-native-builder-bob) and [Nitro Modules](https://nitro.margelo.com/)
