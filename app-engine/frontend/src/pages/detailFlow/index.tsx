
import React, { useEffect, useState } from 'react';
import { LeftArrowIcon } from '@assets/icon';
import { useParams, useHistory } from 'react-router-dom';
import { getAppInfo } from '@shared/http/aipp';
import { JadeFlow } from '@fit-elsa/elsa-react';
import { configMap } from '../addFlow/config';
import './index.scss'

const FlowDetail = () => {
  const { appId, tenantId } = useParams();
  const [ appInfo, setAppInfo] = useState(false);
  const navigate = useHistory().push;
  const { CONFIGS } = configMap[process.env.NODE_ENV];

  useEffect(() => {
    getAippDetails();
  }, [])
  // 获取aipp详情
  async function getAippDetails() {
    const res = await getAppInfo(tenantId, appId);
    if (res.code === 0) {
      setAppInfo(res.data);
      setElsaData(res.data.flowGraph?.appearance);
    }
  }

  // 编辑工作流
  function setElsaData(editData) {
    const stageDom = document.getElementById('stageDetail');
    let data = JSON.parse(JSON.stringify(editData));
    let configIndex = CONFIGS.findIndex(item => item.node === 'llmNodeState');
    CONFIGS[configIndex].params.tenantId = tenantId;
    CONFIGS[configIndex].params.appId = appId;
    const importFiles = [
      () => import(/* webpackIgnore: true */`../chatPreview/components/runtimeForm/fileContentComponent.jsx`),
      () => import(/* webpackIgnore: true */`../chatPreview/components/runtimeForm/interviewQuestionsComponent.jsx`),
      () => import(/* webpackIgnore: true */`../chatPreview/components/runtimeForm/manageCubeCreateReportComponent.jsx`),
      () => import(/* webpackIgnore: true */`../chatPreview/components/runtimeForm/QuestionClar/questionClarComponent`),
      () => import(/* webpackIgnore: true */`../chatPreview/components/runtimeForm/conditionForm/conditionFormComponent`),
    ];
    JadeFlow.edit(stageDom, tenantId, data, CONFIGS, importFiles).then(agent => {
      window.agent ? null : window.agent = agent;
    })
  }
  function handleBackClick() {
    navigate(-1);
  }
  return <>{(
    <div className='graph-detail'>
      <div className='header'>
          <div className='header-left'>
            <LeftArrowIcon className='icon-back' onClick={ handleBackClick } />
            <span className='header-text' title={appInfo?.name}>{ appInfo.name }</span>
          </div>
          <div className='header-right'>
            <span className='header-text'>创建人：{ appInfo.createBy }</span>
            <span className='header-text'>发布时间：{ appInfo.updateAt }</span>
          </div>
        </div>
      <div id='stageDetail'></div>
    </div>
  )}</>
};


export default FlowDetail;
