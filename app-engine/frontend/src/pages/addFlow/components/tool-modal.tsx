
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Input, Modal, Select, Button, Dropdown, Empty } from 'antd';
import { getPlugins } from '@shared/http/plugin';
import { getAddFlowConfig } from '@shared/http/appBuilder';
import { categoryItems } from '../../configForm/common/common';
import { handleClickAddToolNode } from '../utils';
import ToolCard from './tool-card';
import Pagination from '../../../components/pagination/index';
import '../styles/tool-modal.scss';
const { Search } = Input;
const { Option } = Select;

const ToolDrawer = (props) => {
  const { showModal, setShowModal } = props;
  const [ activeKey, setActiveKey ] = useState('AUTHORITY');
  const [ menuName, setMenuName ] = useState('新闻阅读');
  const [ name, setName ] = useState('');
  const [ pageNum, setPageNum ] = useState(1);
  const [ pageSize, setPageSize ] = useState(10);
  const [ total, setTotal ] = useState(0);
  const [ pluginData, setPluginData ] = useState([]);
  const { tenantId, appId } = useParams();
  const tab = [
    { name: '官方', key: 'AUTHORITY' },
    { name: 'HuggingFace', key: 'HUGGINGFACE' },
    { name: 'LangChain', key: 'LANGCHAIN' },
    { name: 'LlamaIndex', key: 'LLAMAINDEX' },
  ]
  useEffect(() => {
    showModal && getPluginList();
  }, [props.showModal, name, pageNum, pageSize, activeKey])
  const items = categoryItems;
  const selectBefore = (
    <Select defaultValue="市场">
      <Option value="个人" disabled>个人</Option>
      <Option value="市场" disabled>市场</Option>
    </Select>
  );
  const handleClick = (key) => {
    setPageNum(1);
    setActiveKey(key);
  }
  const onClick = ({ key }) => {
    let name = items.filter(item => item.key === key)[0].label;
    setMenuName(name);
  };
  // 获取插件列表
  const getPluginList = ()=> {
    getAddFlowConfig(tenantId, {pageNum: 1, pageSize: 1000, tag: activeKey}).then(res => {
      if (res.code === 0) {
        if (activeKey === 'HUGGINGFACE') {
          res.data.tool.forEach(item => {
            item.type = 'huggingFaceNodeState',
            item.context = {
              default_model: item.defaultModel
            }
          })
        };
        setPluginData(res.data.tool);
      }
    });
  }
  // 分页
  const selectPage = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
  }
  // 名称搜索
  const filterByName = (value: string) => {
    if(value !== name) {
      setName(value);
    }
  }
  // 添加插件
  const toolClick = (e, item) => {
    e.stopPropagation();
    const type = item.type || 'toolInvokeNodeState';
    handleClickAddToolNode(type, { clientX: 400, clientY: 300}, item)
    setShowModal(false);
  }
  return <>
    <Modal 
      title='更多插件' 
      open={showModal} 
      onCancel={() => setShowModal(false)} 
      width='1230px'
      footer={null}
    >
      <div className="tool-modal-search">
        <Search size="large" addonBefore={selectBefore} onSearch={filterByName} placeholder="请输入" />
        {/* <Button type="primary">创建</Button> */}
      </div>
      <div className="tool-modal-tab">
        { tab.map(item => {
            return (
              <span className={ activeKey === item.key ? 'active' : null } 
                key={item.key} 
                onClick={() => handleClick(item.key)}
              >
                <span className="text">{ item.name }</span> 
                <span className="line"></span>
              </span>
            )
          })
        }
        <div className="tool-modal-drop">
          {/* <Dropdown menu={{ items, onClick }} trigger={['click']}>
            <span>{ menuName }</span> 
          </Dropdown> */}
        </div>
      </div>
      <div className="mashup-add-content">
        { pluginData.length > 0 && (
          <div className="mashup-add-inner" style={{ height: 'calc(100vh - 400px)' }}>
            {pluginData.slice((pageNum - 1)*pageSize, pageNum*pageSize).map((card: any) => 
              <div className="mashup-add-item" key={card.uniqueName}>
                <ToolCard  pluginData={card} />
                <span className="opration-item" onClick={(e) => toolClick(e, card)}>
                  添加
                </span>
              </div>
            )}
          </div>)
        }
        { !pluginData.length && <div className="tool-empty"><Empty description="暂无数据" /></div> }
      </div>
      <div style={{ paddingTop: 16 }}>
        <Pagination
          total={pluginData.length}
          current={pageNum}
          onChange={selectPage}
          pageSize={pageSize}
        /> 
      </div>
    </Modal>
  </>
};


export default ToolDrawer;
