import React, { useEffect, useState } from 'react';
import { Input, Button } from 'antd';
import { EditOutlined } from '@ant-design/icons';
import ReportChart from './ReportChart.jsx';
import { saveContent } from '@shared/http/sse';
import chartImg from '@/assets/images/chart.png';
import tableImg from '@/assets/images/table.png';
import { Message } from '@shared/utils/message';
import { useTranslation } from 'react-i18next';
import './styles/manage-cube-create-report.scoped.scss';

const ManageCubeCreateReport = (props) => {
  const { t } = useTranslation();
  const id = 'reportResult';
  const { data, mode, saveCallBack, tenantId, confirmCallBack } = props;
  const [chartData, setChartData] = useState(null);
  const [editable, setEditable] = useState(false);
  const [loading, setLoading] = useState(false);
  const [disabled, setDisabled] = useState(true);

  const handleEdit = () => {
    setEditable(true);
    setDisabled(false);
  }
  // 分析报告点击确定回调
  const handleSave = async () => {
    setEditable(false);
    const params = {
      formAppearance: JSON.stringify(data.formAppearance),
      formData: JSON.stringify({ [id]: chartData }),
      businessData: {
        parentInstanceId: data.parentInstanceId,
        [id]: chartData
      }
    };
    try {
      setLoading(true);
      const res = await saveContent(tenantId, data.instanceId, params);
      if (res.status !== 200) {
        Message({ type: 'warning', content: res.msg || t('savingFailed') });
        return;
      };
      confirmCallBack ? confirmCallBack() : saveCallBack(res);
    } finally {
      setLoading(false);
      setDisabled(true);
    }
  };
  // 修改标题回调
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
  // 修改内容回调
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
  // 初始化数据
  useEffect(() => {
    if (!data?.formData) return;
    if (data.formData[id]) {
      typeof (data.formData[id]) === 'string' ? setChartData(JSON.parse(data?.formData[id])) : setChartData(data?.formData[id]);
    }
  }, [data?.formData])

  return (<>
    <div className='form-wrap '>
      <div>
        <div className='report-title'>
          <div style={{ fontSize: '28px', fontWeight: '600' }}>{t('analysisReport')}</div>
          {mode !== 'history' && <EditOutlined onClick={handleEdit} style={{ fontSize: '20px', position: 'absolute', right: '20px' }} />}
        </div>
        {!chartData &&
          <>
            <img src={chartImg} style={{ width: '100%' }} />
            <img src={tableImg} style={{ width: '100%' }} />
          </>
        }
        {chartData && chartData.map((item, index) => (
          <React.Fragment key={index}>
            <Input
              className='report-query'
              defaultValue={item.question.query}
              disabled={!editable}
              onBlur={(e) => handleQueryChange(e, index)}>
            </Input>
            <ReportChart
              chartConfig={item.answer}
              disabled={!editable}
              handleSummaryChange={handleSummaryChange}
              queryIndex={index} />
          </React.Fragment>
        ))}
        <div className='report-btn'>
          {mode !== 'history' && <Button type='primary' onClick={handleSave} disabled={disabled} loading={loading}>{t('save')}</Button>}
        </div>
      </div>
    </div>
  </>);
}

export default ManageCubeCreateReport;
