# 构建说明

本文档旨在说明DataBus的core项目（下称DataBus内核）中的构建系统

## 总览

DataBus内核为C++ 14项目，使用CMake系统构建，配备有标准的shell脚本作为构建功能的包装以及入口

项目选择[git-mm](https://his.huawei.com/csop/index.html#/ToolInfo?toolId=1615250775275995136&samType=his)自研工具作为三方依赖管理

项目构建入口脚本为DataBus内核根路径(形如`jade/lib/framework/data-bus/core`)下的`build.sh`，主要提供以下命令：

- `./build.sh prepare` [下载三方依赖](#下载三方依赖)
- `./build.sh build` [启动构建](#启动构建)，**注意**，当`./build.sh`直接运行的时候就是执行本功能
- `./build.sh release` [启动构建](#启动构建)，**注意**，此命令使用Release模式构建
- `./build.sh test` [进行测试](#进行测试)
- `./build.sh pack` [打包镜像](#打包镜像)

## 下载三方依赖

对应命令`./build.sh prepare`

依据`third_party`目录中的`third_party.mm.xml`下载三方依赖，里面定义了需要的依赖以及本地路径

若在本地环境运行，需要git有访问仓的权限，推荐[生成CodeHub的token](https://12345.huawei.com/unidesk/portal/#/case_details?caseId=KT00141759)
，然后按照如下方式使用：

```xml

<manifest>
    <!--line 4 in third_party.mm.xml-->
    <!-- 改成如下 -->
    <remote name="code-hub-open" fetch="https://带字母的工号:生成的token@open.codehub.huawei.com/"/>
</manifest>
```

需要环境中有`git-mm`，若无，会通过`scripts/prepare_third_party.sh`中的`prepare_git_mm`函数下载

## 启动构建

调用`scripts/build_all.sh`中的步骤，通过`CMake -> make`流程进行构建

分为两种构建模式：

- Debug: 输出目录为`build/`，对应命令`./build.sh`或`./build.sh build`或`./build.sh build all`，三者等价
- Release: 输出目录为`deploy/`，对应命令`./build.sh release`或`./build.sh release all`，二者等价

## 进行测试

对应以下两个命令：

- `./build.sh test` 构建测试并**立即运行测试**
- `./build.sh build test` 仅构建测试，不会运行

## 打包镜像

对应如下两个命令，二者等价：

- `./build.sh pack`
- `./build.sh release image`

命令使用Release模式构建DataBus内核，输出目录指定为`deploy`，输出产物路径为`deploy/bin`
，包含databus程序、flatc程序以及包含databus程序的docker镜像tar包

docker镜像tar包使用dockerfile为[core.dockerfile](../../common/docker/core.dockerfile)，基础镜像使用ubuntu:jammy
