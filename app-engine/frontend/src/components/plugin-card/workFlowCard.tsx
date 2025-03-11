/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { Dropdown, MenuProps, Tag } from 'antd';
import { EllipsisOutlined, StarOutlined, UserOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router';
import { useAppSelector } from '@/store/hook';
import { getAppInfoByVersion } from '@/shared/http/aipp';
import { useTranslation } from 'react-i18next';
import './style.scss';

const WorkflowCard = ({ pluginData, type }: any) => {
  const { t } = useTranslation();
  const navigate = useHistory().push;
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const operatItems: MenuProps['items'] = [
    {
      label: <div onClick={DropdownItemClick}>{t('arrange')}</div>,
      key: 'choreography',
    },
  ];
  async function DropdownItemClick() {
    let id = pluginData?.id;
    if (pluginData?.state === 'active') {
      const res = await getAppInfoByVersion(tenantId, id);
      id = res?.data?.id;
    }
    navigate({
      pathname: `/app-develop/${tenantId}/add-flow/${id}`,
      search: '?type=workFlow',
    });
  }
  return (
    <div className={type === 'plugin' ? 'page-plugin-card' : 'plugin-card'}
      onClick={async () => {
        let id = pluginData?.id;
        if (pluginData?.state === 'active') {
          const res = await getAppInfoByVersion(tenantId, id);
          id = res?.data?.id;
        }
        navigate({
          pathname: `/app-develop/${tenantId}/add-flow/${id}`,
          search: '?type=workFlow',
        });
      }}
    >
      <div className='plugin-card-header'>
      <img src={pluginData.attributes.icon ||'./src/assets/images/knowledge/knowledge-base.png'} />
        <div>
          <div className='plugin-title'>
            <div className='plugin-head'>
              <span className='text' title={pluginData?.name}>{pluginData?.name}</span>
              <Tag className='version'>V{pluginData?.version}</Tag>
            </div>
          </div>
          <div className='plugin-card-user'>
            <img width="18" height="18" src="./src/assets/images/ai/user.jpg" alt="" />
            <span style={{ marginRight: 8 }}>{pluginData?.createBy}</span>
            {pluginData?.tags?.map((tag: string, index: number) => {
              if (tag.trim().length > 0) {
                return <Tag style={{ margin: 0 }} key={index}>{tag}</Tag>
              }
            })}
          </div>
        </div>
      </div>
      <div className='card-content'>
        {pluginData?.attributes?.description}
      </div>
      {/* 卡片底部 */}
      <div className='card-footer'>
        <div>
          <div className='card-footer-content'>
            <span>
              {
                (pluginData?.attributes?.latest_version || pluginData.state === 'active') ?
                  <Tag bordered={false} color='processing' className='footer-type'>{t('active')}</Tag> :
                  <Tag bordered={false} className='footer-type'>{t('draft')}</Tag>
              }
            </span>
            <span hidden>
              <UserOutlined style={{ marginRight: 8 }} />
              {pluginData?.downloadCount}
            </span>
            <span hidden>
              <StarOutlined style={{ marginRight: 8 }} />
              {pluginData?.likeCount}
            </span>
          </div>
        </div>
        <div onClick={(e) => { e.stopPropagation(); }}>
          <Dropdown menu={{ items: operatItems }} trigger={['click']}>
            <EllipsisOutlined className='footer-more' />
          </Dropdown>
        </div>
      </div>
    </div >
  )
}

export default WorkflowCard;
