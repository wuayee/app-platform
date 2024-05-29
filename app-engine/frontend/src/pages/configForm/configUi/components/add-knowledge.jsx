
import React, { useImperativeHandle, useState, useRef } from 'react';
import { Drawer, Checkbox, Table, Button, Space } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { getKnowledges, getKnowledgesList } from "@shared/http/appBuilder";
import '../styles/add-knowledge.scss'

const AddKnowledge = (props) => {
  const { modalRef, tenantId, handleDataChange, checkData } = props;
  const [ open, setOpen] = useState(false);
  const [ knowledgeOptions, setKnowledgeOptions ] = useState([]);
  const [ knowledgeTable, setKnowledgeTable ] = useState( []);
  const [ knowledgeItem, setKnowledgeItem ] = useState(null);
  const [ selectedRowKeys, setSelectedRowKeys ] = useState([]);
  const searchName = useRef('');
  const knowledgeCurrent = useRef([]);
  const columns = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      render: (text) => <span style={{ display: 'flex', alignItems: 'center' }}>
                          <img src='/src/assets/images/ai/iconx.png' style={{ marginRight: '6px' }} />
                          {text}
                        </span>,
    },
    {
      title: '条数',
      dataIndex: 'recordNum',
      key: 'recordNum',
    },
    {
      title: '后端类型',
      dataIndex: 'serviceType',
      key: 'serviceType',
    },
  ]
  const cancle = () => {
    setOpen(false)
  }
  const showModal = () => {
    setOpen(true);
    handleGetKnowledgeOptions();
    setCheck();
  }
  // 设置选中
  const setCheck = () => {
    let arr = checkData.map(item => item.tableId);
    setSelectedRowKeys(arr);
  }
  // 获取左侧知识库列表
  const handleGetKnowledgeOptions = () => {
    const params = {
      tenantId,
      pageNum: 0,
      pageSize: 10,
      name: searchName.current
    };
    getKnowledges(params).then((res) => {
      if (res.code === 0) {
        const data = res.data.items;
        setKnowledgeOptions(data || []);
        data.length && getTableList(data[0]);
        knowledgeCurrent.current = JSON.parse(JSON.stringify(data));
      }
    })
  }
  // 获取右侧列表
  const getTableList = (item) => {
    setKnowledgeItem(item);
    const params = {
      tenantId,
      pageNum: 0,
      pageSize: 10,
      repoId: item.id
    };
    getKnowledgesList(params).then((res) => {
      if (res.code === 0) {
        let knowledgeItem = knowledgeCurrent.current.filter(cItem => cItem.id === item.id)[0];
        setKnowledgeTable(res.data.items || []);
        knowledgeItem.list = res.data.items;
      }
    })
  }
  // 表格选中
  const onSelectChange = (newSelectedRowKeys) => {
    let arr = [];
    setSelectedRowKeys(newSelectedRowKeys);
    knowledgeCurrent.current.forEach(item => {
      let obj = {};
      obj.name = item.name;
      obj.id = item.id;
      obj.show = false;
      obj.child = [];
      item.list?.forEach(lItem => {
        if (newSelectedRowKeys.includes(lItem.id)) {
          obj.child.push(lItem);
          obj.show = true;
        }
      });
      arr.push(obj);
    });
    console.log(arr);
  };
  const rowSelection = {
    selectedRowKeys,
    onChange: onSelectChange,
    preserveSelectedRowKeys: true
  };
  // 确定提交
  const confirm = () => {
    let arr = [];
    knowledgeCurrent.current.forEach(item => {
      item.list?.forEach(lItem => {
        if (selectedRowKeys.includes(lItem.id)) {
          let obj =  {
            'repoId': item.id,
            'tableId': lItem.id,
            'serviceType': lItem.serviceType,
            'recordNum': lItem.recordNum,
            'name': lItem.name
          }
          arr.push(obj);
        }
      })
    })
    handleDataChange(arr);
    setOpen(false);
  }
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  return <>{(
    <Drawer
      title='选择知识库'
      placement='right'
      width='1000px'
      closeIcon={false}
      onClose={cancle}
      open={open}
      footer={
        <div className="drawer-footer">
          <Button onClick={cancle}>取消</Button>
          <Button type="primary" onClick={confirm}>
            确定
          </Button>
        </div>
      }
      extra={
        <CloseOutlined onClick={cancle}/>
      }>
        <div className="knowledge-drawer">
          <div className="knowledge-check-list">
            <div className="knowledge-left">
              <div className="item">知识库</div>
              {
                knowledgeOptions?.map((item, index) => {
                  return (
                    <div className="item" key={index} onClick={() => getTableList(item)}>
                      <span>{item.name}</span>
                    </div>
                  )
                })
              }
            </div>
            <div className="knowledge-right">
              <div className="knowledge-details">
                <div className="left">
                  <img src='/src/assets/images/knowledge/knowledge-base.png' alt='' />
                </div>
                <div className="right">
                  <div className="knowledge-title">{knowledgeItem?.name}</div>
                  <div className="knowledge-user">{knowledgeItem?.ownerName}创建于：{knowledgeItem?.createdAt}</div>
                  <div className="knowledge-desc">{knowledgeItem?.description}</div>
                </div>
              </div>
              <Table 
                rowSelection={rowSelection}
                columns={columns} 
                dataSource={knowledgeTable} 
                rowKey={record => record.id}
              />
            </div>
          </div>
        </div>
    </Drawer>
  )}</>
};


export default AddKnowledge;
