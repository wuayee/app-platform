/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

#include <iostream>
#include <sys/shm.h>
#include <cstring>
#include "modelengine_databus_sdk_client_jni_SharedMemoryReaderWriter.h"

using namespace std;

JNIEXPORT jbyteArray JNICALL Java_modelengine_databus_sdk_client_jni_SharedMemoryReaderWriter_read(JNIEnv *env,
    jobject obj, jint sharedMemoryId, jlong readOffset, jlong readLength)
{
    void* sharedMemoryBuffer = shmat((int) sharedMemoryId, NULL, SHM_RDONLY);
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


JNIEXPORT jlong JNICALL Java_modelengine_databus_sdk_client_jni_SharedMemoryReaderWriter_write(JNIEnv *env, jobject obj,
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
