# 去除HTML标签插件

## 背景

本插件实现将html标签去除的功能

## 调用接口输入
```python
inputs = {
            "businessData": {
                
            },
            "passData": {
                    "data": "",
                    "text": "<p><b>机器学习</b>是<a href=\"/wiki/%E4%BA%BA%E5%B7%A5%E6%99%BA%E8%83%BD\" title=\"人工智能\">人工智能</a>的一个分支。</p>",
                    "meta": {"fileName": "xxx.doc"}  # 待处理数据元信息 
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
                    "text": "机器学习是人工智能的一个分支。",  # 处理后数据
                    "meta": {"fileName": "xxx.doc"}  # 处理后数据元信息 
            },
            "contextData": ""
        }

```

## 执行效率

处理速度毫秒级。451个测试文档、大小33.6MB，耗时464ms

![image-20231024191311067](http://image.huawei.com/tiny-lts/v1/images/4400d11635c7b19709d510051d29bf34_668x450.png)

## 接口并发效率

每秒请求数1209个Request

![image-20231024191524902](http://image.huawei.com/tiny-lts/v1/images/91d9b3ff9369b2c0a540fc6adcea727d_2065x723.png)
