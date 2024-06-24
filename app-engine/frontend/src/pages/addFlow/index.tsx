import React, { useEffect, useState, useRef, useImperativeHandle } from 'react';
import { useParams } from 'react-router-dom'
import { Tooltip } from "antd";
import { getAppInfo } from '@shared/http/aipp';
import { ConfigFlowIcon } from '@assets/icon';
import { Message } from '@shared/utils/message';
import { FlowContext } from '../aippIndex/context';
import LeftMenu from './components/left-menu';
import Stage from './components/elsa-stage';
import FlowHeader from './components/addflow-header';
import './styles/index.scss';
import FlowTest from "./components/flow-test";

const AddFlow = (props) => {
  const { type, appInfo, addFlowRef, setFlowTestTime, setFlowTestStatus } = props;
  const [ dragData, setDragData ] = useState([]);
  const [ flowInfo, setFlowInfo ] = useState({});
  const [ showMenu, setShowMenu ] = useState(false);
  const [ debugTypes, setDebugTypes ] = useState([]);
  const [ showDebug, setShowDebug ] = useState(false);
  const { tenantId, appId } = useParams();

  useEffect(() => {
    !type && initElsa();
  }, [type]);
  // 新建工作流时获取详情
  async function initElsa() {
    const res = await getAppInfo(tenantId, appId);
    if (res.code === 0) {
      setFlowInfo(res.data);
    }
  }
  const appRef = useRef(null);
  const flowIdRef = useRef(null);
  const flowContext ={
    type,
    appInfo: type ? appInfo : flowInfo,
    showMenu,
    setShowMenu
  }
  
  useEffect(() => {
    type ? null : setShowMenu(true);
  }, [props.type])
  // 显示隐藏左侧菜单
  function menuClick() {
    setShowMenu(!showMenu)
  }
  // 测试
  const handleDebugClick = () => {
    window.agent.validate().then(()=> {
      setDebugTypes(window.agent.getFlowRunInputMetaData());
      setShowDebug(true);
    }).catch(err => {
      let str = typeof(err) === 'string' ? err : '请输入流程必填项';
      Message({ type: "warning", content: str});
    })
  }
  // 给父组件的测试回调
  useImperativeHandle(addFlowRef, () => {
    return {
      'handleDebugClick': handleDebugClick,
    }
  })
  return <>{(
    <div className='add-flow-container'>
      <FlowContext.Provider value={flowContext}>
        {!type && 
          <FlowHeader 
            debugTypes={debugTypes}
            handleDebugClick={handleDebugClick}
            showDebug={showDebug}
            setShowDebug={setShowDebug}
        />}
        {type && 
          <FlowTest
            setTestStatus={setFlowTestStatus}
            setTestTime={setFlowTestTime}
            setShowDebug={setShowDebug}
            showDebug={showDebug}
            debugTypes={debugTypes}
            appRef={appRef}
        />}
        <div className={['content', !type ? 'content-add' : null ].join(' ')}>
          {
            showMenu ? (
              <LeftMenu 
                menuClick={menuClick}
                dragData={dragData} 
                setDragData={setDragData}
              />
            ) : (
              <Tooltip placement="rightTop" title="展开编排区">
                <div className="menu-icon" onClick={menuClick}>
                  <ConfigFlowIcon />
                </div>
              </Tooltip>
            )
          }
          <Stage
            setDragData={setDragData} 
            appRef={appRef} 
            flowIdRef={flowIdRef} 
          />
        </div>
      </FlowContext.Provider>
    </div>
  )}</>
};
export default AddFlow;
