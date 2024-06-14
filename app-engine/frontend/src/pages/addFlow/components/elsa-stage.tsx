
import React, { useEffect, useCallback, useState, useRef, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { JadeFlow } from '@fit-elsa/elsa-react';
import { debounce } from '@shared/utils/common';
import {
  getAppInfo,
  updateFlowInfo, } from '@shared/http/aipp';
import { getAddFlowConfig } from '@shared/http/appBuilder';
import { Message } from '@shared/utils/message';
import HuggingFaceModal from './hugging-face-modal';
import { FlowContext } from '../../aippIndex/context';
import { configMap } from '../config';

const Stage = (props) => {
  const { setAddId, setDragData, appRef, flowIdRef } = props;
  const [ showModal, setShowModal ] = useState(false);
  const { CONFIGS } = configMap[process.env.NODE_ENV];
  const { type, appInfo, setModalInfo } = useContext(FlowContext);
  const { tenantId, appId } = useParams();
  const modelCallback = useRef();

  useEffect(() => {
    window.agent = null;
    type ? setElsaData() : initElsa();
  }, [type]);

   // 新建工作流
  async function initElsa() {
    flowIdRef.current = appId;
    setAddId(appId);
    const res = await getAppInfo(tenantId, appId);
    if (res.code === 0) {
      appRef.current = res.data;
      setElsaData(appRef.current.flowGraph.appearance);
    }
  }

  // 编辑工作流
  function setElsaData(editData = undefined) {
    let graphData = editData || appInfo.flowGraph?.appearance || {};
    const stageDom = document.getElementById('stage');
    let data = JSON.parse(JSON.stringify(graphData));
    let configIndex = CONFIGS.findIndex(item => item.node === 'llmNodeState');
    CONFIGS[configIndex].params.tenantId = tenantId;
    CONFIGS[configIndex].params.appId = appId;
    const importFiles = [
      () => import(/* webpackIgnore: true */`../../chatPreview/components/runtimeForm/fileContentComponent.jsx`),
      () => import(/* webpackIgnore: true */`../../chatPreview/components/runtimeForm/interviewQuestionsComponent.jsx`),
      () => import(/* webpackIgnore: true */`../../chatPreview/components/runtimeForm/manageCubeCreateReportComponent.jsx`),
    ];
    JadeFlow.edit(stageDom, data, CONFIGS, importFiles).then(agent => {
      window.agent ? null : window.agent = agent;
      agent.onChange(() => {
        handleChange();
      });
      agent.onModelSelect((fn) => {
        setShowModal(true);
        modelCallback.current = fn;
      })
    })
    getAddFlowConfig(tenantId).then(res => {
      if (res.code === 0) {
        res.data.tool.unshift({
          'name': 'hugging-face',
          'type': 'huggingFaceNodeState',
          "taskId": "1264ab63-62e3-4965-ac91-a7396099f123",
          "schema": {
            "name": "fill-mask",
            "description": "该流水线可以输入带有掩码标记的句子，模型会根据上下文对掩码标记进行填空",
            "parameters": {
              "type": "object",
              "properties": {
                "inputs": {
                  "description": "一个包含掩码标记[MASK]的文本。",
                  "type": "string"
                },
                "targets": {
                  "description": "可通过此参数限定模型向掩码标记填空的token类型，如：'positive'",
                  "type": "array",
                  "items": {
                    "type": "string"
                  }
                },
                "top_k": {
                  "description": "返回的填空结果数量",
                  "type": "integer",
                  "default": 5
                }
              },
              "required": [
                "inputs"
              ]
            },
            "return": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "sequence": {
                    "description": "向掩码标记填空后的输入",
                    "type": "string"
                  },
                  "score": {
                    "description": "该填空token的出现概率。",
                    "type": "number"
                  },
                  "token": {
                    "description": "预测的标记token ID（用于替换掩码标记）。",
                    "type": "integer"
                  },
                  "token_str": {
                    "description": "预测的标记token（用于替换掩码标记）。",
                    "type": "string"
                  }
                },
                "required": [
                  "sequence",
                  "score",
                  "token",
                  "token_str"
                ]
              }
            }
          },
          "context": {
            "model": [],
            "summary": "Masked language modeling is the task of masking some of the words in a sentence and predicting which words should replace those masks. These models are useful when we want to get a statistical understanding of the language in which the model is trained in.",
            "default_model": "distilbert/distilroberta-base",
            "base_url": "huggingface.co/"
          },
          "toolUniqueName": "1264ab63-62e3-4965-ac91-a7396099f123"
        })
        setDragData(res.data);
      }
    });
  }
  // hugging-face模型选中
  const onModelSelectCallBack = (model) => {
    modelCallback.current(model);
  }
  // 数据实时保存
  const handleChange = useCallback(debounce(() => elsaChange(), 2000), []);
  function elsaChange() {
    window.agent.validate().then(() => {
      let graphChangeData = window.agent.serialize();
      if (type) {
        appInfo.flowGraph.appearance = graphChangeData;
        updateAppRunningFlow();
      } else {
        setModalInfo(() => {
          appRef.current.flowGraph.appearance = graphChangeData;
          return appRef.current;
        })
        updateAppRunningFlow();
      }
    }).catch((err) => {
      let str = typeof(err) === 'string' ? err : '请输入流程必填项';
      Message({ type: "warning", content: str});
    });;
  }
  // 编辑更新应用
  async function updateAppRunningFlow() {
    let id = type ? appId : flowIdRef.current;
    let params = type ?  appInfo.flowGraph : appRef.current.flowGraph;
    const res = await updateFlowInfo(tenantId, id, params);
    if (res.code === 0) {
      Message({ type: 'success', content: type ? '高级配置更新成功': '工具流更新成功' })
    }
  }
  // 拖拽完成回调
  function handleDragEnter(e) {
    const nodeTab = e.dataTransfer.getData('itemTab');
    let nodeType;
    switch (nodeTab) {
      case 'basic':
        nodeType = e.dataTransfer.getData('itemType');
        window.agent.createNode(nodeType, e);
        break;
      case 'tool':
        nodeType = e.dataTransfer.getData('itemType');
        let nodeMetaData = e.dataTransfer.getData('itemMetaData');
        window.agent.createNode(nodeType, e, JSON.parse(nodeMetaData));
        break;
      default:
        break;
    }

  }
  return <>
    <div
      className='content-right'
      onDragOver={(e) => e.preventDefault()}
      onDrop ={handleDragEnter}>
        <div className='elsa-canvas' id='stage'></div>
    </div>
    <HuggingFaceModal
      showModal={showModal}
      setShowModal={setShowModal}
      onModelSelectCallBack={onModelSelectCallBack}
    />
  </>
};


export default Stage;
