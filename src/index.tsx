import { NitroModules } from 'react-native-nitro-modules';
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';
import type { Decibel } from './Decibel.nitro';
import { PermissionsAndroid, Platform } from 'react-native';

const DecibelHybridObject = NitroModules.createHybridObject<Decibel>('Decibel');

export async function requestPermission(): Promise<string> {
  if (Platform.OS === 'android') {
    return await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.RECORD_AUDIO
    );
  }

  return await DecibelHybridObject.requestPermission();
}

export function start(interval?: number): void {
  return DecibelHybridObject.start(interval);
}

export function stop(): void {
  return DecibelHybridObject.stop();
}

export function onDecibelUpdate(listener: (decibel: number) => void): void {
  return DecibelHybridObject.onDecibelUpdate(listener);
}

export function playBackgroundSound(asset: number): void {
  const { uri } = resolveAssetSource(asset);
  return DecibelHybridObject.playBackgroundSound(uri);
}

export function stopBackgroundSound(): void {
  return DecibelHybridObject.stopBackgroundSound();
}

export function removeDecibelUpdateListener(
  listener: (decibel: number) => void
): void {
  DecibelHybridObject.removeDecibelUpdateListener(listener);
}
