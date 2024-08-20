
import React, { useEffect, useState, useContext } from 'react';
import { Spin } from "antd";
import { saveChart } from "@shared/http/sse";
import { Message } from '@shared/utils/message';
import { ChatContext } from '@/pages/aippIndex/context';
import ChartCondition from '../../chart-message/chart-condition'
import ChartMessage from '../../chart-message/chart-message';
import { useTranslation } from 'react-i18next';

const ConditionForm = (props) => {
  const { t } = useTranslation();
  const { data, confirmCallBack, tenantId } = props;
  const [filters, setFilters] = useState();
  const [chartConfig, setChartConfig] = useState();
  const [loading, setLoading] = useState(false);
  const { conditionConfirm } = useContext(ChatContext);
  useEffect(() => {
    if (!data?.formData) return;
    if (data.formData) {
      try {
        let chartConfig2 = JSON.parse(data.formData.chartsData);
        setFilters(data.formData);
        setChartConfig(chartConfig2);
      } catch {

      }
    }
  }, [data?.formData]);

  // 表单确定
  const formConfirm = async (filter) => {
    try {
      setLoading(true);
      let params = {
        dimension: data.formData.dimension,
        rewriteQuery: data.formData.rewriteQuery,
        restartMode: 'increment',
        sourceTrace: JSON.stringify({
          queryType: data.formData.queryType,
          dsl: JSON.stringify(filter)
        })
      };
      const res = await saveChart(tenantId, data.formData.instanceId, params);
      if (res.status !== 200) {
        Message({ type: 'warning', content: res.msg || t('savingFailed') });
        return;
      }
      confirmCallBack ? confirmCallBack() : conditionConfirm(res);
    } finally {
      setLoading(false);
    }
  }
  return <>
    { !data && <div className="title">{t('sourceTracingForm')}</div>}
    { filters && (<Spin spinning={loading}>  <ChartCondition data={filters} confirm={formConfirm} /> </Spin>)}
    { chartConfig && <ChartMessage chartConfig={chartConfig} />}
  </>
};


export default ConditionForm;
