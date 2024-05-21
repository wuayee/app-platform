import React, { useState, useEffect, ReactElement } from "react";
import { Button, Input } from "antd";
import { HashRouter, Route, useNavigate, Routes } from "react-router-dom";

import { Icons } from "../../../components/icons";
import { knowledgeBase } from "../../../components/knowledge-card";
import DetailCard from "./components/detail-card";

import "../../../index.scss";
const ModelDetail = () => {
  // 路由
  const navigate = useNavigate();

  // 返回模型列表
  const backToModel = () => {
    navigate("/model");
  };

  const [knowledgeData, setKnowledgeData] = useState<knowledgeBase[]>([
    {
      name: "testName",
      createDate: "2024-05-17",
      createBy: "hzw_test",
      icon: () => (
        <>
          <img src="/src/assets/images/knowledge/knowledge-base.png" />
        </>
      ),
      desc: "管理储存数据，抽取1111dtydtyuftytbyusdcftbuyiyhudfgyhubdfrgbhuidfcgbyuierfdgbyuieyhugierdfhujierhujriefgheiryujdfwgyhuedfirwedrfgbhiuyedrfgyhiugyhu",
      id: "etgyjdvghsfvgyh",
    },
    {
      name: "testName",
      createDate: "2024-05-17",
      createBy: "hzw_test",
      icon: () => (
        <>
          <img src="/src/assets/images/knowledge/knowledge-base.png" />
        </>
      ),
      desc: "管理储存数据，抽取1111dtydtyuftytbyusdcftbuyiyhudfgyhubdfrgbhuidfcgbyuierfdgbyuieyhugierdfhujierhujriefgheiryujdfwgyhuedfirwedrfgbhiuyedrfgyhiugyhu",
      id: "etgyjdvghsfvgyh",
    },
  ]);
  return (
    <div className="aui-fullpage">
      <div className="aui-header-1">
        <div
          className="aui-title-1"
          onClick={() => backToModel()}
          style={{
            cursor: "pointer",
          }}
        >
          模型服务详情
        </div>
      </div>
      <div className="aui-block">
        <div
          style={{
            display: "flex",
          }}
        >
          <div>111</div>
          <div>222</div>
        </div>
        <div>1122334455</div>
        <div
          style={{
            display: "flex",
            gap: 140,
          }}
        >
          <div>
            <div>机构</div>
            <div>aaa</div>
          </div>
          <div>
            <div>类型</div>
            <div>aaa</div>
          </div>
          <div>
            <div>大模型容器镜像名称</div>
            <div>aaa</div>
          </div>
          <div>
            <div>推理精度</div>
            <div>aaa</div>
          </div>
          <div>
            <div>大模型实例数</div>
            <div>aaa</div>
          </div>
          <div>
            <div>单实例消耗的NPU数</div>
            <div>aaa</div>
          </div>
          <div>
            <div>模型服务端口号</div>
            <div>aaa</div>
          </div>
        </div>
        <div
          className="operatorArea"
          style={{
            display: "flex",
            gap: "16px",
          }}
        >
          {knowledgeData.map((knowledge) => (
            <>
              <DetailCard
                key={knowledge.id}
                knowledge={knowledge}
                style={{
                  flex: "0",
                }}
              />
            </>
          ))}
        </div>
      </div>
    </div>
  );
};
export default ModelDetail;
