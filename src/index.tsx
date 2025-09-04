import { NitroModules } from 'react-native-nitro-modules';
import type { Decibel } from './Decibel.nitro';

const DecibelHybridObject = NitroModules.createHybridObject<Decibel>('Decibel');

export async function requestPermission(): Promise<string> {
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

export function removeDecibelUpdateListener(
  listener: (decibel: number) => void
): void {
  DecibelHybridObject.removeDecibelUpdateListener(listener);
}
