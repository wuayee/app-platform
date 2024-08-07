import React, { useEffect, useState, useContext } from 'react';
import {Input, Button} from "antd";
import {EditOutlined} from '@ant-design/icons';
import ReportChart from "./ReportChart.jsx";
import {saveContent} from "@shared/http/appBuilder";
import chartImg from "@/assets/images/chart.png";
import tableImg from "@/assets/images/table.png";
import {Message} from "@shared/utils/message";
import './styles/manage-cube-create-report.scoped.scss';



const ManageCubeCreateReport = (props) => {
  const id = "reportResult";
  const {data, mode, saveCallBack, tenantId} = props;
  const [chartData, setChartData] = useState(null);
  const [title, setTitle] = useState("经营分析报告");
  const [editable, setEditable] = useState(false);
  const [editTime, setEditTime] = useState(0);
  const [canSave, setCanSave] = useState(false);
  
  const handleEdit = () => {
    setEditable(true);
    setEditTime(1);
  }

  const handleSave = () => {
    setEditable(false);
    const params = {
      formAppearance: JSON.stringify(data.formAppearance),
      formData: JSON.stringify({[id]: chartData}),
      businessData: {
        parentInstanceId: data.parentInstanceId,
        [id]: chartData
      }
    }
    saveContent(tenantId, data.instanceId, params).then((res) => {
      if (res.code !== 0) {
        Message({ type: 'warning', content: res.msg || '保存失败' });
      } else {
        saveCallBack();
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
    if (!data?.formData) return;
    if (data.formData[id]) {
       typeof (data.formData[id]) === 'string' ? setChartData(JSON.parse(data?.formData[id])) : setChartData(data?.formData[id]);
    }
    
  }, [data?.formData])

  useEffect(() => {
    if (editTime) {
      setCanSave(true);
    }
  }, [editTime])

  return (<>
      <div className='form-wrap '>
        <div>
          <div className="report-title">
            <div style={{fontSize: "28px", fontWeight: "600"}}>{title}</div>
            { mode !== "history" && <EditOutlined onClick={handleEdit} style={{fontSize: '20px', position: 'absolute', right: '20px'}}/> }
          </div>
            {!chartData &&
              <>
                <img src={chartImg} alt="图表示例图片" style={{width: "100%"}}/>
                <img src={tableImg} alt="表格示例图片" style={{width: "100%"}}/>
              </>
            }
            {chartData && chartData.map((item, index) => (
              <React.Fragment key={index}>
                <Input 
                  className="report-query" 
                  defaultValue={item.question.query} 
                  disabled={!editable} 
                  onBlur={(e) => handleQueryChange(e, index)}>
                </Input>
                <ReportChart 
                  chartConfig={item.answer} 
                  disabled={!editable} 
                  handleSummaryChange={handleSummaryChange} 
                  queryIndex={index}/>
              </React.Fragment>
            ))}
            <div className="report-btn">
              { mode !== "history" && <Button onClick={handleSave} disabled={!canSave} className="save-button">保存</Button> } 
            </div>
        </div>
      </div>
  </>);
}

export default ManageCubeCreateReport;
