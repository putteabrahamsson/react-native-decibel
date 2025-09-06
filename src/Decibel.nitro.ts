import type { HybridObject } from 'react-native-nitro-modules';

export interface Decibel
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  requestPermission(): Promise<string>;
  start(interval?: number): void;
  stop(): void;
  playBackgroundSound(filePath: string): void;
  stopBackgroundSound(): void;

  onDecibelUpdate(listener: (decibel: number) => void): void;
  removeDecibelUpdateListener(listener: (decibel: number) => void): void;
}
