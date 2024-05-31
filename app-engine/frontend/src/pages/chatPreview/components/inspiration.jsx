import React, { useEffect, useRef, useState, useContext } from "react";
import { Input, Button, Popover, Tree, Empty, Dropdown } from "antd";
import { useParams } from "react-router-dom";
import {
  SwapOutlined,
  EllipsisOutlined,
  SearchOutlined,
} from "@ant-design/icons";
import { Message } from "@shared/utils/message";
import { queryDepartMent, queryInspiration } from "../../../shared/http/aipp";
import { getUiD } from "../../../shared/utils/common";
import { inspirationMock } from "../common/config";
import { AippContext } from "../../aippIndex/context";
import {
  getDepth,
  delNodeChild,
  filterArr,
  arrayToTree,
  getDeepNode,
} from "../utils/inspiration-utils";
import "../styles/inspiration.scss";

const Inspiration = (props) => {
  const { chatType } = props;
  const {
    appId,
    tenantId,
    messageChecked,
    reloadInspiration,
    setPrompValue,
    refreshPrompValue,
    setRefreshPrompValue,
  } = useContext(AippContext);
  const { Search } = Input;
  const [showDrop, setShowDrop] = useState(false);
  const [promptTypeList, setPromptTypeList] = useState([]);
  const [dropList, setDropList] = useState([]);
  const [popoverOpen, setPopoverOpen] = useState(false);
  const [allPromptData, setAllPromptData] = useState([]);
  const [prompData, setPrompData] = useState([]);
  const [currentNodeId, setCurrentNodeId] = useState("");
  const [currentPromptType, setCurrentPromptType] = useState("-1");
  const [currentPromptName, setCurrentPromptName] = useState("");
  const [searchValue, setSearchValue] = useState("");
  const treeNormalData = useRef();
  const treeChildData = useRef([]);

  useEffect(() => {
    if(appId) {
      getList();
    }
  }, [reloadInspiration, appId]);
  // 获取灵感大全列表
  async function getList() {
    const res = await queryDepartMent(tenantId, appId);
    if (res.code === 0 && res.data?.length) {
      inspirationProcess(res.data[0]);
    }
  }
  // 灵感大全数据处理
  function inspirationProcess(data) {
    let childNodes = data.children;
    treeNormalData.current = JSON.parse(JSON.stringify(childNodes));
    if (childNodes.length) {
      let h = getDepth(childNodes);
      if (h === 1) {
        let arr = [{ title: "全部", id: "root" }];
        let arr1 = arr.concat(childNodes);
        arr1.push({ title: "其他", id: "others" });
        setPromptTypeList(arr1);
        setCurrentPromptType("root");
        getPromptList("root");
      } else {
        setShowDrop(true);
        let arr = JSON.parse(JSON.stringify(childNodes));
        let list = delNodeChild(arr);
        list.forEach((item) => {
          delete item.childrenEmpty;
        });
        let setArr = filterArr(list);
        setArr.forEach((item) => delete item.children);
        setArr = setArr.filter((item) => item.childrenEmpty === undefined);
        setArr = arrayToTree(setArr);
        setArr.push({
          children: [],
          title: "其他",
          id: "others",
          parent: "root:others",
        });
        setDropList(setArr);
        let obj = getDeepNode(setArr, (node) => {
          return !node.children.length;
        });
        let parentId = obj.parent.split(":")[1];
        nodeClick(obj.id, obj.title, parentId);
      }
    } else {
      getPromptList("root");
    }
  }
  // 根据节点获取灵感大全数据
  async function getPromptList(nodeId) {
    const res = await queryInspiration(tenantId, appId, nodeId);
    if (res.code === 0 && res.data) {
      setAllPromptData(res.data.inspirations);
      search(searchValue, res.data.inspirations);
    } else {
      setAllPromptData([]);
      search(searchValue, []);
    }
  }
  function onSearch(value) {
    setSearchValue(value.trim());
    search(value, allPromptData);
  }
  // 灵感过滤
  function search(e, allPromptData) {
    let arr = allPromptData;
    if (e.trim().length) {
      arr = allPromptData.filter((item) => item.name.indexOf(e) !== -1);
    }
    setPrompData(arr);
  }
  // 灵感大全分类点击
  function radioClick(item) {
    setCurrentPromptType(item.id);
    getPromptList(item.id);
  }
  // 灵感大全点击
  function handleClickPrompt(item) {
    if (messageChecked) {
      Message.warning("问答组勾选中, 请取消后再试");
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
  // 分类点击回调
  function nodeClick(id, name, parentId) {
    setCurrentPromptName(name);
    deepGetChild(treeNormalData.current, id);
    let arr = [{ title: "全部", id: parentId }];
    let arr1 = treeChildData.current.length
      ? arr.concat(treeChildData.current)
      : [];
    setCurrentPromptType(parentId);
    setCurrentNodeId(id);
    setPromptTypeList(parentId === "others" ? [] : arr1);
    getPromptList(parentId);
  }
  // 递归获取点击节点
  function deepGetChild(list, id) {
    list.forEach((item) => {
      if (item.id === id) {
        treeChildData.current = item.children;
      } else if (item.children?.length) {
        deepGetChild(item.children, id);
      }
    });
  }
  function handleOpenChange(newOpen) {
    setPopoverOpen(newOpen);
  }
  const items = [
    {
      key: "1",
      label: "编辑灵感",
    },
    {
      key: "2",
      label: "添加新灵感",
    },
  ];
  return (
    <>
      {
        <div
          className={[
            "inspiration-conyainer",
            !chatType ? "inspiration-active" : null,
          ].join(" ")}
        >
          <div className="right-content">
            <div className={showDrop ? 'has-drop title' : 'title'}>
              <span className="title-icon">
                <span className="inspiration-text">创意灵感</span>
              </span>
              { showDrop &&  (
                <Popover 
                  content={
                    <DropMenu 
                      treeList={dropList} 
                      hide={hide}
                      nodeId={currentNodeId}
                      nodeClick={nodeClick} />
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
            <div className="prompt-search">
              <Input
              disabled
                prefix={<SearchOutlined />}
                allowClear
                placeholder="搜索"
                onSearch={onSearch}
              />
            </div>
            <div className="prompt-container">
              <div className="prompt-type">
                {promptTypeList.map((item, index) => {
                  return (
                    <span
                      key={index}
                      title={item.title}
                      className={
                        currentPromptType === item.id
                          ? "prompt-type-active prompt-type-item"
                          : "prompt-type-item"
                      }
                      onClick={radioClick.bind(this, item)}
                    >
                      <span className="text"> {item.title}</span>
                      <span className="line"></span>
                    </span>
                    
                  );
                })}
              </div>
              {prompData && prompData.length ? (
                <div className="prompt-list">
                  {prompData.map((cItem, cIndex) => {
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
                  <Empty description="暂无灵感大全数据" />
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
  const { treeList, nodeClick, hide, nodeId } = props;

  // 树选中回调
  function onSelect(k, v) {
    if (v.node.children.length) {
      return;
    }
    let currentId = v.node.id;
    let nodeName = v.node.title;
    let parentId = v.node.parent.split(":")[1];
    nodeId !== currentId && nodeClick(currentId, nodeName, parentId);
    hide();
  }
  return (
    <>
      {
        <div className="drop-tree-menu">
          <Tree
            className="tree-inner"
            defaultExpandAll
            treeData={treeList}
            defaultSelectedKeys={[nodeId]}
            onSelect={onSelect}
            fieldNames={{ title: "title", key: "id", children: "children" }}
          />
        </div>
      }
    </>
  );
};

export default Inspiration;
