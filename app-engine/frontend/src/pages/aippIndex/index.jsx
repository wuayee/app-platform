import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { Spin, Tooltip } from 'antd';
import Header from '__pages/components/header.jsx';
import ConfigForm from '__pages/configForm/index.jsx';
import ChatPreview from '__pages/chatPreview/index.jsx';
import AddFlow from '__pages/addFlow/index.jsx';
import { TalkFlowIcon  } from '@assets/icon';
import { AippContext } from './context';
import { templateJson } from './template';
import { getCurUser, getAippInfo } from '../../shared/http/aipp';
import { updateFormInfo } from '../../shared/http/aipp';
import { debounce, getUiD } from "../../shared/utils/common";
import { Message } from "../../shared/utils/message";

const AippIndex = () => {
  const { appId, tenantId } = useParams();
  const [ showElsa, setShowElsa ] = useState(false);
  const [ spinning, setSpinning] = useState(false);
  const [ aippInfo, setAippInfo] = useState({});
  const [ chatRunning, setChatRunning ] = useState(false);
  const [ showChat, setShowChat ] = useState(false);
  const [ messageChecked, setMessageCheck ] = useState(false);
  const [ prompValue, setPrompValue ] = useState({});
  const [ refreshPrompValue, setRefreshPrompValue ] = useState(false);
  const [ reloadInspiration, setReloadInspiration ] = useState('');
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
          aippRef.current = JSON.parse(JSON.stringify(res.data));
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
  // 设置会话状态
  const chatStatusChange = (running) => {
    setChatRunning(running)
  }
  // 获取用户信息
  const getUser = () => {
    getCurUser().then(res => {
      localStorage.setItem('currentUserId', res.data.account?.substr(1));
      localStorage.setItem('currentUser', res.data.chineseName);
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

  const inspirationChange = () => {
    inspirationRefresh.current = true;
  }

  // 编辑工具流设置右侧聊天展开
  const changeChat = () => {
    setShowChat(!showChat)
  }

  const handleSearch = useCallback(debounce((data) => saveConfig(data), 1000), []);
  const handleConfigDataChange = (data) => {
    handleSearch(data);
  };
  const provider = {
    appId,
    tenantId,
    showElsa,
    aippInfo,
    messageChecked,
    setMessageCheck,
    prompValue,
    reloadInspiration,
    setPrompValue,
    refreshPrompValue,
    setRefreshPrompValue,
    chatRunning,
    updateAippCallBack
  };
  return (
    <>
      {
        <div className="container">
          <Header
            aippInfo={aippInfo}
            showElsa={showElsa}
            updateAippCallBack={updateAippCallBack}
            mashupClick={elsaChange}
            chatRunning={chatRunning}
          />
          <div className={[
            "layout-content",
            showElsa ? "layout-elsa-content" : null,
            showChat ? "layout-show-preview" : null
          ].join(' ')}
          >
            <AippContext.Provider value={provider}>
              {showElsa ? (
                <AddFlow type="edit" />
              ) : (
                <ConfigForm
                  mashupClick={elsaChange}
                  configData={aippInfo.config}
                  handleConfigDataChange={handleConfigDataChange}
                  inspirationChange={inspirationChange}
                />
              )}
                <ChatPreview chatStatusChange={chatStatusChange} chatType="preview" previewBack={changeChat}/>
              {
                (!showChat && showElsa) &&
                <Tooltip placement="leftTop" title="展开预览与调试区">
                  <div className="chat-icon" onClick={changeChat}>
                    <TalkFlowIcon />
                  </div>
                </Tooltip>

              }
            </AippContext.Provider>
          </div>
        </div>
      }
       <Spin spinning={spinning} fullscreen />
    </>
  );
};

export default AippIndex;
