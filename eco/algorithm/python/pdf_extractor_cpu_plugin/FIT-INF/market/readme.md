# CPU场景下PDF抽取插件

## 背景

本插件实现使用CPU进行PDF文本抽取

## 实现逻辑

本插件基于视觉的版面分析和传统PDF读取方案，实现抽取PDF的文字和表格的能力

## 约束

### 支持抽取的格式

1. 抽取文字
2. 段落中文字大小一致
3. 支持以HTML格式抽取全框线表格，表格中无换行
4. 支持去除页眉页尾

### 不支持抽取的格式

1. 图片和以图片形式存在的文字/表格无法抽取
2. 多栏的文字排版会存在混乱
3. 段落的换行会存在混乱
4. 连续空格/空行无法被抽取
5. 目录和文档在同一页会导致换行换行处理有误
6. 表格抽取时存在行列丢失
7. 半框线和无框线表格抽取易出错


## 调用接口输入

```python
inputs = {
    "businessData": {

    },
    "passData": {
        "data": b"xxx",
        "text": "",
        "meta": {"fileName": "xxx.pdf", "fileType": "pdf"}  # 待处理数据元信息 
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