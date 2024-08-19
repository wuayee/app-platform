# 检查文档去除目录插件

## 背景

1. 本插件实现了对文档文章的目录去除。

## 调用接口输入
```python
inputs = [
    {
        "businessData": {
           "params": {}
		},
        "passData": {
            "data": "",
            "text": "目录：前言\n本篇文档介绍了",  # 待处理数据
            "meta": {
                "fileName": "xxx.doc"
            }  # 待处理数据元信息 
        },
        "contextData": ""
    },
    {}
]
```

## 调用接口输出
```python
outputs = [
    {
        "businessData": {
            "params": {}
		},
        "passData": {
            "data": "",
            "text": "本篇文档介绍了",  # 处理后数据
            "meta": {
                "fileName": "xxx.doc"
            }  # 处理后数据元信息 
        },
        "contextData": ""
    },
    {}
]
```

