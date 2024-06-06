import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { Spin, Tooltip } from 'antd';
import { TalkFlowIcon  } from '@assets/icon';
import { getCurUser, getAippInfo } from '@shared/http/aipp';
import { updateFormInfo } from '@shared/http/aipp';
import { debounce, getUiD } from "@shared/utils/common";
import { Message } from "@shared/utils/message";
import { AippContext } from './context';
import AddFlow from '../addFlow';
import ConfigForm from '../configForm';
import CommonChat from '../chatPreview/chatComminPage';
import ChoreographyHead from '../components/header';
import { ConfigFormContext } from './context';
import { getUser } from '../helper';

const AippIndex = () => {
  const { appId, tenantId } = useParams();
  const [ showElsa, setShowElsa ] = useState(false);
  const [ spinning, setSpinning] = useState(false);
  const [ aippInfo, setAippInfo] = useState({});
  const [ reloadInspiration, setReloadInspiration ] = useState('');
  const [ showChat, setShowChat ] = useState(false);
  const [ messageChecked, setMessageCheck ] = useState(false);
  const aippRef = useRef(null);
  const inspirationRefresh = useRef(false);

  const elsaChange = () => {
    setShowElsa(!showElsa);
    showElsa && getAippDetails();
  }
  useEffect(() => {
    getUser();
    getAippDetails();
  }, [])

  // 获取aipp详情
  const getAippDetails = async () => {
    setSpinning(true);
    try {
      const res = await getAippInfo(tenantId, appId);
      if (res.code === 0) {
        setAippInfo(() => {
          res.data.hideHistory = true;
          return res.data
        });
      }
    } finally {
      setSpinning(false);
    }
  }
  // 修改aipp更新回调
  const updateAippCallBack = (data) => {
    data && setAippInfo(() => {
      aippRef.current = data;
      return aippRef.current
    })
  }
  // 保存配置
  const saveConfig = (data) => {
    updateFormInfo(tenantId, appId, data).then((res) => {
      if (res.code === 0) {
        Message({type: "success", content: "保存配置成功"});
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
  const contextProvider = {
    appId,
    tenantId,
    aippInfo,
    messageChecked,
    setMessageCheck,
    showElsa,
    updateAippCallBack,
    reloadInspiration
  };

  const configFormProvider ={
    appId,
    tenantId
  }
  return (
    <>
      {
        <div className="container">
          <ChoreographyHead
            aippInfo={aippInfo}
            showElsa={showElsa}
            updateAippCallBack={updateAippCallBack}
            mashupClick={elsaChange}
          />
          <div className={[
            "layout-content",
            showElsa ? "layout-elsa-content" : null,
            showChat ? "layout-show-preview" : null
          ].join(' ')}
          >
            <ConfigFormContext.Provider value={configFormProvider}> 
              {showElsa ? (
                <AddFlow type="edit" aippInfo={aippInfo}/>
              ) : (
                   <ConfigForm
                     mashupClick={elsaChange}
                     configData={aippInfo.config}
                     handleConfigDataChange={handleConfigDataChange}
                     inspirationChange={inspirationChange}
                     showElsa={showElsa}
                   />
              )}
              </ConfigFormContext.Provider>
              <CommonChat chatType="preview" contextProvider={contextProvider} previewBack={changeChat} /> 
              {
                (!showChat && showElsa) &&
                <Tooltip placement="leftTop" title="展开预览与调试区">
                  <div className="chat-icon" onClick={changeChat}>
                    <TalkFlowIcon />
                  </div>
                </Tooltip>
              }
          </div>
        </div>
      }
       <Spin spinning={spinning} fullscreen />
    </>
  );
};

export default AippIndex;
