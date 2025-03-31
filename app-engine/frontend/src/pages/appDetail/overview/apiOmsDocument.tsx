/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { useParams } from 'react-router-dom';
import { toClipboard } from '@/shared/utils/common';
import { Drawer } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { markedProcess } from '@/pages/chatPreview/utils/marked-process';
import './styles/apiOms.scss';
import copyCodeIcon from '@/assets/images/ai/copy_code.png';

/**
 * API文档
 *
 * @param drawerOpen 抽屉是否打开.
 * @param url apiUrl.
 * @param setDrawerOpen 抽屉是否打开的方法.
 * @return {JSX.Element}
 * @constructor
 */
const DocumentDrawer = ({ drawerOpen, url, setDrawerOpen }) => {
  const { t } = useTranslation();
  const { appId } = useParams();
  const [documentContent, setCocumentContent] = useState([]);
  const [currentNav, setCurrentNav] = useState('');
  const documentRef = useRef<any>(null);

  // 转换代码格式
  const tranlateCode = (content) => {
    const regex = /<code>(.*?)<\/code>/g;
    return content.replace(regex, `<div class='inner-code'>
      <div class='inner-code-title'>Code</div>
      <div class='inner-code-content'>$1</div>
    </div>`);
  };

  // 展示当前appId
  const displayAppId = (content) => {
    const regex = /<placeholder>(.*?)<\/placeholder>/g;
    return content.replace(regex, `<span>${appId}</span>`);
  };


  // 获取请求方法代码
  const getMethodCode = (codeContent, index) => {
    return <div className='inner-code' key={index}>
      <div className='inner-code-title'>{codeContent.title}</div>
      {codeContent.description && getHighLightMethod(codeContent.description)}
      <div className='inner-code-content' dangerouslySetInnerHTML={{ __html: codeContent.code }}></div>
    </div>
  };

  // 高亮请求方法
  const getHighLightMethod = (content) => {
    const index = content.indexOf(' ');
    return <div className='inner-code-menthod'>
      <span style={{ color: '#40acff', fontWeight: 700 }}>{content.substring(0, index)}</span>
      <span>{content.substring(index)}</span>
    </div>
  };

  // 获取错误码
  const getErrorContent = (errArr) => {
    return errArr.map((err, index) =>
      <div className='error-item' key={index}>
        <div className='circle-item'></div>
        <div dangerouslySetInnerHTML={{ __html: markedProcess(err.field) }}></div>
        <div>&nbsp;&nbsp;，</div>
        <div dangerouslySetInnerHTML={{ __html: markedProcess(err.description) }}></div>
      </div>
    )
  };

  // 获取请求参数的内容
  const getParamsContent = (params, type = 'param', hierarchy = 1) => {
    return params.map((param, index) => <div key={index} className={`${hierarchy > 1 ? 'meta-item' : 'param-item'}`}>
      {param.title ? <div className='body-title'>{param.title}</div> : <div>
        {
          (param.field || param.type) &&
          <div className='param-top'>
            {type === 'meta' && <div className='circle-item'></div>}
            {param.field && <span className={`${type === 'meta' ? 'meta' : ''}`} dangerouslySetInnerHTML={{ __html: markedProcess(param.field) }}></span>}
            {param.type && <span dangerouslySetInnerHTML={{ __html: markedProcess(param.type) }}></span>}
          </div>
        }
        {param.description && <div className='param-content' dangerouslySetInnerHTML={{ __html: markedProcess(displayAppId(param.description)) }}></div>}
      </div>
      }
      {param.children?.length > 0 && <div style={{ padding: hierarchy > 1 ? '0 30px' : '0 16px' }}>{getParamsContent(param.children, 'meta', hierarchy + 1)}</div>}
    </div>
    )
  };

  // 获取文档内容
  const getDocument = async () => {
    setCocumentContent([
      {
        "id": 'header',
        "title": "鉴权",
        "content": "Service API 使用 `API-Key` 进行鉴权。 ***强烈建议开发者把 `API-Key` 放在后端存储，而非分享或者放在客户端存储，以免 `API-Key` 泄露，导致财产损失。*** 所有 `API` 请求都应在 `Authorization` HTTP Header 中包含您的 `API-Key`。如下所示：\n<code>Authorization: Bearer {API_KEY}</code>"
      },
      {
        "id": 'first',
        "method": "POST",
        "url": "<font color=#d0cdcd>/app_chat</font>",
        "title": "发送对话消息",
        "content": "该接口向大模型发送一个问题信息，并开启一个对话。",
        "children": [
          {
            "title": "WebSocket调用",
            "content": "支持<span id=jump-wss-oms>Websocket调用</a>。"
          },
          {
            "title": "Request Body",
            "children": [
              {
                "field": "`app_id`",
                "type": "<font color=#d0cdcd>string</font>",
                "description": "用于对话的 app 的 id。\n当前应用 app_id: <placeholder>app_id</placeholder>。"
              },
              {
                "field": "`chat_id`",
                "type": "<font color=#d0cdcd>string</font>",
                "description": "<font color=#d0cdcd>选填</font> 当前对话的 id，在第一次对话后生成，后续可用该 id 连续对话。"
              },
              {
                "field": "`question`",
                "type": "<font color=#d0cdcd>string</font>",
                "description": "向大模型询问的问题。"
              },
              {
                "field": "`context`",
                "type": "<font color=#d0cdcd>object</font>",
                "description": "初始化上下文。",
                "children": [
                  {
                    "field": "`use_memory`",
                    "type": "<font color=#d0cdcd>bool</font>",
                    "description": "对话时是否使用历史对话记录，开启后历史对话的数据会影响到大模型的回答。"
                  },
                  {
                    "field": "`user_context`",
                    "type": "<font color=#d0cdcd>object</font>",
                    "description": "用户自定义输入，可以为空。"
                  },
                  {
                    "field": "`at_app_id`",
                    "type": "<font color=#d0cdcd>string</font>",
                    "description": "引用其他应用。"
                  },
                  {
                    "field": "`at_chat_id`",
                    "type": "<font color=#d0cdcd>string</font>",
                    "description": "引用其他应用对话。"
                  }
                ]
              }
            ]
          },
          {
            "title": "Response",
            "content": "基于SSE(Service-Send Events)实现的流式响应。响应的 HTTPS 头的 `Content-Type` 为 `text/event-stream`。每个流式块都以 `data:` 开头，块之间以 `\\n\\n`，也就是两个换行符进行分隔。\n<code>data:{\"status\":\"ARCHIVED\",\"answer\":[{\"content\":\"你好！很高兴为你提供帮助。请问有什么问题我可以解答吗？\",\"type\":\"MSG\",\"msgId\":null}],\"chat_id\":\"5f2a914bae594e229b1cf29121950b5d\",\"at_chat_id\":null,\"instance_id\":\"76ef4f0a5e184633924d9b792b65761b\",\"log_id\":\"70\"}\\n\\n</code>\n流式块中 `data:` 字段后的内容为序列化后的对话响应结构 `AppChatRsp`。",
            "children": [
              {
                "title": "AppChatRsp",
                "children": [
                  {
                    "field": "`status`",
                    "type": "<font color=#d0cdcd>string</font>",
                    "description": "表示大模型响应的状态，响应的状态可以是`READY`，`RUNNING` 和 `ARCHIVED` 三种状态之一。`READY` 状态表示大模型接收到对话问题；`RUNNING` 状态表示对话正在进行的状态，此状态下会流式地返回对话模型的输出结果；`ARCHIVED` 状态表示流式响应结束，返回的数据中会包含完整的大模型输出。"
                  },
                  {
                    "field": "`answer`",
                    "type": "<font color=#d0cdcd>list<object></font>",
                    "description": "大模型的回答。",
                    "children": [
                      {
                        "field": "`content`",
                        "type": "<font color=#d0cdcd>object</font>",
                        "description": "表示对话大模型响应结果的对象，其内容与 `type` 字段的值相关。`app_chat` 流式响应返回的第一个流式数据块中，`content` 表示输入的问题的定义，除此以外，`content` 表示大模型的输出。"
                      },
                      {
                        "field": "`type`",
                        "type": "<font color=#d0cdcd>string</font>",
                        "description": "表示响应的流式块中 `answer` 字段的 `content` 的类型。`type` 的值为 `QUESTION` 或者 `MSG`，分别表示提问和大模型的回答。"
                      },
                      {
                        "field": "`msgId`",
                        "type": "<font color=#d0cdcd>string</font>",
                        "description": "表示大模型响应消息的 id。"
                      }
                    ]
                  },
                  {
                    "field": "`chat_id`",
                    "type": "<font color=#d0cdcd>string</font>",
                    "description": "表示当前对话轮的 id。带有相同 `chat_id` 的问答都属于同一对话轮次，同一对话轮次内先进行的问答会成为之后问答的历史，在开启了 `use_memory` 后，会对后续大模型的回答产生影响。"
                  },
                  {
                    "field": "`at_chat_id`",
                    "type": "<font color=#d0cdcd>string</font>",
                    "description": "表示引用的对话轮次的 id。"
                  },
                  {
                    "field": "`instance_id`",
                    "type": "<font color=#d0cdcd>string</font>",
                    "description": "表示当前问答实例的 id。一个 `instance_id` 通常会对应一问一答。"
                  },
                  {
                    "field": "`logId`",
                    "type": "<font color=#d0cdcd>string</font>",
                    "description": "表示当前问答记录的 id，用户提问和大模型的回答都会对应唯一的、递增的 `logId`。"
                  }
                ]
              }
            ]
          },
          {
            "title": "Errors",
            "isError": true,
            "children": [
              {
                "field": "`90000000`",
                "description": "入参不合法。比如输入问题过长或者为空。"
              },
              {
                "field": "`90000002`",
                "description": "服务器内部错误。"
              },
              {
                "field": "`90002918`",
                "description": "会话请求结构有误。"
              },
              {
                "field": "`90002919`",
                "description": "该应用未发布，无法进行对话。"
              },
              {
                "field": "`90002922`",
                "description": "会话响应失败。"
              },
              {
                "field": "`90002925`",
                "description": "对话失败：应用不存在，或者已经被删除。"
              },
              {
                "field": "`90002927`",
                "description": "未输入问题。"
              }
            ]
          }
        ],
        "codes": [
          {
            "title": "Request",
            "description": "POST · /app_chat",
            "code": "<code>curl --location 'https://{base_url}/app_chat' \\\n--header 'Content-Type: application/json' \\\n--header 'Authorization: Bearer {api_key}' \\\n--data '{\n    \"app_id\": \"8dbe244d7e2c4d49ae6a4ff582ea3924\",\n    \"question\": \"你好\",\n    \"context\": {\n        \"use_memory\": true,\n        \"user_context\": {}\n    }\n}'</code>"
          },
          {
            "title": "Response",
            "code": "<code>{\n    \"status\": \"RUNNING\",\n    \"answer\": [{\n            \"content\": {\n                \"formId\": null,\n                \"formVersion\": null,\n                \"formArgs\": null,\n                \"msg\": \"你好\",\n                \"formAppearance\": null,\n                \"formData\": null\n            },\n            \"type\": \"QUESTION\",\n            \"msgId\": null\n        }\n    ],\n    \"chat_id\": \"287cb0ba6d6d4569a3d46f610b9e6b66\",\n    \"at_chat_id\": null,\n    \"instance_id\": \"908931206040447989f605eccad62919\",\n    \"log_id\": \"1334\"\n}\n{\n \"status\": \"READY\",\n \"answer\": null,\n \"chat_id\": \"97c2ceb8099648bbbf2d61d44dbf1000\",\n  \"at_chat_id\": null,\n  \"instance_id\": \"1e18331c3bfd4a68aec21ea2c2d65037\",\n \"log_id\": null\n}\n{\n    \"status\": \"ARCHIVED\",\n    \"answer\": [{\n            \"content\": \"你好！很高兴为你提供帮助。请告诉我你有什么问题或需要什么信息，我会尽力协助你。\",\n            \"type\": \"MSG\",\n            \"msgId\": null\n        }\n    ],\n    \"chat_id\": \"287cb0ba6d6d4569a3d46f610b9e6b66\",\n    \"at_chat_id\": null,\n    \"instance_id\": \"908931206040447989f605eccad62919\",\n    \"log_id\": \"1335\"\n}</code>"
          }
        ]
      },
      {
        "id": 'wss-target-oms',
        "method": "WSS",
        "url": "<font color=#d0cdcd id=wss-target-oms>/ws</font>",
        "title": "**调用接口建立 websocket 连接**",
        "content": "建立 websocket 连接后，通过 websocket 的 session 发送对话请求并接收对话的响应。",
        "children": [
          {
            "title": "Request Body",
            "content": "websocket 连接建立后，通过响应结构的 message 发送对话请求。",
            "children": [
              {
                "field": "`requestId`",
                "type": "<font color=#d0cdcd>string</font>",
                "description": "用于表示请求的唯一 id，websocket 的返回的流中都会带有 requestId，用于在复用 session 的情况下区分返回的流式消息属于哪一次请求。"
              },
              {
                "field": "`method`",
                "type": "<font color=#d0cdcd>string</font>",
                "description": "需要使用 websocket 请求的方法的名称。当前 `method` 仅支持 `appChat`。"
              },
              {
                "field": "`params`",
                "type": "<font color=#d0cdcd>object</font>",
                "description": "websocket 请求的方法的参数，根据 `method` 不同会有不一样的结构。",
                "children": [
                  {
                    "field": "`method: appChat`",
                    "description": "websocket 请求 app_chat 方法。",
                    "children": [
                      {
                        "field": "`data`",
                        "type": "<font color=#d0cdcd>object</font>",
                        "description": "websocket 请求 app_chat 方法的实际的参数结构，与 app_chat 接口的 request body 一致。"
                      }
                    ]
                  }
                ]
              }
            ]
          },
          {
            "title": "Response",
            "children": [
              {
                "field": "`requestId`",
                "type": "<font color=#d0cdcd>string</font>",
                "description": "用于表示请求的唯一 id，websocket 的返回的流中都会带有 requestId，用于在复用 session 的情况下区分返回的流式消息属于哪一次请求。"
              },
              {
                "field": "`code`",
                "type": "<font color=#d0cdcd>number</font>",
                "description": "表示请求结果的状态码，正常状态为0，出现异常时返回异常状态码。"
              },
              {
                "field": "`msg`",
                "type": "<font color=#d0cdcd>string</font>",
                "description": "表示接口执行失败时的错误信息，`code` 为 0 时，`msg` 为 `null`。"
              },
              {
                "field": "`data`",
                "type": "<font color=#d0cdcd>object</font>",
                "description": "表示接口流式返回的数据，具体结构需要根据调用的不同的接口进行区分；在 websocket 的失败或者完成响应中，`data` 为 `null`。",
                "children": [
                  {
                    "field": "`method: appChat`",
                    "description": "websocket 请求 app_chat 的响应。与 `app_chat` 的 Response 结构一致。"
                  }
                ]
              },
              {
                "field": "`completed`",
                "type": "<font color=#d0cdcd>bool</font>",
                "description": "表示流式响应是否结束的字段，在 websocket 的失败或者结束的响应中，`completed` 为 `true`。"
              }
            ]
          }
        ],
        "codes": [
          {
            "title": "Request",
            "description": "WSS · /app_chat",
            "code": "<code>{\n    \"requestId\": \"123456\",\n    \"method\": \"appChat\",\n    \"params\": {\n        \"data\": {\n            \"app_id\": \"56c6818563b74433af2984d55a6d972b\",\n            \"question\": \"你好\",\n            \"context\": {\n                \"use_memory\": true,\n                \"user_context\": {}\n            }\n        }\n    }\n}</code>"
          },
          {
            "title": "Response",
            "code": "<code>{\n    \"requestId\": \"123456\",\n    \"code\": 0,\n    \"msg\": null,\n    \"data\": {\n        \"status\": \"ARCHIVED\",\n        \"answer\": [\n            {\n                \"content\": \"回复：你好！很高兴为你提供帮助。请告诉我你有什么问题或需要什么信息。\",\n                \"type\": \"MSG\",\n                \"msgId\": null\n            }\n        ],\n        \"chat_id\": \"e4cff2d2eb1949efb43b64802c8750ce\",\n        \"at_chat_id\": null,\n        \"instance_id\": \"a302c335a50148c2b44460ba183a07b5\",\n        \"log_id\": \"72\"\n    },\n    \"completed\": false\n}</code>"
          }
        ]
      }
    ]);
  };

  // 滚动到到wss部分
  const jumpToWss = () => {
    document.querySelector('#wss-target-oms')?.scrollIntoView(true);
  };

  const handleAnchorClick = (e, target) => {
    e.preventDefault();
    if (target) {
      setCurrentNav(target);
      document.querySelector(target)?.scrollIntoView(true);
    }
  }

  const handleScroll = () => {
    const sections = document.querySelectorAll('.document-markdown-oms section');
    const navLinks = document.querySelectorAll('.document-nav-oms div');
    sections.forEach(section => {
      const sectionTop = section.offsetTop;
      const sectionHeight = section.clientHeight;
      const sectionBottom = sectionTop + sectionHeight;
      if (documentRef.current.scrollTop >= (sectionTop - 100) && documentRef.current.scrollTop < (sectionBottom - 200)) {
        navLinks.forEach(link => link.classList.remove('current-nav'));
        const link: any = document.getElementById(`${section.id}-link`);
        link?.classList.add('current-nav');
      }
    })
  };

  useEffect(() => {
    if (documentContent.length) {
      document.querySelector('#jump-wss-oms')?.addEventListener('click', jumpToWss);
    }
  }, [documentContent]);

  useEffect(() => {
    if (drawerOpen) {
      getDocument();
      documentRef.current.addEventListener('scroll', handleScroll);
    } else {
      setCocumentContent([]);
    }
  }, [drawerOpen]);

  useEffect(() => {
    return () => {
      document.querySelector('#jump-wss-oms')?.removeEventListener('click', jumpToWss);
    }
  }, []);

  return (
    <Drawer
      title={t('ApiDocument')}
      width={1100}
      open={drawerOpen}
      onClose={() => setDrawerOpen(false)}
      closable={false}
      className='api-drawer-oms'
      extra={
        <CloseOutlined
          onClick={() => {
            setDrawerOpen(false);
          }}
        />
      }
    >
      <div className='document-nav-oms'>
        <div 
          id='first-link'
          className={`nav-item ${currentNav === '#POST' ? 'current-nav' : ''}`}
          onClick={(e) => handleAnchorClick(e, '#POST')}>
            POST
        </div>
        <div 
          id='wss-target-oms-link'
          className={`nav-item ${currentNav === '#WSS' ? 'current-nav' : ''}`}
          onClick={(e) => handleAnchorClick(e, '#WSS')}>
            WSS
        </div>
      </div>
      <div className='document-markdown-oms' ref={documentRef}>
        <div id='first'>
          <div className='content-title'>{t('basic')}URL</div>
          <div className='inner-code'>
            <div className='inner-code-title'>Code</div>
            <div className='inner-code-content'>
              <span>{url}</span>
              <div style={{ cursor: 'pointer' }} onClick={() => toClipboard(url)}>
                <img src={copyCodeIcon} style={{ marginRight: 8 }} alt="" />
                <span>Copy</span>
              </div>
            </div>
          </div>
        </div>
        {
          documentContent.map((item:any, index) =>
            <section key={index} id={item.method} className={`item-content ${item.method ? 'method-item' : ''}`}>
              <div style={{ width: item.codes?.length ? 'calc(50% - 10px)' : '100%' }}>
                {
                  item.method && <div className='method-title'>
                    <div className='method-name'>{item.method}</div>
                    <span dangerouslySetInnerHTML={{ __html: item.url }}></span>
                  </div>
                }
                <div className='content-title' dangerouslySetInnerHTML={{ __html: markedProcess(item.title) }}></div>
                <div className='content-description' dangerouslySetInnerHTML={{ __html: markedProcess(tranlateCode(item.content)) }}></div>
                {
                  item.children?.length > 0 && item.children.map((body, i) =>
                    <div key={i} className='method-body'>
                      <div className='body-title'>{body.title}</div>
                      {body.content && <div className='body-description' dangerouslySetInnerHTML={{ __html: markedProcess(tranlateCode(body.content)) }}></div>}
                      {body.children?.length > 0 && (body.isError ? getErrorContent(body.children) : getParamsContent(body.children))}
                    </div>
                  )
                }
              </div>
              {
                item.codes?.length > 0 && <div className='content-right'>
                  {
                    item.codes.map((code, idx) =>
                      getMethodCode(code, idx)
                    )
                  }
                </div>
              }
            </section>
          )
        }
      </div>
    </Drawer>
  );
};

export default DocumentDrawer;
