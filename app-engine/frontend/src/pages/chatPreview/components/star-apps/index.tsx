import React, { useEffect, useState } from "react";
import { Avatar, Button, Drawer, Input, Dropdown } from "antd";
import type { MenuProps } from "antd";
import {
  SearchOutlined,
  EllipsisOutlined,
  CloseOutlined,
} from "@ant-design/icons";
import "./style.scoped.scss";
import { httpUrlMap } from "../../../../shared/http/httpConfig";
import { cancleUserCollection, collectionApp, getUserCollection, updateCollectionApp } from "../../../../shared/http/appDev";
import { setCollectionValue, setDefaultApp } from "../../../../store/collection/collection";
import { useAppSelector, useAppDispatch } from "../../../../store/hook";
import { AnyAction } from "redux";
import { useNavigate, useLocation } from "react-router-dom";

const { ICON_URL } = process.env.NODE_ENV === 'development' ? { ICON_URL: `${window.location.origin}/api`} : httpUrlMap[process.env.NODE_ENV];

interface StarAppsProps {
  open: boolean;
  setOpen: (val: boolean) => void;
  handleAt: (val: any) => void;
  chatClick: (val: any) => void;
}



const StarApps: React.FC<StarAppsProps> = ({ open, setOpen, handleAt, chatClick }) => {
  const tenantId = '31f20efc7e0848deab6a6bc10fc3021e';
  const navigate = useNavigate();
  const [apps, setApps] = useState<any[]>([]);
  const clickMap: any = {

    // 设为默认
    2: async (item: AnyAction) => {
      try {
        await updateCollectionApp(getLoaclUser(), item.appId) 
        getUserCollectionList();
      } catch (error) {
        
      }
    },
    3: async (item: AnyAction) => {
      try {
        await updateCollectionApp(getLoaclUser(), '3a617d8aeb1d41a9ad7453f2f0f70d61')
        getUserCollectionList();
      } catch (error) {
        
      }
    },
    1: async (item: AnyAction) => {
      try {
        if(item?.id) {
          await cancleUserCollection({
            usrInfo: getLoaclUser(),
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
      key: "2",
      label: "设为默认",
    },
    {
      key: "3",
      label: "取消默认",
    },
    // {
    //   key: "1",
    //   label: "取消收藏",
    // },
  ];

  const count = useAppSelector((state: any) => state.collectionStore.value);

  // 获取当前登录用户名
  const getLoaclUser = () => {
    return localStorage.getItem('currentUserIdComplete') ?? '';
  }

  const dispatch = useAppDispatch();

  // 数据转换
  const translateData = (remoteData: any): any[] => {
    
    const defaultData = remoteData?.data?.defaultApp || null;

    // 设置默认应用
    // dispatch(setDefaultApp(defaultData?.appId || ''))
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
        authorAvatar: "https://api.dicebear.com/7.x/miniavs/svg?seed=1",
      })
    })
    return res.filter(item=> item);
  }

  // 获取用户收藏列表
  const getUserCollectionList = async () => {
    try {
      const res = await getUserCollection(getLoaclUser());
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
    dispatch(setDefaultApp(item?.appId || ''));
    // navigate(`/app/${tenantId}/chat/${item.aippId}`);
  }

  useEffect(()=> {
    getUserCollectionList()
  }, [])
  return (
    <Drawer
      destroyOnClose
      mask={false}
      title={
        <div className="app-title">
          <div className="app-title-left">
            <span>选择收藏的应用</span>
          </div>
          <CloseOutlined
            style={{ fontSize: 20 }}
            onClick={() => setOpen(false)}
          />
        </div>
      }
      closeIcon={false}
      onClose={() => setOpen(false)}
      open={open}
    >
      <Input placeholder="搜索应用" prefix={<SearchOutlined />} />
      <div className="app-wrapper">
        {apps.map((app, index) => (
          <div className="app-item" key={app.name}>
            {index=== 0 ? <div className="app-item-default">默认应用</div> : ''}
            
            <div className="app-item-content">
              <Avatar size={48} src={app.appAvatar} />
              <div className="app-item-text">
                <div className="app-item-text-header">
                  <div className="app-item-title">{app.name}</div>
                  <div className="app-item-title-actions">
                    {/* <span style={{ cursor: "pointer" }} onClick={() => handleAt(app)}>
                      @Ta
                    </span> */}
                    <span
                      style={{ color: "#1677ff", cursor: "pointer" }}
                      onClick={() => startChat(app)}
                    >
                      开始聊天
                    </span>
                  </div>
                </div>
                <div className="app-item-text-desc">{app.desc}</div>
              </div>
            </div>
            <div className="app-item-footer">
              <div>
                <Avatar size={32} src={app.authorAvatar} />
                <span className="text">由{app.author}创建</span>
              </div>
              <Dropdown menu={{ items: [items[index>0?0:1]], onClick: (info)=> {
                clickOpera(info, app)
              } }} trigger={["click"]} >
                <EllipsisOutlined className="app-item-footer-more" />
              </Dropdown>
            </div>
          </div>
        ))}
      </div>
      <div style={{ display: "flex", justifyContent: "center", marginTop: 12 }}>
        <Button onClick={() => setOpen(false)} className="close-button">
          关闭
        </Button>
      </div>
    </Drawer>
  );
};

export default StarApps;
