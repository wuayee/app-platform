import React, { useEffect, useState } from 'react';
import { Avatar, Button, Drawer, Input, Dropdown } from 'antd';
import { AnyAction } from 'redux';
import { useNavigate } from 'react-router-dom';
import {
  SearchOutlined,
  EllipsisOutlined,
  CloseOutlined,
  UserOutlined
} from '@ant-design/icons';
import { cancelUserCollection, getUserCollection, updateCollectionApp } from '@/shared/http/appDev';
import { setCurAppId } from '@/store/collection/collection';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setAtChatId, setOpenStar } from '@/store/chatStore/chatStore';
import { setAtAppId, setAtAppInfo } from "@/store/appInfo/appInfo";
import { Message } from '@shared/utils/message';
import avatarNormal from '@/assets/images/knowledge/knowledge-base.png';
import './style.scoped.scss';

interface StarAppsProps {
  handleAt: (val: any) => void;
}

const StarApps: React.FC<StarAppsProps> = ({handleAt}) => {
  const dispatch = useAppDispatch();
  const openStar = useAppSelector((state) => state.chatCommonStore.openStar);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const [apps, setApps] = useState<any[]>([]);
  const clickMap: any = {

    // 设为默认
    2: async (item: AnyAction) => {
      try {
        await updateCollectionApp(getLocalUser(), item.appId)
        getUserCollectionList();
      } catch (error) {
        
      }
    },
    3: async (item: AnyAction) => {
      try {
        await updateCollectionApp(getLocalUser(), '3a617d8aeb1d41a9ad7453f2f0f70d61')
        getUserCollectionList();
      } catch (error) {
        
      }
    },
    1: async (item: AnyAction) => {
      try {
        if(item?.id) {
          await cancelUserCollection({
            usrInfo: getLocalUser(),
            aippId: item.aippId,
          })
        }
        getUserCollectionList();
      } catch (error) {
        
      }
    }
  }
  const items: any[] = [
    {
      key: '2',
      label: '设为默认',
    },
    {
      key: '3',
      label: '取消默认',
    },
  ];

  const count = useAppSelector((state: any) => state.collectionStore.value);

  // 获取当前登录用户名
  const getLocalUser = () => {
    return localStorage.getItem('currentUserIdComplete') ?? '';
  }

  // 数据转换
  const translateData = (remoteData: any): any[] => {
    
    const defaultData = remoteData?.data?.defaultApp || null;

    // 设置默认应用
    const collectionList: any[] = remoteData?.data?.collectionPoList || [];
    collectionList.unshift(defaultData);
    const data = collectionList.filter(item=> item);
    const res = data.map(item => {
      const attr = JSON.parse((item?.attributes ?? JSON.stringify({})))
      return ({
        ...item,
        rawData: item,
        name: item.name,
        author: item.createBy,
        desc: attr.description,
        appAvatar: attr.icon,
        authorAvatar: 'https://api.dicebear.com/7.x/miniavs/svg?seed=1',
      })
    })
    return res.filter(item=> item);
  }

  // 获取用户收藏列表
  const getUserCollectionList = async () => {
    try {
      const res = await getUserCollection(getLocalUser());
      setApps([...translateData(res)]);
    } catch (error) {
      console.error(error);
    }
  }

  // 点击操作
  const clickOpera = (btn: any, item: any) => {
    clickMap[btn.key](item)
  }

  // 开始聊天
  const startChat = (item: any) => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return
    }
    dispatch(setCurAppId(item?.appId))
    dispatch(setOpenStar(false));
    dispatch(setAtAppId(null));
    dispatch(setAtAppInfo(null));
    dispatch(setAtChatId(null));
  }

  // @应用
  const atApp = (item) => {
    let app = item;
    app.id = item.appId;
    handleAt(app);
  }

  useEffect(()=> {
    getUserCollectionList()
  }, [])
  return (
    <Drawer
      destroyOnClose
      mask={false}
      title={
        <div className='app-title'>
          <div className='app-title-left'>
            <span>选择收藏的应用</span>
          </div>
          <CloseOutlined
            style={{ fontSize: 20 }}
            onClick={() => dispatch(setOpenStar(false))}
          />
        </div>
      }
      closeIcon={false}
      onClose={() => dispatch(setOpenStar(false))}
      open={openStar}
    >
      <Input placeholder='搜索应用' prefix={<SearchOutlined />} />
      <div className='app-wrapper'>
        {apps.map((app, index) => (
          <div className='app-item' key={app.id}>
            {index=== 0 ? <div className='app-item-default'>默认应用</div> : ''}
            
            <div className='app-item-content'>
              { app.appAvatar ? <Avatar size={48} src={app.appAvatar} /> : <Avatar size={48} src={avatarNormal} />}
              <div className='app-item-text'>
                <div className='app-item-text-header'>
                  <div className='app-item-title' title={app.name}>{app.name}</div>
                  <div className='app-item-title-actions'>
                    {<span style={{ cursor: 'pointer' }} onClick={() => atApp(app)}>
                      @Ta
                    </span>}
                    <span
                      style={{ color: '#1677ff', cursor: 'pointer' }}
                      onClick={() => startChat(app)}
                    >
                      开始聊天
                    </span>
                  </div>
                </div>
                <div className='app-item-text-desc'>{app.desc}</div>
              </div>
            </div>
            <div className='app-item-footer'>
              <div>
                <UserOutlined />
                <span className='text'>由{app.author}创建</span>
              </div>
              <Dropdown menu={{ items: [items[index>0?0:1]], onClick: (info)=> {
                clickOpera(info, app)
              } }} trigger={['click']} >
                <EllipsisOutlined className='app-item-footer-more' />
              </Dropdown>
            </div>
          </div>
        ))}
      </div>
      <div style={{float:'right', marginTop: 12 }}>
        <Button onClick={() => dispatch(setOpenStar(false))} className='close-button'>
          关闭
        </Button>
      </div>
    </Drawer>
  );
};

export default StarApps;
