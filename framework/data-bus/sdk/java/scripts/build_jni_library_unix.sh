# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: Build the JNI bridged library for DataBus Java SDK on Unix platform.

# 创建动态链接库目标目录
mkdir -p src/main/resources/jni

# 编译源文件并生成x86目标文件
g++ -c -fPIC -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/linux \
src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.cpp -o \
src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.o

# 链接目标文件并生成动态链接库
g++ -shared -fPIC -o \
src/main/resources/jni/libdatabus_x86.so src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.o -lc

# 编译源文件并生成aarch64目标文件
aarch64-linux-gnu-g++ -c -fPIC -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/linux \
src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.cpp -o \
src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.o

# 链接目标文件并生成动态链接库
aarch64-linux-gnu-g++ -shared -fPIC -o \
src/main/resources/jni/libdatabus_aarch64.so src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.o -lc
