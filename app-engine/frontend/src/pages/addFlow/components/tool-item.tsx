
import React, { useEffect, useState, useRef } from 'react';
import { Input, Pagination, Empty, Spin, Select } from "antd";
import { handleClickAddToolNode, handleDragToolNode } from '../utils';
import { getToolList } from "@shared/http/aipp";
import ToolModal from './tool-modal';
import '../styles/tool-item.scss';
const { Search } = Input;
const { Option } = Select;

const ToolItem = (props) => {
  const { activeKey } = props;
  const [ toolKey, setToolKey ] = useState('Builtin');
  const [ loading, setLoading ] = useState(false);
  const [ pageNum, setPageNum ] = useState(1);
  const [ pluginData, setPluginData ] = useState([]);
  const [ showModal, setShowModal ] = useState(false);
  const currentUser = localStorage.getItem('currentUser') || '';
  const searchName = useRef('');
  const listType = useRef('all');
  useEffect(() => {
    getPluginList()
  }, [activeKey])
  const tab = [
    { name: '系统内置', key: 'Builtin' },
    { name: 'HuggingFace', key: 'HUGGINGFACE' },
    { name: 'LangChain', key: 'LANGCHAIN' },
  ];
  const handleChange = (value: string) => {
    setPageNum(1);
    listType.current = value;
    getPluginList();
  };
  const selectBefore = (
    <Select defaultValue="市场" onChange={handleChange}>
      <Option value="owner">个人</Option>
      <Option value="all">市场</Option>
    </Select>
  );
  // 获取插件列表
  const getPluginList = (key = undefined)=> {
    setLoading(true);
    let params:any = { pageNum: 1, pageSize: 200, includeTags: (key || toolKey), name: searchName.current };
    listType.current === 'owner' && (params.owner = currentUser)
    getToolList(params).then(res => {
      setLoading(false);
      if (res.code === 0) {
        if (activeKey === 'HUGGINGFACE') {
          res.data.forEach(item => {
            item.type = 'huggingFaceNodeState',
            item.context = {
              default_model: item.defaultModel
            }
          })
        };
        setPluginData(res.data);
      }
    });
  }
  const handleClick = (key) => {
    setPageNum(1);
    setToolKey(key);
    getPluginList(key);
  }
  // 分页
  const selectPage = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
  }
  // 名称搜索
  const filterByName = (value: string) => {
    searchName.current = value.trim();
    setPageNum(1);
    getPluginList();
  }
  return <>
    <Search 
      size="large" 
      addonBefore={selectBefore} 
      onSearch={filterByName}
      size="small"
      placeholder="请输入" />
    <div className="tool-tab">
      { tab.map(item => {
          return (
            <span className={ toolKey === item.key ? 'active' : null } 
              key={item.key} 
              onClick={() => handleClick(item.key)}
            >{ item.name }
            </span>
          )
        })
      }
      <span className="more" onClick={() => setShowModal(true)}>更多</span>
    </div>
    <Spin spinning={loading}>
      {
        pluginData.length > 0 && <div className="drag-list">
          { pluginData.map((item, index) => {
              return (
                <div
                  className='drag-item'
                  onDragStart={(e) => handleDragToolNode(item, e)}
                  draggable={true}
                  key={index}
                >
                  <div className='drag-item-title'>
                    <div>
                      <span className='content-node-name node-tool'>
                        <img src='/src/assets/images/ai/plugin.png' alt='' />
                        { item.name }
                      </span>
                    </div>
                    <span className='drag-item-icon' 
                      onClick={(event) => handleClickAddToolNode(item.type || 'toolInvokeNodeState', event, item)}>
                      <img src='/src/assets/images/ai/flow.png'  />
                    </span>
                  </div>
                </div>
              )
            })
          }
        </div>
      }
      { pluginData.length === 0 && <div className="tool-empty"><Empty description="暂无数据" /></div> }
    </Spin>
    <ToolModal showModal={showModal} setShowModal={setShowModal} />
  </>
};
export default ToolItem;
