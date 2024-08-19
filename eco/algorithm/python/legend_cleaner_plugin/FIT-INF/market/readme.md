# 图注表注去除插件

## 背景

本插件实现移除文档中图注、表注等

## 调用接口输入

```python
inputs = {
    "businessData": {

    },
    "passData": {
        "data": "",
        "text": "图1.1.1 图注名称\n优秀",
        "meta": {}  # 待处理数据元信息 
    },
    "contextData": ""
}

```

## 调用接口输出

```python
outputs = {
    "businessData": {

    },
    "passData": {
        "data": "",
        "text": "优秀",  # 处理后数据
        "meta": {}  # 处理后数据元信息 
    },
    "contextData": ""
}

```