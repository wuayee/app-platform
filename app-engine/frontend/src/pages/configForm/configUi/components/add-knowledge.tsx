
import React, { useImperativeHandle, useState, useRef, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import { Drawer, Pagination, Table, Button, Input, Dropdown, Select, Tag } from 'antd';
import { CloseOutlined, SearchOutlined, DownOutlined } from '@ant-design/icons';
import { getKnowledges, getKnowledgesList } from '@/shared/http/appBuilder';
import { formatDateTime } from '@/shared/utils/common';
import { useTranslation } from 'react-i18next';
import '../styles/add-knowledge.scss';
const { Search } = Input;
const { Option } = Select;

const AddKnowledge = (props) => {
  const { t } = useTranslation();
  const { modalRef, tenantId, handleDataChange } = props;
  const [open, setOpen] = useState(false);
  const [knowledgeOptions, setKnowledgeOptions] = useState([]);
  const [knowledgeTable, setKnowledgeTable] = useState([]);
  const [knowledgeList, setKnowledgeList] = useState([]);
  const [knowledgeItem, setKnowledgeItem] = useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [total, setTotal] = useState(0);
  const searchName = useRef('');
  const knowledgeCurrent = useRef([]);
  const checkData = useRef([]);
  const navigate = useHistory().push;
  const columns = [
    {
      title: t('name'),
      dataIndex: 'name',
      key: 'name',
      render: (text) => <span style={{ display: 'flex', alignItems: 'center' }}>
        <img src='./src/assets/images/ai/iconx.png' style={{ marginRight: '6px' }} />
        {text}
      </span>,
    },
    {
      title: t('numberOfPieces'),
      dataIndex: 'recordNum',
      key: 'recordNum',
      sorter: (a, b) => a.recordNum - b.recordNum,
    },
    {
      title: t('backendType'),
      dataIndex: 'serviceType',
      key: 'serviceType',
    },
  ]
  const selectBefore = (
    <Select defaultValue={t('market')}>
      <Option value={t('owner')} disabled>{t('owner')}</Option>
      <Option value={t('market')} disabled>{t('market')}</Option>
    </Select>
  );
  const btnItems = [
    { key: 'knowledge', label: t('knowledgeBase') }
  ];
  const cancel = () => {
    setOpen(false)
  }
  const showModal = (list = []) => {
    checkData.current = list;
    setOpen(true);
    setCheck();
  }
  // 设置选中列表
  const setCheck = () => {
    let arr = checkData.current.map(item => Number(item.tableId));
    setSelectedRowKeys(arr);
    handleGetKnowledgeOptions();
  }
  // 获取左侧知识库列表
  const handleGetKnowledgeOptions = (page = null) => {
    const params = {
      tenantId,
      pageNum: page || 0,
      pageSize: 100,
      name: searchName.current
    };
    getKnowledges(params).then((res) => {
      if (res.code === 0) {
        const data = res.data.items;
        setKnowledgeOptions(data || []);
        if (data.length) {
          initTagList(data);
          setKnowledgeItem(data[0]);
        }
        setTotal(res.data.total);
      }
    })
  }
  // 初始化tag
  const initTagList = (data) => {
    let arr = [];
    data.forEach(item => {
      let list = checkData.current.filter(cItem => Number(cItem.repoId) === item.id);
      item.child = [];
      if (list.length) {
        let obj = {
          name: item.name,
          id: item.id,
          child: list
        }
        arr.push(obj);
        item.child = list;
      }
    });
    setKnowledgeList(arr);
    knowledgeCurrent.current = JSON.parse(JSON.stringify(data));
  }
  // 获取右侧列表
  const getTableList = (item) => {
    const params = {
      tenantId,
      pageNum: 0,
      pageSize: 100,
      repoId: item.id
    };
    getKnowledgesList(params).then((res) => {
      if (res.code === 0) {
        let knowledgeItem = knowledgeCurrent.current.filter(cItem => cItem.id === item.id)[0];
        setKnowledgeTable(res.data.items || []);
        knowledgeItem ? knowledgeItem.list = res.data.items : null;
      }
    })
  }
  // 表格选中
  const onSelectChange = (newSelectedRowKeys) => {
    let arr = [];
    setSelectedRowKeys(newSelectedRowKeys);
    knowledgeCurrent.current.forEach(item => {
      if (item.id === knowledgeItem.id) {
        let obj = {};
        obj.name = item.name;
        obj.id = item.id;
        obj.child = [];
        item.list?.forEach(lItem => {
          if (newSelectedRowKeys.includes(lItem.id)) {
            obj.child.push(lItem);
          }
        });
        item.child = obj.child;
        arr.push(obj);
      } else {
        arr.push(item);
      }
    });
    let list = arr.filter(item => item.child.length > 0);
    setKnowledgeList(list);
  };
  // 取消选中
  const tagClose = (e, item) => {
    let list = JSON.parse(JSON.stringify(knowledgeList));
    let keyArr = selectedRowKeys.filter(kItem => kItem !== (item.tableId || item.id));
    let ListItem = list.filter(kItem => kItem.id === (item.repoId || item.repositoryId))[0];
    ListItem.child = ListItem.child.filter(lItem => lItem.tableId !== item.tableId);
    list = list.filter(lItem => lItem.child.length > 0);
    setSelectedRowKeys(keyArr);
    setKnowledgeList(list);
  }
  // 创建知识库
  const createClick = ({ key }) => {
    if (window.self !== window.top) {
      window.parent.location.href = `${window.parent.location.origin}/#/model-knowledge/create`;
    } else {
      navigate(`/knowledge-base/create`);
    }
  }
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
          let obj = {
            'repoId': item.id,
            'tableId': lItem.id,
            'serviceType': lItem.serviceType,
            'recordNum': lItem.recordNum,
            'name': lItem.name
          }
          arr.push(obj);
        }
      })
    });
    handleDataChange(arr);
    setOpen(false);
  }
  // 搜索
  const onSearch = (value) => {
    searchName.current = value.trim();
    handleGetKnowledgeOptions();
  }
  // 分页
  const pageChange = (page) => {
    handleGetKnowledgeOptions(page - 1);
  }
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  useEffect(() => {
    knowledgeItem?.id ? getTableList(knowledgeItem) : setKnowledgeTable([]);
  }, [knowledgeItem]);

  return <>
    <Drawer
      title={t('selectRepository')}
      placement='right'
      width='1000px'
      closeIcon={false}
      onClose={cancel}
      open={open}
      footer={
        <div className='drawer-footer'>
          <Button onClick={cancel}>{t('cancel')}</Button>
          <Button type='primary' onClick={confirm}>
            {t('ok')}
          </Button>
        </div>
      }
      extra={
        <CloseOutlined onClick={cancel} />
      }>
      <div className='mashup-add-drawer'>
        <div className='knowledge-search'>
          <Search
            prefix={<SearchOutlined />}
            addonBefore={selectBefore}
            allowClear
            placeholder={t('search')}
            onSearch={onSearch}
          />
          <Dropdown menu={{ items: btnItems, onClick: createClick }} trigger={['click']}>
            <Button type='primary' icon={<DownOutlined />}>{t('create')}</Button>
          </Dropdown>
        </div>
        <div className='knowledge-check-info'>
          {knowledgeList.map(item => {
            return (
              <div className='info' key={item.name}>
                <div className='info-left'>{item.name}</div>
                <div className='info-right'>
                  {item.child?.map(tItem => {
                    return <Tag closeIcon key={tItem.name} onClose={(e) => tagClose(e, tItem)}>{tItem.name}</Tag>
                  })}
                </div>
              </div>
            )
          })
          }
        </div>
        <div className='knowledge-check-list'>
          <div className='knowledge-left'>
            <div className='item-title'>{t('knowledgeBase')}</div>
            <div className='item-inner'>
              {
                knowledgeOptions?.map((item, index) => {
                  return (
                    <div className='item' key={index} onClick={() => setKnowledgeItem(item)}>
                      <span className={knowledgeItem?.id === item.id ? 'active' : null}>{item.name}</span>
                    </div>
                  )
                })
              }
            </div>
            <div className='item-page'>
              <Pagination total={total} pageSize={100} onChange={pageChange} />
            </div>
          </div>
          <div className='knowledge-right'>
            <div className='knowledge-details'>
              <div className='left'>
                <img src='./src/assets/images/knowledge/knowledge-base.png' alt='' />
              </div>
              <div className='right'>
                <div className='knowledge-title'>{knowledgeItem?.name}</div>
                <div className='knowledge-user'>{knowledgeItem?.ownerName}
                  {t('createAt')}：{formatDateTime(knowledgeItem?.createdAt)}
                </div>
                <div className='knowledge-desc'>{knowledgeItem?.description}</div>
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
  </>
};


export default AddKnowledge;
