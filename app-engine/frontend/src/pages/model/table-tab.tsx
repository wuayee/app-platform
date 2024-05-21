import React, { useEffect, useState } from "react";
import { Form } from "antd";
import { Button, Table } from "antd";
import type { TableProps } from "antd";
import { useNavigate } from "react-router-dom";
import { useSearchParams } from "react-router-dom";

import { columns } from "./components/columns";
import Pagination from "../../components/pagination/index";

const TableTab = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const id = searchParams.get("id");

  const [knowledgeDetail, setKnowledgeDetail] = useState<any>(null);
  const [data, setData] = useState<any>([]);

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
          dataSource={data}
          size="small"
          pagination={false}
        />
      </div>
      <div />
    </div>
  );
};
export default TableTab;
