
import React, { useEffect, useState, useRef } from 'react';
import { initChartData } from './utils/chart-utils';
import ChartItem from './chart-item';

const ChartMessage = (props) => {
  const { chartAnswer, chartData, chartTitle, chartType } = props.chartConfig;
  const [ chartList, setChartList ] = useState([]);
  const listRef = useRef(null);

  useEffect(() => {
    handleChartData();
  }, [])
  // 图表数据格式化
  function handleChartData() {
    if (chartData) {
      if (Array.isArray(chartData)) {
        chartData.forEach((item, index) => {
          if (typeof item === 'string') {
            item = JSON.parse(item);
          }
          const chartTypeItem = chartType[index];
          const chartTitleItem = chartTitle[index];
          const chartItem = initChartData(item, chartAnswer, chartTypeItem, chartTitleItem);
          setChartList(() => {
            let current = listRef.current || [];
            let arr = [ ...current, JSON.parse(JSON.stringify(chartItem)) ];
            listRef.current = arr;
            return arr
          })
        });
      } else {
        const chartItem = initChartData( chartData, chartAnswer, chartType);
        setChartList(() => {
          let current = listRef.current || [];
          let arr = [ ...current, chartItem ];
          listRef.current = arr;
          return arr
        })
      }
    }
  }
  return <>{(
    <div className='receive-info'>
      {
        chartList.map((item, index) => {
          return(
            <ChartItem 
              key={index} 
              chatItem={item} 
              chartAnswer={chartAnswer && chartAnswer[index]}
            />
          )
        })
      }
    </div>
  )}</>
};

export default ChartMessage;
