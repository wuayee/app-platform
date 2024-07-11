
import React, { useEffect, useState, useRef } from 'react';
import { Dropdown } from 'antd';
import screenfull from 'screenfull';
import { chartTypeMap, items, getOptions } from '../utils/chart-graphs';
import { DownOutlined } from '@ant-design/icons';
import * as echarts from 'echarts';

// 图表组件
const ChartGraphs = (props) => {
  const { chartPieData, chartData, chartType} = props.chatItem;
  const chartRef = useRef(null);
  const myChart = useRef(null);
  const [ chartTypeName, setChartTypeName ] = useState('推荐');
  useEffect(() => {
    initChart();
    screenfull.on('change', () => {
      myChart?.current.resize();
    });
  }, [props.chatItem]);
  const seriesMap:any = {
    pie: chartPieData,
    circlepie: chartPieData,
    line: chartData,
    bar: chartData,
  };
  // 渲染图表
  function initChart() {
    let currentChartType = chartTypeMap[chartType];
    myChart.current = echarts.init(chartRef.current);
    const option = getOptions(props, seriesMap[currentChartType], currentChartType, chartRef);
    myChart?.current.setOption(option, true);
    resizeObserver.observe(chartRef.current);
  }
  // 图表切换回调
  function onClick ({ key }) {
    const clickItem = items.filter(item => item.key === key)[0];
    let { label } = clickItem;
    setChartTypeName(label);
    chartSwitchFn(key);
  }
  // 切换图表类型
  function chartSwitchFn(curtype) {
    let type = curtype === 'normal' ? chartTypeMap[chartType] : chartTypeMap[curtype];
    let seriesData = JSON.parse(JSON.stringify(seriesMap[type]));
    if (curtype !== 'normal') {
      if (Array.isArray(seriesData)) {
        seriesData.forEach((item) => {
          item.type = type === 'circlepie' ? 'pie' : type;
          if (type === 'bar') {
            item.barMaxWidth = 20;
            item.barMinWidth = 10;
          }
          type === 'line' ? (item.smooth = true) : delete item.smooth;
        });
      } else {
        seriesData.type = type === 'circlepie' ? 'pie' : type;
        if (type === 'bar') {
          seriesData.barMaxWidth = 30;
          seriesData.barMinWidth = 10;
        }
        type === 'line' ? (seriesData.smooth = true) : delete seriesData.smooth;
      }
    }
    let options = getOptions(props, seriesData, type, chartRef);
    myChart?.current.clear();
    myChart?.current.setOption(options, true);
  }
  const resizeObserver = new ResizeObserver(entries => {
    myChart?.current.resize();
  })
  return <>{(
    <div className='chart-dom-parent'>
      <div className='chart-dom' ref={chartRef}></div>
      <div className='chart-drop'>
        <Dropdown menu={{ items, onClick }} trigger={['click']}>
          <span>
            { chartTypeName }  
            <DownOutlined className='chart-icon' />
          </span>
        </Dropdown>
      </div>
    </div>
  )}</>
}

export default ChartGraphs;
