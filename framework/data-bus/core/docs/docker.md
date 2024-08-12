# docker镜像说明

本文档旨在说明DataBus的core项目（下称DataBus内核）中的docker镜像打包等相关信息

## 如何打包

dockerfile位置：`jade/framework/data-bus/common/docker/core.dockerfile`
容器初始化脚本位置：`jade/framework/data-bus/core/src/scripts/core_init.sh`

环境依赖项：

- `docker`程序
- `ubuntu:jammy`镜像

### 手动打包

将需要打包的databus内核主程序(文件名为`databus`)、容器初始化脚本位置(文件名为`core_init.sh`)、与`core.dockerfile`放置在同一目录下，
运行如下命令：
```shell

docker build -f core.dockerfile core_init.sh -t databus-core:latest .
```

命令执行完后，即可获得docker镜像`databus-core:latest`。运行命令可以查看：

```shell

> docker images -a | grep databus-core
databus-core      latest         62f075501d03   12 minutes ago   78.7MB
```

### 自动打包

在DataBus内核根目录下，执行`./build.sh pack`命令

- 脚本会将release模式的databus内核程序打包成`databus-core:latest`镜像
- 脚本会将镜像导出至`deploy/bin/databus-core_latest.tar`文件

> `databus-core_latest.tar`文件可以通过`docker load -i databus-core_latest.tar`命令导入其他机器的docker

## 如何运行

执行`docker run --name="databus-core" -d -p5284:5284 -t databus-core"

查看日志：

- `docker exec -it databus-core /bin/bash`进入容器
- `tail -f /home/log.txt`查看日志
