import screenfull from '@/shared/screenfull/screenfull';

// 初始化echarts图表
export const getOptions = (props, seriesData, type, chartRef) => {
  const { chartPieData, chartData, chartTitle, chartType, xAxisData, yAxisData, legendData } = props.chatItem;
  const gridMap: any = {
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
      bottom: getEndPosition(legendData) < 100 ? 50 : 20,
    },
  };
  const legendConfig: any = {
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
  const seriesMap: any = {
    pie: chartPieData,
    circlepie: chartPieData,
    line: chartData,
    bar: chartData,
  };
  const itemTooltip = chartType === 'BAR_STACK' || type.includes('pie');
  type === 'circlepie' && (seriesData[0].radius = ['50%', '60%']);
  return {
    color: ['#3388FF', '#3DCCAB', '#FBCC3F', '#A6DD82', '#FAC20A'],
    xAxis: {
      type: 'category',
      show: !type.includes('pie'),
      axisTick: {
        alignWithLabel: true,
        show: false
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
        show: !type.includes('pie') && getEndPosition(xAxisData) < 100,
        start: 0,
        end: getEndPosition(xAxisData),
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
        type: 'shadow',
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
            fullScreenTable(chartRef)
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
export const getEndPosition = (xAxisData) => {
  return xAxisData && Math.ceil((6 * 100) / xAxisData.length);
}
export const fullScreenTable = (chartRef) => {
  if (screenfull.isFullscreen) {
    screenfull.exit();
    return;
  }
  screenfull.request(chartRef.current);
}
export const chartTypeMap: any = {
  TABLE: 'table',
  BAR: 'bar',
  LINE: 'line',
  MIX_LINE_BAR: 'bar',
  PIE: 'pie',
  FORECAST: 'line',
  BAR_STACK: 'bar'
}
export const items: any = [
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