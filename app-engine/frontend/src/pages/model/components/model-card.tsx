import React, { ReactElement } from "react";
import { Card } from "antd";
import type { MenuProps } from "antd";
import { Button, Dropdown, Space } from "antd";
import { HashRouter, Route, useNavigate, Routes } from "react-router-dom";
import { url } from "inspector";
import { Icons } from "../../../components/icons";

export interface knowledgeBase {
  name: string;
  createDate: string;
  createBy: string;
  icon: () => ReactElement;

  desc: string;

  id: string;
}

const ModelCard = ({ knowledge }: { knowledge: knowledgeBase }) => {
  const operatorItems: MenuProps["items"] = [
    {
      key: "delete",
      label: (
        <div
          style={{
            width: 200,
          }}
        >
          删除
        </div>
      ),
    },
  ];
  // 路由
  const navigate = useNavigate();
  // 创建知识库
  const toModelDetail = () => {
    navigate("/model/detail");
  };
  return (
    <Card
      style={{
        width: 376,
        background:
          "url(/src/assets/images/knowledge/knowledge-background.png)",
        height: 250,
      }}
    >
      {/* 头部区域 */}
      <div
        style={{
          display: "flex",
          height: 40,
        }}
      >
        <div>
          <div
            style={{
              fontSize: 20,
              color: "rgba(5, 5, 5, .96)",
              cursor: "pointer",
            }}
            onClick={() => toModelDetail()}
          >
            {knowledge.name}
          </div>
        </div>
        <div
          style={{
            marginLeft: 150,
          }}
        >
          <knowledge.icon />
        </div>
        <div
          style={{
            fontSize: 14,
            color: "rgba(5, 5, 5, .96)",
            marginTop: 5,
          }}
        >
          {knowledge.name}
        </div>
      </div>
      <div
        style={{
          display: "flex",
          gap: "16px",
          height: 30,
        }}
      >
        <div
          style={{
            fontSize: 14,
            color: " rgb(26, 26, 26);",
            background: "rgb(242, 242, 242)",
            borderWidth: 1,
            borderStyle: "dashed",
            borderColor: "rgb(221, 221, 221)",
            borderRadius: "4px",
            padding: "1px 8px 1px 8px",
          }}
        >
          {knowledge.createBy}
        </div>
        <div
          style={{
            fontSize: 14,
            color: " rgb(26, 26, 26);",
            background: "rgb(242, 242, 242)",
            borderWidth: 1,
            borderStyle: "dashed",
            borderColor: "rgb(221, 221, 221)",
            borderRadius: "4px",
            padding: "1px 8px 1px 8px",
          }}
        >
          {knowledge.createBy}
        </div>
      </div>

      {/* 描述 */}
      <div
        style={{
          wordBreak: "break-all",
          fontSize: "14px",
          lineHeight: "22px",
          textAlign: "justify",
          marginTop: 16,
        }}
      >
        {knowledge.desc}
      </div>
      <div
        style={{
          marginTop: 16,
          width: "330px",
          border: "1px solid rgb(229, 239, 252)",
        }}
      ></div>
      {/* 底部 */}
      <div
        style={{
          display: "flex",
          justifyContent: "flex-end",
          marginTop: 16,
        }}
      >
        <div>
          <Dropdown
            menu={{
              items: operatorItems,
              onClick: (info) => {
                clickItem(info);
              },
            }}
            placement="bottomLeft"
            trigger={["click"]}
          >
            <div
              style={{
                cursor: "pointer",
              }}
            >
              <Icons.more width={20} />
            </div>
          </Dropdown>
        </div>
      </div>
    </Card>
  );
};

export default ModelCard;
