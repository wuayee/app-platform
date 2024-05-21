import React, { ReactElement } from "react";
import { Card } from "antd";
import type { MenuProps } from "antd";
import { Button, Dropdown, Space } from "antd";
import { url } from "inspector";
import { Icons } from "../../../../components/icons";
import "./style.scoped.scss";

function Avatar(){
  const employeeNumber ="60032692"
  return (
    <div
      style={{
        width: "18px",
        height: "18px",
        borderRadius: "50%",
        overflow: "hidden",
        background:
          `url(https://w3.huawei.com/w3lab/rest/yellowpage/face/${employeeNumber}/120)`,
        backgroundSize:'contain'
      }}
    >
    </div>
  );
}

const AppCard = ({ cardInfo }: any) => {
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
      <div
        className="app_card_root"
        style={{
          background:
            "url(/src/assets/images/knowledge/knowledge-background.png)",
        }}
      >
        {/* 头部区域 */}
        <div className="app_card_header">
          <img src="/src/assets/images/knowledge/knowledge-base.png" />
          <div className="infoArea">
            <div className="headerTitle">{cardInfo.name}</div>
            <div className="title_info" style={{display:'flex',alignItems:'center'}}>
              <Avatar/>
              <div className="createBy">{cardInfo.createBy}</div>
            </div>
          </div>
        </div>

        {/* 描述 */}
        <div className="app_card_body">{cardInfo.attributes?.description}</div>

        {/* 底部 */}
        <div className="app_card_footer">
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
              <div style={{ cursor: "pointer" }}>
                <Icons.more width={20} />
              </div>
            </Dropdown>
          </div>
        </div>
      </div>
    );
};

export default AppCard;
