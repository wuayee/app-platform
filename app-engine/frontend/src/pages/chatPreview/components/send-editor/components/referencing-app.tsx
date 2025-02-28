/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef, useState } from 'react';
import { Input, Spin } from 'antd';
import knowledgeBase from '@assets/images/knowledge/knowledge-base.png';
import { useAppSelector } from '@/store/hook';
import { SearchOutlined } from '@ant-design/icons';
import { queryAppsApi } from '@/shared/http/apps';
import { FINANCE_APP_ID } from '../common/config';
import { useTranslation } from 'react-i18next';
import '../styles/referencing-app.scss';


const ReferencingApp = (props) => {
  const { t } = useTranslation();
  const { atItemClick, atClick, searchKey, setSearchKey } = props;
  const [appArr, setAppArr] = useState([]);
  const [tableLoading, setTableLoading] = useState(false);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const pageNo = useRef(1);

  // 应用点击回调
  const itemClick = (item) => {
    atItemClick(item);
  }

  // 拿取应用列表
  const getAppList = async () => {
    setTableLoading(true);
    try {
      const params = {
        pageNum: pageNo.current,
        pageSize: 3,
        includeTags: 'App',
        name: searchKey
      }
      const res = await queryAppsApi(tenantId, params);
      if (res.code === 0) {
        const { data, total } = res;
        const list = data.filter(item => {
          let aippId = item.runnables.APP.aippId;
          return aippId !== FINANCE_APP_ID;
        })
        setAppArr(list);
      }
    } finally {
      setTableLoading(false);
    }
  }
  useEffect(() => {
    getAppList();
  }, [searchKey]);
  return <>{(
    <div className='at-content' onClick={(e) => e.stopPropagation()}>
      <div className='at-head'>
        <div className='at-app-search'>
          <Input
            value={searchKey}
            prefix={<SearchOutlined />}
            allowClear
            placeholder={t('search')}
            maxLength={20}
            showCount
            onChange={(e) => { setSearchKey(e.target.value) }}
          />
        </div>
      </div>
      <Spin spinning={tableLoading}>
        <div className='at-content-inner'>
          {
            appArr.map((item, index) => {
              return (
                <div className='at-list-item' key={index} onClick={() => itemClick(item)}>
                  <div className='left'>
                    <span>
                      {item.icon ? <img src={item.icon} /> : <img src={knowledgeBase} />}
                    </span>
                    <span className='name'>{item.name}</span>
                    <span className='description'>{item.description}</span>
                  </div>
                </div>
              )
            })
          }
        </div>
      </Spin>
    </div>
  )}</>
};

export default ReferencingApp;
