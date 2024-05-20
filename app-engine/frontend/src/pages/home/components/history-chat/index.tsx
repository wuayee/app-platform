import React from "react";
import { Drawer, Input } from "antd";
import { SearchOutlined, EllipsisOutlined } from "@ant-design/icons";
import "./style.scoped.scss";

interface HistoryChatProps {
  open: boolean;
  setOpen: (val: boolean) => void;
}

const data = [
  {
    title: "鲁棒性",
    content:
      "鲁棒性指的是系统或算法在面对各种异常情况时，仍能保持稳定的能力。这些异常情况...",
    time: "1小时前",
  },
  {
    title: "鲁棒性",
    content:
      "鲁棒性指的是系统或算法在面对各种异常情况时，仍能保持稳定的能力。这些异常情况...",
    time: "1小时前",
  },
  {
    title: "鲁棒性",
    content:
      "鲁棒性指的是系统或算法在面对各种异常情况时，仍能保持稳定的能力。这些异常情况...",
    time: "1小时前",
  },
  {
    title: "鲁棒性",
    content:
      "鲁棒性指的是系统或算法在面对各种异常情况时，仍能保持稳定的能力。这些异常情况...",
    time: "1小时前",
  },
];

const HistoryChat: React.FC<HistoryChatProps> = ({ open, setOpen }) => {
  return (
    <Drawer
      title="历史聊天"
      onClose={() => setOpen(false)}
      open={open}
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
              <EllipsisOutlined style={{ fontSize: 24 }} />
            </div>
          </div>
        ))}
      </div>
    </Drawer>
  );
};

export default HistoryChat;
