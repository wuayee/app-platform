
import React, { useEffect, useState, useContext, useCallback, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Tooltip } from "antd";
import {
  EditIcon,
  AddFlowIcon,
  LeftArrowIcon,
  UploadIcon,
  StartIcon,
  DataRetrievalIcon,
  EndIcon,
  ApiIcon,
  ManualCheckIcon,
  LlmIcon,
  ConfigFlowIcon
} from '@assets/icon';
import { AippContext } from '../aippIndex/context';
import { JadeFlow } from '../../shared/elsa-react/fit-elsa-react';
import { debounce } from '../../shared/utils/common';
import { Message } from '../../shared/utils/message';
import { createAipp, updateAippInfo, updateFlowInfo } from '../../shared/http/aipp';
import { graphData } from '../components/common/testFlowData';
import EditTitleModal from "../components/edit-title-modal.jsx";
import PublishModal from '../components/publish-modal.jsx';
import './styles/index.scss';
import {configMap} from "./config";

const AddFlow = (props) => {
  const { type } = props;
  const [ dragData, setDragData ] = useState([]);
  const { appId, tenantId } = useParams();
  const [ timestamp, setTimestamp ] = useState(new Date());
  const [ added, setAdded ] = useState(false);
  const [ showMenu, setShowMenu ] = useState(false);
  const [ modalInfo, setModalInfo ] = useState({
    name: '新建工具流',
    type: 'waterFlow',
    attributes: {
      description: ''
    },
    flowGraph: {
      appearance: null
    }
  });
  const { aippInfo }  = useContext(AippContext);
  const navigate = useNavigate();
  const appRef = useRef(null);
  const addRef = useRef(false);
  const flowIdRef = useRef(null);
  let editRef = React.createRef();
  let modalRef = React.createRef();
  const { CONFIGS } = configMap[process.env.NODE_ENV];
  useEffect(() => {
    type ? null : setShowMenu(true);
  }, [props.type])
  // 拖拽完成回调
  function handleDragEnter(e) {
    let nodeType = e.dataTransfer.getData('itemType');
    window.agent.createNode(nodeType, e);
  }
  useEffect(() => {
    window.agent = null;
    type ? setElsaData() : initElsa();
  }, [props.type]);
  // 新建工作流
  function initElsa() {
    const stageDom = document.getElementById('stage');
    JadeFlow.new(stageDom, CONFIGS).then(agent => {
      window.agent ? null : window.agent = agent;
      let list = agent.getAvailableNodes() || [];
      agent.onChange(() => {
        handleSearch();
      })
      setDragData(list);
      appRef.current = JSON.parse(JSON.stringify(modalInfo));
    })
  }
  // 编辑工作流
  function setElsaData() {
    let graphData = aippInfo.flowGraph?.appearance || {};
    const stageDom = document.getElementById('stage');
    let data = JSON.parse(JSON.stringify(graphData));
    JadeFlow.edit(stageDom, data, CONFIGS).then(agent => {
      window.agent ? null : window.agent = agent;
      let list = agent.getAvailableNodes() || [];
      agent.onChange(() => {
        handleSearch();
      })
      setDragData(list);
    })
  }
  // 保存回调
  function onFlowNameChange(params) {
    setModalInfo(() => {
      appRef.current.name = params.name;
      appRef.current.attributes.description = params.description;
      let list = JSON.parse(JSON.stringify(appRef.current))
      return list;
    })
    editRef.current.handleCancel();
    updateAppWorkFlow(addRef.current);
  }
  const handleSearch = useCallback(debounce((e) => elsaChange(e), 2000), []);
  // 发布
  const handleUploadFlow = () => {
    if (!addRef.current) {
      Message({ type: 'warning', content: '请先创建保存后再发布' })
      return
    }
    modalRef.current.showModal();
  }
  // 编辑
  const handleEditClick = () => {
    editRef.current.showModal();
  }
  const handleBackClick = () => {
    navigate(`/aipp/${tenantId}/detail/${appId}`);
  }
  const formatTimeStamp = (now) => {
      let hours = now.getHours().toString().padStart(2, '0');
      let minutes = now.getMinutes().toString().padStart(2, '0');
      let seconds = now.getSeconds().toString().padStart(2, '0');
      // 返回格式化后的时间字符串
      return `${hours}:${minutes}:${seconds}`;
  }
  // elsa 数据实时获取
  function elsaChange() {
    let graphChangeData = window.agent.serialize();
    if (type) {
      aippInfo.flowGraph.appearance = graphChangeData;
      updateAppRunningFlow();
    } else {
      setModalInfo(() => {
        appRef.current.flowGraph.appearance = graphChangeData;
        updateAppWorkFlow(addRef.current);
        return appRef.current;
      })
    }
  }
  // 创建更新应用
  async function updateAppWorkFlow(isAdd) {
    if (!isAdd) {
      const res = await createAipp(tenantId, appId, appRef.current);
      if (res.code === 0) {
        setAdded(() => {
          addRef.current = true;
          return true;
        });
        flowIdRef.current = res.data.id
        setTimestamp(new Date());
      } else {
        Message({ type: 'warning', content: res.msg || '新建工具流失败' })
      }
    } else {
      console.log(type);
      console.log(flowIdRef.current);
      let id = type ? appId : flowIdRef.current;
      const res = await updateAippInfo(tenantId, id, appRef.current);
      if (res.code === 0) {
        setTimestamp(new Date());
      } else {
        Message({ type: 'warning', content: res.msg || '更新工具流失败' })
      }
    }
  }
  // 编辑更新应用
  async function updateAppRunningFlow() {
    let params = aippInfo.flowGraph;
    const res = await updateFlowInfo(tenantId, appId, params);
    if (res.code === 0) {
      Message({ type: 'success', content: '高级配置更新成功' })
    }
  }
  // 显示隐藏左侧菜单
  function menuClick() {
    setShowMenu(!showMenu)
  }
  return <>{(
    <div className='add-flow-container'>
      {
        !type && <div className='header'>
          <div className='header-left'>
            <LeftArrowIcon className="icon-back" onClick={ handleBackClick } />
            <span className='header-text'>{ modalInfo.name }</span>
            <span className='header-edit'><EditIcon onClick={ handleEditClick } /></span>
            { added && <span className='header-last-saved'>自动保存于 { formatTimeStamp(timestamp) }</span> }
          </div>
          <span className="header-btn" onClick={handleUploadFlow}><UploadIcon />发布</span>
        </div>
      }
      <div className={['content', !type ? 'content-add' : null ].join(' ')}>
        {
          showMenu ? (
            <LeftMenu type={type} dragData={dragData} menuClick={menuClick}/>
          ) : (
            <Tooltip placement="rightTop" title="展开编排区">
              <div className="menu-icon" onClick={menuClick}>
                <ConfigFlowIcon />
              </div>
            </Tooltip>
            
          )
        }
        <div
          className='content-right'
          onDragOver={(e) => e.preventDefault()}
          onDrop ={handleDragEnter}>
            <div className='elsa-canvas' id='stage'></div>
        </div>
      </div>
      <PublishModal modalRef={modalRef} aippInfo={aippInfo} modalInfo={modalInfo} publishType="waterflow" />
      <EditTitleModal modalRef={editRef} onFlowNameChange={onFlowNameChange} modalInfo={modalInfo} />
    </div>
  )}</>
};

const LeftMenu = (props) => {
  const { type, dragData, menuClick } = props;

  const getIconByType = (type) => {
    return {
      "startNodeStart": <StartIcon />,
      "retrievalNodeState": <DataRetrievalIcon />,
      "llmNodeState": <LlmIcon />,
      "endNodeEnd": <EndIcon />,
      "manualCheckNodeState": <ManualCheckIcon />,
      "fitInvokeNodeState": <ApiIcon />
    }[type];
  }
  const handleClickAdd = (type, e) => {
    e.clientX += 100;
    window.agent.createNode(type, e);
  }
  return <>{(
    <div className='content-left'>
      {
        <div className='content-header'>
          <LeftArrowIcon onClick={menuClick} />
          <span className="flow-title">高级配置</span>
        </div>
      }
      <div className="content-desc">将工作流节点拖进画布进行配置</div>
      {
        dragData.map((item, index) => {
          return (
            <div
              className='drag-item'
              onDragStart={(e) => {e.dataTransfer.setData('itemType', item.type)}}
              draggable={true}
              key={index}
              >
              <div className='drag-item-title'>
                <div>
                  { getIconByType(item.type) }
                  <span className='content-node-name'>{ item.name }</span>
                </div>
                <span className='drag-item-icon' onClick={(event) => handleClickAdd(item.type, event)}><AddFlowIcon /></span>
              </div>
            </div>
          )
        })
      }
    </div>
  )}</>
}

export default AddFlow;
