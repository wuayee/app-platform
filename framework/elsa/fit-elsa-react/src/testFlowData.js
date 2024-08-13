export const graphData = {
    "id": "ee1d1c6d8f314fa090b5a3afe8adaf40",
    "title": "jadeFlow",
    "source": "elsa",
    "type": "jadeFlowGraph",
    "tenant": "default",
    "flowMeta": {
        "exceptionFitables": ["com.huawei.fit.jober.aipp.fitable.AippFlowExceptionHandler"]
    },
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
        "outlineWidth": 10,
        "outlineColor": 'rgba(74,147,255,0.12)',
        "globalAlpha": 1,
        "backAlpha": 0.15,
        "cornerRadius": 4,
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
            "id": "elsa-page:tvp1s6",
            "text": "newFlowPage",
            "namespace": "jadeFlow",
            "x": 297.46031746031747,
            "y": 121.01190476190482,
            "width": 1600,
            "height": 800,
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
            "container": "elsa-page:tvp1s6",
            "scaleX": 0.8,
            "scaleY": 0.8,
            "focusBackColor": "#fbfbfc",
            "dirty": true,
            "isPage": true,
            "mode": "configuration",
            "shapes": [
                {
                    "type": "startNodeStart",
                    "container": "elsa-page:tvp1s6",
                    "id": "jade6qm5eg",
                    "text": "开始",
                    "namespace": "flowable",
                    "x": -170.8928571428571,
                    "y": 32.5,
                    "width": 360,
                    "height": 554,
                    "bold": false,
                    "italic": false,
                    "deletable": false,
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
                    "mouseInBorderColor": "rgba(28,31,35,.08)",
                    "shadow": "0 2px 4px 0 rgba(0,0,0,.1)",
                    "focusShadow": "0 0 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)",
                    "borderWidth": 1,
                    "outlineWidth": 10,
                    "outlineColor": 'rgba(74,147,255,0.12)',
                    "dashWidth": 0,
                    "backColor": "white",
                    "focusBackColor": "white",
                    "cornerRadius": 8,
                    "flowMeta": {
                        "triggerMode": "auto",
                        "inputParams": [
                            {
                                "id": "91138f09-b635-43df-95c6-1fe3d1745829",
                                "name": "input",
                                "type": "Object",
                                "from": "Expand",
                                "config": [{"allowAdd":true}],
                                "value": [
                                    {
                                        "id": "input_ae2ffd6e-2b9e-4e73-9d7f-0e661ec3dbdb",
                                        "name": "Question",
                                        "type": "String",
                                        "from": "Input",
                                        "description": "这是用户输入的问题",
                                        "value": "",
                                        "disableModifiable": true,
                                    }
                                ]
                            },
                            {
                                "id": "4a770dc6-e3c9-475d-84c7-48dacc74a5b6",
                                "name": "memory",
                                "type": "Object",
                                "from": "Expand",
                                "value": [
                                    {
                                        id: "cee9a31b-781c-4835-a616-ceed73be22a7",
                                        name: "memorySwitch",
                                        type: "Boolean",
                                        from: "Input",
                                        value: true
                                    },
                                    {
                                        "id": "cee9a31b-781c-4835-a616-ceed73be22f2",
                                        "name": "type",
                                        "type": "String",
                                        "from": "Input",
                                        "value": "ByConversationTurn"
                                    },
                                    {
                                        "id": "69592622-4291-409d-9d65-9faea83db657",
                                        "name": "value",
                                        "type": "Integer",
                                        "from": "Input",
                                        "value": "3"
                                    }
                                ]
                            }
                        ]
                    },
                    "sourcePlatform": "official",
                    "componentName": "startComponent",
                    "index": 100,
                    "dirty": true,
                    "runnable": true
                },
                {
                    "type": "retrievalNodeState",
                    "container": "elsa-page:tvp1s6",
                    "id": "jade0pg2ag",
                    "text": "普通检索",
                    "namespace": "flowable",
                    "x": 275.7499999999999,
                    "y": 192.3571428571428,
                    "width": 360,
                    "height": 459,
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
                    "mouseInBorderColor": "rgba(28,31,35,.08)",
                    "shadow": "0 2px 4px 0 rgba(0,0,0,.1)",
                    "focusShadow": "0 0 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)",
                    "borderWidth": 1,
                    "outlineWidth": 10,
                    "outlineColor": 'rgba(74,147,255,0.12)',
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
                                            "id": "query_0ab55575-f21d-4b19-9676-57fcb4b0b783",
                                            "name": "query",
                                            "type": "String",
                                            "from": "Reference",
                                            "referenceNode": "jade6qm5eg",
                                            "referenceId": "input_ae2ffd6e-2b9e-4e73-9d7f-0e661ec3dbdb",
                                            "referenceKey": "Question",
                                            "value": [
                                                "Question"
                                            ]
                                        },
                                        {
                                            "id": "knowledge_01c41edd-a22b-4289-b1cf-8db835833261",
                                            "name": "knowledge",
                                            "type": "Array",
                                            "from": "Expand",
                                            "value": [
                                                {
                                                    "id": "55f8e6eb-dab5-435f-94ea-18108eaba982",
                                                    "type": "Object",
                                                    "from": "Expand",
                                                    "value": []
                                                }
                                            ]
                                        },
                                        {
                                            "id": "maximum_2da115cd-c1ce-485f-ba98-b4c995f3d6ff",
                                            "name": "maximum",
                                            "type": "Integer",
                                            "from": "Input",
                                            "value": 3
                                        }
                                    ],
                                    "outputParams": [
                                        {
                                            "id": "output_cd5cbe89-0d9f-4cf1-9e09-afb325576b84",
                                            "name": "output",
                                            "type": "Object",
                                            "from": "Expand",
                                            "value": [
                                                {
                                                    "id": "5c9c6535-c127-445a-862a-966cf1083929",
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
                        },
                        "joberFilter": {
                            "type": "MINIMUM_SIZE_FILTER",
                            "threshold": 1
                        }
                    },
                    "sourcePlatform": "official",
                    "componentName": "retrievalComponent",
                    "index": 101,
                    "dirty": true,
                    "runnable": true
                },
                {
                    "type": "llmNodeState",
                    "container": "elsa-page:tvp1s6",
                    "id": "jadewdnjbq",
                    "text": "大模型",
                    "namespace": "flowable",
                    "x": 719.1428571428571,
                    "y": -53.21428571428578,
                    "width": 360,
                    "height": 787,
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
                    "mouseInBorderColor": "rgba(28,31,35,.08)",
                    "shadow": "0 2px 4px 0 rgba(0,0,0,.1)",
                    "focusShadow": "0 0 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)",
                    "borderWidth": 1,
                    "outlineWidth": 10,
                    "outlineColor": 'rgba(74,147,255,0.12)',
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
                                            "id": "6c414e75-971e-403a-b2b1-c6850f128cc4",
                                            "name": "model",
                                            "type": "String",
                                            "from": "Input",
                                            "value": "Qwen1.5-32B-Chat"
                                        },
                                        {
                                            "id": "db5fdafa-4cbf-44ba-9cca-8a98f1f771f4",
                                            "name": "temperature",
                                            "type": "Number",
                                            "from": "Input",
                                            "value": "0.3"
                                        },
                                        {
                                            "id": "88f74d78-4711-4f81-a2e7-74d0034c5e88",
                                            "name": "prompt",
                                            "type": "Object",
                                            "from": "Expand",
                                            "value": [
                                                {
                                                    "id": "35a710cf-1b79-4523-b16f-b50878d677fe",
                                                    "name": "template",
                                                    "type": "String",
                                                    "from": "Input",
                                                    "value": "请按照以下步骤生成您的回复：\n1. 递归地将问题分解为更小的问题。\n2. 对于每个原子问题，从上下文和对话历史记录中选择最相关的信息。\n3. 使用所选信息生成回复草稿。\n4. 删除回复草稿中的重复内容。\n5. 在调整后生成最终答案，以提高准确性和相关性。\n6. 请注意，只需要回复最终答案。\n-------------------------------------\n上下文信息：\n\n{{knowledge}}\n\n问题：{{query}}"
                                                },
                                                {
                                                    "id": "38fb27a1-71f4-4fcc-87d5-9d8a880bc04d",
                                                    "name": "variables",
                                                    "type": "Object",
                                                    "from": "Expand",
                                                    "value": [
                                                        {
                                                            "id": "aeba7823-8d14-4750-9723-55265ae71c4e",
                                                            "name": "knowledge",
                                                            "type": "String",
                                                            "from": "Reference",
                                                            "value": [
                                                                "output",
                                                                "retrievalOutput"
                                                            ],
                                                            "referenceNode": "jade0pg2ag",
                                                            "referenceId": "5c9c6535-c127-445a-862a-966cf1083929",
                                                            "referenceKey": "retrievalOutput"
                                                        },
                                                        {
                                                            "id": "eee66922-4304-4209-89fc-b13ffa101630",
                                                            "name": "query",
                                                            "type": "String",
                                                            "from": "Reference",
                                                            "value": [
                                                                "Question"
                                                            ],
                                                            "referenceKey": "Question",
                                                            "referenceNode": "jade6qm5eg",
                                                            "referenceId": "input_ae2ffd6e-2b9e-4e73-9d7f-0e661ec3dbdb"
                                                        }
                                                    ]
                                                }
                                            ]
                                        },
                                        {
                                            "id": "a6865419-867c-4bfb-855c-f5c1876c965a",
                                            "name": "tools",
                                            "type": "Array",
                                            "from": "Input",
                                            "value": []
                                        },
                                        {
                                            "id": "308e2023-a8e9-486e-9784-8680addbb786",
                                            "name": "workflows",
                                            "type": "Array",
                                            "from": "Input",
                                            "value": []
                                        },
                                        {
                                            "id": "68f92923-d5da-42ce-8478-d7ac7d90664e",
                                            "name": "systemPrompt",
                                            "type": "String",
                                            "from": "Input",
                                            "value": ""
                                        }
                                    ],
                                    "outputParams": [
                                        {
                                            "id": "95d84d67-3198-415e-a63c-bc9a2da8d821",
                                            "name": "output",
                                            "type": "Object",
                                            "from": "Expand",
                                            "value": [
                                                {
                                                    "id": "272c927a-9e25-48b6-a921-6a8ab20267a4",
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
                        },
                        "joberFilter": {
                            "type": "MINIMUM_SIZE_FILTER",
                            "threshold": 1
                        }
                    },
                    "sourcePlatform": "official",
                    "componentName": "llmComponent",
                    "index": 102,
                    "dirty": true,
                    "runnable": true
                },
                {
                    "type": "endNodeEnd",
                    "container": "elsa-page:tvp1s6",
                    "id": "jadesoux5i",
                    "text": "结束",
                    "namespace": "flowable",
                    "x": 1169.4642857142849,
                    "y": 306.4285714285713,
                    "width": 360,
                    "height": 212,
                    "bold": false,
                    "italic": false,
                    "deletable": true,
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
                    "mouseInBorderColor": "rgba(28,31,35,.08)",
                    "shadow": "0 2px 4px 0 rgba(0,0,0,.1)",
                    "focusShadow": "0 0 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)",
                    "borderWidth": 1,
                    "outlineWidth": 10,
                    "outlineColor": 'rgba(74,147,255,0.12)',
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
                                            "id": "ffad80c2-3f60-4d57-93b2-c2362a5dab9c",
                                            "name": "finalOutput",
                                            "type": "String",
                                            "from": "Reference",
                                            "referenceNode": "jadewdnjbq",
                                            "referenceId": "272c927a-9e25-48b6-a921-6a8ab20267a4",
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
                    "sourcePlatform": "official",
                    "componentName": "endComponent",
                    "index": 104,
                    "dirty": true,
                    "runnable": true
                },
                {
                    "type": "jadeEvent",
                    "container": "elsa-page:tvp1s6",
                    "id": "jade2zanyx",
                    "text": "",
                    "namespace": "flowable",
                    "x": 189.1071428571429,
                    "y": 309.5,
                    "width": 86.642857142857,
                    "height": 112.35714285714278,
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
                    "fromShape": "jade6qm5eg",
                    "toShape": "jade0pg2ag",
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
                    "allowSwitchLineMode": false,
                    "allowLink": false,
                    "borderWidth": 1,
                    "borderColor": "#B1B1B7",
                    "mouseInBorderColor": "#B1B1B7",
                    "index": -95,
                    "dirty": true,
                    "runnable": true
                },
                {
                    "type": "jadeEvent",
                    "container": "elsa-page:tvp1s6",
                    "id": "jade5c5urs",
                    "text": "",
                    "namespace": "flowable",
                    "x": 635.7499999999999,
                    "y": 421.8571428571428,
                    "width": 83.39285714285722,
                    "height": -81.57142857142856,
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
                    "fromShape": "jade0pg2ag",
                    "toShape": "jadewdnjbq",
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
                    "allowSwitchLineMode": false,
                    "allowLink": false,
                    "borderWidth": 1,
                    "borderColor": "#B1B1B7",
                    "mouseInBorderColor": "#B1B1B7",
                    "index": -94,
                    "dirty": true,
                    "runnable": true
                },
                {
                    "type": "jadeEvent",
                    "container": "elsa-page:tvp1s6",
                    "id": "jade1p0cdu",
                    "text": "",
                    "namespace": "flowable",
                    "x": 1079.142857142857,
                    "y": 340.2857142857142,
                    "width": 90.32142857142776,
                    "height": 72.14285714285705,
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
                    "fromShape": "jadewdnjbq",
                    "toShape": "jadesoux5i",
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
                    "allowSwitchLineMode": false,
                    "allowLink": false,
                    "borderWidth": 1,
                    "borderColor": "#B1B1B7",
                    "mouseInBorderColor": "#B1B1B7",
                    "index": -94,
                    "dirty": true,
                    "runnable": true
                }
            ],
            "index": 0
        }
    ],
    "enableText": false
}