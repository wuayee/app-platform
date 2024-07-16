import React, { useEffect, useState } from 'react';
import { Dropdown, Tooltip } from 'antd';
import type { MenuProps } from 'antd';
import { StarFilled, UserOutlined, StarOutlined } from '@ant-design/icons';
import { Icons } from '../icons';
import { cancleUserCollection, collectionApp } from '@/shared/http/appDev';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { addCollectionApp, removeCollectionApp } from '@/store/collection/collection';
import './style.scoped.scss';

export interface CardInfoType {
  name: string;
  createBy: string;
  icon: string;
  description: string;
  id: string;
}

const AppCard = ({ cardInfo, clickMore, showOptions = true }: any) => {

  const [count, setCount] = useState(0);

  const [loading, setLoading] = useState(false);

  const collectionStore = useAppSelector((state: any) => state.collectionStore.value);

  const dispatch = useAppDispatch();

  const operatorItems: MenuProps['items'] = [
    {
      key: 'delete',
      label: <div style={{ width: 200 }}>删除</div>,
    },
  ];
  const clickItem = (info: any) => {
    clickMore(info.key, cardInfo.id);
  };

  // 获取当前登录用户名
  const getLoaclUser = () => {
    return localStorage.getItem('currentUserIdComplete') ?? '';
  }

  // 点击收藏
  const collectionClick = async () => {
    setLoading(true);
    await collectionApp({
      aippId: cardInfo.id,
      usrInfo: getLoaclUser(),
      isDefault: false,
    });
    // 刷新收藏数
    dispatch(addCollectionApp(cardInfo.id));
    setLoading(false);
  }

  // 取消收藏
  const cancleCollection = async () => {
    setLoading(true);
    await cancleUserCollection({
      usrInfo: getLoaclUser(),
      aippId: cardInfo.id,
    })
    dispatch(removeCollectionApp(cardInfo.id));
    setLoading(false);
  }

  // 
  const clickCollection = (e: Event) => {
    if (loading) {
      // 处于请求状态不允许点击
    } else {
      if (collectionStore[cardInfo.id]) {
        cancleCollection()
      } else {
        collectionClick();
      }
    }
    e.stopPropagation();
  }

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
            <img width={'100%'} src='/src/assets/images/knowledge/knowledge-base.png' alt='' />
          )}
        </div>
        <div className='infoArea'>
          <Tooltip title={cardInfo?.name}>
            <div className='headerTitle'>{cardInfo?.name}</div>
          </Tooltip>
          <div className='title_info'>
            <img width={18} height={18}  src='/src/assets/images/ai/user.jpg' alt='' />
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
          <div className='icon_box' onClick={(e) => { clickCollection(e) }} style={{
            cursor: loading ? 'not-allowed' : 'pointer'
          }}>
            {collectionStore[cardInfo.id] ? <StarFilled /> : <StarOutlined />} {count}
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
    </div >
  );
};

export default AppCard;
