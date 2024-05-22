import React, { useState, useEffect, ReactElement } from "react";
import { Button, Input } from "antd";
import { HashRouter, Route, useNavigate, Routes } from "react-router-dom";

import DetailCard from "./components/detail-card";
import { ModelItem } from "../cards-tab";
import { getModelList } from "../../../shared/http/model";

import "../../../index.scss";
const ModelDetail = () => {
  // 路由
  const navigate = useNavigate();

  // 返回模型列表
  const backToModel = () => {
    navigate("/model");
  };
  const obj = {
    id: "",
    name: "",
    model: "",
    scale: "",
    type: "",
    orgnization: "",
    description: "",
    precision: {},
    gpu: {},
    npu: {},
    supported_images: [],
    status: "",
    latency: 0,
    speed: 0,
    requests: 0,
    responses: 0,
    exceptions: 0,
    throughput: 0,
    total_input_tokens: 0,
    total_output_tokens: 0,
  };
  const [modelItem, setModelList] = useState<ModelItem>(obj);

  // 获取数据列表
  const queryModelList = () => {
    getModelList().then((res) => {
      if (res) {
        setModelList(res.llms[0]);
      }
    });
  };
  useEffect(() => {
    queryModelList();
  });

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
          <div>{modelItem.name}</div>
          <div>{modelItem.status}</div>
        </div>
        <div>{modelItem.description}</div>
        <div
          style={{
            display: "flex",
            gap: 140,
          }}
        >
          <div>
            <div>机构</div>
            <div>{modelItem.orgnization}</div>
          </div>
          <div>
            <div>类型</div>
            <div>{modelItem.type}</div>
          </div>
          <div>
            <div>大模型容器镜像名称</div>
            <div>{modelItem.name}</div>
          </div>
          <div>
            <div>推理精度</div>
            <div>{modelItem.precision.default}</div>
          </div>
          <div>
            <div>大模型实例数</div>
            <div>{modelItem.gpu.max}</div>
          </div>
          <div>
            <div>单实例消耗的NPU数</div>
            <div>{modelItem.npu.max}</div>
          </div>
          <div>
            <div>模型服务端口号</div>
            <div>8000</div>
          </div>
        </div>
        <div
          className="operatorArea"
          style={{
            display: "flex",
            gap: "16px",
          }}
        >
          <DetailCard
            data={{title: "时延", content: modelItem.latency}}
            style={{
              flex: "0",
            }}
          />
          <DetailCard
            data={{title: "速度", content: modelItem.speed}}
            style={{
              flex: "0",
            }}
          />
        </div>
      </div>
    </div>
  );
};
export default ModelDetail;
