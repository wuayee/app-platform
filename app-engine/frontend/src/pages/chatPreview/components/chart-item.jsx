
import React, { useEffect, useState, useRef } from 'react';
import { Table, Dropdown, Space } from "antd";
import { DownOutlined } from '@ant-design/icons';
import screenfull from 'screenfull';
import * as echarts from 'echarts';
import { DownLoadIcon, FullScreenIcon } from '../../../assets/icon';
import '../styles/chart-item.scss';

const ChatItem = (props) => {
  const { chartAnswer, chatItem } = props;
  const { chartType } = chatItem;
  return <>{(
    <div>
      <div className='receive-mark'>{ chartAnswer || '' }</div>
      {  chartType === 'TABLE' ? <ChartTable chatItem={chatItem} /> : <ChartGraphs chatItem={chatItem}/> }
    </div>
  )}</>
};

// 表格组件
const ChartTable = (props) => {
  const { chartTitle, tableData, headers } = props.chatItem;
  const [ isFullscreen, setIsFullScreen ] = useState(false);
  const tableRef = useRef(null);
  useEffect(() => {
    getChartWidth();
    screenfull.on('change', () => {
      setIsFullScreen(screenfull.isFullscreen);
    })
  }, [ props ]);

  // 设置表格细节
  function getChartWidth() {
    const messageDiv = document.querySelector('.receive-box');
    if (messageDiv) {
      const width = messageDiv?.clientWidth - 80;
      let tableWidth = 0;
      headers.forEach((a, b) => {
        tableWidth += a.width;
      });
      let reg = /^\d+$/;
      const isPureNumber = tableData.every((element) => {
        return reg.test(parseInt(element[headers[0].key]));
      });
      if (tableWidth > width) {
        !isPureNumber && (headers[0].fixed = 'left');
      } else {
        headers.forEach((a) => {
          if (a.width < (width - 10) / headers.length) {
            delete a.width;
          }
        });
      }
    }
  }
  // 表格数据导出
  function exportTableData() {
    let str = '';
    str += `序号,${headers.map((item) => item.title).join(',')}\n`;
    tableData.forEach((element, index) => {
      str += `${index + 1},${getColumnData(element)}\n`;
    });
    let blob = new Blob([str], { type: 'text/plain;charset=utf-8' });
    blob = new Blob([String.fromCharCode(0xfeff), blob], { type: blob.type });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${chartTitle || '下载'}.csv`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
  function getColumnData(row) {
    let str = '';
    headers.forEach((item) => {
      str += `${row[item.key]},`;
    });
    return str;
  }
  // 全屏
  function fullScreenTable() {
    if (screenfull.isFullscreen) {
      screenfull.exit();
      return;
    }
    screenfull.request(tableRef.current);
  }
  return <>{(
    <div className={ ['table-dom', isFullscreen ? 'table-full-dom' : null].join(' ') } ref={tableRef}>
      <div className='tool-box'>
        <div className='tool-text'>{ chartTitle || ''}</div>
        <div className='table-tool'>
          <DownLoadIcon onClick={exportTableData} />
          <FullScreenIcon onClick={fullScreenTable}/>
        </div>
      </div>
      <Table 
        dataSource={tableData} 
        columns={headers} 
        scroll={{ y: isFullscreen ? 900 : 240 }} 
        bordered
        size="small"
        pagination={false} 
        rowKey={record => record.rowIndex } />
    </div>
  )}</>
}

// 图表组件
const ChartGraphs = (props) => {
  const { 
    chartPieData, 
    chartData, 
    chartTitle, 
    chartType, 
    xAxisData, 
    yAxisData, 
    legendData 
  } = props.chatItem;
  const chartRef = useRef(null);
  const myChart = useRef(null);
  const [ chartTypeName, setChartTypeName ] = useState('推荐');
  useEffect(() => {
    initChart();
    screenfull.on('change', () => {
      myChart?.current.resize();
    });
  }, [props.chatItem]);
  const items = [
    {
      label: '推荐',
      key: 'normal',
    },
    {
      label: '柱状图',
      key: 'BAR',
    },
    {
      label: '折线图',
      key: 'LINE',
    },
  ]
  const seriesMap = {
    pie: chartPieData,
    circlepie: chartPieData,
    line: chartData,
    bar: chartData,
  };
  const chartTypeMap = {
    TABLE: 'table',
    BAR: 'bar',
    LINE: 'line',
    MIX_LINE_BAR: 'bar',
    PIE: 'pie',
    FORECAST: 'line',
    BAR_STACK: 'bar'
  }
  const gridMap = {
    pie: {
      right: legendData && 140,
    },
    line: {
      top: 60,
      left: 20,
      right: legendData && 140,
      bottom: 40,
    },
    bar: {
      top: 60,
      left: 20,
      right: legendData && 140,
      bottom: getEndPosition() < 100 ? 50 : 20,
    },
  };
  const legendConfig = {
    type: 'scroll',
    textStyle: {
      width: 120,
      overflow: 'break',
    },
    bottom: '0',
    tooltip: {
      show: true,
      trigger: 'item',
    },
    data: legendData,
  };
  // 渲染图表
  function initChart() {
    let currentChartType = chartTypeMap[chartType];
    myChart.current = echarts.init(chartRef.current);
    const option = getOptions(seriesMap[currentChartType], currentChartType);
    myChart?.current.setOption(option, true);
    resizeObserver.observe(chartRef.current);
  }
  // 全屏
  function fullScreenTable() {
    if (screenfull.isFullscreen) {
      screenfull.exit();
      return;
    }
    screenfull.request(chartRef.current);
  }
  // 设置图表配置
  function getOptions(seriesData = null ,type) {
    const itemTooltip = chartType === 'BAR_STACK' || type.includes('pie');
    type === 'circlepie' && (seriesData[0].radius = ['50%', '60%']);
    return {
      color: ['#5E7CE0', '#6CBFFF', '#50D4AB', '#A6DD82', '#FAC20A'],
      xAxis: {
        type: 'category',
        show: !type.includes('pie'),
        axisTick: {
          alignWithLabel: true,
          show: false
        },
        axisLine: {
          lineStyle: {
            color: '#575d6c'
          }
        },
        axisLabel: {
          formatter: function (params) {
            let newParamsName = ''
            const paramsNameNumber = params.length
            const provideNumber = 6 // 单行显示文字个数
            const rowNumber = Math.ceil(paramsNameNumber / provideNumber)
            if (paramsNameNumber > provideNumber) {
              for (let p = 0; p < rowNumber; p++) {
                let tempStr = ''
                let start = p * provideNumber
                let end = start + provideNumber
                if (p === rowNumber - 1) {
                  tempStr = params.substring(start, paramsNameNumber);
                } else {
                  tempStr = params.substring(start, end) + '\n';
                }
                newParamsName += tempStr;
              }
            } else {
              newParamsName = params;
            }
            return newParamsName;
          },
        },
        data: xAxisData,
      },
      yAxis: yAxisData,
      dataZoom: [
        {
          show:  !type.includes('pie') && getEndPosition() < 100,
          start: 0,
          end: getEndPosition(),
          type: 'slider',
          xAxisIndex: 0,
          width: '80%',
          height: 10,
          showDataShadow: false,
          left: '20',
          bottom: 40,
        },
      ],
      grid: gridMap[chartType],
      legend: legendConfig,
      tooltip: {
        trigger: itemTooltip ? 'item' : 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow', // 默认为直线，可选为：'line' | 'shadow'
        }
      },
      toolbox: {
        feature: {
          saveAsImage: {
            pixelRatio: 2,
            title: '保存'
          },
          myFull: { // 全屏
            show: true,
            title: '全屏',
            icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAANVJREFUWEdjZBhgwDjA9jPAHaBiaCjK8fu/NiEHXbly4QA+NTo6Bg6EzPjBynj1zvnzr0Hq4A7Q0dEPY2JmXElI86WLF/CGmp6+wX9CZvz/xxB2+fKF1YPLAbq6BqGMTAyrwK7/z9Dw7x/DQWw+ITcKmJgYHBgYGeoh5v8LvXTp0hqcIYAcRISCk1h55CgmGAWjDhgNgQEJAWJTM7XVDZ66gNo+I9a80RAYDYHBEwK4aitiUzMhdaO14WgIDP4QGFSNUnxZavj2Cwa8a0aoJKOV/IDXBQDgZwww+OlbrwAAAABJRU5ErkJggg==',
            onclick: (e) => {
              fullScreenTable()
            }
          }
        }
      },
      title: {
        text: chartTitle,
        top: 10,
        left: 10,
        textStyle: {
          fontSize: 14,
          color: '#71757F',
          fontWeight: 400,
        },
      },
      series: seriesData || seriesMap[type],
    }
  }
  function getEndPosition() {
    return xAxisData && Math.ceil((6 * 100) / xAxisData.length);
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
    let options = getOptions(seriesData, type);
    myChart?.current.clear();
    myChart?.current.setOption(options, true);
  }
  const resizeObserver = new ResizeObserver(entries => {
    myChart?.current.resize();
  })
  return <>{(
    <div className="chart-dom-parent">
      <div className='chart-dom' ref={chartRef}></div>
      <div className="chart-drop">
        <Dropdown menu={{ items, onClick }} trigger={['click']}>
          <span>
            { chartTypeName }  
            <DownOutlined className="chart-icon" />
          </span>
        </Dropdown>
      </div>
    </div>
  )}</>
}
export default ChatItem;
