# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: Build the JNI bridged library for DataBus Java SDK on Unix platform.

# 创建动态链接库目标目录
mkdir -p src/main/resources/jni

if [ "${PLATFORM}" = "arm_64" ]; then
    databus_compiler="aarch64-linux-gnu-g++"
else
    databus_compiler="g++"
fi

# 编译源文件并生成目标文件
${databus_compiler} -c -fPIC -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/linux \
src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.cpp -o \
src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.o

# 链接目标文件并生成动态链接库
${databus_compiler} -shared -fPIC -o \
src/main/resources/jni/libdatabus.so src/main/java/com/huawei/databus/sdk/client/jni/SharedMemoryReaderWriter.o -lc
