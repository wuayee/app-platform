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
import TestModal from "../components/test-modal";


const AddFlow = (props) => {
  const { type, appInfo, addFlowRef, setFlowTestTime, setFlowTestStatus } = props;
  const [ dragData, setDragData ] = useState([]);
  const [ addId, setAddId ] = useState(false);
  const [ showMenu, setShowMenu ] = useState(false);
  const [ debugTypes, setDebugTypes ] = useState([]);
  const [ showDebug, setShowDebug ] = useState(false);
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
  const flowContext ={
    type,
    appInfo,
    modalInfo,
    setModalInfo,
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
        {!type && <FlowHeader addId={addId}
                              appRef={appRef}
                              flowIdRef={flowIdRef}
                              debugTypes={debugTypes}
                              handleDebugClick={handleDebugClick}
                              showDebug={showDebug}
                              setShowDebug={setShowDebug}
        />}
        {type && <FlowTest
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
                addId={addId} 
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
            setAddId={setAddId} 
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
