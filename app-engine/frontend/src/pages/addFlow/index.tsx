import React, { useEffect, useState, useRef, useImperativeHandle } from 'react';
import { Tooltip } from 'antd';
import { useParams } from 'react-router-dom'
import { getAppInfo } from '@/shared/http/aipp';
import { ConfigFlowIcon } from '@assets/icon';
import { Message } from '@/shared/utils/message';
import { FlowContext } from '../aippIndex/context';
import { setTestTime, setTestStatus } from "@/store/flowTest/flowTest";
import { useAppDispatch } from "@/store/hook";
import LeftMenu from './components/left-menu';
import Stage from './components/elsa-stage';
import FlowHeader from './components/addflow-header';
import FlowTest from './components/flow-test';
import { useTranslation } from 'react-i18next';
import './styles/index.scss';

const AddFlow = (props) => {
  const { t } = useTranslation();
  const dispatch = useAppDispatch();
  const { type, appInfo, addFlowRef,
    showFlowChangeWarning, setShowFlowChangeWarning } = props;
  const [dragData, setDragData] = useState([]);
  const [flowInfo, setFlowInfo] = useState({});
  const [showTime, setShowTime] = useState(false);
  const [showMenu, setShowMenu] = useState(false);
  const [debugTypes, setDebugTypes] = useState([]);
  const [showDebug, setShowDebug] = useState(false);
  const [showToolFlowChangeWarning, setShowToolFlowChangeWarning] = useState(false);
  const { tenantId, appId } = useParams();
  const appRef = useRef(null);
  const flowIdRef = useRef(null);
  const elsaRunningCtl = useRef(null);
  const flowContext = {
    type,
    appInfo: type ? appInfo : flowInfo,
    setFlowInfo,
    showTime,
    setShowTime
  }
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
  useEffect(() => {
    if (!type) return;
    dispatch(setTestTime(null));
    dispatch(setTestStatus(null));
    elsaRunningCtl.current?.reset();
  }, [props.type])

  // 显示隐藏左侧菜单
  function menuClick() {
    setShowMenu(!showMenu)
  }
  // 测试
  const handleDebugClick = () => {
    window.agent.validate().then(() => {
      setDebugTypes(window.agent.getFlowRunInputMetaData());
      setShowDebug(true);
    }).catch(err => {
      let str = typeof (err) === 'string' ? err : t('plsEnterFlowRequiredItem');
      Message({ type: 'warning', content: str });
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
        <FlowTest
          setShowDebug={setShowDebug}
          showDebug={showDebug}
          debugTypes={debugTypes}
          appRef={appRef}
          elsaRunningCtl={elsaRunningCtl}
          setShowFlowChangeWarning={type ? setShowFlowChangeWarning : setShowToolFlowChangeWarning}
        />
        <div className={['content', !type ? 'content-add' : null].join(' ')}>
          {
            showMenu ? (
              <LeftMenu
                menuClick={menuClick}
                dragData={dragData}
                setDragData={setDragData}
              />
            ) : (
                <Tooltip placement='rightTop' title={t('expandArrange')}>
                  <div className='menu-icon' onClick={menuClick}>
                    <ConfigFlowIcon />
                  </div>
                </Tooltip>
              )
          }
          <Stage
            setDragData={setDragData}
            appRef={appRef}
            flowIdRef={flowIdRef}
            elsaRunningCtl={elsaRunningCtl}
            showFlowChangeWarning={type ? showFlowChangeWarning : showToolFlowChangeWarning}
            setShowFlowChangeWarning={type ? setShowFlowChangeWarning : setShowToolFlowChangeWarning}
          />
        </div>
      </FlowContext.Provider>
    </div>
  )}</>
};
export default AddFlow;
