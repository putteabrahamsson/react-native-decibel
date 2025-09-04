package com.margelo.nitro.decibel
  
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class Decibel : HybridDecibelSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }
}
