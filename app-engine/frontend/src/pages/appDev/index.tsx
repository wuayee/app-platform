import React, { useState, useEffect, useRef } from 'react';
import { Button, Input, Tabs, Modal, notification } from 'antd';
import { useHistory } from 'react-router-dom';
import { Icons } from '@/components/icons';
import AppCard from '@/components/appCard';
import EditModal from '../components/edit-modal';
import Pagination from '@/components/pagination';
import Empty from '@/components/empty/empty-item';
import { deleteAppApi, getUserCollectionNoDesc, queryAppDevApi } from '@/shared/http/appDev.js';
import { debounce } from '@/shared/utils/common';
import { useAppDispatch } from '@/store/hook';
import { setCollectionValue } from '@/store/collection/collection';
import { Message } from '@/shared/utils/message';
import { TENANT_ID } from '../chatPreview/components/send-editor/common/config';
import { useTranslation } from 'react-i18next';
import './index.scoped.scss';

const AppDev: React.FC = () => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const tenantId = TENANT_ID;
  const currentId = useRef('');
  const navigate = useHistory().push;
  const [api, contextHolder] = notification.useNotification();
  // 数据初始化
  const [appData, setAppData] = useState([]);
  async function queryApps() {
    const params = {
      offset: (page - 1) * pageSize,
      limit: pageSize,
      name: search || undefined
    };
    const res: any = await queryAppDevApi(tenantId, params);
    if (res.code === 0) {
      const { results, range } = res.data;
      const arr = results.map((v: any) => {
        const { description, icon } = v.attributes;
        return {
          ...v,
          description,
          icon
        };
      });
      setAppData(arr);
      setTotal(range.total);
    }
  }
  // tab栏
  const [activkey, setActiveKey] = useState('1');
  function tabChange(key: string) {
    setActiveKey(key);
  }
  // 分页
  const pageNo = useRef(1);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(8);
  const [search, setSearch] = useState('');
  const paginationChange = (curPage: number, curPageSize: number) => {
    if (page !== curPage) {
      setPage(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
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
          app_type: t('programmingDevelopment'),
        },
      };
    });
  };
  function addAippCallBack(appId: string) {
    navigate(`/app-develop/${tenantId}/app-detail/${appId}`);
  }
  // 搜索
  function onSearchValueChange(newSearchVal: string) {
    if (newSearchVal !== search) {
      setPage(1);
      setSearch(newSearchVal);
    }
  }
  const handleSearch = debounce(onSearchValueChange, 500);
  // 点击卡片
  function clickCard(item: any, e: any) {
    navigate(`/app-develop/${tenantId}/appDetail/${item.id}`);
  }

  // 点击更多操作选项
  function clickMore(type: string, appId: string) {
    if (type === 'delete') {
      currentId.current = appId;
      setOpen(true);
    }
  }
  // 删除
  async function deleteApp() {
    setLoading(true);
    try {
      const res: any = await deleteAppApi(tenantId, currentId.current);
      if (res.code === 0) {
        queryApps();
        setOpen(false);
        Message({ type: 'success', content: t('deleteAppSuccess') });
        if (appData.length === 1) {
          setPage(1);
          return;
        }
      } else {
        Message({ type: 'error', content: res.msg || t('deleteFail') });
      }
    } finally {
      setLoading(false);
    }
  }
  const dispatch = useAppDispatch();
  // 获取当前登录用户名
  const getLoaclUser = () => {
    return localStorage.getItem('currentUserIdComplete') ?? '';
  }
  // 获取用户收藏列表
  const getUserCollectionList = async () => {
    const res = await getUserCollectionNoDesc(getLoaclUser());
    const collectMap = (res?.data ?? []).reduce((prev: any, next: any) => {
      prev[next.appId] = true;
      return prev
    }, {})
    dispatch(setCollectionValue(collectMap));
  }
  useEffect(() => {
    queryApps();
  }, [page, pageSize, search]);

  return (
    <div className='apps_root'>
      <div className='apps_header'>
        <div className='apps_title'>{t('appDevelopment')}</div>
      </div>
      <div className='apps_main'>
        <div className='tabs'>
          <Tabs
            onChange={tabChange}
            defaultActiveKey={activkey}
            items={[
              {
                label: t('mineApp'),
                key: '1',
                children: '',
              },
              {
                label: t('teamApp'),
                key: '2',
                children: '',
                disabled: true,
              },
            ]}
          />
        </div>
        <div className='operatorArea'>
          <Button type='primary' onClick={create}>
            {t('create')}
          </Button>
          <Input
            showCount
            maxLength={64}
            placeholder={t('search')}
            style={{ width: '200px', height: '35px', marginLeft: '16px' }}
            prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>
        {appData.length > 0 ?
          <div className='card_list'>
            {appData.map((item: any) => (
              <div className='card_box' key={item.id} onClick={(e) => clickCard(item, e)}>
                <AppCard cardInfo={item} clickMore={clickMore} showOptions />
              </div>
            ))}
          </div> :
          <div className='empty-box'>
            <Empty />
          </div>
        }

        <div className='page_box'>
          <Pagination
            current={page}
            onChange={paginationChange}
            pageSizeOptions={[8, 16, 32, 60]}
            total={total}
            pageSize={pageSize}
          />
        </div>
      </div>
      {/*创建弹窗*/}
      <EditModal
        type='add'
        modalRef={modalRef}
        appInfo={modalInfo}
        addAippCallBack={addAippCallBack}
      />
      {/* 删除弹窗 */}
      <Modal
        title={t('deleteAppModalTitle')}
        width='380px'
        open={open}
        centered
        onOk={() => deleteApp()}
        onCancel={() => setOpen(false)}
        okButtonProps={{ loading }}
        okText={t('ok')}
        cancelText={t('cancel')}
      >
        <p>{t('deleteAppModalAlert')}</p>
      </Modal>
    </div>
  );
};
export default AppDev;
