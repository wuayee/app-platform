export const graphData = {
  "id": "f4ec7f617431487cad1232c29ef9ed29",
  "title": "jadeFlow",
  "source": "elsa",
  "type": "jadeFlowGraph",
  "tenant": "default",
  "enableText": false,
  "setting": {
      "borderColor": "#047bfc",
      "backColor": "whitesmoke",
      "headColor": "steelblue",
      "fontColor": "steelblue",
      "captionfontColor": "whitesmoke",
      "fontFace": "arial",
      "captionfontFace": "arial black",
      "fontSize": 12,
      "captionfontSize": 14,
      "fontStyle": "normal",
      "captionfontStyle": "normal",
      "fontWeight": "lighter",
      "captionfontWeight": "lighter",
      "hAlign": "center",
      "vAlign": "top",
      "captionhAlign": "center",
      "lineHeight": 1.5,
      "lineWidth": 2,
      "captionlineHeight": 1,
      "focusMargin": 0,
      "focusBorderColor": "#047bfc",
      "focusFontColor": "darkorange",
      "focusBackColor": "whitesmoke",
      "mouseInColor": "orange",
      "mouseInBorderColor": "#047bfc",
      "mouseInFontColor": "orange",
      "mouseInBackColor": "whitesmoke",
      "borderWidth": 1,
      "focusBorderWidth": 1,
      "globalAlpha": 1,
      "backAlpha": 0.15,
      "cornerRadius": 8,
      "dashWidth": 0,
      "autoText": false,
      "autoHeight": false,
      "autoWidth": false,
      "margin": 25,
      "pad": 10,
      "code": "",
      "rotateDegree": 0,
      "shadow": "",
      "focusShadow": "",
      "shadowData": "2px 2px 4px",
      "outstanding": false,
      "pDock": "none",
      "dockMode": "none",
      "priority": 0,
      "infoType": {
          "next": "INFORMATION",
          "name": "none"
      },
      "progressStatus": {
          "next": "UNKNOWN",
          "color": "gray",
          "name": "NONE"
      },
      "progressPercent": 0.65,
      "showedProgress": false,
      "itemPad": [
          5,
          5,
          5,
          5
      ],
      "itemScroll": {
          "x": 0,
          "y": 0
      },
      "scrollLock": {
          "x": false,
          "y": false
      },
      "resizeable": true,
      "selectable": true,
      "rotateAble": true,
      "editable": true,
      "moveable": true,
      "dragable": true,
      "visible": true,
      "deletable": true,
      "allowLink": true,
      "shared": false,
      "strikethrough": false,
      "underline": false,
      "numberedList": false,
      "bulletedList": false,
      "enableAnimation": false,
      "enableSocial": true,
      "emphasized": false,
      "bulletSpeed": 1,
      "tag": {}
  },
  "pages": [
      {
          "type": "jadeFlowPage",
          "id": "elsa-page:nxp2u0",
          "text": "newFlowPage",
          "namespace": "jadeFlow",
          "x": -295,
          "y": 71,
          "scaleX": 1,
          "scaleY": 1,
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
          "backColor": "#fbfbfc",
          "fontSize": 18,
          "fontFace": "arial",
          "fontColor": "#ECD0A7",
          "fontWeight": "bold",
          "fontStyle": "normal",
          "hAlign": "left",
          "vAlign": "top",
          "moveable": true,
          "container": "elsa-page:nxp2u0",
          "focusBackColor": "#fbfbfc",
          "dirty": false,
          "isPage": true,
          "mode": "configuration",
          "index": 0,
          "shapes": [
              {
                  "type": "startNodeStart",
                  "container": "elsa-page:nxp2u0",
                  "id": "b0dl77",
                  "text": "开始",
                  "namespace": "flowable",
                  "x": 87,
                  "y": 73,
                  "width": 360,
                  "height": 576,
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
                  "borderColor": "rgba(28,31,35,.08)",
                  "shadow": "0 2px 4px 0 rgba(0,0,0,.1)",
                  "focusShadow": "0 0 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)",
                  "borderWidth": 1,
                  "focusBorderWidth": 2,
                  "dashWidth": 0,
                  "backColor": "white",
                  "focusBackColor": "white",
                  "cornerRadius": 8,
                  "flowMeta": {
                      "triggerMode": "auto",
                      "inputParams": [
                          {
                              "id": "12671fab-56d0-46dd-b9f7-6b01c021a215",
                              "name": "input",
                              "type": "Object",
                              "from": "Expand",
                              "value": [
                                  {
                                      "id": "input_26555fd5-ec34-4a91-bd77-469c16f65dbc",
                                      "name": "Question",
                                      "type": "String",
                                      "from": "Input",
                                      "description": "",
                                      "value": ""
                                  }
                              ]
                          },
                          {
                              "id": "d81c3ee3-cd50-45c0-a9b6-28fcd65dcce6",
                              "name": "memory",
                              "type": "Object",
                              "from": "Expand",
                              "value": [
                                  {
                                      "id": "04fa76fc-86c5-40a0-ab7a-9e6052785af9",
                                      "name": "type",
                                      "type": "String",
                                      "from": "Input",
                                      "value": "ByConversationTurn"
                                  },
                                  {
                                      "id": "8eea36bd-9917-4e85-8d40-4ebf55a9e2cf",
                                      "name": "value",
                                      "type": "Integer",
                                      "from": "Input",
                                      "value": "3"
                                  }
                              ]
                          }
                      ]
                  },
                  "componentName": "startComponent",
                  "index": 103,
                  "dirty": false
              },
              {
                  "type": "retrievalNodeState",
                  "container": "elsa-page:nxp2u0",
                  "id": "sciinj",
                  "text": "普通检索",
                  "namespace": "flowable",
                  "x": 517,
                  "y": 77,
                  "width": 360,
                  "height": 481,
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
                  "borderColor": "rgba(28,31,35,.08)",
                  "shadow": "0 2px 4px 0 rgba(0,0,0,.1)",
                  "focusShadow": "0 0 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)",
                  "borderWidth": 1,
                  "focusBorderWidth": 2,
                  "dashWidth": 0,
                  "backColor": "white",
                  "focusBackColor": "white",
                  "cornerRadius": 8,
                  "flowMeta": {
                      "triggerMode": "auto",
                      "jober": {
                          "type": "general_jober",
                          "name": "",
                          "fitables": [
                              "com.huawei.fit.jober.aipp.fitable.NaiveRAGComponent"
                          ],
                          "converter": {
                              "type": "mapping_converter",
                              "entity": {
                                  "inputParams": [
                                      {
                                          "id": "query_b509ba64-90d9-4c9e-9d23-64a4e7fe7167",
                                          "name": "query",
                                          "type": "String",
                                          "from": "Reference",
                                          "referenceNode": "b0dl77",
                                          "referenceId": "input_26555fd5-ec34-4a91-bd77-469c16f65dbc",
                                          "referenceKey": "Question",
                                          "value": [
                                              "Question"
                                          ]
                                      },
                                      {
                                          "id": "knowledge_0b51bdaf-e486-4a70-ab4b-594efdad3a94",
                                          "name": "knowledge",
                                          "type": "Array",
                                          "from": "Expand",
                                          "value": [
                                              {
                                                  "id": "47382f24-b699-4c23-a7e1-96e49d2a88ad",
                                                  "type": "Object",
                                                  "from": "Expand",
                                                  "value": [
                                                      {
                                                          "id": "4b6559b0-0c12-45d8-9235-c41d5f93f759",
                                                          "name": "id",
                                                          "from": "Input",
                                                          "type": "String",
                                                          "value": ""
                                                      },
                                                      {
                                                          "id": "83b14cd9-d558-455c-90ce-a6291f12cc35",
                                                          "name": "name",
                                                          "from": "Input",
                                                          "type": "String",
                                                          "value": ""
                                                      }
                                                  ]
                                              }
                                          ]
                                      },
                                      {
                                          "id": "maximum_2aa47ff9-f41e-48b0-bb63-f6e453562e68",
                                          "name": "maximum",
                                          "type": "Integer",
                                          "from": "Input",
                                          "value": 3
                                      }
                                  ],
                                  "outputParams": [
                                      {
                                          "id": "output_b1f2b3c2-5076-4efc-8ba4-68d9b179960d",
                                          "name": "output",
                                          "type": "Object",
                                          "from": "Expand",
                                          "value": [
                                              {
                                                  "id": "783f2714-d81a-44e2-8d32-2fefba3dcf1c",
                                                  "name": "retrievalOutput",
                                                  "type": "String",
                                                  "from": "Input",
                                                  "value": "String"
                                              }
                                          ]
                                      }
                                  ]
                              }
                          }
                      }
                  },
                  "componentName": "retrievalComponent",
                  "index": 104,
                  "dirty": true
              },
              {
                  "type": "fitInvokeNodeState",
                  "container": "elsa-page:nxp2u0",
                  "id": "4pj5fp",
                  "text": "",
                  "namespace": "flowable",
                  "x": 1319,
                  "y": 138,
                  "width": 500,
                  "height": 368,
                  "bold": false,
                  "italic": false,
                  "pad": 6,
                  "rotateAble": false,
                  "triggerMode": "auto",
                  "enableAnimation": false,
                  "warningTask": 0,
                  "runningTask": 0,
                  "completedTask": 0,
                  "hideText": true,
                  "autoHeight": true,
                  "borderColor": "rgba(28,31,35,.08)",
                  "shadow": "0 2px 4px 0 rgba(0,0,0,.1)",
                  "focusShadow": "0 0 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)",
                  "borderWidth": 1,
                  "focusBorderWidth": 2,
                  "dashWidth": 0,
                  "backColor": "white",
                  "focusBackColor": "white",
                  "cornerRadius": 8,
                  "flowMeta": {
                      "triggerMode": "auto",
                      "jober": {
                          "type": "GENERICABLE_JOBER",
                          "name": "",
                          "fitables": [],
                          "converter": {
                              "type": "mapping_converter",
                              "entity": {
                                  "inputParams": [
                                      {
                                          "id": "p1_22111459-63e0-40eb-8a3b-6a5545fe3c77",
                                          "name": "p1",
                                          "type": "String",
                                          "from": "Reference",
                                          "referenceNode": "b0dl77",
                                          "referenceId": "input_26555fd5-ec34-4a91-bd77-469c16f65dbc",
                                          "referenceKey": "Question",
                                          "value": [
                                              "Question"
                                          ]
                                      },
                                      {
                                          "id": "p2_55e0a31a-251d-4876-910a-bcfe9f51c812",
                                          "name": "p2",
                                          "type": "Object",
                                          "from": "Expand",
                                          "props": [
                                              {
                                                  "id": "55ca7cb8-5652-4013-a926-f2b4df24c8cd",
                                                  "name": "testStr",
                                                  "type": "String",
                                                  "from": "Reference",
                                                  "value": [],
                                                  "referenceNode": "",
                                                  "referenceId": "",
                                                  "referenceKey": ""
                                              }
                                          ],
                                          "value": [
                                              {
                                                  "id": "b6c72d68-2fab-4484-a5a2-35786a94fa9e",
                                                  "name": "testStr",
                                                  "type": "String",
                                                  "from": "Reference",
                                                  "value": [],
                                                  "referenceNode": "",
                                                  "referenceId": "",
                                                  "referenceKey": ""
                                              }
                                          ]
                                      },
                                      {
                                          "id": "object2_3ce59e5d-c013-4e89-bbfc-c6473d089c58",
                                          "name": "object2",
                                          "type": "Object",
                                          "from": "Reference",
                                          "referenceNode": "b0dl77",
                                          "referenceId": "input_26555fd5-ec34-4a91-bd77-469c16f65dbc",
                                          "referenceKey": "Question",
                                          "props": [
                                              {
                                                  "id": "57132d8b-422c-4364-a600-a4dc18b50e47",
                                                  "name": "testStr",
                                                  "type": "String",
                                                  "from": "Reference",
                                                  "value": [],
                                                  "referenceNode": "",
                                                  "referenceId": "",
                                                  "referenceKey": ""
                                              }
                                          ],
                                          "value": [
                                              "Question"
                                          ]
                                      }
                                  ],
                                  "genericable": {
                                      "id": "genericable_c37e44fa-f7e0-4018-a80d-3b42588c4352",
                                      "name": "genericable",
                                      "type": "Object",
                                      "from": "Expand",
                                      "value": [
                                          {
                                              "id": "265ecfc0-9f5e-4387-89cb-2328307b21f9",
                                              "name": "id",
                                              "type": "String",
                                              "from": "Input",
                                              "value": ""
                                          }
                                      ]
                                  },
                                  "fitable": {
                                      "id": "fitable_4ac06170-0d82-4ef5-b90d-655772d2b110",
                                      "name": "fitable",
                                      "type": "Object",
                                      "from": "Expand",
                                      "value": [
                                          {
                                              "id": "fb8a79dd-2b6b-4e79-ad35-b169b0b447f3",
                                              "name": "id",
                                              "type": "String",
                                              "from": "Input",
                                              "value": ""
                                          }
                                      ]
                                  },
                                  "outputParams": [
                                      {
                                          "id": "output_d67dcf74-1083-49af-bcf6-569ce094cb7c",
                                          "name": "output",
                                          "type": "Object",
                                          "value": [
                                              {
                                                  "id": "b7f6c689-bde5-474b-96be-dc421598ebb2",
                                                  "name": "age",
                                                  "type": "Integer",
                                                  "value": "Integer"
                                              },
                                              {
                                                  "id": "54e036eb-74a9-4c9c-8072-dcf0a7f652c2",
                                                  "name": "height",
                                                  "type": "Integer",
                                                  "value": "Integer"
                                              }
                                          ]
                                      }
                                  ]
                              }
                          },
                          "entity": {
                              "genericable": {
                                  "id": "",
                                  "params": []
                              }
                          }
                      }
                  },
                  "componentName": "fitInvokeComponent",
                  "index": 107,
                  "dirty": true
              },
              {
                  "type": "jadeEvent",
                  "container": "elsa-page:nxp2u0",
                  "id": "iq7gi9",
                  "text": "",
                  "namespace": "flowable",
                  "borderColor": "#B1B1B7",
                  "x": 874,
                  "y": 299.5,
                  "width": 482,
                  "height": -64.5,
                  "bold": false,
                  "italic": false,
                  "pad": 0,
                  "margin": 20,
                  "backColor": "white",
                  "hideText": true,
                  "beginArrow": false,
                  "beginArrowEmpty": false,
                  "beginArrowSize": 4,
                  "endArrow": true,
                  "endArrowEmpty": false,
                  "endArrowSize": 4,
                  "textX": 0,
                  "textY": 0,
                  "hAlign": "center",
                  "lineWidth": 2,
                  "fromShape": "sciinj",
                  "toShape": "4pj5fp",
                  "definedFromConnector": "E",
                  "definedToConnector": "W",
                  "arrowBeginPoint": {
                      "x": 0,
                      "y": 0,
                      "direction": {
                          "cursor": "ew-resize",
                          "key": "E",
                          "color": "whitesmoke",
                          "ax": "x",
                          "vector": 1,
                          "value": "E"
                      }
                  },
                  "arrowEndPoint": {
                      "x": 478,
                      "y": -64.5,
                      "direction": {
                          "cursor": "ew-resize",
                          "key": "W",
                          "color": "whitesmoke",
                          "ax": "x",
                          "vector": -1,
                          "value": "W"
                      }
                  },
                  "curvePoint1": {"x": 0, "y": 0},
                  "curvePoint2": {"x": 0, "y": 0},
                  "brokenPoints": [{"x": 241, "y": 0}, {"x": 241, "y": -64.5}],
                  "lineMode": {"type": "auto_curve"},
                  "allowLink": false,
                  "borderWidth": 1,
                  "index": 108,
                  "dirty": true
              },
              {
                  "type": "llmNodeState",
                  "container": "elsa-page:nxp2u0",
                  "id": "4agtld",
                  "text": "大模型",
                  "namespace": "flowable",
                  "x": 951,
                  "y": -21,
                  "width": 360,
                  "height": 877,
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
                  "borderColor": "rgba(28,31,35,.08)",
                  "shadow": "0 2px 4px 0 rgba(0,0,0,.1)",
                  "focusShadow": "0 0 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)",
                  "borderWidth": 1,
                  "focusBorderWidth": 2,
                  "dashWidth": 0,
                  "backColor": "white",
                  "focusBackColor": "white",
                  "cornerRadius": 8,
                  "flowMeta": {
                      "triggerMode": "auto",
                      "jober": {
                          "type": "general_jober",
                          "name": "",
                          "fitables": [
                              "com.huawei.fit.jober.aipp.fitable.LLMComponent"
                          ],
                          "converter": {
                              "type": "mapping_converter",
                              "entity": {
                                  "inputParams": [
                                      {
                                          "id": "ffe17733-08be-4297-b429-b07fa4cb0696",
                                          "name": "model",
                                          "type": "String",
                                          "from": "Input",
                                          "value": "Qwen-72B"
                                      },
                                      {
                                          "id": "3cffb1f4-20c3-487a-aa12-0c8ccaeba786",
                                          "name": "temperature",
                                          "type": "Number",
                                          "from": "Input",
                                          "value": "0.3"
                                      },
                                      {
                                          "id": "6c7489e9-7410-4e47-bd0f-ed6b1575460b",
                                          "name": "prompt",
                                          "type": "Object",
                                          "from": "Expand",
                                          "value": [
                                              {
                                                  "id": "63602287-b10e-418e-8ead-0f576da094bc",
                                                  "name": "template",
                                                  "type": "String",
                                                  "from": "Input",
                                                  "value": "用户的问题是{{query}}，相关的资料是{{knowledge}}"
                                              },
                                              {
                                                  "id": "d0f61c0b-cbdd-4e76-a43e-45dd74123ffa",
                                                  "name": "variables",
                                                  "type": "Object",
                                                  "from": "Expand",
                                                  "value": [
                                                      {
                                                          "id": "b68cba72-7de3-43e9-a5a0-992e144c1ff6",
                                                          "name": "knowledge",
                                                          "type": "String",
                                                          "from": "Reference",
                                                          "value": [
                                                              "output",
                                                              "retrievalOutput"
                                                          ],
                                                          "referenceNode": "sciinj",
                                                          "referenceId": "783f2714-d81a-44e2-8d32-2fefba3dcf1c",
                                                          "referenceKey": "retrievalOutput"
                                                      },
                                                      {
                                                          "id": "ea05cd7c-6801-4127-b88d-0c9bd7e76dda",
                                                          "name": "query",
                                                          "type": "String",
                                                          "from": "Reference",
                                                          "value": [
                                                              "Question"
                                                          ],
                                                          "referenceKey": "Question",
                                                          "referenceNode": "b0dl77",
                                                          "referenceId": "input_26555fd5-ec34-4a91-bd77-469c16f65dbc"
                                                      }
                                                  ]
                                              }
                                          ]
                                      },
                                      {
                                          "id": "90d51691-7858-46da-a3d2-b528f6b89059",
                                          "name": "tools",
                                          "type": "Array",
                                          "from": "Input",
                                          "value": []
                                      },
                                      {
                                          "id": "cdcd3ec9-a4dc-4b22-9cd4-dcd40bf60917",
                                          "name": "workflows",
                                          "type": "Array",
                                          "from": "Input",
                                          "value": []
                                      },
                                      {
                                          "id": "90d69169-7585-46ab-a3d2-b528f6b89333",
                                          "name": "systemPrompt",
                                          "type": "String",
                                          "from": "Input",
                                          "value": ""
                                      }
                                  ],
                                  "outputParams": [
                                      {
                                          "id": "fface40b-e0d6-4d4d-a0e8-35c77af3df7e",
                                          "name": "output",
                                          "type": "Object",
                                          "from": "Expand",
                                          "value": [
                                              {
                                                  "id": "51020cba-2d8e-4962-b39e-8f542856a8d7",
                                                  "name": "llmOutput",
                                                  "type": "string",
                                                  "from": "Input",
                                                  "description": "",
                                                  "value": ""
                                              }
                                          ]
                                      }
                                  ]
                              }
                          },
                          "isAsync": "true"
                      }
                  },
                  "componentName": "llmComponent",
                  "index": 105,
                  "dirty": true
              },
              {
                  "type": "endNodeEnd",
                  "container": "elsa-page:nxp2u0",
                  "id": "7123np",
                  "text": "结束",
                  "namespace": "flowable",
                  "x": 1402,
                  "y": 234,
                  "width": 360,
                  "height": 216,
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
                  "borderColor": "rgba(28,31,35,.08)",
                  "shadow": "0 2px 4px 0 rgba(0,0,0,.1)",
                  "focusShadow": "0 0 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)",
                  "borderWidth": 1,
                  "focusBorderWidth": 2,
                  "dashWidth": 0,
                  "backColor": "white",
                  "focusBackColor": "white",
                  "cornerRadius": 8,
                  "flowMeta": {
                      "triggerMode": "auto",
                      "callback": {
                          "type": "general_callback",
                          "name": "通知回调",
                          "fitables": [
                              "com.huawei.fit.jober.aipp.fitable.AippFlowEndCallback"
                          ],
                          "converter": {
                              "type": "mapping_converter",
                              "entity": {
                                  "inputParams": [
                                      {
                                          "id": "1342fea6-aa8a-4176-857c-c5309bd0f912",
                                          "name": "finalOutput",
                                          "type": "String",
                                          "from": "Reference",
                                          "referenceNode": "4agtld",
                                          "referenceId": "51020cba-2d8e-4962-b39e-8f542856a8d7",
                                          "referenceKey": "llmOutput",
                                          "value": [
                                              "output",
                                              "llmOutput"
                                          ]
                                      }
                                  ],
                                  "outputParams": [
                                      {}
                                  ]
                              }
                          }
                      }
                  },
                  "componentName": "endComponent",
                  "index": 106,
                  "dirty": true
              },
              {
                  "type": "jadeEvent",
                  "container": "elsa-page:nxp2u0",
                  "id": "hecodm",
                  "text": "",
                  "namespace": "flowable",
                  "x": 447,
                  "y": 361,
                  "borderColor": "#B1B1B7",
                  "width": 70,
                  "height": -43.5,
                  "bold": false,
                  "italic": false,
                  "pad": 0,
                  "margin": 20,
                  "backColor": "white",
                  "hideText": true,
                  "beginArrow": false,
                  "beginArrowEmpty": false,
                  "beginArrowSize": 4,
                  "endArrow": true,
                  "endArrowEmpty": false,
                  "endArrowSize": 4,
                  "textX": 0,
                  "textY": 0,
                  "hAlign": "center",
                  "lineWidth": 2,
                  "fromShape": "b0dl77",
                  "toShape": "sciinj",
                  "definedFromConnector": "E",
                  "definedToConnector": "W",
                  "arrowBeginPoint": {
                      "x": 0,
                      "y": 0,
                      "direction": {
                          "cursor": "ew-resize",
                          "key": "E",
                          "color": "whitesmoke",
                          "ax": "x",
                          "vector": 1,
                          "value": "E"
                      }
                  },
                  "arrowEndPoint": {
                      "x": 96,
                      "y": 80,
                      "direction": {
                          "cursor": "ew-resize",
                          "key": "W",
                          "color": "whitesmoke",
                          "ax": "x",
                          "vector": -1,
                          "value": "W"
                      }
                  },
                  "curvePoint1": {
                      "x": 0,
                      "y": 0
                  },
                  "curvePoint2": {
                      "x": 0,
                      "y": 0
                  },
                  "brokenPoints": [
                      {
                          "x": 50,
                          "y": 0
                      },
                      {
                          "x": 50,
                          "y": 80
                      }
                  ],
                  "lineMode": {
                      "type": "auto_curve"
                  },
                  "allowLink": false,
                  "borderWidth": 1,
                  "index": -96,
                  "dirty": true
              },
              {
                  "type": "jadeEvent",
                  "container": "elsa-page:nxp2u0",
                  "id": "yt69ai",
                  "text": "",
                  "namespace": "flowable",
                  "x": 877,
                  "y": 317.5,
                  "borderColor": "#B1B1B7",
                  "width": 74,
                  "height": 100,
                  "bold": false,
                  "italic": false,
                  "pad": 0,
                  "margin": 20,
                  "backColor": "white",
                  "hideText": true,
                  "beginArrow": false,
                  "beginArrowEmpty": false,
                  "beginArrowSize": 4,
                  "endArrow": true,
                  "endArrowEmpty": false,
                  "endArrowSize": 4,
                  "textX": 0,
                  "textY": 0,
                  "hAlign": "center",
                  "lineWidth": 2,
                  "fromShape": "sciinj",
                  "toShape": "4agtld",
                  "definedFromConnector": "E",
                  "definedToConnector": "W",
                  "arrowBeginPoint": {
                      "x": 0,
                      "y": 0,
                      "direction": {
                          "cursor": "ew-resize",
                          "key": "E",
                          "color": "whitesmoke",
                          "ax": "x",
                          "vector": 1,
                          "value": "E"
                      }
                  },
                  "arrowEndPoint": {
                      "x": 96,
                      "y": 80,
                      "direction": {
                          "cursor": "ew-resize",
                          "key": "W",
                          "color": "whitesmoke",
                          "ax": "x",
                          "vector": -1,
                          "value": "W"
                      }
                  },
                  "curvePoint1": {
                      "x": 0,
                      "y": 0
                  },
                  "curvePoint2": {
                      "x": 0,
                      "y": 0
                  },
                  "brokenPoints": [
                      {
                          "x": 50,
                          "y": 0
                      },
                      {
                          "x": 50,
                          "y": 80
                      }
                  ],
                  "lineMode": {
                      "type": "auto_curve"
                  },
                  "allowLink": false,
                  "borderWidth": 1,
                  "index": -95,
                  "dirty": true
              },
              {
                  "type": "jadeEvent",
                  "container": "elsa-page:nxp2u0",
                  "id": "udc2mr",
                  "text": "",
                  "namespace": "flowable",
                  "x": 1311,
                  "y": 417.5,
                  "borderColor": "#B1B1B7",
                  "width": 91,
                  "height": -75.5,
                  "bold": false,
                  "italic": false,
                  "pad": 0,
                  "margin": 20,
                  "backColor": "white",
                  "hideText": true,
                  "beginArrow": false,
                  "beginArrowEmpty": false,
                  "beginArrowSize": 4,
                  "endArrow": true,
                  "endArrowEmpty": false,
                  "endArrowSize": 4,
                  "textX": 0,
                  "textY": 0,
                  "hAlign": "center",
                  "lineWidth": 2,
                  "fromShape": "4agtld",
                  "toShape": "7123np",
                  "definedFromConnector": "E",
                  "definedToConnector": "W",
                  "arrowBeginPoint": {
                      "x": 0,
                      "y": 0,
                      "direction": {
                          "cursor": "ew-resize",
                          "key": "E",
                          "color": "whitesmoke",
                          "ax": "x",
                          "vector": 1,
                          "value": "E"
                      }
                  },
                  "arrowEndPoint": {
                      "x": 96,
                      "y": 80,
                      "direction": {
                          "cursor": "ew-resize",
                          "key": "W",
                          "color": "whitesmoke",
                          "ax": "x",
                          "vector": -1,
                          "value": "W"
                      }
                  },
                  "curvePoint1": {
                      "x": 0,
                      "y": 0
                  },
                  "curvePoint2": {
                      "x": 0,
                      "y": 0
                  },
                  "brokenPoints": [
                      {
                          "x": 50,
                          "y": 0
                      },
                      {
                          "x": 50,
                          "y": 80
                      }
                  ],
                  "lineMode": {
                      "type": "auto_curve"
                  },
                  "allowLink": false,
                  "borderWidth": 1,
                  "index": -94,
                  "dirty": true
              }
          ]
      }
  ]
}