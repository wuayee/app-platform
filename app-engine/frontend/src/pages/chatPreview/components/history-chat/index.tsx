import React, { useState } from "react";
import { Drawer, Input, Dropdown } from "antd";
import type { MenuProps } from "antd";
import {
  SearchOutlined,
  EllipsisOutlined,
  ClearOutlined,
  CloseOutlined,
} from "@ant-design/icons";
import "./style.scoped.scss";

interface HistoryChatProps {
  open: boolean;
  setOpen: (val: boolean) => void;
}

const items: MenuProps["items"] = [
  {
    key: "1",
    label: "删除",
  },
];

const HistoryChat: React.FC<HistoryChatProps> = ({ open, setOpen }) => {
  const [data, setData] = useState(
    new Array(10).fill(0).map((_, index) => ({
      title: `鲁棒性-${index}`,
      content:
        "鲁棒性指的是系统或算法在面对各种异常情况时，仍能保持稳定的能力。这些异常情况...",
      time: `${index}小时前`,
    }))
  );
  return (
    <Drawer
      destroyOnClose
      title={
        <div className="history-title">
          <div className="history-title-left">
            <span>历史聊天</span>
            <div className="history-clear-btn" onClick={() => setData([])}>
              <ClearOutlined style={{ fontSize: 14, marginLeft: 8 }} />
              <span className="history-clear-btn-text">清空</span>
            </div>
          </div>
          <CloseOutlined
            style={{ fontSize: 20 }}
            onClick={() => setOpen(false)}
          />
        </div>
      }
      onClose={() => setOpen(false)}
      open={open}
      closeIcon={false}
      bodyStyle={{ padding: 0 }}
    >
      <div style={{ padding: 24 }}>
        <Input placeholder="搜索..." prefix={<SearchOutlined />} />
      </div>
      <div className="history-wrapper">
        {data.map((history) => (
          <div className="history-item" key={history.title}>
            <div className="history-item-content">
              <div className="history-item-header">
                <div className="history-item-title">{history.title}</div>
                <span
                  style={{ cursor: "pointer", color: "#1677ff" }}
                  onClick={() => setOpen(false)}
                >
                  继续聊天
                </span>
              </div>
              <div className="history-item-desc">{history.content}</div>
            </div>
            <div className="history-item-footer">
              <span>{history.time}</span>
              <Dropdown menu={{ items }} trigger={["click"]}>
                <EllipsisOutlined className="history-item-footer-more" />
              </Dropdown>
            </div>
          </div>
        ))}
      </div>
    </Drawer>
  );
};

export default HistoryChat;
