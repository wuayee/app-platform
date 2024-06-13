
import React, { useImperativeHandle, useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Drawer, Tabs, Button, Input, Checkbox } from 'antd';
import { CloseOutlined, SearchOutlined } from '@ant-design/icons';
import { getPlugins } from '@shared/http/plugin';
import { getWaterFlows } from "@shared/http/appBuilder";
import { createAipp } from "@shared/http/aipp";
import { pluginItems } from '../../common/common';
import { Icons } from "../../../../components/icons";
import ToolCard from '../../../addFlow/components/tool-card';
import Pagination from '../../../../components/pagination/index';
import '../styles/add-skill.scss';
const { Search } = Input;

const AddSkill = (props) => {
  const { modalRef, tenantId, checkData, confirmCallBack } = props;
  const [ open, setOpen ] = useState(false);
  const [ name, setName ] = useState('');
  const [ pageNum, setPageNum ] = useState(1);
  const [ pageSize, setPageSize ] = useState(10);
  const [ total, setTotal ] = useState(0);
  const [ pluginCategory, setPluginCategory ] = useState(pluginItems[0].key);
  const [ pluginData, setPluginData ] = useState([]);
  const navigate = useNavigate();
  const checkedList = useRef([]);

  useEffect(()=> {
    open && getPluginList();
    setPluginCategory(pluginItems[0].key);
  }, [open, name, pageNum, pageSize]);

  useEffect(() => {
    checkedList.current = JSON.parse(JSON.stringify(checkData));
  }, [props.checkData])

  const showModal = () => {
    setOpen(true);
  }
  // 确定提交
  const confirm = () => {
    let workFlowList:any = [];
    let fitList:any = [];
    checkedList.current.forEach(item => {
      if (item.tags.includes('WATERFLOW')) {
        workFlowList.push(item);
      } else {
        fitList.push(item);
      }
    })
    let workFlowId = workFlowList.map(item => item.uniqueName);
    let fitId = fitList.map(item => item.uniqueName);
    confirmCallBack(workFlowId, fitId);
    setOpen(false);
  }  
  const selectCategory = (category: string) => {
    setPluginCategory(category);
    setPageNum(1);
    setName('');
    if (category === 'FIT') {
      getPluginList();
    } else {
      handleGetWaterFlows();
    }
  }
  // 获取插件列表
  const getPluginList = (category = pluginCategory)=> {
    getPlugins({ pageNum: pageNum - 1, pageSize, includeTags: 'FIT', name })
      .then(({ data, total }) => {
        setTotal(total);
        setDefaultCheck(data);
        setPluginData(data);
      })
  }
  // 获取工具流列表
  const handleGetWaterFlows = () => {
    const params = { pageNum: pageNum - 1, pageSize: 100, tenantId };
    getWaterFlows(params).then(async (res) => {
      if (res.code === 0) {
        let list = res.data.map(item => item.itemData)
        setDefaultCheck(list);
        setPluginData(list);
      }
    })
  }
  // 新增工具流
  const handleAddWaterFlow = async () => {
    const timeStr = new Date().getTime().toString();
    const res = await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc3', { type: 'waterFlow', name: timeStr });
    if (res.code === 0) {
      const aippId = res.data.id;
      navigate(`/app-develop/${tenantId}/app-detail/add-flow/${aippId}`);
    }
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
  // 选中
  const onChange = (e, item) => {
    item.checked = e.target.checked;
    if (e.target.checked) {
      checkedList.current.push(item);
    } else {
      checkedList.current = checkedList.current.filter(cItem => cItem.uniqueName !== item.uniqueName);
    }
  }
  // 设置默认选中
  const setDefaultCheck = (data) => {
    let nameList = checkedList.current.map(item => item.uniqueName);
    data.forEach(item => {
      item.checked = nameList.includes(item.uniqueName);
    })
  }
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  return <>
    <Drawer
      title='选择插件'
      placement='right'
      width='1230px'
      closeIcon={false}
      onClose={() => setOpen(false)}
      open={open}
      footer={
        <div className="drawer-footer">
          <Button onClick={() => setOpen(false)}>取消</Button>
          <Button type="primary" onClick={confirm}>确定</Button>
        </div>
      }
      extra={
        <CloseOutlined onClick={() => setOpen(false)}/>
      }>
      <div className="mashup-add-drawer">
        <div className="mashup-add-tab">
          <span className="active"><img src='/src/assets/images/ai/load.png' />市场</span>
          <span><img src='/src/assets/images/ai/account.png' />我的</span>
        </div>
        <div className="mashup-add-tablist">
          <Tabs
            items={pluginItems}
            activeKey={pluginCategory}
            onChange={(key: string) => selectCategory(key)}
          />
        </div>
        <div className="mashup-add-content">
          <div className="mashup-add-head">
            {
              pluginCategory !== 'WATERFLOW' ? (<Input
                placeholder="搜索"
                style={{
                  marginBottom: 16,
                  width: '200px',
                  borderRadius: '4px',
                  border: '1px solid rgb(230, 230, 230)',
                }}
                onPressEnter={(e) => filterByName(e.target.value)}
                prefix={<Icons.search color={'rgb(230, 230, 230)'}/>}
                defaultValue={name}
              />) : (<Button type="primary" onClick={handleAddWaterFlow} style={{ marginBottom: 16 }}>创建工具流</Button>)
            }
          </div>
          <div className="mashup-add-inner">
            {pluginData.map((card: any) => 
              <div className="mashup-add-item" key={card.uniqueName}>
                <ToolCard  pluginData={card} />
                <span className="check-item">
                  <Checkbox defaultChecked={card.checked} onChange={(e) => onChange(e, card)}></Checkbox>
                </span>
              </div>
            )}
          </div>
        </div>
        <div style={{ paddingTop: 16 }}>
          { pluginCategory === 'FIT' && <Pagination
              total={total}
              current={pageNum}
              onChange={selectPage}
              pageSize={pageSize}
          /> }
        </div>
      </div>
    </Drawer>
  </>
};
export default AddSkill;
