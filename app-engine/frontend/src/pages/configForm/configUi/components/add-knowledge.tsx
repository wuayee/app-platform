
import React, { useImperativeHandle, useState, useRef } from 'react';
import { useHistory } from 'react-router-dom';
import { Drawer, Table, Button, Input, Dropdown, Select, Tag } from 'antd';
import { CloseOutlined, SearchOutlined, DownOutlined } from '@ant-design/icons';
import Pagination from '@/components/pagination';
import { getKnowledges, getKnowledgesList } from '@/shared/http/appBuilder';
import { formatDateTime } from '@/shared/utils/common';
import { useTranslation } from 'react-i18next';
import { listFormate } from '@/common/util';
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
  const [listPage, setListPage] = useState(1);
  const [knowledgeItem, setKnowledgeItem] = useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [total, setTotal] = useState(0);
  const [detailTotal, setDetailTotal] = useState(0);
  const [detailPage, setDetailPage] = useState(1);
  const searchName = useRef('');
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
    setKnowledgeList(listFormate(list));
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
  const handleGetKnowledgeOptions = () => {
    const params = {
      tenantId,
      pageNum: listPage - 1,
      pageSize: 20,
      name: searchName.current
    };
    getKnowledges(params).then((res) => {
      if (res.code === 0) {
        const data = res.data.items;
        setKnowledgeOptions(data || []);
        if (data.length) {
          getTableList(data[0]);
        }
        setTotal(res.data.total);
      }
    })
  }
  // 获取右侧列表
  const getTableList = (item) => {
    setKnowledgeItem(item);
    const params = {
      tenantId,
      pageNum: detailPage - 1,
      pageSize: 20,
      repoId: item.id
    };
    getKnowledgesList(params).then((res) => {
      if (res.code === 0) {
        let list = res.data.items || [];
        list.forEach(lItem => {
          lItem.repoId = item.id;
          lItem.parentName = item.name;
        });
        setDetailTotal(res.data.total || 0);
        setKnowledgeTable(list);
      }
    })
  }
  // 表格选中
  const onSelectChange = (record, selected) => {
    if (selected) {
      checkData.current.push({
        'repoId': record.repoId,
        'tableId': record.id,
        'serviceType': record.serviceType,
        'recordNum': record.recordNum,
        'name': record.name,
        'parentName': record.parentName
      })
    } else {
      checkData.current = checkData.current.filter(item => item.tableId !== record.id);
    }
    setKnowledgeList(listFormate(checkData.current));
  };
  const onSelectKeyChange = (newSelectedRowKeys) => {
    setSelectedRowKeys(newSelectedRowKeys);
  }
  // 取消选中
  const tagClose = (e, item) => {
    let selectedRowKeys = [];
    checkData.current = checkData.current.filter(cItem => cItem.tableId !== item.tableId);
    selectedRowKeys = checkData.current.map(item => item.tableId);
    setKnowledgeList(listFormate(checkData.current));
    setSelectedRowKeys(selectedRowKeys);
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
    onSelect: onSelectChange,
    onChange: onSelectKeyChange,
    preserveSelectedRowKeys: true
  };
  // 确定提交
  const confirm = () => {
    handleDataChange(checkData.current);
    setOpen(false);
  }
  // 搜索
  const onSearch = (value) => {
    searchName.current = value.trim();
    handleGetKnowledgeOptions();
  }
  // 分页
  const pageChange = (page) => {
    setListPage(page);
    handleGetKnowledgeOptions();
  }
  const detailPageChange = (page) => {
    setDetailPage(page);
    getTableList(knowledgeItem);
  }
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  });
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
              <div className='info' key={item.parentName}>
                <div className='info-left'>{item.parentName}</div>
                <div className='info-right'>
                  {item.data?.map(tItem => {
                    return <Tag closable key={tItem.name} onClose={(e) => tagClose(e, tItem)}>{tItem.name}</Tag>
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
                    <div className='item' key={index} onClick={() => getTableList(item)}>
                      <span className={knowledgeItem?.id === item.id ? 'active' : null}>{item.name}</span>
                    </div>
                  )
                })
              }
            </div>
            <div className='item-page'>
              <Pagination
                total={total}
                current={listPage}
                pageSize={20}
                showQuickJumper={false}
                showSizeChanger={false}
                onChange={pageChange} />
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
              pagination={false}
              dataSource={knowledgeTable}
              rowKey={record => record.id}
            />
            <div className='table-page'>
              <Pagination
                total={detailTotal}
                current={detailPage}
                pageSize={20}
                showQuickJumper={false}
                showSizeChanger={false}
                onChange={detailPageChange} />
            </div>
          </div>
        </div>
      </div>
    </Drawer>
  </>
};


export default AddKnowledge;
