import React, { useState, useEffect } from "react";
import { Button, Input } from "antd";
import { Icons } from "../../components/icons";
import { queryAppsApi } from "../../shared/http/apps.js";
import AppCard from "./components/appCard";
import './index.scoped.scss'

const Apps: React.FC = () => {
    const create = () => {
        console.log("创建");
    };

    const [appData, setAppData] = useState([]);
    const queryApps = async () => {
        const res: any = await queryAppsApi();
        setAppData(res.data);
        console.log("res", res.data);
    };
    useEffect(() => {
        queryApps();
    }, []);

    return (
        <div className="aui-fullpage">
            <div className="aui-header-1">
                <div className="aui-title-1">应用市场</div>
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
                        onClick={create}
                    >
                        创建
                    </Button>
                    <Input
                        placeholder="搜索"
                        style={{
                            width: "200px",
                            borderRadius: "4px",
                            border: "1px solid rgb(230, 230, 230)",
                        }}
                        prefix={<Icons.search color={"rgb(230, 230, 230)"} />}
                    />
                </div>
                <div className="card_list">
                    {appData.map((item: any) => (
                        <>
                            <AppCard
                                key={item.id}
                                cardInfo={item}
                                style={{
                                    flex: "0",
                                }}
                            />
                        </>
                    ))}
                </div>
            </div>
        </div>
    );
};
export default Apps;
