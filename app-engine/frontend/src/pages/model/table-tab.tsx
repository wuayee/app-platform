import React, { useEffect, useState } from "react";
import { Form } from "antd";
import { Button, Table } from "antd";
import type { TableProps } from "antd";
import { useNavigate } from "react-router-dom";
import { useSearchParams } from "react-router-dom";

import { columns } from "./components/columns";
import { ModelItem } from "./cards-tab";

const TableTab = ({modelList}: {modelList: ModelItem[]}) => {
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
