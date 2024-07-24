import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { Spin } from 'antd';
import AddFlow from '../addFlow';
import ConfigForm from '../configForm';
import CommonChat from '../chatPreview/chatComminPage';
import ChoreographyHead from '../components/header';
import { getAppInfo } from '@shared/http/aipp';
import { updateFormInfo } from '@shared/http/aipp';
import { debounce, getUiD } from '@shared/utils/common';
import { Message } from '@shared/utils/message';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setAppId, setAppInfo } from '@/store/appInfo/appInfo';
import { getUser } from '../helper';

const AippIndex = () => {
  const { appId, tenantId } = useParams();
  const [ showElsa, setShowElsa ] = useState(false);
  const [ spinning, setSpinning] = useState(false);
  const [ reloadInspiration, setReloadInspiration ] = useState('');
  const [ showChat, setShowChat ] = useState(false);
  const [ messageChecked, setMessageCheck ] = useState(false);
  const [ testStatus, setTestStatus ] = useState(null);
  const [ testTime, setTestTime ] = useState(null);
  const aippRef = useRef(null);
  const inspirationRefresh = useRef(false);
  const dispatch = useAppDispatch();
  const appInfo = JSON.parse(JSON.stringify(useAppSelector((state) => state.appStore.appInfo)));
  let addFlowRef = React.createRef();

  const elsaChange = () => {
    setShowElsa(!showElsa);
    showElsa && getAippDetails();
  }

  useEffect(() => {
    dispatch(setAppId(appId));
    getUser();
    getAippDetails();
  }, []);

  // 获取aipp详情
  const getAippDetails = async () => {
    setSpinning(true);
    try {
      const res = await getAppInfo(tenantId, appId);
      if (res.code === 0) {
        res.data.hideHistory = true;
        dispatch(setAppInfo(res.data));
      }
    } finally {
      setSpinning(false);
    }
  }
  // 修改aipp更新回调
  const updateAippCallBack = (data) => {
    if(data){
      aippRef.current = data;
      dispatch(setAppInfo(aippRef.current));
    }
  }
  // 保存配置
  const saveConfig = (data) => {
    updateFormInfo(tenantId, appId, data).then((res) => {
      if (res.code === 0) {
        Message({type: 'success', content: '保存配置成功'});
        getAippDetails();
        if (inspirationRefresh.current) {
          inspirationRefresh.current = false;
          let key = getUiD();
          setReloadInspiration(key);
        }
      }
    })
  }
  // 灵感大全更新后自动刷新
  const inspirationChange = () => {
    inspirationRefresh.current = true;
  }
  // 编辑工具流设置右侧聊天展开
  const changeChat = () => {
    setShowChat(!showChat)
  }
  // 实时自动保存
  const handleSearch = useCallback(debounce((data) => saveConfig(data), 1000), []);
  const handleConfigDataChange = (data) => {
    handleSearch(data);
  };
  // 打开调试抽屉方法
  const openDebug = () => {
    if (!showElsa) {
      setShowElsa(true);
    }
    addFlowRef.current.handleDebugClick();
  }
  const handleTestStatus = (value) => setTestStatus(value);
  const handleTestTime = (value) => setTestTime(value);
  const contextProvider = {
    messageChecked,
    setMessageCheck,
    showElsa,
    updateAippCallBack,
    reloadInspiration,
    tenantId,
  };

  return (
    <>
      {
        <div 
          className={`container ${showElsa ? 'layout-elsa-content' : ''} ${showChat ? 'layout-show-preview' : ''}`}>
          <ChoreographyHead
            appInfo={appInfo}
            showElsa={showElsa}
            updateAippCallBack={updateAippCallBack}
            mashupClick={elsaChange}
            openDebug={openDebug}
            testTime={testTime}
            testStatus={testStatus}
            addFlowRef={addFlowRef}
          />
          <div className='layout-content'>
            {showElsa ?
              (
                <AddFlow 
                  type='edit'
                  addFlowRef={addFlowRef}
                  setFlowTestStatus={handleTestStatus}
                  setFlowTestTime={handleTestTime}
                  appInfo={appInfo}
                />
              ) :
              (
                <ConfigForm
                  mashupClick={elsaChange}
                  configData={appInfo.config}
                  handleConfigDataChange={handleConfigDataChange}
                  inspirationChange={inspirationChange}
                  showElsa={showElsa}
                />
              )}
            <CommonChat 
              chatType={appInfo.state}
              contextProvider={contextProvider} 
              previewBack={changeChat} 
            />
          </div>
        </div>
      }
       <Spin spinning={spinning} fullscreen />
    </>
  );
};

export default AippIndex;
