
import React, { useEffect, useState, useRef } from 'react';
import ChartItem from './chart-item.jsx';

const ChartMessage = (props) => {
  const { answer, chartAnswer, chartData, chartTitle, chartType } = props.chartConfig;
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
          const chartItem = initChartData(item, chartTypeItem, chartTitleItem);
          setChartList(() => {
            let current = listRef.current || [];
            let arr = [ ...current, JSON.parse(JSON.stringify(chartItem)) ];
            listRef.current = arr;
            return arr
          })
        });
      } else {
        const chartItem = initChartData( chartData, chartType);
        setChartList(() => {
          let current = listRef.current || [];
          let arr = [ ...current, chartItem ];
          listRef.current = arr;
          return arr
        })
      }
    }
  }
  // 组装数据
  function initChartData(chartData, chartType, chartTitle) {
    const chartItem = {
      chartData,
      chartType,
      chartAnswer,
      chartTitle,
      yAxisData: {
        type: 'value',
        show: false,
        splitLine: {
          show: false,
        },
      },
    };
    const legend = [];
    let list = [];
    let data = [];
    switch (chartType) {
      case 'TABLE':
        try {
          chartItem.headers = chartData.columns?.map((field, index) => {
            return {
              key: field + index,
              title: field,
              dataIndex: field + index,
              align: 'center',
              resizable: true,
              width: getColumnWidthFromField(field, index, chartData.rows),
              showHeadOverflowTooltip: true,
              showOverflowTooltip: true,
            };
          });
          list = chartData.rows?.map((item, index) => {
            let obj = {};
            item.forEach((a, b) => {
              obj[chartItem.headers[b].key] = a;
            });
            obj.rowIndex = index;
            return obj;
          });
          chartItem.tableData = list;
        } catch {
          chartItem.headers = [];
          chartData.rows = [];
          chartItem.tableData = [];
        }
        break;
      case 'PIE':
        chartItem.xAxisData = chartData.labels;
        chartItem.chartData = [{
          data: chartData.data,
          barMaxWidth: 30,
          barMinWidth: 10,
          label: {
            show: true,
            position: 'top',
          },
          type: 'bar',
        }];
        chartItem.legendData = chartData.labels;
        const pieData = chartData.data?.map((value, index) => {
          return {
            name: chartData.labels[index],
            value
          };
        });
        chartItem.chartPieData = [{
          data: pieData,
          type: 'pie',
          label: {
            formatter: '{b}: ({d}%)',
          },
          center: ['40%', '50%'],
          selectedMode: 'single',
        }]
        break;
      case 'LINE':
        chartItem.xAxisData = chartData.labels;
        chartData.datasets.forEach((item) => {
          legend.push(item.label);
          list.push({
            name: item.label,
            data: item.data,
            type: 'line',
          });
        });
        chartItem.chartData = list;
        chartItem.legendData = legend;
        break;
      case 'BAR':
        chartItem.xAxisData = chartData.labels;
        chartItem.chartData = {
          data: chartData.data,
          barMaxWidth: 30,
          barMinWidth: 10,
          label: {
            show: true,
            position: 'top',
          },
          type: 'bar',
        };
        break;
      case 'MIX_LINE_BAR':
        chartItem.xAxisData = chartData.labels;
        chartData.bar?.forEach((item) => {
          legend.push(item.title);
          data.push({
            type: 'bar',
            name: item.title,
            data: item.data,
            barMaxWidth: 30,
            barMinWidth: 10,
            label: {
              show: true,
              position: 'top',
            },
          });
        });
        chartItem.yAxisData = [
          {
            type: 'value',
            position: 'left',
            show: false,
          },
          {
            type: 'value',
            position: 'right',
            show: false,
            axisLabel: {
              formatter: '{value}%',
            },
          },
        ];
        chartData.line?.forEach((item) => {
          legend.push(item.title);
          data.push({
            type: 'line',
            name: item.title,
            smooth: true,
            data: item.data?.map((item) => item.substr(0, item.length - 1)),
            label: {
              show: true,
              position: 'top',
              formatter: '{c}%',
            },
            yAxisIndex: 1,
          });
        });
        chartItem.legendData = legend;
        chartItem.chartData = data;
        break;
      case 'BAR_STACK':
        chartItem.xAxisData = chartData.labels;
        chartData.stack.forEach((item, index) => {
          legend.push(item.title);
          data.push({
            type: 'bar',
            name: item.title,
            data: item.data,
            stack: '总量',
            barMaxWidth: 20,
            barMinWidth: 10,
            label: {
              show: false,
            },
          });
        });
        chartItem.yAxisData = [
          {
            type: 'value',
            position: 'left',
            show: false,
          },
        ];
        chartData.line.forEach((item) => {
          legend.push(item.title);
          data.push({
            type: 'line',
            name: item.title,
            smooth: true,
            data: item.data,
            label: {
              show: true,
              position: 'top',
            },
            yAxisIndex: 0,
          });
        });
        chartItem.legendData = legend;
        chartItem.chartData = data;
        break;
        default:
    }
    return chartItem;
  }
  const is2KResolution = window.innerWidth >= 2048 && window.innerHeight >= 1080;
  function getColumnWidthFromField(field, index, chartData) {
    const lengthList = chartData.map(item => item[index].replace(/[^\x00-\xff]/g,"01").length);
    const dataFiled = chartData[lengthList.indexOf(Math.max(...lengthList))][index];
    const aimFiled = dataFiled.replace(/[^\x00-\xff]/g, "01").length > field.replace(/[^\x00-\xff]/g, "01").length ? dataFiled : field;
    let canvas = document.createElement('canvas');
    let ctx = canvas.getContext('2d');
    ctx.fontSize = '20px';
    let { width } = ctx.measureText(aimFiled);
    const result = is2KResolution ? width * 1.4 + 80 : width * 1.4 + 36;
    return result > 80 ? result : 80
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
