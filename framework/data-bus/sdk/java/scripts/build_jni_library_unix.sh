# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: databus core source build all script

# Description: Build the JNI bridged library for DataBus Java SDK on Unix platform.

g++ -c -fPIC -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/linux \
src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.cpp -o \
src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.o && g++ -shared -fPIC -o \
/usr/lib/libnative.so src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.o -lc