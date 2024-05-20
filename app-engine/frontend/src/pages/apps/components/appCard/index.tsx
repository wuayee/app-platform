import React, { ReactElement } from "react";
import { Card } from "antd";
import type { MenuProps } from "antd";
import { Button, Dropdown, Space } from "antd";
import { url } from "inspector";
import { Icons } from "../../../../components/icons";
import "./style.scoped.scss";


export interface knowledgeBase {
    name: string;
    createBy: string;
    // icon: () => ReactElement;
    desc: string;
    id: string;
}

const AppCard = ({ cardInfo }: any) => {
    console.log("cardInfo", cardInfo.attributes.icon);
    const operatorItems: MenuProps["items"] = [
        {
            key: "delete",
            label: <div style={{ width: 200 }}>删除</div>,
        },
    ];
    const clickItem = (info: any) => {
        console.log("info", info);
    };
    return (
      //   <div className="app_card_root">
      <div
        className="app_card_root"
        style={{
          background:"url(/src/assets/images/knowledge/knowledge-background.png)",
        }}
      >
        <div className="content">
          {/* 头部区域 */}
          <div className="app_card_header">
            <img src="/src/assets/images/knowledge/knowledge-base.png" />
            <div className="infoArea">
              <div className="headerTitle">{cardInfo.name}</div>
              <div className="title_info" style={{ display: "flex", alignItems: "center" }}>
                <img
                  width={18}
                  height={18}
                  style={{ marginRight: "10px" }}
                  src="/src/assets/images/knowledge/knowledge-base.png"
                />
                <div className="createBy">{cardInfo.createBy}</div>
              </div>
            </div>
          </div>

          {/* 描述 */}
          <div
            className="body"
            style={{
              wordBreak: "break-all",
              marginTop: 16,
              fontSize: "14px",
              lineHeight: "22px",
              textAlign: "justify",
              flex: 1,
            }}
          >
            {cardInfo.attributes?.description}
          </div>

          {/* 底部 */}
          <div
            className="footer"
            style={{
              display: "flex",
              justifyContent: "flex-end",
              marginTop: 16,
            }}
          >
            <div className="operator">
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
        </div>
      </div>
      //   </div>
    );
};

export default AppCard;
