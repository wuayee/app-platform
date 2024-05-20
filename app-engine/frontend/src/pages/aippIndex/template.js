export const templateJson = {
  "id": "template1",
  "name": "LLM application template",
  "createUser": "xxx",
  "createTime": "xxx",
  "updateUser": "xxx",
  "updateTime": "xxx",
  "type": "template",
  "config": {
      "id": "config1",
      "tenantId": "",
      "createUser": "xxx",
      "createTime": "xxx",
      "updateUser": "xxx",
      "updateTime": "xxx",
      "form":
      {
          "id": "",
          "name": "llm_config",
          "appearance": "{}",
          "type": "",
          "properties":
          [
              {
                  "id": "prop1",
                  "name": "model",
                  "dataType": "String",
                  "defaultValue": "Qwen",
                  "nodeId": "llm"
              },
              {
                  "id": "prop2",
                  "name": "temperature",
                  "dataType": "Float",
                  "defaultValue": 0.3,
                  "nodeId": "llm"
              },
              {
                  "id": "prop3",
                  "name": "systemPrompt",
                  "dataType": "String",
                  "defaultValue": "这是一段提示词",
                  "nodeId": "llm"
              },
              {
                  "id": "prop4",
                  "name": "tool",
                  "dataType": "List<String>",
                  "defaultValue": ["id1", "id2"],
                  "nodeId": "llm"
              },
              {
                  "id": "prop5",
                  "name": "workflows",
                  "dataType": "List<String>",
                  "defaultValue": ["id1", "id2"],
                  "nodeId": "llm"
              },
              {
                  "id": "prop6",
                  "name": "knowledge",
                  "dataType": "List<Object>",
                  "defaultValue": [
                      {
                          "id": 92,
                          "name": "testsess",
                          "description": "",
                      },
                      {
                          "id": 91,
                          "name": "testseg",
                          "description": "",
                      }
                  ],
                  "nodeId": "llm2sql"
              },
              {
                  "id": "prop7",
                  "name": "inspiration",
                  "dataType": "Object",
                  "defaultValue": {
                      "category": [
                        {
                            "title": "root",
                            "id": "root",
                            "children": [
                                {
                                    "title": "产品线",
                                    "id": "11",
                                    "parent": "root:11",
                                    "children": [
                                        {
                                            "title": "数存",
                                            "id": "111",
                                            "parent": "11:111",
                                            "children": [
                                                {
                                                    "title": "数字化工具",
                                                    "id": "1111",
                                                    "parent": "111:1111",
                                                    "children": []
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "title": "BG",
                                    "id": "12",
                                    "parent": "root:12",
                                    "children": [
                                        {
                                            "title": "运营商",
                                            "id": "121",
                                            "parent": "12:121",
                                            "children": [
                                                {
                                                    "title": "数字化工具",
                                                    "id": "1211",
                                                    "parent": "121:1211",
                                                    "children": []
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "title": "区域",
                                    "id": "13",
                                    "parent": "root:13",
                                    "children": [
                                        {
                                            "title": "中国区",
                                            "id": "131",
                                            "parent": "13:131",
                                            "children": [
                                                {
                                                    "title": "数字化工具",
                                                    "id": "1311",
                                                    "parent": "131:1311",
                                                    "children": []
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                      ],
                      "inspirations": [
                          {
                              "id": "ins1",
                              "name": "灵感1",
                              "description": "这是考试用的",
                              "prompt": "这是一段提示词{{XXXX}}{{YYYY}}",
                              "category": "121:1211",
                              "promptVarData": [
                                  {
                                      "key": "1",
                                      "var": "XXXX",
                                      "varType": "选择框",
                                      "sourceType": "fitable",
                                      "sourceInfo": "GetQAFromLog",
                                      "multiple": true
                                  },
                                  {
                                      "key": "2",
                                      "var": "YYYY",
                                      "varType": "选择框",
                                      "sourceType": "input",
                                      "sourceInfo": "1;2;3;4;5",
                                      "multiple": false
                                  }
                              ],
                              "auto": false
                          },
                          {
                              "id": "ins2",
                              "name": "灵感2",
                              "description": "这是吃饭用的",
                              "prompt": "这是一段提示词",
                              "category": "111:1111",
                              "auto": true
                          }
                      ]
                  }
              },
              {
                  "id": "prop8",
                  "name": "multimodal",
                  "dataType": "List<String>",
                  "defaultValue": ["file", "image", "radio", "video"]
              }
          ]
      }

  },
  "flowGraph": {
      "id": "graph1",
      "name": "LLM application graph",
      "createUser": "xxx",
      "createTime": "xxx",
      "updateUser": "xxx",
      "updateTime": "xxx",
      "appearance": {
          "id": "729719c5bcdf4fd181d8821e647cb0bc",
          "title": "jadeFlow",
          "source": "elsa",
          "type": "jadeFlowGraph",
          "tenant": "default",
          "setting": {},
          "pages": [
              {
                  "type": "jadeFlowPage",
                  "id": "elsa-page:cdhm25",
                  "text": "newFlowPage",
                  "namespace": "jadeFlow",
                  "x": 0,
                  "y": 0,
                  "width": 2000,
                  "height": 1600,
                  "bold": false,
                  "italic": false,
                  "dockAlign": "top",
                  "division": -1,
                  "itemSpace": 5,
                  "itemPad": [
                      0,
                      0,
                      0,
                      0
                  ],
                  "itemScroll": {
                      "x": 0,
                      "y": 0
                  },
                  "hideText": true,
                  "dockMode": "none",
                  "shapesAs": {},
                  "borderColor": "white",
                  "backColor": "white",
                  "fontSize": 18,
                  "fontFace": "arial",
                  "fontColor": "#ECD0A7",
                  "fontWeight": "bold",
                  "fontStyle": "normal",
                  "hAlign": "left",
                  "vAlign": "top",
                  "moveable": true,
                  "container": "elsa-page:cdhm25",
                  "dirty": false,
                  "isPage": true,
                  "mode": "configuration",
                  "shapes": [
                      {
                          "type": "start",
                          "container": "elsa-page:cdhm25",
                          "id": "start",
                          "componentId":"component1",
                          "config": {

                          },
                          "text": "",
                          "namespace": "flowable",
                          "x": 236,
                          "y": 171,
                          "width": 500,
                          "height": 193,
                          "bold": false,
                          "italic": false,
                          "pad": 6,
                          "rotateAble": false,
                          "triggerMode": "auto",
                          "enableAnimation": true,
                          "warningTask": 0,
                          "runningTask": 0,
                          "completedTask": 0,
                          "hideText": true,
                          "autoHeight": true,
                          "focusBorderColor": "#4d53e8",
                          "borderWidth": 2,
                          "dashWidth": 0,
                          "index": 100,
                          "dirty": true
                      },
                      {
                          "type": "taskNode",
                          "container": "elsa-page:cdhm25",
                          "id": "llm2sql",
                          "componentId":"component2",
                          "text": "",
                          "namespace": "flowable",
                          "x": 1140,
                          "y": 208,
                          "width": 500,
                          "height": 164,
                          "bold": false,
                          "italic": false,
                          "pad": 6,
                          "rotateAble": false,
                          "triggerMode": "auto",
                          "enableAnimation": true,
                          "warningTask": 0,
                          "runningTask": 0,
                          "completedTask": 0,
                          "hideText": true,
                          "autoHeight": true,
                          "focusBorderColor": "#4d53e8",
                          "borderWidth": 2,
                          "dashWidth": 0,
                          "index": 101,
                          "dirty": true
                      },
                      {
                          "type": "taskNode",
                          "container": "elsa-page:cdhm25",
                          "id": "llm",
                          "componentId":"component3",
                          "text": "",
                          "namespace": "flowable",
                          "x": 1140,
                          "y": 208,
                          "width": 500,
                          "height": 164,
                          "bold": false,
                          "italic": false,
                          "pad": 6,
                          "rotateAble": false,
                          "triggerMode": "auto",
                          "enableAnimation": true,
                          "warningTask": 0,
                          "runningTask": 0,
                          "completedTask": 0,
                          "hideText": true,
                          "autoHeight": true,
                          "focusBorderColor": "#4d53e8",
                          "borderWidth": 2,
                          "dashWidth": 0,
                          "index": 101,
                          "dirty": true
                      },
                      {
                          "type": "end",
                          "container": "elsa-page:cdhm25",
                          "id": "end",
                          "componentId":"component4",
                          "text": "",
                          "namespace": "flowable",
                          "x": 1140,
                          "y": 208,
                          "width": 500,
                          "height": 164,
                          "bold": false,
                          "italic": false,
                          "pad": 6,
                          "rotateAble": false,
                          "triggerMode": "auto",
                          "enableAnimation": true,
                          "warningTask": 0,
                          "runningTask": 0,
                          "completedTask": 0,
                          "hideText": true,
                          "autoHeight": true,
                          "focusBorderColor": "#4d53e8",
                          "borderWidth": 2,
                          "dashWidth": 0,
                          "index": 101,
                          "dirty": true
                      }
                  ],
                  "index": 0
              }
          ]
      }
  }
}
