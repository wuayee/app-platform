import React, { ReactElement } from "react";
import { Card } from "antd";
import type { MenuProps } from "antd";
import { Button, Dropdown, Space } from "antd";
import { HashRouter, Route, useNavigate, Routes } from "react-router-dom";
import { url } from "inspector";
import { Icons } from "../../../../components/icons";
interface DetailItem {
  title: string;
  content: number;
}

const DetailCard = ({ data }: { data: DetailItem }) => {
  return (
    <Card
      style={{
        width: 900,
        background:
          "url(/src/assets/images/knowledge/knowledge-background.png)",
        height: 126,
      }}
    >
      <div
        style={{
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
          >
            {data.title}
          </div>
        </div>
        <div
          style={{
            textAlign: "right",
          }}
        >
        </div>
      </div>{" "}
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
        {data.content}
      </div>
    </Card>
  );
};

export default DetailCard;
