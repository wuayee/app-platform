import React, { useState } from "react";
import { Avatar, Button, Drawer, Input } from "antd";
import { SearchOutlined, EllipsisOutlined } from "@ant-design/icons";
import './style.scoped.scss';

interface StarAppsProps {
  open: boolean;
  setOpen: (val: boolean) => void;
}

const StarApps: React.FC<StarAppsProps> = ({ open, setOpen }) => {
  const [apps, setApps] = useState(
    new Array(10).fill(0).map((_, index) => ({
      name: `小海-${index}`,
      desc: "超级应用助手，存储领域高级专家",
      author: "APP Engine",
      appAvatar:
        "https://dthezntil550i.cloudfront.net/p4/latest/p42102052243097410008650553/1280_960/12bc8bc0-2186-48fb-b432-6c011a559ec0.png",
      authorAvatar: "https://api.dicebear.com/7.x/miniavs/svg?seed=1",
    }))
  );

  const handleAt = () => {
    console.log("at someone");
  };
  return (
    <Drawer title="选择收藏的应用" onClose={() => setOpen(false)} open={open}>
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
                    <span
                      style={{ cursor: "pointer" }}
                      onClick={() => handleAt}
                    >
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
              <EllipsisOutlined style={{ fontSize: 24 }} />
            </div>
          </div>
        ))}
      </div>
      <div style={{ display: "flex", justifyContent: "center", marginTop: 12 }}>
        <Button onClick={() => setOpen(false)}>关闭</Button>
      </div>
    </Drawer>
  );
};

export default StarApps;
