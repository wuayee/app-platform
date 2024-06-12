
import React, { useEffect, useState } from 'react';
import { Input, Modal, Select, Button, Dropdown } from 'antd';
import { getPlugins } from '@shared/http/plugin';
import { categoryItems } from '../../configForm/common/common';
import { handleClickAddToolNode } from '../utils';
import PluginCard from '../../../components/plugin-card';
import Pagination from '../../../components/pagination/index';
import '../styles/tool-modal.scss';
const { Search } = Input;
const { Option } = Select;

const ToolDrawer = (props) => {
  const { showModal, setShowModal } = props;
  const [ activeKey, setActiveKey ] = useState('1');
  const [ menuName, setMenuName ] = useState('新闻阅读');
  const [ name, setName ] = useState('');
  const [ pageNum, setPageNum ] = useState(1);
  const [ pageSize, setPageSize ] = useState(10);
  const [ total, setTotal ] = useState(0);
  const [ pluginCategory, setPluginCategory ] = useState(categoryItems[0].key);
  const [ pluginData, setPluginData ] = useState([]);
  const tab = [
    { name: '官方', key: '1' },
    { name: 'HuggingFace', key: '2' },
    { name: 'LangChain', key: '3' },
    { name: 'LlamaIndex', key: '4' },
  ]
  useEffect(() => {
    showModal && getPluginList();
  }, [props.showModal, name, pageNum, pageSize])
  const items = categoryItems;
  const selectBefore = (
    <Select defaultValue="市场">
      <Option value="个人">个人</Option>
      <Option value="市场">市场</Option>
    </Select>
  );
  const handleClick = (key) => {
    setActiveKey(key);
  }
  const onClick = ({ key }) => {
    let name = items.filter(item => item.key === key)[0].label;
    setMenuName(name);
  };
  // 获取插件列表
  const getPluginList = (category = pluginCategory)=> {
    getPlugins({ pageNum: pageNum - 1, pageSize, includeTags: 'FIT', name })
      .then(({ data, total }) => {
        setTotal(total);
        setPluginData(data);
      })
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
  const toolClick = (item) => {
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
        <Button type="primary">创建</Button>
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
          <Dropdown menu={{ items, onClick }} trigger={['click']}>
            <span>{ menuName }</span> 
          </Dropdown>
        </div>
      </div>
      <div className="mashup-add-content">
        <div className="mashup-add-inner" style={{ height: 'calc(100vh - 400px)' }}>
            {pluginData.map((card: any) => 
              <div className="mashup-add-item" key={card.uniqueName}>
                <PluginCard  pluginData={card} />
                <span className="opration-item" onClick={() => toolClick(card)}>
                  添加
                </span>
              </div>
            )}
          </div>
      </div>
      <div style={{ paddingTop: 16 }}>
        <Pagination
          total={total}
          current={pageNum}
          onChange={selectPage}
          pageSize={pageSize}
        /> 
      </div>
    </Modal>
  </>
};


export default ToolDrawer;
