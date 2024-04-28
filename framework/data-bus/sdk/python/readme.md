# DataBus Python SDK

DataBus Python SDK旨在为DataBus提供给Python开发者使用的接口，使能对于数据总线的共享内存申请、读取、写入等功能

## 打包

DataBus Python SDK使用`setuptools==69.1.1`作为打包工具，你可以通过`pip install -r requirements-dev.txt`安装

因为多种原因，暂时仍使用setup.py作为项目打包配置文件

你可以在此readme.md所在的Python SDK根目录通过如下方式在开发时进行打包：

- 直接安装：`pip install .`或`python setup.py install`
- 通过生成egg文件并软链接的方式安装：`python setup.py develop`

如果你想直接生成wheel文件，可以通过运行`python setup.py bdist_wheel`命令生成，注意需要`wheel`包