import React, { useState } from "react";
import { Avatar, Button, Drawer, Input, Dropdown } from "antd";
import type { MenuProps } from "antd";
import {
  SearchOutlined,
  EllipsisOutlined,
  CloseOutlined,
} from "@ant-design/icons";
import "./style.scoped.scss";
import {httpUrlMap} from "../../../../shared/http/httpConfig";
const { ICON_URL } = process.env.NODE_ENV === 'development' ? { ICON_URL: `${window.location.origin}/api`} : httpUrlMap[process.env.NODE_ENV];

interface StarAppsProps {
  open: boolean;
  setOpen: (val: boolean) => void;
}

const items: MenuProps["items"] = [
  {
    key: "1",
    label: "删除",
  },
];

const StarApps: React.FC<StarAppsProps> = ({ open, setOpen }) => {
  const [apps, setApps] = useState(
    new Array(10).fill(0).map((_, index) => ({
      name: `小海-${index}`,
      desc: "超级应用助手，存储领域高级专家",
      author: "APP Engine",
      appAvatar:
        `${ICON_URL}/jober/v1/files/17e9ee28e8914b48aa54e084b67bf878`,
      authorAvatar: "https://api.dicebear.com/7.x/miniavs/svg?seed=1",
    }))
  );

  const handleAt = () => {
    console.log("at someone");
  };
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
        {apps.map((app) => (
          <div className="app-item" key={app.name}>
            <div className="app-item-content">
              <Avatar size={48} src={app.appAvatar} />
              <div className="app-item-text">
                <div className="app-item-text-header">
                  <div className="app-item-title">{app.name}</div>
                  <div className="app-item-title-actions">
                    <span style={{ cursor: "pointer" }} onClick={handleAt}>
                      @Ta
                    </span>
                    <span
                      style={{ color: "#1677ff", cursor: "pointer" }}
                      onClick={() => setOpen(false)}
                    >
                      返回聊天
                    </span>
                  </div>
                </div>
                <div className="app-item-text-desc">{app.desc}</div>
              </div>
            </div>
            <div className="app-item-footer">
              <div>
                <Avatar size={32} src={app.authorAvatar} />
                <span>由{app.author}创建</span>
              </div>
              <Dropdown menu={{ items }} trigger={["click"]}>
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
