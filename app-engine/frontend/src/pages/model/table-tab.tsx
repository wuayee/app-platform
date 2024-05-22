import React, { useEffect, useState } from "react";
import { Form } from "antd";
import { Button, Table } from "antd";
import type { TableProps } from "antd";
import { useNavigate } from "react-router-dom";
import { useSearchParams } from "react-router-dom";

import { ModelItem } from "./cards-tab";

const TableTab = ({ modelList }: { modelList: ModelItem[] }) => {
  const navigate = useNavigate();
  const toModelDetail = (id: string) => {
    navigate("/model/detail", { state: { modelId: id } });
  };
  const columns: TableProps<ModelItem>["columns"] = [
    {
      title: "模型",
      dataIndex: "name",
      key: "name",
      render: (value, record) => (
        <a
          onClick={() => {
            toModelDetail(record.id);
          }}
        >
          {record.name}
        </a>
      ),
    },
    {
      title: "描述",
      dataIndex: "description",
      key: "description",
      width: 500,
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
      render: (value) => (
        <div
          style={{
            display: "flex",
          }}
        >
          <div>
            {value === "healthy" && (
              <img src="/src/assets/images/model/healthy.svg" />
            )}
            {value === "unhealthy" && (
              <img src="/src/assets/images/model/unhealthy.svg" />
            )}
            {value === "undeployed" && (
              <img src="/src/assets/images/model/undeployed.svg" />
            )}
          </div>
          <div style={{
            marginTop: -2,
          }}>{value}</div>
        </div>
      ),
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
  return (
    <div
      className="aui-block"
      style={{
        display: "flex",
        flexDirection: "column",
        gap: 8,
      }}
    >
      <div className="knowledge-detail-table">
        <Table
          columns={columns}
          dataSource={modelList}
          size="small"
          pagination={false}
        />
      </div>
      <div />
    </div>
  );
};
export default TableTab;
