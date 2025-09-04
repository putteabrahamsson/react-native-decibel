import { NitroModules } from 'react-native-nitro-modules';
import type { Decibel } from './Decibel.nitro';

const DecibelHybridObject =
  NitroModules.createHybridObject<Decibel>('Decibel');

export function multiply(a: number, b: number): number {
  return DecibelHybridObject.multiply(a, b);
}
