import React, { useState, useEffect, useRef } from 'react';
import { Button, Divider, Input, Tabs } from 'antd';
import { Icons } from '../../components/icons';
import { queryAppsApi } from '../../shared/http/apps.js';
import AppCard from '../../components/appCard';
import './index.scoped.scss';
import { debounce } from '../../shared/utils/common';
import { HashRouter, Route, useNavigate, Routes } from 'react-router-dom';
import { deleteAppApi, getUserCollection, getUserCollectionNoDesc } from '../../shared/http/appDev';
import { setCollectionValue } from '../../store/collection/collection';
import { useAppDispatch, useAppSelector } from '../../store/hook';
import Pagination from '@/components/pagination';
import Empty from '@/components/empty/empty-item';
import { TENANT_ID } from '../chatPreview/components/send-editor/common/config';

const Apps: React.FC = () => {
  const tenantId = TENANT_ID;
  const navigate = useNavigate();
  const [appData, setAppData] = useState<any[]>([]);
  const [total, setTotal] = useState(1);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(8);
  const [search, setSearch] = useState('');

  async function queryApps() {
    const params = {
      pageNum:page,
      pageSize,
      includeTags: 'APP',
      name: search
    };
    const res: any = await queryAppsApi(tenantId, params);
    if (res.code === 0) {
      const { data, total } = res;
      setAppData([...data]);
      setTotal(total);
    }
  }

  const paginationChange = (curPage: number, curPageSize: number) => {
    if(page!==curPage) {
      setPage(curPage);
    }
    if(pageSize!=curPageSize) {
      setPageSize(curPageSize);
    }
  }

  // 搜索
  function onSearchValueChange(newSearchVal: string) {
    if(newSearchVal !== search) {
      setPage(1);
      setSearch(newSearchVal);
    }
  }
  const handleSearch = debounce(onSearchValueChange, 500);

  // 点击卡片
  function clickCard(item: any, e: any) {
    let id = item.runnables?.APP?.appId ||  '';
    navigate(`/app/${tenantId}/chat/${id}`);
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
        setPage(1);
        return;
      }
      queryApps();
    }
  }

  const count = useAppSelector((state: any) => state.collectionStore.value);

  // 获取当前登录用户名
  const getLoaclUser = () => {
    return localStorage.getItem('currentUserIdComplete') ?? '';
  }

  const dispatch = useAppDispatch();

  // 获取用户收藏列表
  const getUserCollectionList = async () => {
    const res = await getUserCollectionNoDesc(getLoaclUser());
    const collectMap = (res?.data ?? []).reduce((prev: any, next: any)=> {
        prev[next.appId] = true;
      return prev
    }, {})
    dispatch(setCollectionValue(collectMap))
  }

  // useEffect(()=> {
  //   getUserCollectionList()
  // }, [])

  useEffect(() => {
    queryApps();
  }, [page, pageSize, search]);
  return (
    <div className='apps_root'>
      <div className='apps_header'>
        <div className='apps_title'>应用市场</div>
      </div>
      <div className='apps_main'>
        <div className='operatorArea'>
          <Input
            showCount
            maxLength={20}
            placeholder='搜索'
            style={{ width: '200px', height: '35px' }}
            prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>
        { appData.length > 0 ? 
          <div className='card_list'>
            {appData.map((item: any) => (
              <div
                className='card_box'
                key={item.id}
                onClick={(e) => clickCard(item, e)}
              >
                <AppCard cardInfo={item} clickMore={clickMore} showOptions={false} />
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
            pageSizeOptions={[8,16,32,60]}
            total={total}
            pageSize={pageSize}
          />
        </div>
      </div>
    </div>
  );
};
export default Apps;
