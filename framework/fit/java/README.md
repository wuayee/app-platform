# 环境配置

## 开发环境配置

- 开发环境：`IntelliJ IDEA`
- 代码格式化文件：`CodeFormatterFromIdea.xml`
- `Maven` 配置：`settings.xml`

## 构建命令

```
mvn clean install -s settings.xml
```

## 输出目录

```
/target
```

## 启动命令

```
/target/bin/fit start
```

# 模块设计

- [工具集设计](fit-util/README.md)
- [API设计](fit-api/README.md)
- [IoC容器设计](fit-ioc/README.md)
- [配置架构设计](fit-conf/README.md)
- [插件架构设计](fit-plugin/README.md)
- [运行时设计](fit-runtime/README.md)
- [启动程序设计](fit-launcher/README.md)

# 遗留问题

## `fit-util`

### VirtualDirectory能力实现

当前仅提供了基于文件系统的实现，待提供基于如ZIP的实现。

消费方：
- JAR in JAR形态类加载、资源读取等。

### Class文件解析及生成

在 `com.huawei.fitframework.jvm` 包中，当前仅实现了对类型最基础信息的读取，待完善剩余功能。

同时需要设计并实现类生成能力，提供友好的API（可参考C# Emit）。

消费方：
- `AOP` 能力替换。
- `IoC` 模块类扫描能力增强。

### 泛型匹配

当前仅提供 `Class` 和 `ParameterizedType` 的泛型匹配，即整个泛型中都只能出现这两种元素。其他类型待支持：`TypeVariable`、`GenericArrayType`、`WildcardType`。

消费方：
- `IoC` 泛型注入。

## `fit-launcher`

### JAR in JAR

待实现

### FIT App

当前只调试到加载插件，具体是否还有问题待支持 `BrokerServer` 后调试。
