[toc]

# Hakuna Python

## 使用帮助

[使用帮助](https://openx.huawei.com/fit/wikiHome/domain17905/WIKI20220827271302)

## 联系人 & committer

张中恩 z00560464

## 环境准备

支持python版本3.8.x

框架版本

| 时间  | 版本号        |
| ---- | ------------- |
|      | 0.0.5-RELEASE |
|      |               |
|      |               |

## 开发指南

开发者主要通过python装饰器引入框架能力。

### Fit服务调用和实现

基本规则：

1. Fit服务调用的入参不支持**kwargs
2. @fit/@private_fit调用函数声明可以放在任何地方，包括Module，其他函数，或类中
3. @fitable/@private_fitable函数必须定义在Module中
4. 微化的@private_fit方法名和@private_fitable服务实现的方法名字必须一致）

Fit服务调用（IDE插件自动生成）

```
   from fitframework.api.decorator import fit
   from numpy import int32
   
   @fit(generic_id='xxxxxxxxxxxxxxxxx')
   def funcA(x: str, y: str) -> int32
       pass
```

Fit服务指定别名调用（IDE插件自动生成）

```
   from fitframework.api.decorator import fit
   from numpy import int32
   
   @fit(generic_id='xxxxxxxxxxxxxxxxx', alias='impl1.functionA')
   def funcA(x: str, y: str) -> int32
       pass
```

Fitable服务实现（IDE插件自动生成）

```
    from fitframework.api.decorator import fitable
    from numpy import int32
    
    @fitable(generic_id='xxxxxxxxxxxxxxxxx', fitable_id='yyyyyyyyyyyyyyyyyyyyy')
    def funcA(x: str, y: str) -> int32
        return len(x) + len(y)
```

微化Fit服务调用

```
    from fitframework.api.decorator import private_fit
    
    @private_fit
    def private_funcA() -> string
        pass
```

微化Fit服务实现

```
    from fitframework.api.decorator import private_fitable
    
    @private_fitable
    def private_funcA() -> string
        return 'hello world'
```

低阶函数调用

```python
from fitframework.core.broker.select_broker import select

select(genericableId).invoke(args)
```

### 读取环境变量和进程输入参数

通过local_context装饰器读取环境变量和进程输入参数。如果环境变量的Key和进程输入参数重名，前者覆盖后者。

```
    from fitframework.api.decorator import local_context
    
    @local_context(key='a.b.c', converter=to_list, default_value='abc')
    def get_abc():
        pass
        
    x = get_abc()
    ...
```

### 读取插件配置

通过value装饰器读取插件配置application.yml

```
    from fitframework.api.decorator import value
    from numpy import int32
    
    @value(key='x.y.z', default_value='xyz')
    def get_xyz():
        pass
        
    x = get_xyz()
```

### 异常处理

Fit框架提供FitBaseException错误基类，Fit接口实现方式抛异常，异常类推荐继承此类，FitBaseException提供error_code和message，框架远程
通讯时将传递这两个信息，并根据错误码在接口调用客户端还原成FitBaseException。

```
    from fitframework.api.exception import FitBaseExeception
```

启动参数

```text
"--sys_folders=D:\workspace\fit\plugin-bootstrap\fit_py_bootstrap,D:\workspace\fit\plugin-bootstrap\fit_py_configuration_repo,D:\workspace\fit\plugin-bootstrap\fit_py_runtime,D:\workspace\fit\plugin-bootstrap\fit_py_service_db" 
"--plugin_folders=D:\workspace\fit\plugin\fit_py_configuration_center_agent,D:\workspace\fit\plugin\fit_py_configuration_center_agent_empty,D:\workspace\fit\plugin\fit_py_debugger_client,D:\workspace\fit\plugin\fit_py_debugger_server,D:\workspace\fit\plugin\fit_py_grpc_client,D:\workspace\fit\plugin\fit_py_heart_beat_agent,D:\workspace\fit\plugin\fit_py_heart_beat_agent_empty,D:\workspace\fit\plugin\fit_py_http_client,D:\workspace\fit\plugin\fit_py_load_balancer,D:\workspace\fit\plugin\fit_py_registry_client,D:\workspace\fit\plugin\fit_py_registry_client_empty,D:\workspace\fit\plugin\fit_py_server,D:\workspace\fit\plugin\fit_py_server_grpc" 
"--config_folder=D:\workspace\fit\core\python\config"
"--comp_folder=D:\workspace\fit\core\python\protoConverter"
```

### 单元测试

##### 环境准备

需要安装两个PyPi包：`pytest`, `pytest-cov`（用于测试覆盖率统计）

##### 单个插件的单元测试

用户在单独的一个`plugin`目录下进行开发，里面的每一个目录对应了一个开发的单独插件；对于每个插件的测试用例，开发应在该插件目录下的`test`文件夹下进行维护。

以一个例子来说明：假设开发者在`plugin`目录下有一个开发中的插件，名为hello_world，其对应的插件目录为`plugin/fit_py_hello_world`，测试文件应在其中的`test`
文件夹应该里进行维护，假设其为`hello_world_test.py`，则其完整目录为`plugin/fit_py_hello_world/test/hello_world_test.py`
，需要单独测试hello_world插件时，直接单独运行该py文件即可（注意`PYTHONPATH`在IDE或其他相应环境的维护）。

##### 集成单元测试

框架本身在与`plugin`目录同级的位置（即父目录为`fit`）下提供了一个继承单元测试脚本`run_tests_for_python.bat`
，直接执行即可（目前仅支持batch环境，同时开发需要将自己的插件目录加在脚本里的全局`PYTHONPATH`变量）。

脚本本身调用的为pytest框架，故支持pytest相应的基本结果展示、测试报告、覆盖率等能力。

### CLI

[FIT CLI使用帮助](http://3ms.huawei.com/km/groups/3944708/blogs/details/10194175?l=zh-cn)

用管理员模式打开gitbash，激活要使用的python环境

1、安装FIT CLI

```bash
curl -k -o install.sh https://dgg.artifactory.cd-cloud-artifact.tools.huawei.com/artifactory/sz-software-snapshot/fitlab/snapshot/Fit/fit-cli/install.sh && sh install.sh && source ~/.bash_profile
```

2、切换CLI 语言

```bash
fpm use -l python
```

3、安装指定版本

```
fpm install -l python -v 0.0.4-RELEASE
```

3、启动 切换到插件目录

```bash
fit start
```

4、debug模式启动

首先在pycharm启动debug server， 配置端口

然后执行启动命令，并配置端口

```bash
fit debug -dp port
```

## 升级过程

python包版本号后缀a表示alpha，无后缀表示正式版

hakuna-python的版本号遵循-SNAPSHOT,-RELEASE区分测试和正式版

### 1、版本号约定

1. 假设升级前的版本号为 `{$current}a{$d}`，升级后的版本号为 `{$current}`

其中$d为数字，表示第几个快照版本

### 2、release发布

1. 在最新的 `develop` 点位拉出一个 `release` 分支，记为 `release-python-{$current}`，切换到该分支
2. 对 `core/python, plugin, plugin-bootstrap` 目录下的所有 `setup.py` 进行搜索，替换 `{$current}a` 为 `{$current}`
3. 更新`/CloudDragon/hakuna_python/build_hakuna_python/requirements`目下的`bootstrap_requirements.txt`
   、`core_requirements.txt`,`plugin_requirements.txt`中的依赖的版本号为 `{$current}`
4. 将 `cli/python/version.properties` 中的版本号 `FIT_FRAMEWORK_VERSION` 由 `{$current}-SNAPSHOT` 改为 `{$current}-RELEASE`
   ，`RELEASE_TYPE` 由 `snapshot` 改为 `release`
5. 提交修改，并push到仓库
6. 执行`/script/`目录下的`packing.py`，打包所有的插件，以及底座
7. 打一个 `tag`，命令为 `git tag release-python-{$current}-tag`，推送 `tag` 到远端，命令为 `git push origin --tags`
8. 引擎流水线(`Hakuna_Python_Foundation`)发布正式版,分支选择`release-python-{$current}`
9. `FIT_CLI_Python` 流水线发布正式版，分支选择`release-python-{$current}`,版本与引擎保持一致

### 3、snapshot发布

1. 切回 `develop` 分支
2. 对 `core/python, plugin, plugin-bootstrap` 目录下的所有 `setup.py` 进行搜索，替换 `{$current}a{$d}` 为 `{$current + 1}a0`
3. 更新`/CloudDragon/hakuna_python/build_hakuna_python/requirements`目下的`bootstrap_requirements.txt`
   、`core_requirements.txt`,`plugin_requirements.txt`中的版本号
4. 将 `cli/python/version.properties` 中的版本号 `FIT_FRAMEWORK_VERSION` 由 `{$current}-SNAPSHOT` 改为 `{$current + 1}-SNAPSHOT`
   ，`RELEASE_TYPE` 改为 `snapshot`
5. 将 `cli/cli.properties` 中的 `python_default_version` 的值改为 `{$current}-RELEASE`
6. 提交修改，并push到仓库
7. 执行`/script/`目录下的`packing.py`，打包所有的插件，以及底座
8. 修改 `FIT_CLI_python` 流水线执行计划 `develop` 的构建参数 `snapshot_version` ，改为 `{$current + 1}-SNAPSHOT`，使用该方案，选择develop分支，启动流水线
9. 修改 `Hakuna_Python_Foundation` 流水线执行计划 `develop` 的构建参数 `snapshot_version` ，改为 `{$current + 1}-SNAPSHOT`
   ，使用该方案，选择develop分支，启动流水线
10. 启动 `FIT_CLI_default_properties` 流水线，选择执行方案 -> `develop`


### 4、简化版发布方案
1. 切换至 `main` 分支，假定当前在 `setup.py` 中所记录的版本为 `a.b.0`，该版本号用于标记打包后的 `whl` 文件的版本
2. 在 `main` 分支当前提交处打 tag，此时可以通过执行 `python setup.py bdist_wheel` 获得打包后的框架制品包
3. 由 `main` 分支创建新分支 `python-a.b.x`
4. 在主干提交改动“准备下一个”版本，并修改`setup.py` 中所记录版本号为 `a.b+1.0`
5. 编写 release note，并在附件中添加三部分内容：框架制品包、系统插件压缩包、配置文件压缩包


### 5、bug 修复方案
1. 在 `python-a.b.x` 分支上修改代码
2. 不断向更年轻的分支上合入改动直至主干

## 回退方案

