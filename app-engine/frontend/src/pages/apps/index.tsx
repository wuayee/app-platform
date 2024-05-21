import React, { useState, useEffect, useRef } from "react";
import { Button, Divider, Input } from "antd";
import { Icons } from "../../components/icons";
import { queryAppsApi } from "../../shared/http/apps.js";
import AppCard from "./components/appCard";
import './index.scoped.scss'
import { debounce } from "../../shared/utils/common";
import EditModal from "../components/edit-modal";
import { HashRouter, Route, useNavigate, Routes } from "react-router-dom";

const Apps: React.FC = () => {
    const tenantId = "31f20efc7e0848deab6a6bc10fc3021e";
    const navigate = useNavigate();

    // 数据初始化
    const [appData, setAppData] = useState([]);
    const [oriData, setOriData] = useState([]);
    async function queryApps(){
      const res: any = await queryAppsApi(tenantId);
      if(res.code === 0){
        const {results}=res.data
        setAppData(results);
        setOriData(results);
      }
    };
    useEffect(() => {
      queryApps();
    }, []);

    // 创建
    let modalRef:any = useRef();
    const [modalInfo, setModalInfo] = useState({});
    const create = () => {
      setModalInfo(() => {
        modalRef.current.showModal();
        return {
          name: "",
          attributes: {
            description: "",
            greeting: "",
            icon: "",
            app_type: "编程开发",
          },
        };
      });
    };
    function addAippCallBack(appId:string) {
      navigate(`/app/${tenantId}/detail/${appId}`);
    }

    // 搜索
    function onSearchValueChange(value: string) {
      const arr = oriData.filter((v: any) => v.name.includes(value));
      setAppData(arr);
    }
    const handleSearch = debounce(onSearchValueChange, 500);

    // 点击卡片
    function clickCard(item:any){
      navigate(`/app/${tenantId}/appDetail/${item.id}`);
    }

    return (
      <div className="aui-fullpage">
        <div className="aui-header-1">
          <div className="aui-title-1">应用市场</div>
        </div>
        <div className="aui-block">
          <div className="operatorArea">
            <Button type="primary" onClick={create}>
              创建
            </Button>
            <Input
              placeholder="搜索"
              style={{ width: "200px", height: "35px", marginLeft: "16px" }}
              prefix={<Icons.search color={"rgb(230, 230, 230)"} />}
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>
          {appData.length > 0 ? (
            <div className="card_list">
              {appData.map((item: any) => (
                <div key={item.id} onClick={() => clickCard(item)}>
                  <AppCard cardInfo={item} />
                </div>
              ))}
            </div>
          ) : (
            <div>暂无数据</div>
          )}
        </div>
        <EditModal
          type="add"
          modalRef={modalRef}
          aippInfo={modalInfo}
          addAippCallBack={addAippCallBack}
        />
      </div>
    );
};
export default Apps;
