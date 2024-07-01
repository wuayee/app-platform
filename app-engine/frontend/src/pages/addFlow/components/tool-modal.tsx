import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Input, Modal, Select, Button, Dropdown, Empty, Checkbox, Pagination, Spin } from 'antd';
import { DownOutlined } from '@ant-design/icons';
import { categoryItems } from '../../configForm/common/common';
import { handleClickAddToolNode } from '../utils';
import ToolCard from './tool-card';
import AddWaterFlow from './add-waterflow-drawer';
import '../styles/tool-modal.scss';
import { Message } from '@shared/utils/message';
import CreateWorkflow from './create-workflow';
import { getMyPlugin, getPlugins } from '../../../shared/http/plugin';
import { useAppSelector } from '../../../store/hook';

const { Search } = Input;
const { Option } = Select;

enum PluginTypeE {
  MARKET='market',
  OWER='owner'
}

const ToolDrawer = (props) => {
  const { showModal, setShowModal, checkData, confirmCallBack, type } = props;
  const [activeKey, setActiveKey] = useState('Builtin');
  const [loading, setLoading] = useState(false);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [pluginData, setPluginData] = useState([]);
  const currentUser = localStorage.getItem('currentUser') || '';
  const [open, setOpen] = useState(false);
  const checkedList = useRef([]);
  const [createWorkflowSignal, setCreateWorkflowSignal] = useState(false);
  const pluginList = useRef([]);
  const searchName = useRef(undefined);
  const listType = useRef('market');
  const navigate = useNavigate();
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const tab = [
    { name: 'Builtin', key: 'Builtin' },
    { name: 'HuggingFace', key: 'HUGGINGFACE' },
    { name: 'LangChain', key: 'LANGCHAIN' },
    { name: 'LlamaIndex', key: 'LLAMAINDEX' },
  ];

  useEffect(() => {
    showModal && getPluginList();
  }, [props.showModal, pageNum, pageSize, activeKey]);
  useEffect(() => {
    type === 'addSkill' && (checkedList.current = JSON.parse(JSON.stringify(checkData)));
  }, [props.checkData]);
  const items = categoryItems;
  const btnItems = [
    { key: 'tool', label: '插件' },
    { key: 'workflow', label: '工具流' },
  ];
  const handleChange = (value: string) => {
    setPageNum(1);
    listType.current = value;
    getPluginList();
  };
  const selectBefore = (
    <Select defaultValue='市场' onChange={handleChange}>
      <Option value={PluginTypeE.OWER}>个人</Option>
      <Option value={PluginTypeE.MARKET}>市场</Option>
    </Select>
  );
  const handleClick = (key) => {
    setPageNum(1);
    setActiveKey(key);
  };
  const createClick = async ({ key, domEvent }) => {
    if (key === 'tool') {
      navigate(`/plugin`);
    }
    if (key === 'workflow') {
      setCreateWorkflowSignal(domEvent?.timeStamp);
    }
  };
  // 获取插件列表
  const getPluginList = async () => {
    setLoading(true);

    let res;
    if (listType.current === PluginTypeE.MARKET) {
      res = await getPlugins({
        pageNum,
        pageSize,
        includeTags: activeKey,
        isPublished: true,
        name: searchName.current,
      });
    } else {
      res = await getMyPlugin(tenantId, {
        pageNum,
        pageSize,
      });
    }

    setLoading(false);
    const data=listType.current === PluginTypeE.MARKET? res?.data : res?.data?.toolData;
    if (activeKey === 'HUGGINGFACE') {
      data.forEach((item) => {
        (item.type = 'huggingFaceNodeState'),
          (item.context = {
            default_model: item.defaultModel,
          });
      });
    }
    setTotal(res?.data?.total);
    setDefaultCheck(data);
    setPluginData(data);
  };

  // 分页
  const selectPage = (curPage: number, curPageSize: number) => {
      setPageNum(curPage);
      setPageSize(curPageSize);
  };
  // 名称搜索
  const filterByName = (value: string) => {
    searchName.current = value.trim();
    setPageNum(1);
    getPluginList();
  };
  // 添加插件
  const toolClick = () => {
    checkedList.current.forEach((item, index) => {
      const type = item.type || 'toolInvokeNodeState';
      handleClickAddToolNode(type, { clientX: 400 + 10 * index, clientY: 300 + 10 * index }, item);
    });
    Message({ type: 'success', content: '添加插件成功' });
  };
  // 选中
  const onChange = (e, item) => {
    item.checked = e.target.checked;
    if (e.target.checked) {
      checkedList.current.push(item);
    } else {
      checkedList.current = checkedList.current.filter(
        (cItem) => cItem.uniqueName !== item.uniqueName
      );
    }
  };
  // 确定提交
  const confirm = () => {
    if (type !== 'addSkill') {
      toolClick();
    } else {
      const workFlowList: any = [];
      const fitList: any = [];
      checkedList.current.forEach((item) => {
        if (item.tags.includes('WATERFLOW')) {
          workFlowList.push(item);
        } else {
          fitList.push(item);
        }
      });
      const workFlowId = workFlowList.map((item) => item.uniqueName);
      const fitId = fitList.map((item) => item.uniqueName);
      confirmCallBack(workFlowId, fitId);
    }
    setShowModal(false);
  };
  // 设置默认选中
  const setDefaultCheck = (data) => {
    const nameList = checkedList.current.map((item) => item.uniqueName);
    data.forEach((item) => {
      item.checked = nameList.includes(item.uniqueName);
    });
  };
  return (
    <>
      <Modal
        title='更多插件'
        open={showModal}
        onCancel={() => setShowModal(false)}
        width='1100px'
        footer={
          <div className='drawer-footer'>
            <Button onClick={() => setShowModal(false)}>取消</Button>
            <Button type='primary' onClick={confirm}>
              确定
            </Button>
          </div>
        }
      >
        <div className='tool-modal-search'>
          <Search
            size='large'
            addonBefore={selectBefore}
            onSearch={filterByName}
            placeholder='请输入'
          />
          <Dropdown menu={{ items: btnItems, onClick: createClick }} trigger={['click']}>
            <Button type='primary' icon={<DownOutlined />}>
              创建
            </Button>
          </Dropdown>
        </div>
        <div className='tool-modal-tab'>
          {tab?.map((item) => (
            <span
              hidden={listType.current !== 'market'}
              className={activeKey === item.key ? 'active' : null}
              key={item.key}
              onClick={() => handleClick(item.key)}
            >
              <span className='text'>{item.name}</span>
              <span className='line' />
            </span>
          ))}
          <div className='tool-modal-drop'>
            {/* <Dropdown menu={{ items, onClick }} trigger={['click']}>
            <span>{ menuName }</span> 
          </Dropdown> */}
          </div>
        </div>
        <div className='mashup-add-content'>
          <Spin spinning={loading}>
            {pluginData.length > 0 && (
              <div className='mashup-add-inner' style={{ height: 'calc(100vh - 500px)' }}>
                {pluginData.map((card: any) => (
                  <div className='mashup-add-item' key={card.uniqueName}>
                    <ToolCard pluginData={card} />
                    <span className='opration-item'>
                      <Checkbox defaultChecked={card.checked} onChange={(e) => onChange(e, card)} />
                    </span>
                  </div>
                ))}
              </div>
            )}
            {!pluginData.length && (
              <div className='tool-empty'>
                <Empty description='暂无数据' />
              </div>
            )}
          </Spin>
        </div>
        <div style={{ paddingTop: 16 }}>
          <Pagination
            total={pluginData.length}
            current={pageNum}
            onChange={selectPage}
            pageSize={pageSize}
          />
        </div>
        <CreateWorkflow createWorkflowSignal={createWorkflowSignal} />
      </Modal>
      <AddWaterFlow open={open} setOpen={setOpen} />
    </>
  );
};

export default ToolDrawer;
