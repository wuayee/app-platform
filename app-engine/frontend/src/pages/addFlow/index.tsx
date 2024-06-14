import React, { useEffect, useState, useRef } from 'react';
import { Tooltip } from "antd";
import { ConfigFlowIcon } from '@assets/icon';
import { FlowContext } from '../aippIndex/context';
import LeftMenu from './components/left-menu';
import Stage from './components/elsa-stage';
import FlowHeader from './components/addflow-header';
import './styles/index.scss';


const AddFlow = (props) => {
  const { type, appInfo } = props;
  const [ dragData, setDragData ] = useState([]);
  const [ addId, setAddId ] = useState(false);
  const [ showMenu, setShowMenu ] = useState(false);
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
  return <>{(
    <div className='add-flow-container'>
      <FlowContext.Provider value={flowContext}>
        {!type && <FlowHeader addId={addId} appRef={appRef} flowIdRef={flowIdRef} />}
        <div className={['content', !type ? 'content-add' : null ].join(' ')}>
          {
            showMenu ? (
              <LeftMenu menuClick={menuClick} addId={addId} dragData={dragData}/>
            ) : (
              <Tooltip placement="rightTop" title="展开编排区">
                <div className="menu-icon" onClick={menuClick}>
                  <ConfigFlowIcon />
                </div>
              </Tooltip>
            )
          }
          <Stage setAddId={setAddId} setDragData={setDragData} appRef={appRef} flowIdRef={flowIdRef} />
        </div>
      </FlowContext.Provider>
    </div>
  )}</>
};
export default AddFlow;
