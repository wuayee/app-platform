import React, { ReactElement } from 'react';
import { Card } from 'antd';
import type { MenuProps } from 'antd';
import { Button, Dropdown, Space } from 'antd';
import { StarFilled, UserOutlined } from '@ant-design/icons';
import { url } from 'inspector';
import { Icons } from '../icons';
import './style.scoped.scss';

function Avatar() {
  const employeeNumber = '123';
  return (
    <div
      style={{
        width: '18px',
        height: '18px',
        borderRadius: '50%',
        overflow: 'hidden',
        background: `url(https://w3.huawei.com/w3lab/rest/yellowpage/face/${employeeNumber}/120)`,
        backgroundSize: 'contain',
      }}
    ></div>
  );
}

export interface CardInfoType {
  name: string;
  createBy: string;
  icon: string;
  description: string;
  id: string;
}

const AppCard = ({ cardInfo, clickMore, showOptions = true }: any) => {
  const operatorItems: MenuProps['items'] = [
    {
      key: 'delete',
      label: <div style={{ width: 200 }}>删除</div>,
    },
  ];
  const clickItem = (info: any) => {
    clickMore(info.key, cardInfo.id);
  };
  return (
    <div
      className='app_card_root'
      style={{
        background: 'url(/src/assets/images/knowledge/knowledge-background.png)',
      }}
    >
      {/* 头部区域 */}
      <div className='app_card_header'>
        {cardInfo.icon && <img src={cardInfo.icon} alt='' />}
        {!cardInfo.icon && <img src='/src/assets/images/knowledge/knowledge-base.png' alt='' />}
        <div className='infoArea'>
          <div className='headerTitle'>{cardInfo.name}</div>
          <div className='title_info' style={{ display: 'flex', alignItems: 'center' }}>
            <Avatar />
            <div className='createBy'>{cardInfo.createBy}</div>
          </div>
        </div>
      </div>

      {/* 描述 */}
      <div className='app_card_body'>{cardInfo.description}</div>

      {/* 底部 */}
      <div className='app_card_footer'>
        <div className='left'>
          <div className='icon_box'>
            <UserOutlined /> 2.36k
          </div>
          <div className='icon_box'>
            <StarFilled /> 126
          </div>
        </div>
        <div style={{ flex: 1 }}></div>
        {showOptions && (
          <div className='operator'>
            <Dropdown
              menu={{
                items: operatorItems,
                onClick: (info) => {
                  clickItem(info);
                  info.domEvent.stopPropagation();
                },
              }}
              placement='bottomLeft'
              trigger={['click']}
            >
              <div
                style={{ cursor: 'pointer' }}
                onClick={(e) => {
                  e.stopPropagation();
                }}
              >
                <Icons.more width={20} />
              </div>
            </Dropdown>
          </div>
        )}
      </div>
    </div>
  );
};

export default AppCard;
