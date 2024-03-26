/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#include <iostream>
#include <sys/shm.h>
#include <cstring>
#include "com_huawei_databus_sdk_client_jni_SharedMemoryReaderWriter.h"

using namespace std;

JNIEXPORT jbyteArray JNICALL Java_com_huawei_databus_sdk_client_jni_SharedMemoryReaderWriter_read(JNIEnv *env,
    jobject obj, jint sharedMemoryId, jlong readOffset, jlong readLength)
{
    void* sharedMemoryBuffer = shmat((int) sharedMemoryId, NULL, 0);
    if (sharedMemoryBuffer == (void*)-1) {
        env -> ThrowNew(env -> FindClass("java/io/IOException"), "Failed to attach the shared memory");
    }
    jbyteArray arr = env -> NewByteArray(readLength);
    env -> SetByteArrayRegion(arr, 0, readLength, reinterpret_cast<jbyte *>(sharedMemoryBuffer) + readOffset);
    if (shmdt(sharedMemoryBuffer) == -1) {
        env -> ThrowNew(env -> FindClass("java/io/IOException"), "Failed to detach the shared memory");
    }
    return arr;
}


JNIEXPORT jlong JNICALL Java_com_huawei_databus_sdk_client_jni_SharedMemoryReaderWriter_write(JNIEnv *env, jobject obj,
    jint sharedMemoryId, jlong writeOffset, jlong writeLength, jbyteArray bytes)
{
    void* sharedMemoryBuffer = shmat((int) sharedMemoryId, NULL, 0);
    if (sharedMemoryBuffer == (void*)-1) {
        env -> ThrowNew(env -> FindClass("java/io/IOException"), "Failed to attach the shared memory");
    }
    jbyte* body = env -> GetByteArrayElements(bytes, 0);
    auto startPtr = body + writeOffset;
    std::copy(startPtr, startPtr + writeLength, reinterpret_cast<jbyte *>(sharedMemoryBuffer));
    env -> ReleaseByteArrayElements(bytes, body, 0);
    if (shmdt(sharedMemoryBuffer) == -1) {
        env -> ThrowNew(env -> FindClass("java/io/IOException"), "Failed to detach the shared memory");
    }
    return writeLength;
}
