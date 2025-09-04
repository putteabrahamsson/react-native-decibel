#include <jni.h>
#include "decibelOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::decibel::initialize(vm);
}
