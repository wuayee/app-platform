export const listData = [
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
