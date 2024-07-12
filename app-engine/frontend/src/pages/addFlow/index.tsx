import React, { useEffect, useState, useRef, useImperativeHandle } from 'react';
import { Tooltip } from "antd";
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
  const [ addId, setAddId ] = useState(false);
  const [ loading, setLoading ] = useState(false);
  const [ showMenu, setShowMenu ] = useState(false);
  const [ debugTypes, setDebugTypes ] = useState([]);
  const [ showDebug, setShowDebug ] = useState(false);
  const [ testStatus, setTestStatus ] = useState(null);
  const [ testTime, setTestTime ] = useState(0);
  const [ modalInfo, setModalInfo ] = useState({
    name: '无标题',
    type: 'waterFlow',
    attributes: {
      description: ''
    },
    flowGraph: {
      appearance: null
    }
  });
  const appRef = useRef(null);
  const flowIdRef = useRef(null);
  const elsaRunningCtl = useRef(null);
  const flowContext ={
    type,
    appInfo,
    modalInfo,
    setModalInfo,
    showMenu,
    setShowMenu
  }
  
  useEffect(() => {
    if (!type) {
      return;
    }
    setShowMenu(true)
    setFlowTestTime(null);
    setFlowTestStatus(null);
    elsaRunningCtl.current?.reset();
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
        {!type && <FlowHeader addId={addId}
                              appRef={appRef}
                              flowIdRef={flowIdRef}
                              handleDebugClick={handleDebugClick}
                              testStatus={testStatus}
                              testTime={testTime}
        />}
        <FlowTest setTestStatus={type ? setFlowTestStatus : setTestStatus}
                  setTestTime={type ? setFlowTestTime : setTestTime}
                  setShowDebug={setShowDebug}
                  showDebug={showDebug}
                  debugTypes={debugTypes}
                  appRef={appRef}
                  elsaRunningCtl={elsaRunningCtl}
        />
        <div className={['content', !type ? 'content-add' : null ].join(' ')}>
          {
            showMenu ? (
              <LeftMenu 
                menuClick={menuClick} 
                addId={addId} 
                dragData={dragData} 
                loading={loading}
                setLoading={setLoading}
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
            setAddId={setAddId} 
            setDragData={setDragData} 
            setLoading={setLoading}
            appRef={appRef} 
            flowIdRef={flowIdRef} 
          />
        </div>
      </FlowContext.Provider>
    </div>
  )}</>
};
export default AddFlow;
