import React, { useEffect, useState } from 'react';
import { Dropdown, Tooltip } from 'antd';
import type { MenuProps } from 'antd';
import { Icons } from '../icons';
import { useTranslation } from 'react-i18next';
import './style.scoped.scss';


export interface CardInfoType {
  name: string;
  createBy: string;
  icon: string;
  description: string;
  id: string;
}

const AppCard = ({ cardInfo, clickMore, showOptions = true }: any) => {
  const { t } = useTranslation();
  const [count, setCount] = useState(0);
  const operatorItems: MenuProps['items'] = [
    {
      key: 'delete',
      label: <div>{t('delete')}</div>,
    },
  ];
  const clickItem = (info: any) => {
    clickMore(info.key, cardInfo.id);
  };

  useEffect(() => {
    let { likeCount } = cardInfo;
    setCount(likeCount || 0);
  }, [cardInfo])
  return (
    <div className='app_card_root'>
      {/* 头部区域 */}
      <div className='app_card_header'>
        <div className='img_box'>
          {cardInfo.icon && <img width={'100%'} src={cardInfo.icon} alt='' />}
          {!cardInfo.icon && (
            <img width={'100%'} src='./src/assets/images/knowledge/knowledge-base.png' alt='' />
          )}
        </div>
        <div className='infoArea'>
          <Tooltip title={cardInfo?.name}>
            <div className='headerTitle'>{cardInfo?.name}</div>
          </Tooltip>
          <div className='title_info'>
            <img width={18} height={18} src='./src/assets/images/ai/user.jpg' alt='' />
            <div className='createBy'>{cardInfo.createBy || cardInfo.creator}</div>
          </div>
        </div>
      </div>

      {/* 描述 */}
      <div className='app_card_body'>{cardInfo.description}</div>

      {/* 底部 */}
      <div className='app_card_footer'>
        <div className='left'>
          {/* <div className='icon_box'>
            <UserOutlined /> 2.36k
          </div>
          <div className='icon_box' onClick={(e) => { clickCollection(e) }} style={{
            cursor: loading ? 'not-allowed' : 'pointer'
          }}>
            {collectionStore[cardInfo.id] ? <StarFilled /> : <StarOutlined />} {count}
          </div> */}
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
