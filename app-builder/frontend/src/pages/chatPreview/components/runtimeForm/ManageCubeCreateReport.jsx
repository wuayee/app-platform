import React, { useEffect, useState, useContext } from 'react';
import {Input, Button} from "antd";
import {EditOutlined} from '@ant-design/icons';
import styled from 'styled-components';
import ReportChart from "./ReportChart.jsx";
import {saveContent} from "../../../../shared/http/appBuilder";
import {AippContext} from "../../../aippIndex/context";
import chartImg from "../../../../../src/assets/images/chart.png";
import tableImg from "../../../../../src/assets/images/table.png";
import {Message} from "../../../../shared/utils/message";

const FormWrap = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: white;
    color: rgb(37, 43, 58);
    .report-title {
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-right: 10px;
    }
    .ant-input {
        //border: none !important;
        color: rgb(37, 43, 58)
    }
    .report-query {
        font-size: 16px;
        font-weight: 600;
    }
    .ant-input-disabled {
        border: none !important;
        background-color: white;
        color: rgb(37, 43, 58)
    }
    .save-button {
        background-color: rgb(4, 123, 252);
        border-radius: 4px;
        font-size: 14px;
        color: white;
        border-color: white;
        width: 60px;
        height: 32px;
        display: flex;
        justify-content: center;
        align-items: center;
    }
`;


const ManageCubeCreateReport = (props) => {
    const id = "reportResult";
    const {data, mode, instanceId} = props;
    const [chartData, setChartData] = useState(data);
    const [title, setTitle] = useState("经营分析报告");
    const [editable, setEditable] = useState(false);
    const [editTime, setEditTime] = useState(0);
    const [canSave, setCanSave] = useState(false);
    const {appId, tenantId, showElsa, agent, chatRunning} = useContext(AippContext);

    const handleEdit = () => {
        setEditable(true);
        setEditTime(1);
    }

    const handleSave = () => {
        setEditable(false);
        const data = {
            businessData: {[id]: chartData}
        }
        saveContent(tenantId, appId, instanceId, data).then((res) => {
            if (res.code !== 0) {
                Message({ type: 'warning', content: res.msg || '保存失败' });
            }
        })
    }

    const handleQueryChange = (e, indexChange) => {
        const newChartData = chartData.map((item, index) => {
            if (index === indexChange) {
                return {
                    ...item,
                    question: {
                        ...item.question,
                        query: e.target.value,
                    }
                };
            }
            return item;
        });
        setChartData(newChartData);
    }

    const handleSummaryChange = (e, indexChange, queryIndex) => {
        const newChartData = chartData.map((item, index) => {
            if (index === queryIndex) {
                return {
                    ...item,
                    answer: {
                        ...item.answer,
                        chartSummary: item.answer.chartSummary.map((item, index) => {
                            if (index === indexChange) {
                                return e.target.value;
                            };
                            return item;
                        })
                    }
                };
            }
            return item;
        });
        setChartData(newChartData);
    }

    useEffect(() => {
        if (!data) return;
        setChartData(data[id]);
    }, [data])

    useEffect(() => {
        if (editTime) {
            setCanSave(true);
        }
    }, [editTime])

    return (
        <>
            <FormWrap>
                <div style={{pointerEvents: mode === "history" ? "none" : "auto", width: "100%"}}>
                    <div className="report-title">
                        <div></div>
                        <div style={{fontSize: "28px", fontWeight: "600"}}>{title}</div>
                        <EditOutlined onClick={handleEdit} style={{fontSize: '20px',}}/>
                    </div>
                    {!chartData &&
                        <>
                            <img src={chartImg} alt="图表示例图片" style={{width: "300px"}}/>
                            <img src={tableImg} alt="表格示例图片" style={{width: "600px"}}/>
                        </>
                    }
                    {chartData && chartData.map((item, index) => (
                        <React.Fragment key={index}>
                            <Input className="report-query" defaultValue={item.question.query} disabled={!editable} onBlur={(e) => handleQueryChange(e, index)}></Input>
                            <ReportChart chartConfig={item.answer} disabled={!editable} handleSummaryChange={handleSummaryChange} queryIndex={index}/>
                        </React.Fragment>
                    ))}
                    <Button onClick={handleSave} disabled={!canSave} className="save-button">保存</Button>
                </div>
            </FormWrap>
        </>
    )
}

export default ManageCubeCreateReport;
