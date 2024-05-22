import React, { useState, useEffect, ReactElement } from "react";
import { Button, Input } from "antd";
import { HashRouter, Route, useNavigate, Routes } from "react-router-dom";

import Pagination from "../../components/pagination/index";
import { getModelList } from "../../shared/http/model";
import CardsTab from "./cards-tab";
import TableTab from "./table-tab";

import "../../index.scss";
const ModelList = () => {
  // 总条数
  const [total, setTotal] = useState(0);

  const [modelTab, setModelTab] = useState(1);

  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(10);

  const [modelList, setModelList] = useState([]);

  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if (page !== curPage) {
      setPage(curPage);
    }
    if (pageSize != curPageSize) {
      setPageSize(curPageSize);
    }
  };

  // 获取数据列表
  const queryModelList = () => {
    getModelList({
      offset: page - 1,
      size: pageSize,
    }).then((res) => {
      if (res) {
        setModelList(res.llms);
        setTotal(res.llms.length);
      }
    });
  };

  useEffect(() => {
    queryModelList();
  }, [page, pageSize]);

  return (
    <div className="aui-fullpage">
      <div
        className="aui-header-1"
        style={{
          display: "flex",
          gap: "1000px",
        }}
      >
        <div className="aui-title-1">模型服务</div>
        <div
          className="aui-block"
          style={{
            background: "transparent",
            textAlign: "right",
          }}
        >
          {modelTab === 1 && (
            <img
              src="/src/assets/images/model/card-active.svg"
              onClick={() => setModelTab(1)}
            />
          )}
          {modelTab === 2 && (
            <img
              src="/src/assets/images/model/card.svg"
              onClick={() => setModelTab(1)}
            />
          )}
          {modelTab === 1 && (
            <img
              src="/src/assets/images/model/table.svg"
              onClick={() => setModelTab(2)}
            />
          )}
          {modelTab === 2 && (
            <img
              src="/src/assets/images/model/table-active.svg"
              onClick={() => setModelTab(2)}
            />
          )}
        </div>
      </div>
      <div className="aui-block">
        <div
          className="operatorArea"
          style={{
            display: "flex",
            gap: "16px",
          }}
        >
          <Button
            type="primary"
            style={{
              background: "#2673E5",
              width: "96px",
              height: "32px",
              fontSize: "14px",
              borderRadius: "4px",
              letterSpacing: "0",
            }}
          >
            创建
          </Button>
        </div>
        <div
          style={{
            marginLeft: -20,
          }}
        >
          {modelTab === 1 && <CardsTab modelList={modelList} />}
          {modelTab === 2 && <TableTab modelList={modelList} />}
        </div>
        <Pagination
          total={total}
          current={page}
          onChange={paginationChange}
          pageSize={pageSize}
        />
      </div>
    </div>
  );
};
export default ModelList;
