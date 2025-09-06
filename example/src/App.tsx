import { useEffect } from 'react';
import { Text, View, StyleSheet, TouchableOpacity } from 'react-native';
import {
  requestPermission,
  start,
  stop,
  onDecibelUpdate,
  playBackgroundSound,
  stopBackgroundSound,
} from 'react-native-decibel';

export default function App() {
  const req = async () => {
    const spec = await requestPermission();
    console.log(spec, 'spec');
  };

  useEffect(() => {
    onDecibelUpdate((decibel) => {
      console.log(decibel, 'deccy');
    });
  }, []);

  return (
    <View style={styles.container}>
      <TouchableOpacity onPress={req} style={styles.btn}>
        <Text>Request permission</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => start(0.1)} style={styles.btn}>
        <Text>Start</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={stop} style={styles.btn}>
        <Text>Stop</Text>
      </TouchableOpacity>

      <TouchableOpacity
        onPress={() => playBackgroundSound(require('./white-noise.mp3'))}
        style={styles.btn}
      >
        <Text>Play Background Sound</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={stopBackgroundSound} style={styles.btn}>
        <Text>Stop Background Sound</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'white',
  },
  btn: {
    padding: 16,
    borderRadius: 16,
    backgroundColor: 'lightblue',
    marginTop: 16,
  },
});
