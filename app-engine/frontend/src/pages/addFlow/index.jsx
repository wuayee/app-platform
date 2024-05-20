import React, { useEffect, useState, useContext, useCallback, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Tooltip, Tabs, Input } from "antd";
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
  ConfigFlowIcon,
  IfIcon,
  FitIcon
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
import { configMap } from "./config";
import { getAddFlowConfig } from "../../shared/http/appBuilder";
const { Search } = Input;

const AddFlow = (props) => {
  const { type } = props;
  const [ dragData, setDragData ] = useState([]);
  const { tenantId, appId } = useParams();
  const [ timestamp, setTimestamp ] = useState(new Date());
  const [ addId, setAddId ] = useState('');
  const [ added, setAdded ] = useState(false);
  const [ waterFlowName, setWaterFlowName ] = useState('无标题');
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
  const { aippInfo }  = useContext(AippContext);
  const navigate = useNavigate();
  const appRef = useRef(null);
  const addRef = useRef(false);
  const isChange = useRef(false);
  const flowIdRef = useRef(null);
  let editRef = useRef(null);
  let modalRef = useRef(null);
  const { CONFIGS } = configMap[process.env.NODE_ENV];
  useEffect(() => {
    type ? null : setShowMenu(true);
  }, [props.type])
  // 拖拽完成回调
  function handleDragEnter(e) {
    const nodeTab = e.dataTransfer.getData('itemTab');
    let nodeType;
    switch (nodeTab) {
        case 'basic':
            nodeType = e.dataTransfer.getData('itemType');
            window.agent.createNode(nodeType, e);
            break;
        case 'tool':
            nodeType = e.dataTransfer.getData('itemType');
            let nodeMetaData = e.dataTransfer.getData('itemMetaData');
            window.agent.createNode(nodeType, e, JSON.parse(nodeMetaData));
            break;
        default:
            break;
    }

  }
  useEffect(() => {
    window.agent = null;
    type ? setElsaData() : initElsa();
  }, [props.type]);
  // 新建工作流
  async function initElsa() {
    const timeStr = new Date().getTime().toString();
    const res = await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc3', { type: 'waterFlow', name: timeStr });
    if (res.code === 0) {
      setAdded(() => {
        addRef.current = true;
        return true;
      });
      flowIdRef.current = res.data.id;
      setAddId(res.data.id);
      appRef.current = res.data;
      setElsaData(appRef.current.flowGraph.appearance);
    }
  }
  // 编辑工作流
  function setElsaData(editData) {
    let graphData = editData || aippInfo.flowGraph?.appearance || {};
    const stageDom = document.getElementById('stage');
    let data = JSON.parse(JSON.stringify(graphData));
    let configIndex = CONFIGS.findIndex(item => item.node === 'llmNodeState');
    CONFIGS[configIndex].params.tenantId = tenantId;
    CONFIGS[configIndex].params.appId = appId;
    const importFiles = [
        () => import(/* webpackIgnore: true */`../chatPreview/components/runtimeForm/fileContentComponent.jsx`),
        () => import(/* webpackIgnore: true */`../chatPreview/components/runtimeForm/interviewQuestionsComponent.jsx`),
        () => import(/* webpackIgnore: true */`../chatPreview/components/runtimeForm/manageCubeCreateReportComponent.jsx`),
    ];
    JadeFlow.edit(stageDom, data, CONFIGS).then(agent => {
      window.agent ? null : window.agent = agent;
      agent.onChange(() => {
        handleSearch();
      })
    })
    getAddFlowConfig(tenantId).then(res => {
      if (res.code === 0) {
        setDragData(res.data);
      }
    });
  }
  // 保存回调
  function onFlowNameChange(params) {
    setModalInfo(() => {
      appRef.current.name = params.name;
      appRef.current.attributes.description = params.description;
      let list = JSON.parse(JSON.stringify(appRef.current))
      return list;
    })
    updateAppWorkFlow('waterFlow');
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
    if (!isChange.current) {
      isChange.current = true
      return
    }
    window.agent.validate().then(()=> {
      if (type) {
        aippInfo.flowGraph.appearance = graphChangeData;
        updateAppRunningFlow();
      } else {
        setModalInfo(() => {
          appRef.current.flowGraph.appearance = graphChangeData;
          return appRef.current;
        })
      }
    }).catch(err => {
      Message({ type: 'warning', content: '请输入必填项' });
    })
  }
  // 创建更新应用
  async function updateAppWorkFlow(optionType = undefined) {
    let id = type ? appId : flowIdRef.current;
    const res = await updateAippInfo(tenantId, id, appRef.current);
    if (res.code === 0) {
      setWaterFlowName(appRef.current.name);
      setTimestamp(new Date());
      optionType && editRef.current.handleCancel();
    } else {
      optionType && editRef.current.handleLoading();
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
            <span className='header-text'>{ waterFlowName }</span>
            <span className='header-edit'><EditIcon onClick={ handleEditClick } /></span>
            {/* { added && <span className='header-last-saved'>自动保存于 { formatTimeStamp(timestamp) }</span> } */}
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
      <PublishModal
        modalRef={modalRef}
        aippInfo={aippInfo}
        waterFlowName={waterFlowName}
        modalInfo={modalInfo}
        addId={addId}
        publishType="waterflow"
      />
      <EditTitleModal
        modalRef={editRef}
        onFlowNameChange={onFlowNameChange}
        waterFlowName={waterFlowName}
        modalInfo={modalInfo}
      />
    </div>
  )}</>
};

const LeftMenu = (props) => {
  const { type, dragData, menuClick } = props;

  // 搜索文本变化，更新工具列表
  const handleSearch = (value, event, source) => {
    console.log(value);
  }

  const getIconByType = (type) => {
    return {
      "startNodeStart": <StartIcon />,
      "retrievalNodeState": <DataRetrievalIcon />,
      "llmNodeState": <LlmIcon />,
      "endNodeEnd": <EndIcon />,
      "manualCheckNodeState": <ManualCheckIcon />,
      "fitInvokeNodeState": <FitIcon />,
      "conditionNodeCondition": <IfIcon />,
      "toolInvokeNodeState": <FitIcon />
    }[type];
  }

    const BasicItems = (props) => {
      const {dragData, tab} = props;
        return <>
            { tab === 'tool' &&
                <Search
                    placeholder="请输入搜索关键词"
                    allowClear
                    onSearch={handleSearch}
                />
            }
            { dragData.map((item, index) => {
                return (
                    <div
                        className='drag-item'
                        onDragStart={(e) => config[tab].dragFunc(item, e)}
                        draggable={true}
                        key={index}
                    >
                        <div className='drag-item-title'>
                            <div>
                                { tab === 'basic' ? getIconByType(item.type) : getIconByType("toolInvokeNodeState") }
                                <span className='content-node-name'>{ item.name }</span>
                            </div>
                            <span className='drag-item-icon' onClick={(event) => config[tab].addFunc(item, event)}><AddFlowIcon /></span>
                        </div>
                    </div>
                )
            })}
        </>
    }

    const config = {
      basic: {
          addFunc: (item, e) => handleClickAddBasicNode(item.type, e),
          dragFunc: (item, e) => handleDragBasicNode(item, e)
      },
      tool: {
          addFunc:(item, e) => handleClickAddToolNode("toolInvokeNodeState", e, item),
          dragFunc:(item, e) => handleDragToolNode(item, e)
      }
    }

    const items = [
      {
          key: 'basic',
          label: '基础',
          children: <BasicItems dragData={dragData.basic || []} tab={"basic"}/>,
          icon: <LeftArrowIcon onClick={menuClick} />
      },
      {
          key: 'tool',
          label: '工具',
          children: <BasicItems dragData={dragData.tool || []} tab={"tool"}/>,
      },
    ];

  const handleClickAddBasicNode = (type, e) => {
    e.clientX += 100;
    window.agent.createNode(type, e);
  }

  const handleClickAddToolNode = (type, e, metaData) => {
      e.clientX += 100;
      window.agent.createNode(type, e, metaData);
  }

  const handleDragBasicNode = (item, e) => {
      e.dataTransfer.setData('itemTab', 'basic');
      e.dataTransfer.setData('itemType', item.type);
  }

  const handleDragToolNode = (item, e) => {
      e.dataTransfer.setData('itemTab', 'tool');
      e.dataTransfer.setData('itemType', 'toolInvokeNodeState');
      e.dataTransfer.setData('itemMetaData', JSON.stringify(item));
  }

  return <>{(
    <div className='content-left'>
      {/*{*/}
      {/*  <div className='content-header'>*/}
      {/*    <LeftArrowIcon onClick={menuClick} />*/}
      {/*    <span className="flow-title">高级配置</span>*/}
      {/*  </div>*/}
      {/*}*/}
      {/*<div className="content-desc">将工作流节点拖进画布进行配置</div>*/}
        <Tabs defaultActiveKey="basic" items={items} />
    </div>
  )}</>
}

export default AddFlow;
