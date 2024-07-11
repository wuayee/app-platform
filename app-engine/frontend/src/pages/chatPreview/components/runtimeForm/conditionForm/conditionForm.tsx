
import React, { useEffect, useState, useContext } from 'react';
import { reSendChat } from "@shared/http/aipp";
import { Message } from "@shared/utils/message";
import { ChatContext } from '@/pages/aippIndex/context';
import ChartCondition from '../../chart-message/chart-condition'
import ChartMessage from '../../chart-message/chart-message';

const ConditionForm = (props) => {
  const { data, chatRunning } = props;
  const [filters, setFilters] = useState();
  const [chartConfig, setChartConfig] = useState();
  const { tenantId, conditionConfirm } = useContext(ChatContext);
  useEffect(() => {
    if (!data?.formData) return;
    if (data.formData) {
      try {
        let chartConfig2 = JSON.parse(data.formData.chartsData);
        setFilters(data.formData.dsl);
        setChartConfig(chartConfig2);
      } catch {

      }
    }
  }, [data?.formData]);

  // 表单确定
  const formConfirm = (filter) => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中，请稍后再试' });
      return
    }
    let params = {
        dimension: data.formData.dimension,
        rewriteQuery: data.formData.rewriteQuery,
        sourceTrace: JSON.stringify(filter)
    };
    reSendChat(tenantId, data.formData.instanceId, params).then((res) => {
      if (res.code !== 0) {
        Message({ type: 'warning', content: res.msg || '保存失败' });
      } else {
        conditionConfirm(data.logId, data.formData.instanceId);
      }
    })
  }
  return <>
      { !data && <div className="title">溯源表单</div> }
      { filters &&  <ChartCondition data={filters} confirm={formConfirm} />}
      { chartConfig && <ChartMessage chartConfig={ chartConfig } /> }
    </>
};


export default ConditionForm;
