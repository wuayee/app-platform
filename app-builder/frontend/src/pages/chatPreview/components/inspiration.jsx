import React, { useEffect, useRef, useState, useContext } from 'react';
import { Input, Button, Popover, Tree, Empty  } from 'antd';
import { useParams } from 'react-router-dom';
import { SwapOutlined } from '@ant-design/icons';
import { Message } from '../../../shared/utils/message';
import { queryDepartMent, queryInspiration } from '../../../shared/http/aipp';
import { getUiD } from '../../../shared/utils/common';
import { AippContext } from '../../aippIndex/context';
import '../styles/inspiration.scss';


const Inspiration = (props) => {
  const { chatType } = props;
  const {
    messageChecked,
    reloadInspiration,
    setPrompValue,
    refreshPrompValue,
    setRefreshPrompValue,
  } = useContext(AippContext);
  const { Search } = Input;
  const { appId, tenantId } = useParams();
  const [ showDrop, setShowDrop ] = useState(false);
  const [ promptTypeList, setPromptTypeList ] = useState([]);
  const [ dropList, setDropList ] = useState([]);
  const [ popoverOpen, setPopoverOpen ] = useState(false);
  const [ allPromptData, setAllPromptData ] = useState([]);
  const [ prompData, setPrompData ] = useState([]);
  const [ currentNodeId, setCurrentNodeId ] = useState('');
  const [ currentPromptType, setCurrentPromptType ] = useState('-1');
  const [ currentPromptName, setCurrentPromptName ] = useState('');
  const [ searchValue, setSearchValue ] = useState('');
  const categoriesEmpty = useRef(false);
  
  useEffect(() => {
    getList();
  }, [])
  useEffect(() => {
    currentNodeId && getList();
  }, [ reloadInspiration ])
  // 获取灵感大全列表
  async function getList() {
    const res = await queryDepartMent(tenantId, appId);
    if (res.code === 0 && res.data?.length) {
      setDeepNodeKey(res.data);
      let currentItem = res.data[0].children || [];
      let nodeId = '';
      let nodeName = '';
      if (currentItem.length) {
        let obj = getDeepNode(currentItem, (node) => {
          return !node.children.length
        });
        nodeId = obj.id;
        nodeName = obj.title;
      } else {
        nodeId = res.data[0].id;
        nodeName = res.data[0].title;
      }
      setDropList(res.data);
      setCurrentPromptName(nodeName);
      setShowDrop(true);
      getPromptList(nodeId);
    } else {
      getPromptList('root');
    }
  }
  // 递归获取第一个节点
  function getDeepNode(list, func) {
    for ( const node of list ) {
      if(func(node)) {
        return node
      }
      if (node.children.length) {
        const res = getDeepNode(node.children, func)
        if(res) {
          return res
        }
      }
    }
  }
  // 递归设置下拉树的key
  function setDeepNodeKey(list) {
    list.forEach(item => {
      item.key = item.id;
      if (item.children?.length) {
        setDeepNodeKey(item.children)
      }
    })
  }
  // 根据节点获取灵感大全数据
  async function getPromptList(nodeId, nodeName = undefined) {
    nodeName && setCurrentPromptName(nodeName);
    setCurrentPromptType('-1');
    setCurrentNodeId(nodeId);
    const res = await queryInspiration(tenantId, appId, nodeId);
    if (res.code === 0 && res.data) {
      let arr = [{ title: '全部', parent: '-1' }];
      let arr1 = res.data.categories?.length ? arr.concat(res.data.categories) : [];
      categoriesEmpty.current = (arr1.length === 0);
      setPromptTypeList(arr1);
      setAllPromptData(res.data.inspirations || []);
      search(searchValue, '-1', res.data.inspirations);
    } 
  }
  function onSearch(value) {
    setSearchValue(value.trim());
    search(value, currentPromptType, allPromptData);
  }
  // 灵感过滤
  function search(e, currentPromptType, allPromptData) {
    if (e.trim().length) {
      if (currentPromptType === '-1') {
        let arr = allPromptData.filter((item) => item.name.indexOf(e) !== -1);
        setPrompData(arr);
      } else {
        let arr = allPromptData.filter((item) => item.name.indexOf(e) !== -1 && item.category === currentPromptType )
        setPrompData(arr);
      }
    } else {
      setSearchValue('');
      if (currentPromptType === '-1' || categoriesEmpty.current) {
        setPrompData(allPromptData);
      } else {
        let arr = allPromptData.filter((item) => item.category === currentPromptType);
        setPrompData(arr);
      }
    }
  }
  // 灵感大全分类点击
  function radioClick(item) {
    setCurrentPromptType(item.parent);
    search(searchValue, item.parent, allPromptData);
  }
  // 灵感大全点击
  function handleClickPrompt(item) {
    if (messageChecked) {
      Message.warning('问答组勾选中, 请取消后再试');
      return;
    }
    item.key = getUiD();
    setPrompValue(item);
    setRefreshPrompValue(!refreshPrompValue);
  }
  // 隐藏下拉框
  function hide() {
    setPopoverOpen(false);
  }
  function handleOpenChange(newOpen) {
    setPopoverOpen(newOpen);
  };
  return (
    <>
      {
        <div className={["inspiration-conyainer", !chatType ? 'inspiration-active' : null].join(' ')}>
          <div className="right-content">
            <div className={showDrop ? 'has-drop title' : 'title'}>
              <span className="title-icon">
                <span className="inspiration-text">灵感大全</span>
                {/* <PlusCircleOutlined
                  style={{ fontSize: '16px', marginLeft: '8px', marginTop: '4px' }}
                  onClick={addInspiration}
                /> */}
              </span>
              <Search
                className="prompt-search"
                size="small"
                allowClear
                onSearch={onSearch}
                variant="borderless"
                style={{ width: 120 }}
              />
              { showDrop &&  (
                <Popover 
                  content={
                    <DropMenu 
                      treeList={dropList} 
                      hide={hide}
                      nodeId={currentNodeId}
                      getPromptList={getPromptList} />
                    } 
                  open={popoverOpen} 
                  onOpenChange={handleOpenChange}
                  arrow={false} 
                  trigger="click" 
                  placement="bottomRight"
                >
                  <Button size="small" icon={<SwapOutlined />} >
                    <span className="btn-text" title={currentPromptName}>{ currentPromptName }</span>
                  </Button>
                </Popover>
              )}
            </div>
              <div className="prompt-container">
                <div className="prompt-type">
                  { promptTypeList.map((item, index) => {
                    return (
                      <span
                        key={index}
                        className={
                          currentPromptType === item.parent
                            ? 'prompt-type-active prompt-type-item'
                            : 'prompt-type-item'
                        }
                        onClick={radioClick.bind(this, item)}
                      >
                        {item.title}
                      </span>
                    );
                    })
                  }
                </div>
               { (prompData && prompData.length) ? (
                 <div className="prompt-list">
                 { prompData.map((cItem, cIndex) => {
                   return (
                     <div
                       key={cIndex}
                       className="prompt-item"
                       onClick={handleClickPrompt.bind(this, cItem)}
                     >
                       <div className="title"> {cItem.name}</div>
                       <div
                         className="content text-mul-ellipsis"
                         title={cItem.description}
                       >
                         {cItem.description}
                       </div>
                     </div>
                   );
                 })}
               </div>
               ) : (
               <div className="prompt-empty">
                  <Empty description="暂无灵感大全数据"/>
                </div>
              )}
              </div>
          </div>
        </div>
      }
    </>
  );
};

const DropMenu = (props) => {
  const { treeList, getPromptList, hide, nodeId } = props;

  // 树选中回调
  function onSelect(k, v) {
    if (v.node.children.length) {
      return
    }
    let currentId = v.node.id;
    let nodeName = v.node.title;
    nodeId !== currentId && getPromptList(currentId, nodeName);
    hide();
  }
  return <>{(
    <div className="drop-tree-menu">
      <Tree
        defaultExpandAll
        treeData={treeList}
        defaultSelectedKeys={[nodeId]}
        onSelect={onSelect}
      />
    </div>
  )}</>
}

export default Inspiration;
