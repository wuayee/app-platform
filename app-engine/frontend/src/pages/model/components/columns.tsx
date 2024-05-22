import type { TableProps } from "antd";
import { Button } from "antd";
import React from "react";
import { ModelItem } from "../cards-tab";
import { useNavigate } from "react-router-dom";

// 列配置
export const columns: TableProps<ModelItem>["columns"] = [
  {
    title: "模型",
    dataIndex: "name",
    key: "name",
  },
  {
    title: "描述",
    dataIndex: "description",
    key: "description",
  },
  {
    title: "机构",
    dataIndex: "orgnization",
    key: "orgnization",
  },
  {
    title: "类型",
    dataIndex: "type",
    key: "type",
  },
  {
    title: "健康状态",
    dataIndex: "status",
    key: "status",
  },
  {
    title: "请求数",
    dataIndex: "requests",
    key: "requests",
  },
  {
    title: "回答数",
    dataIndex: "responses",
    key: "responses",
  },
  {
    title: "异常数",
    dataIndex: "exceptions",
    key: "exceptions",
  },
  {
    title: "吞吐量",
    dataIndex: "throughput",
    key: "throughput",
  },
  {
    title: "输入token",
    dataIndex: "total_input_tokens",
    key: "total_input_tokens",
  },
  {
    title: "输出token",
    dataIndex: "total_output_tokens",
    key: "total_output_tokens",
  },
  {
    title: "时延",
    dataIndex: "latency",
    key: "latency",
  },
  {
    title: "速度",
    dataIndex: "speed",
    key: "speed",
  },
  {
    title: "操作",
    dataIndex: "operator",
    key: "operator",
    render() {
      return (
        <div>
          <Button type="link">删除</Button>
        </div>
      );
    },
  },
];
