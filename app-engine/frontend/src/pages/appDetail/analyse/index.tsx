import React, { useEffect, useState } from 'react';
import * as echarts from 'echarts';
import './style.scoped.scss';
import { float } from 'html2canvas/dist/types/css/property-descriptors/float';
import { Card, Select } from 'antd';


const timeOption=[
  { value: '1', label: '过去1天' },
  { value: '2', label: '过去7天 '},
  { value: '3', label: '过去1月' },
  { value: '4', label: '过去半年'},
]

const top5Option = {
  xAxis: {
    type: 'category',
    axisTick: {
      show: false, // 是否显示坐标轴刻度
    },
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
  },
  yAxis: {
    type: 'value',
    max: 100, //取100为最大刻度
    axisLabel: {  
      show: true,  
      interval: 'auto',  
      formatter: '{value} %'  
      },  
  },
  series: [
    {
      data: [20, 56, 15, 80, 70, 10, 30],
      type: 'bar',
      barWidth: '25%',
    },
  ],
};

const AnalyseCard = ({ info }) => (
  <Card className='card'>
    <div className='title'>总请求数</div>
    <div style={{display:'flex',justifyContent:'space-between'}}>
      <div>
    <span className='number'>2523</span>
    <span className='number-unit'>次</span>
    <div className='number-tip'>
      <span>相比昨日下降</span>
      <span style={{ color: '#c63939' }}> 2%</span>
    </div>
    </div>
    <img width={60} height={65} src='/src/assets/images/knowledge/knowledge-base.png'/>
    
    </div>
  </Card>
);
const ChartCard = ({ info }) => (
  <Card className='chart-card' style={{ height: info.height + 50 }}>
    <div className='title'>{info.title}</div>
    <div id={info.id} className='chart' style={{ height: info.height }} />
  </Card>
);
const handleSelectTimeChange = (value: string) => {
  console.log(`selected ${value}`);
};
const AppAnalyse: React.FC = () => {
  const tradeData=[820, 932, 901, 934, 1290, 1330, 1320];
  const [top5User,setTop5User]=useState([]);
  const speedData = [
    { value: 48, name: '500ms以下' },
    { value: 32, name: '500-1000ms' },
    { value: 20, name: '10001-2000ms' },
  ];
  
  const tradeOption = {
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
    },
    yAxis: {
      type: 'value',
    },
    series: [
      {
        data:tradeData,
        type: 'line',
        smooth: true,
      },
    ],
  };
  
  const speedOption = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      top: 80,
      left: 450,
      selectedMode: false,
      formatter(params) {
        return params;
      },
    },
    series: [
      {
        type: 'pie',
        radius: ['80%', '94%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 2,
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          show: false,
          position: 'center',
        },
        labelLine: {
          show: false,
        },
        data: speedData,
      },
    ],
  };
  
  useEffect(() => {
    const tradeChart = echarts.init(document.getElementById('trade'));
    const speedChart = echarts.init(document.getElementById('speed'));
    const top5UserChart = echarts.init(document.getElementById('top5User'));
    const top5DepartmentChart = echarts.init(document.getElementById('top5Department'));
    tradeChart.setOption(tradeOption);
    speedChart.setOption(speedOption);
    top5UserChart.setOption(top5Option);
    top5DepartmentChart.setOption(top5Option);
    window.addEventListener('resize', function () {
      tradeChart.resize();
      speedChart.resize();
      top5UserChart.resize();
      top5DepartmentChart.resize();
    });

    //赋值

  }, []);
  return (
    <div>
      <div className='select'>
      <Select
        className='select-time'
        defaultValue='2'
        style={{ width: 120 }}
        onChange={handleSelectTimeChange}
        options={timeOption}
      />
      </div>
      <div className='cards'>
        <AnalyseCard />
        <AnalyseCard />
        <AnalyseCard />
      </div>
      <div className='chart-cards'>
        <ChartCard info={{ id: 'trade', height: 350 }} />
        <Card className='chart-card'>
          <div className='title'>用户访趋势</div>
          <div style={{ display: 'flex' }}>
            <div id='speed' style={{ width: 600, height: 250 }} />
            <div className='speed-number'>
              <div className='number'>100</div>
              <div>数据条款</div>
            </div>
          </div>
        </Card>
      </div>
      <div className='chart-cards'>
        <ChartCard info={{ id: 'top5User',title:'Top5用户', height: 300 }} />
        <ChartCard info={{ id: 'top5Department',title:'Top5部门',  height: 300 }} />
      </div>
    </div>
  );
};

export default AppAnalyse;
