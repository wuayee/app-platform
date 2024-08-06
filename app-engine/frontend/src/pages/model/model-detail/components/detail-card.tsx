import React, { ReactElement } from "react";
import { Card } from "antd";
import type { MenuProps } from "antd";
import { Button, Dropdown, Space } from "antd";
import { HashRouter, Route, useHistory, Switch } from "react-router-dom";
import { url } from "inspector";
interface DetailItem {
  title: string;
  content: number;
  unit: string;
}

const DetailCard = ({ data }: { data: DetailItem }) => {
  return (
    <Card
      style={{
        width: "49%",
        background:
          "url(/src/assets/images/knowledge/knowledge-background.png)",
        height: 126,
        backgroundRepeat: "no-repeat",
        backgroundSize: "100% 126px",
        padding: 0,
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
            marginTop: -10,
          }}
        >
          <img
            width={55}
            height={64}
            src="/src/assets/images/knowledge/knowledge-base.png"
          />
        </div>
      </div>
      {/* 描述 */}
      <div
        style={{
          display: "flex",
          gap: 4
        }}
      >
        <div
          style={{
            fontSize: "20px",
            marginTop: 10,
          }}
        >
          {data.content}
        </div>
        <div
          style={{
            fontSize: "14px",
            marginTop: 16,
          }}
        >
          {data.unit}
        </div>
      </div>
    </Card>
  );
};

export default DetailCard;
