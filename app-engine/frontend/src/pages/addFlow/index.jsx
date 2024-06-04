import React, {
  useEffect,
  useState,
  useCallback,
  useRef,
  useImperativeHandle
} from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Tooltip, Tabs, Input, Drawer, Form, InputNumber, Switch, Button, Modal } from "antd";
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
  FitIcon,
  CloseIcon,
  RunIcon
} from '@assets/icon';
import { JadeFlow } from '@fit-elsa/elsa-react';
import { debounce } from '../../shared/utils/common';
import { Message } from '../../shared/utils/message';
import { updateAippInfo, updateFlowInfo, reTestInstance, startInstance, getAippInfo } from '@shared/http/aipp';
import EditTitleModal from "../components/edit-title-modal.jsx";
import PublishModal from '../components/publish-modal.jsx';
import './styles/index.scss';
import { configMap } from "./config";
import { getAddFlowConfig } from "@shared/http/appBuilder";
import TestModal from "../components/test-modal";
import TestStatus from "../components/test-status";
const { Search } = Input;

const AddFlow = (props) => {
  const { type,aippInfo } = props;
  const [ dragData, setDragData ] = useState([]);
  const { tenantId, appId } = useParams();
  const [ timestamp, setTimestamp ] = useState(new Date());
  const [ addId, setAddId ] = useState('');
  const [ added, setAdded ] = useState(false);
  const [ waterFlowName, setWaterFlowName ] = useState('无标题');
  const [ showMenu, setShowMenu ] = useState(false);
  const [ showDebug, setShowDebug ] = useState(false);
  const [ isTested, setIsTested ] = useState(false);
  const [ testStatus, setTestStatus ] = useState('Running');
  const [ isTesting, setIsTesting ] = useState(false);
  const [ testTime, setTestTime ] = useState(0);
  const [ debugTypes, setDebugTypes ] = useState([]);
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
  const navigate = useNavigate();
  const appRef = useRef(null);
  const isChange = useRef(false);
  const flowIdRef = useRef(null);
  let editRef = useRef(null);
  let modalRef = useRef(null);
  let timerRef = useRef(null);
  let testRef = useRef(null);
  const [form] = Form.useForm();
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
    setIsTested(false);
    setIsTesting(false);
  }, [props.type]);
  // 新建工作流
  async function initElsa() {
    flowIdRef.current = appId;
    setAddId(appId);
    const res = await getAippInfo(tenantId, appId);
    if (res.code === 0) {
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
    JadeFlow.edit(stageDom, data, CONFIGS, importFiles).then(agent => {
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
    if (!isTested) {
      testRef.current.showModal();
      return;
    }
    modalRef.current.showModal();
  }
  // 编辑
  const handleEditClick = () => {
    editRef.current.showModal();
  }
  const handleBackClick = () => {
    navigate(-1);
  }
  // 测试
  const handleDebugClick = () => {
    window.agent.validate().then(()=> {
      setDebugTypes(window.agent.getFlowRunInputMetaData());
      setShowDebug(true);
    }).catch(err => {
      Message({ type: 'warning', content: '请输入流程必填项' });
    })
  }
  // 关闭测试抽屉
  const handleCloseDebug = () => {
    setShowDebug(false);
  }
  // 点击运行
  const handleRun = async (values) => {
    let appDto = type ? aippInfo : appRef.current;
    const params = {
      appDto,
      context: {
        initContext: values
      }
    };
    const res = await startInstance(tenantId, appId, params);
    if (res.code === 0) {
      const {aippCreate, instanceId} = res.data;
      setIsTesting(true);
      setTestStatus('Running');
      // 调用轮询
      startTestInstance(aippCreate.aippId, aippCreate.version, instanceId);
    }
  }
  // 判断是否流程结束
  const isEnd = (nodeInfos) => {
    return nodeInfos.some((value) => value.nodeType === 'END');
  }
  // 判断是否流程出错
  const isError = (nodeInfos) => {
    return nodeInfos.some((value) => value.status === 'ERROR');
  }
  // 测试轮询
  const startTestInstance = (aippId, version, instanceId) => {
    timerRef.current = setInterval(async () => {
      const res = await reTestInstance(tenantId, aippId, instanceId, version);
      if (res.code !== 0) {
        onStop( res.msg || '测试失败');
      }
      const runtimeData = res.data;
      if (runtimeData) {
        if (isError(runtimeData.nodeInfos)) {
          clearInterval(timerRef.current);
          setTestStatus('Error');
        } else if (isEnd(runtimeData.nodeInfos)) {
          clearInterval(timerRef.current);
          setIsTesting(false);
          setIsTested(true);
          setTestStatus('Finished');
        }
        window.agent.setFlowRunData(runtimeData.nodeInfos);
        const time = (runtimeData.executeTime / 1000).toFixed(3);
        setTestTime(time);
      }
    }, 1000);
  }
  // 终止轮询
  const onStop = (content) => {
    clearInterval(timerRef.current);
    Message({ type: 'warning', content: content });
  }
  // 展示上一次测试
  const handleDisplayLastRun = () => {

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
    if (type) {
      aippInfo.flowGraph.appearance = graphChangeData;
      updateAppRunningFlow();
    } else {
      setModalInfo(() => {
        appRef.current.flowGraph.appearance = graphChangeData;
        return appRef.current;
      })
      updateAppRunningFlow();
    }
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
    let id = type ? appId : flowIdRef.current;
    let params = type ?  aippInfo.flowGraph : appRef.current.flowGraph;
    const res = await updateFlowInfo(tenantId, id, params);
    if (res.code === 0) {
      Message({ type: 'success', content: type ? '高级配置更新成功': '工具流更新成功' })
    }
  }
  // 显示隐藏左侧菜单
  function menuClick() {
    setShowMenu(!showMenu)
  }
  //
  const handleRunTest = () => {
    window.agent.resetStatus();
    setIsTested(false);
    setTestTime(0);
    handleRun(form.getFieldsValue());
    handleCloseDebug();
  }

  const RenderFormItem = (props) => {
    const {type, name} = props;

    useEffect(() => {
      const value = form.getFieldValue(name);
      if (value && isNaN(value) && (type === 'Number' || type === 'Integer')) {
        form.setFieldValue(name, null);
      }
    }, [])

    const customLabel = (
      <span className='debug-form-label'>
      <span className='item-name'>{name}</span>
      <span className='item-type'>{type}</span>
    </span>
    );

    const validateNumber = (_, value) => {
      if (value === undefined || value === null || value === '') {
        return Promise.resolve();
      }
      if (isNaN(value)) {
        return Promise.reject(new Error('请输入一个有效的数字'));
      }
      return Promise.resolve();
    };

    const handleBlur = (value, isInteger) => {
      if (isNaN(value)) {
        form.setFieldValue(name, null);
        form.validateFields([name]);
      } else {
        form.setFieldValue(name, isInteger ? Math.floor(value) : value);
      }
    }

    return <>
      {type === 'String' &&
        <Form.Item
          name={name}
          label={customLabel}
          rules={[
            { required: true, message: '请输入字符串' },
          ]}
          className='debug-form-item'
        >
          <Input placeholder={`请输入${name}`} />
        </Form.Item>
      }
      {type === 'Integer' &&
        <Form.Item
          name={name}
          label={customLabel}
          initialValue={null}
          rules={[
            { required: true, message: '请输入一个整数' },
            { validator: validateNumber }
          ]}
          className='debug-form-item'
        >
          <InputNumber
            min={0}
            step={1}
            style={{width: '100%'}}
            placeholder={`请输入${name}`}
            onBlur={(e) => handleBlur(e.target.value, true)}
          />
        </Form.Item>
      }
      {type === 'Number' &&
        <Form.Item
          name={name}
          label={customLabel}
          initialValue={null}
          rules={[
            { required: true, message: '请输入一个数字' },
            { validator: validateNumber }
          ]}
          className='debug-form-item'
        >
          <InputNumber
            min={0}
            step={1}
            formatter={(value) => value > 1e21 ? 'Infinity' : value}
            style={{width: '100%'}}
            placeholder={`请输入${name}`}
            onBlur={(e) => handleBlur(e.target.value, false)}
          />
        </Form.Item>
      }
      {type === 'Boolean' &&
        <Form.Item
          name={name}
          label={customLabel}
          initialValue={true}
          rules={[
            { required: true, message: '请输入一个bool值' },
          ]}
          className='debug-form-item'
        >
          <Switch />
        </Form.Item>
      }
    </>
  }

  return <>{(
    <div className='add-flow-container'>
      {
        !type && <div className='header'>
          <div className='header-left'>
            <LeftArrowIcon className="icon-back" onClick={ handleBackClick } />
            <span className='header-text'>{ waterFlowName }</span>
            <span className='header-edit'><EditIcon onClick={ handleEditClick } /></span>
             { added && <span className='header-last-saved'>自动保存于 { formatTimeStamp(timestamp) }</span> }
            <TestStatus isTested={isTested} isTesting={isTesting} testTime={testTime} testStatus={testStatus}/>
          </div>
          <div className='header-grid'>
            {/*{isTested && <span className="header-btn last-run-btn" onClick={handleDisplayLastRun}>展示上一次运行</span>}*/}
            <span className="header-btn test-btn" onClick={handleDebugClick}>测试</span>
            <span className="header-btn" onClick={handleUploadFlow}><UploadIcon />发布</span>
          </div>
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
      <TestModal
        testRef={testRef}
        handleDebugClick={handleDebugClick}
      />
      <EditTitleModal
        modalRef={editRef}
        onFlowNameChange={onFlowNameChange}
        waterFlowName={waterFlowName}
        modalInfo={modalInfo}
      />
      <Drawer title={<h5>测试运行</h5>} open={showDebug} onClose={handleCloseDebug} width={600}
              footer={
                <div style={{ textAlign: 'right' }}>
                  <span onClick={handleRunTest} className="run-btn">
                    <RunIcon className="run-icon"/>运行
                  </span>
                </div>
              }
              closeIcon={
                <CloseIcon />
              }
      >
        <div className='debug'>
          <div className='debug-header'>
            <StartIcon className='header-icon' />
            <span className='header-title'>开始节点</span>
          </div>
          <Form
            form={form}
            layout="vertical"
            className="debug-form"
          >
            {debugTypes.map((debugType, index) => {
              return (
                <RenderFormItem type={debugType.type} name={debugType.name} key={index} />
              )
            })}
          </Form>
        </div>
      </Drawer>
    </div>
  )}</>
};

const LeftMenu = (props) => {
  const { type, dragData, menuClick } = props;

  // 搜索文本变化，更新工具列表
  const handleSearch = (value, event, source) => {
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
