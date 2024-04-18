# fbs代码生成

## 背景

.fbs是Flatbuffers格式的信息定义文件。DataBus SDK与主服务之间的信息交换使用Flatbuffers进行序列化和反序列化

关于Flatbuffers和.fbs的其他技术细节，请见[https://flatbuffers.dev/](!https://flatbuffers.dev/)

## fbs开发

修改.fbs文件后，需同步更新Java和Python SDK中的生成代码。**请务必将fbs文件和生成代码的修改放在同一个MR中**

### C++代码生成

使用build.sh编译DataBus主服务时，CMake会自动根据.fbs文件生成并更新C++生成文件，无需额外步骤

### Java SDK代码生成

Java SDK需要手动生成代码，具体步骤为

1. 在`core/`文件夹下执行`./build.sh`，生成`core/build/bin/flatc`文件
2. 改变工作目录至`common/fbs`文件夹下
3. 执行`../../core/build/bin/flatc --java [你修改、新增的所有fbs文件]`
4. 当前目录会产生相关的.Java文件，请将它们移动至`sdk/java/src/main/java/com/huawei/databus/sdk/message`目录下
5. 在生成的.java文件中添加版权头部及修改包名
6. 提交代码并发MR。

### Python SDK代码生成

TODO
