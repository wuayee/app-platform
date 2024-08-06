import React, { useState, useEffect, ReactElement } from "react";
import { Button, Input } from "antd";
import { HashRouter, Route, useLocation, Switch } from "react-router-dom";
import GoBack from "../../../components/go-back/GoBack";

import DetailCard from "./components/detail-card";
import BarCard from "./components/bar-card";
import { ModelItem } from "../cards-tab";
import { getModelList } from "../../../shared/http/model";

import "../../../index.scss";
const ModelDetail = () => {
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
    replicas: 0,
    npu_flag: true,
    xpu_consume: 0,
    port: 0,
    image: ""
  };
  const [modelItem, setModelItem] = useState<ModelItem>(obj);
  const location = useLocation();

  // 获取数据列表
  const queryModelList = () => {
    getModelList().then((res) => {
      if (res) {
        res.llms.map((item: any) => {
          if (item.id === location.state.modelId) {
            setModelItem(item);
          }
        });
      }
    });
  };
  useEffect(() => {
    queryModelList();
  }, []);

  return (
    <div className="aui-fullpage">
      <div className="aui-title-1">
        <GoBack path={"/model"} title="模型服务详情" />
      </div>
      <div className="aui-block">
        <div
          style={{
            display: "flex",
          }}
        >
          <div
            style={{
              fontSize: 20,
            }}
          >
            {modelItem.name}
          </div>
          <div
            style={{
              marginLeft: 30,
              marginTop: 8,
            }}
          >
            {modelItem.status === "healthy" && (
              <img src="/src/assets/images/model/healthy.svg" />
            )}
            {modelItem.status === "unhealthy" && (
              <img src="/src/assets/images/model/unhealthy.svg" />
            )}
            {modelItem.status === "undeployed" && (
              <img src="/src/assets/images/model/undeployed.svg" />
            )}
          </div>
          <div
            style={{
              marginTop: 5,
            }}
          >
            {modelItem.status}
          </div>
        </div>
        <div
          style={{
            marginTop: 10,
          }}
        >
          {modelItem.description}
        </div>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginTop: 10,
          }}
        >
          <div>
            <div
              style={{
                fontSize: 12,
              }}
            >
              机构
            </div>
            <div>{modelItem.orgnization}</div>
          </div>
          <div>
            <div
              style={{
                fontSize: 12,
              }}
            >
              类型
            </div>
            <div>{modelItem.type}</div>
          </div>
          <div>
            <div
              style={{
                fontSize: 12,
              }}
            >
              大模型容器镜像名称
            </div>
            <div>{modelItem.image}</div>
          </div>
          <div>
            <div
              style={{
                fontSize: 12,
              }}
            >
              推理精度
            </div>
            <div>{modelItem.precision.default}</div>
          </div>
          <div>
            <div
              style={{
                fontSize: 12,
              }}
            >
              大模型实例数
            </div>
            <div>{modelItem.replicas}</div>
          </div>
          <div>
            <div
              style={{
                fontSize: 12,
              }}
            >
              {modelItem.npu_flag === true && "单实例消耗的NPU数"}
              {modelItem.npu_flag === false && "单实例消耗的GPU数"}
            </div>
            <div>{modelItem.xpu_consume}</div>
          </div>
          <div>
            <div
              style={{
                fontSize: 12,
              }}
            >
              模型服务端口号
            </div>
            <div>{modelItem.port === 0 && "--"}</div>
            <div>{modelItem.port !== 0 && modelItem.port}</div>
          </div>
        </div>
        <div
          className="operatorArea"
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginTop: 10,
          }}
        >
          <DetailCard
            data={{ title: "时延", content: modelItem.latency, unit: "s" }}
            style={{
              borderRadius: "4px",
            }}
          />
          <DetailCard
            data={{ title: "速度", content: modelItem.speed, unit: "token/s" }}
            style={{
              borderRadius: "4px",
            }}
          />
        </div>
        <div
          className="operatorArea"
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginTop: 10,
          }}
        >
          <BarCard
            data={{ title: "数据表现", content: modelItem }}
            style={{
              borderRadius: "4px",
            }}
          />
          <BarCard
            data={{ title: "Token", content: modelItem }}
            style={{
              borderRadius: "4px",
            }}
          />
        </div>
      </div>
    </div>
  );
};
export default ModelDetail;
