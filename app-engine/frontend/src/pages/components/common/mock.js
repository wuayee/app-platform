export const listData = [
  {
      "type": "aippState",
      "name": "提取图像信息",
      "icon": "xx.icon",
      "description": "这是一个图像信息提取节点\n入参的key: imagePath, llmModelName\n出参的key: llmImage2TextResult",
      "group": [
          "imageProcessor"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "提取图像信息",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMImage2Text"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "文本生成文本",
      "icon": "xx.icon",
      "description": "这是一个文本生成文本节点\n入参的key: prompt, llmModelName\n出参的key: llmText2TextResult",
      "group": [
          "textProcessor"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "文本生成文本",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMText2Text"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "解析并识别采购信息",
      "icon": "xx.icon",
      "description": "这是一个文本生成文本(提取采购信息)节点\n入参的key: prompt, llmModelName\n出参的key: llmText2TextResult",
      "group": [
          "textProcessor"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "ohscript_jober",
          "name": "解析并识别采购信息",
          "entity": {
              "code": "let input = ext::context; \nlet prompt = input.get(0).get(\"businessData\").get(\"prompt\"); \ninput.get(0).get(\"businessData\").put(\"prompt\", \"你是一个存储产品采购分析员，请根据以下信息，分析出对方的采购内容，要求输出内容简洁。信息为：\"+prompt);\nlet context1 = entity{.async = true;.id = \"com.huawei.fit.jober.aipp.fitable.LLMText2Text\";};\nlet f1 = fit::handleTask(context1);\nlet ans = input >> f1;\nans",
              "retryNum": 2
          },
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMText2Text"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "word生成脑图",
      "icon": "xx.icon",
      "description": "这是一个word生成脑图节点\n入参的key: filePath, llmModelName\n出参的key: llmWord2MindResult",
      "group": [
          "textProcessor"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "word生成脑图",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMWord2Mind"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "pdf生成文本",
      "icon": "xx.icon",
      "description": "这是一个pdf生成文本节点\n入参的key: pdfPath, llmModelName\n出参的key: llmPdf2TextResult",
      "group": [
          "textProcessor"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "pdf生成文本",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMPdf2Text"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "大模型检索",
      "icon": "xx.icon",
      "description": "这是一个大模型检索节点;\n入参的key: prompt, llmModelName\n出参的key: llmRecommendDocResult",
      "group": [
          "search"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "大模型检索",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMSearchFile"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "检索",
      "icon": "xx.icon",
      "description": "这是一个检索节点;\n入参的key: w3QueryContent, w3QueryTopK\n出参的key: w3QueryResult",
      "group": [
          "search"
      ],
      "triggerMode": "auto",
      "tags": [],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "检索",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.SearchFileFromW3"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "检索数存知识",
      "icon": "xx.icon",
      "description": "这是一个大模型检索(检索数存知识)节点;\n入参的key: prompt, llmModelName\n出参的key: llmRecommendDocResult",
      "group": [
          "search"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "ohscript_jober",
          "name": "检索数存知识",
          "entity": {
              "code": "let input = ext::context;\nlet llmText2TextResult = input.get(0).get(\"businessData\").get(\"llmText2TextResult\");\ninput.get(0).get(\"businessData\").put(\"prompt\", \"你是数存知识专家，请根据下面的采购内容，给出匹配的数存产品信息资料。采购内容为：\"+llmText2TextResult);\nlet context1 = entity{.async = true;.id = \"com.huawei.fit.jober.aipp.fitable.LLMSearchFile\";};\nlet f1 = fit::handleTask(context1);\nlet ans = input >> f1;\nans",
              "retryNum": 2
          },
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMSearchFile"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "JSON生成脑图",
      "icon": "xx.icon",
      "description": "这是一个JSON生成脑图节点;\n入参的key: prompt, llmModelName\n出参的key: llmJson2MindResult",
      "group": [
          "textProcessor"
      ],
      "triggerMode": "auto",
      "tags": [],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "JSON生成脑图",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMJson2Mind"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "智能推荐数据产品Agent",
      "icon": "xx.icon",
      "description": "这是一个智能推荐数据产品Agent节点;\n入参的key: llmPdf2TextResult\n出参的key: llmRecommendDocResult",
      "group": [
          "agent"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "智能推荐数据产品Agent",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.AppFlowAgentSearch"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "生成脑图Agent",
      "icon": "xx.icon",
      "description": "这是一个生成脑图Agent节点;\n入参的key: llmRecommendDocResult\n出参的key: mindUrl",
      "group": [
          "agent"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "生成脑图Agent",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.AppFlowAgentMind"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "生成PPT",
      "icon": "xx.icon",
      "description": "这是一个生成PPT节点;\n入参的key: text\n出参的key: text2pptResult",
      "group": [
          "textProcessor"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "生成PPT",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMText2PptJson"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "提取文件信息",
      "icon": "xx.icon",
      "description": "这是一个提取文件信息节点，支持pdf、word、xml、yaml、html;\n入参的key: filePath\n出参的key: llmFile2TextResult",
      "group": [
          "textProcessor"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "提取文件信息",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.CompositeFileReader"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "视频生成音频",
      "icon": "xx.icon",
      "description": "这是一个视频生成音频的节点\n入参的key: videoPath\n出参的key: llmVideo2AudioResult",
      "group": [
          "AudioAndViedoProcessor"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "视频生成音频",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.FfmpegVideo2Audio"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "音频生成概要",
      "icon": "xx.icon",
      "description": "这是一个音频生成概要的节点\n入参的key: llmVideo2AudioResult\n出参的key: llmVideo2textResult",
      "group": [
          "AudioAndViedoProcessor"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "音频生成概要",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LlmAudio2Summary"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "音频转任务列表",
      "icon": "xx.icon",
      "description": "这是一个音频转任务列表的节点\n入参的key: audioPath\n出参的key: w3Task",
      "group": [
          "AudioAndViedoProcessor"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "音频转任务列表",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LlmAudio2Task"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "创建待办",
      "icon": "xx.icon",
      "description": "这是一个创建待办的节点\n入参的key: w3Task\n出参的key: w3TaskDisplay",
      "group": [
          "other"
      ],
      "triggerMode": "auto",
      "tags": null,
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "创建待办",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.W3TaskPublish"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "ELSA ppt发布",
      "icon": "xx.icon",
      "description": "这是一个ELSA ppt发布的节点\n入参的key: text2pptResult\n出参的key: elsaPptResult",
      "group": [
          "other"
      ],
      "triggerMode": "auto",
      "tags": [
          "prompt"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "ELSA ppt发布",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.ReleaseElsaPpt"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "调用智能体",
      "icon": "xx.icon",
      "description": "这是一个调用智能体的节点\n入参的key: aipp_agent_param_key\n出参的key: agentResult",
      "group": [
          "agent"
      ],
      "triggerMode": "auto",
      "tags": [
          "agent"
      ],
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "调用智能体",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.AppFlowAgent"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "标书分析",
      "icon": "xx.icon",
      "description": "这是一个标书分析的节点\n入参的key: filetPath\n出参的key: tenderAnalyseResult",
      "group": [
          "other"
      ],
      "triggerMode": "auto",
      "tags": null,
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "标书分析",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMTenderAnalyse"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "生成doc文档",
      "icon": "xx.icon",
      "description": "这是一个生成doc文档的节点\n入参的key: toDocText\n出参的key: downloadDocFileUrl",
      "group": [
          "other"
      ],
      "triggerMode": "auto",
      "tags": null,
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "生成doc文档",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.GenerateWordDoc"
          ]
      }
  },
  {
      "type": "aippState",
      "name": "生成报告",
      "icon": "xx.icon",
      "description": "这是一个把聊天记录生成报告的节点\n入参的key: aipp$QAlist\n出参的key: reportResult",
      "group": [
          "other"
      ],
      "triggerMode": "auto",
      "tags": null,
      "jober": {
          "type": "GENERAL_JOBER",
          "name": "生成报告",
          "fitables": [
              "com.huawei.fit.jober.aipp.fitable.LLMGenerateOperationReport"
          ]
      }
  }
]

export const dragData = [
  {
    name: 'LLM',
    desc: 'test'
  },
  {
    name: 'Code',
    desc: 'test'
  },
  {
    name: 'Knowledge',
    desc: 'test'
  },
  {
    name: 'Condition',
    desc: 'test'
  },
  {
    name: 'Variable',
    desc: 'test'
  }
]