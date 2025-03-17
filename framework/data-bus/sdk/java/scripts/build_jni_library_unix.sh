#  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
#  This file is a part of the ModelEngine Project.
#  Licensed under the MIT License. See License.txt in the project root for license information.

# 创建动态链接库目标目录
mkdir -p src/main/resources/jni

# 编译源文件并生成x86目标文件
g++ -c -fPIC -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/linux \
src/main/java/modelengine/databus/sdk/client/jni/SharedMemoryReaderWriter.cpp -o \
src/main/java/modelengine/databus/sdk/client/jni/SharedMemoryReaderWriter.o

# 链接目标文件并生成动态链接库
g++ -shared -fPIC -o \
src/main/resources/jni/libdatabus_x86.so src/main/java/modelengine/databus/sdk/client/jni/SharedMemoryReaderWriter.o -lc

# 编译源文件并生成aarch64目标文件
aarch64-linux-gnu-g++ -c -fPIC -I ${JAVA_HOME}/include -I ${JAVA_HOME}/include/linux \
src/main/java/modelengine/databus/sdk/client/jni/SharedMemoryReaderWriter.cpp -o \
src/main/java/modelengine/databus/sdk/client/jni/SharedMemoryReaderWriter.o

# 链接目标文件并生成动态链接库
aarch64-linux-gnu-g++ -shared -fPIC -o \
src/main/resources/jni/libdatabus_aarch64.so src/main/java/modelengine/databus/sdk/client/jni/SharedMemoryReaderWriter.o -lc
