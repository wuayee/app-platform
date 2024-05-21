import type { TableProps } from "antd";
import { Button } from "antd";
import React from "react";
// 数据类型
interface DataType {}

// 列配置
export const columns: TableProps<DataType>["columns"] = [
  {
    title: "模型",
    dataIndex: "name",
    key: "name",
  },
  {
    title: "描述",
    dataIndex: "recordNum",
    key: "recordNum",
  },
  {
    title: "机构",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "类型",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "健康状态",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "请求数",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "回答数",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "异常数",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "吞吐量",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "输入token",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "输出token",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "时延",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "速度",
    dataIndex: "serviceType",
    key: "serviceType",
  },
  {
    title: "操作",
    dataIndex: "operator",
    key: "operator",
    render(value, record, index) {
      return (
        <>
          <div>
            <Button type="link">删除</Button>
          </div>
        </>
      );
    },
  },
];
