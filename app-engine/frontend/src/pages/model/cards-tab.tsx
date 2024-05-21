import React, { useState, useEffect, ReactElement } from "react";
import { Button, Input } from "antd";
import ModelCard from "./components/model-card";

import "../../index.scss";
export interface ModelItem {
  id: string;
  name: string;
  model: string;
  scale: string;
  type:string;
  orgnization: string;
  description: string;
  precision: object;
  gpu: object;
  npu: object;
  supported_images: Array<string>;
  status: string;
  latency: number;
  speed: number;
  requests: number;
  responses: number;
  exceptions: number;
  throughput: number;
  total_input_tokens: number;
  total_output_tokens: number;
}
const CardsTab = ({modelList}: {modelList: ModelItem[]}) => {
  // 路由
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
        {modelList.map((modelItem) => (
          <>
            <ModelCard
              modelItem={modelItem}
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
