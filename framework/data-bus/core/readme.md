# DataBus

[简略技术文档](https://onebox.huawei.com/v/2f5db2349d6585d394c90b128745d96e?type=1)

## 构建

使用根目录下的`build.sh`脚本作为构建入口，提供如下命令，其余信息详见[构建说明](docs/构建说明.md)

```shell
# help message
./build.sh help

# 下载三方依赖
# 环境中没有git-mm的时候会请求许可下载
./build.sh prepare

# 构建
./build.sh
```
