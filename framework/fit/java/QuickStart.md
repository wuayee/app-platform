## FIT

FIT 函数平台是一套支持多语言融合编程、插件化开发、聚散部署和共享内存的解决方案，旨在提供一种灵活的服务开发范式。

* 多语言融合编程：FIT 在多种编程语言中采用统一的接口描述标准，分别进行实现，并进行相互调用，以达到多语言互相融合协作的目的。
* 插件化开发：FIT 为多种编程语言分别提供了插件开发标准，允许插件在不依赖其他插件的前提下调用其他插件所提供的服务。
* 聚散部署：FIT 为多种编程语言分别提供了运行时环境，允许插件在其运行时环境中既可以单个插件独立进程运行，也可以多个插件共享进程运行。
* 共享内存：FIT 支持插件之间共享内存，即使多个插件分散为多个进程部署，也可以以内存访问的效率来访问共享数据。

## 快速开始

xx 文档包含详细的教程信息，以下是在`Java`中完整的`FIT`应用程序的简单说明。
#### 0. 依赖
在系统生成的 pom 文件中，添加 Web 相关的依赖，如下所示：
```bash
<dependencies>
        <dependency>
            <groupId>modelengine.fit.starter</groupId>
            <artifactId>fit-starter</artifactId>
            <version>${fit.version}</version>
        </dependency>
        <dependency>
            <groupId>modelengine.fit.starter</groupId>
            <artifactId>fit-plugins-starter-web</artifactId>
            <version>${fit.version}</version>
        </dependency>
        <dependency>
            <groupId>modelengine.fitframework</groupId>
            <artifactId>fit-api</artifactId>
            <version>${fit.version}</version>
        </dependency>
    </dependencies>

```
#### 1. 创建启动器
通过 FitStarter 类的静态方法 start 去启动整个应用，其中，@Component注解为必须添加，使 FIT 框架对启动类进行识别，示例如下：
```java
package modelengine.fit.demo1;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.runtime.FitStarter;

@Component
public class DemoApplication {
    public static void main(String[] args) {
        FitStarter.start(DemoApplication.class, args);
    }
}
```
#### 2. 创建控制器
在该应用程序中，HTTP 请求由控制器处理。我们可以创建相应的控制器和方法来进行处理，示例如下：
```java
package modelengine.fit.demo1.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fitframework.annotation.Component;

/**
 * 控制器。
 */
@Component
public class Controller {
    @GetMapping(path = "/hello")
    public String hello() {
        return "hello world!";
    }
}
```
#### 3. 启动
当前应用启动有两种方式：
- 通过 IDEA 启动：您可以直接在 IDEA 运行 main 方法。
- 通过执行 JAR 文件：您可以使用 Maven 在命令行运行，构建一个包含所有必要依赖项、类和资源的单个可执行 JAR 文件，并运行该文件。
## 构建

所需要的环境：

* 编辑器，如 IntelliJ IDEA
* Java 8
* Maven，推荐版本 Maven 3.8+
* 对 fitframework 构建：在framework/fit/java目录下使用 Maven 对框架进行编译：`mvn clean install`
* 对 您的项目 构建：在您的项目下进行编译：`mvn clean install`
