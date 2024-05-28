import React, { useState, useEffect, useRef } from 'react';
import { Button, Divider, Input, Pagination, Tabs } from 'antd';
import { Icons } from '../../components/icons';
import { deleteAppApi, queryAppDevApi } from '../../shared/http/appDev.js';
import AppCard from '../../components/appCard';
import './index.scoped.scss';
import { debounce } from '../../shared/utils/common';
import EditModal from '../components/edit-modal';
import { HashRouter, Route, useNavigate, Routes } from 'react-router-dom';

const AppDev: React.FC = () => {
  const tenantId = '31f20efc7e0848deab6a6bc10fc3021e';
  const navigate = useNavigate();

  // 数据初始化
  const [appData, setAppData] = useState([]);
  async function queryApps() {
    const params = {
      offset: (pageNo.current - 1) * 10,
      limit: 10,
    };
    const res: any = await queryAppDevApi(tenantId, params);
    if (res.code === 0) {
      const { results, range } = res.data;
      const arr = results.map((v: any) => {
        const { description, icon } = v.attributes;
        return {
          ...v,
          description,
          icon,
        };
      });
      setAppData(arr);
      setTotal(range.total);
    }
  }
  useEffect(() => {
    queryApps();
  }, []);

  // tab栏
  const [activkey, setActiveKey] = useState('1');
  function tabChange(key: string) {
    setActiveKey(key);
  }

  // 分页
  const pageNo = useRef(1);
  const [total, setTotal] = useState(1);
  const [current, setCurrent] = useState(1);
  function currentPageChange(page: number, pageSize: number) {
    setCurrent(() => {
      pageNo.current = page;
      queryApps();
      return page;
    });
  }

  // 创建
  let modalRef: any = useRef();
  const [modalInfo, setModalInfo] = useState({});
  const create = () => {
    setModalInfo(() => {
      modalRef.current.showModal();
      return {
        name: '',
        attributes: {
          description: '',
          greeting: '',
          icon: '',
          app_type: '编程开发',
        },
      };
    });
  };
  function addAippCallBack(appId: string) {
    navigate(`/app/${tenantId}/detail/${appId}`);
  }

  // 搜索
  function onSearchValueChange(value: string) {}
  const handleSearch = debounce(onSearchValueChange, 500);

  // 点击卡片
  function clickCard(item: any, e: any) {
    navigate(`/app-develop/${tenantId}/appDetail/${item.id}`);
  }

  // 点击更多操作选项
  function clickMore(type: string, appId: string) {
    if (type === 'delete') {
      deleteApp(appId);
    }
  }
  // 删除
  async function deleteApp(appId: string) {
    const res: any = await deleteAppApi(tenantId, appId);
    if (res.code === 0) {
      if (appData.length === 1) {
        setCurrent(() => {
          pageNo.current = 1;
          queryApps();
          return 1;
        });
        return;
      }
      queryApps();
    }
  }

  return (
    <div className=' apps_root'>
      <div className='apps_header'>
        <div className='apps_title'>应用开发</div>
      </div>
      <div className='apps_main'>
        <div className='tabs'>
          <Tabs
            onChange={tabChange}
            defaultActiveKey={activkey}
            items={[
              {
                label: '我的应用',
                key: '1',
                children: '',
              },
              {
                label: '团队应用',
                key: '2',
                children: '',
                disabled: true,
              },
            ]}
          />
        </div>
        <div className='operatorArea'>
          <Button type='primary' onClick={create}>
            创建
          </Button>
          <Input
            placeholder='搜索'
            style={{ width: '200px', height: '35px', marginLeft: '16px' }}
            prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>
        <div className='card_list'>
          {appData.map((item: any) => (
            <div className='card_box' key={item.id} onClick={(e) => clickCard(item, e)}>
              <AppCard cardInfo={item} clickMore={clickMore} />
            </div>
          ))}
        </div>
        <div className='page_box'>
          <Pagination
            current={current}
            pageSize={10}
            onChange={currentPageChange}
            showSizeChanger={false}
            total={total}
            showTotal={(total) => `总条数 ${total}`}
          />
        </div>
      </div>
      {/*创建弹窗*/}
      <EditModal
        type='add'
        modalRef={modalRef}
        aippInfo={modalInfo}
        addAippCallBack={addAippCallBack}
      />
    </div>
  );
};
export default AppDev;
