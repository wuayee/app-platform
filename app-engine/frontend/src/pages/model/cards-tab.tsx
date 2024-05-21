import React, { useState, useEffect, ReactElement } from "react";
import { Button, Input } from "antd";
import { HashRouter, Route, useNavigate, Routes } from "react-router-dom";

import { Icons } from "../../components/icons";
import { knowledgeBase } from "../../components/knowledge-card";
import ModelCard from "./components/model-card";

import "../../index.scss";
const CardsTab = () => {
  // 路由
  const navigate = useNavigate();

  // 总条数
  const [total, setTotal] = useState(100);

  // 数据
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

  useEffect(() => {
    const index = 1;
    setInterval(() => {
      setTotal(Math.floor(Math.random() * 1000));
    }, 1000);
  }, []);
  return (
    <div className="aui-block">
      <div
        className="containerArea"
        style={{
          width: "100%",
          minHeight: "500px",
          maxHeight: "calc(100% - 200px)",
          boxSizing: "border-box",
          paddingTop: "20px",
          paddingBottom: "20px",
          display: "flex",
          gap: "17px",
          flexWrap: "wrap",
        }}
      >
        {knowledgeData.map((knowledge) => (
          <>
            <ModelCard
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
  );
};
export default CardsTab;
