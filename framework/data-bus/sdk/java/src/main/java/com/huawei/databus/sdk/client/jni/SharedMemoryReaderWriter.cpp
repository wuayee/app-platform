/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#include <sys/shm.h>
#include <cstring>
#include "com_huawei_databus_sdk_client_jni_SharedMemoryReaderWriter.h"

JNIEXPORT jbyteArray JNICALL Java_com_huawei_databus_sdk_client_jni_SharedMemoryReaderWriter_read(JNIEnv *env,
    jobject obj, jint sharedMemoryId, jlong readOffset, jlong readLength)
{
    char* sharedMemoryBuffer = static_cast<char*>(shmat((int) sharedMemoryId, NULL, 0));
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
    char* sharedMemoryBuffer = static_cast<char*>(shmat((int) sharedMemoryId, NULL, 0));
    if (sharedMemoryBuffer == (void*)-1) {
        env -> ThrowNew(env -> FindClass("java/io/IOException"), "Failed to attach the shared memory");
    }
    jbyte* body = env -> GetByteArrayElements(bytes, 0);
    unsigned char* nativeChar = new unsigned char[writeLength];
    for (jint i = 0, j = writeOffset; i < writeLength; i++, j++) {
        nativeChar[i] = (unsigned char)body[j];
    }
    env -> ReleaseByteArrayElements(bytes, body, 0);
    strcpy(sharedMemoryBuffer, (const char*)(char*)nativeChar);
    if (shmdt(sharedMemoryBuffer) == -1) {
        env -> ThrowNew(env -> FindClass("java/io/IOException"), "Failed to detach the shared memory");
    }
    return writeLength;
}
