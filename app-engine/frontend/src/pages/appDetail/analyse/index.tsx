import React, { useEffect, useState } from 'react';
import * as echarts from 'echarts';
import './style.scoped.scss';
import { Card, Select } from 'antd';
import { getAnalysisData } from '../../../shared/http/apps';
import { useParams } from 'react-router-dom';

const timeOption = [
  { value: '0', label: '今天' },
  { value: '1', label: '昨天' },
  { value: '2', label: '过去7天 ' },
  { value: '3', label: '过去30天' },
  { value: '4', label: '本周' },
  { value: '5', label: '上周' },
  { value: '6', label: '本月' },
  { value: '7', label: '上月' },
];

const top5UserOption = {
  xAxis: {
    type: 'category',
    axisTick: {
      show: false, // 是否显示坐标轴刻度
    },
    data: [],
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      show: true,
      interval: 'auto',
    },
  },
  series: [
    {
      data: [],
      type: 'bar',
      barWidth: '25%',
    },
  ],
};

const top5DepartmentOption = {
  xAxis: {
    type: 'category',
    axisTick: {
      show: false, // 是否显示坐标轴刻度
    },
    data: [],
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      show: true,
      interval: 'auto',
      formatter: '{value} %',
    },
  },
  series: [
    {
      data: [],
      type: 'bar',
      barWidth: '25%',
    },
  ],
};

const speedOption = {
  tooltip: {
    trigger: 'item',
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
      data: [],
    },
  ],
};

const tradeOption = {
  tooltip: {
    trigger: 'item'
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [],
  },
  yAxis: {
    type: 'value',
  },
  series: [
    {
      data: [],
      type: 'line',
      smooth: true,
    },
  ],
};

const AnalyseCard = ({ info }) => (
  <Card className='card'>
    <div className='title'>{info?.title}</div>
    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
      <div>
        <span className='number'>{info?.num}</span>
        <span className='number-unit'>{info?.unit}</span>
        {/* <div className='number-tip'>
          <span>{info?.sign==='down'? '相比昨日下降':'相比昨日上升'}</span>
          <span style={{ color: info?.sign==='down'?'#c63939':'#53ab6b' }}> {info?.percent}%</span>
        </div> */}
      </div>
      <img width={60} height={65} src='/src/assets/images/knowledge/knowledge-base.png' />
    </div>
  </Card>
);
const ChartCard = ({ info }: any) => (
  <Card className='chart-card' style={{
    height: info.height + 50,
    width: `${info?.width ?? '49.5%'}`
  }}>
    <div className='title'>{info.title}</div>
    <div id={info.id} className='chart' style={{ height: info.height }} />
  </Card>
);

const AppAnalyse: React.FC = () => {
  const [graphData, setgraphData] = useState({ allRequest: {}, allActive: {}, avgSpeed: {} });
  const [time, setTime] = useState('0');
  const [total, setTotal] = useState(0);
  const [speedList, setSpeedList] = useState([]);

  const { tenantId, appId } = useParams();

  const initData = () => {
    const tradeChart = echarts.init(document.getElementById('trade'));
    const speedChart = echarts.init(document.getElementById('speed'));
    const top5UserChart = echarts.init(document.getElementById('top5User'));
    getAnalysisDataByTime(top5UserChart, speedChart, tradeChart);
    window.addEventListener('resize', function () {
      tradeChart.resize();
      speedChart.resize();
      top5UserChart.resize();
    });
  }

  useEffect(() => {
    initData();
    const timer = setInterval(() => {
      initData()
    }, 60000);
    return () => clearInterval(timer);
  }, [time]);


  // 设置Tab数据
  const setTabData = (metrics: any) => {
    const allRequest = { title: '总请求数', num: metrics?.total_requests?.value ?? 0, unit: '个', sign: 'down', percent: 2 };
    const allActive = { title: '总活跃用户数', num: metrics?.total_active_users?.value ?? 0, unit: '个', sign: 'up', percent: 2 };
    const avgSpeed = { title: '平均响应速度', num: metrics?.average_response_time?.value ?? 0, unit: 'ms', sign: 'down', percent: 2 };
    setgraphData({ allRequest, allActive, avgSpeed })
  }

  // 设置Top5用户数据
  const setTop5UserData = (topUsers: any, top5UserChart) => {

    const x = (topUsers ?? []).map(user => (user?.createUser || ''));
    const y = (topUsers ?? []).map(user => (user?.accessCount || 0));
    top5UserOption.xAxis.data = x;
    top5UserOption.series[0].data = y;
    top5UserChart.setOption(top5UserOption);
  }

  const order = ['below 500ms', '501-1000ms', '1001ms-2000ms', 'above 2000ms'];

  // 设置平均响应速度
  const setAvargeSpeed = (avgResponseRange, speedChart) => {
    let totalData = 0;
    const data = Object.keys(avgResponseRange || {}).map(item => {
      totalData += avgResponseRange[item]?.count || 0;
      return ({
        value: avgResponseRange[item]?.count || 0,
        name: avgResponseRange[item]?.range || ''
      })
    });
    setTotal(totalData);
    data.sort((start, next) => {
      return order.indexOf(start.name) - order.indexOf(next.name)
    });
    speedOption.series[0].data = data;
    speedChart.setOption(speedOption);
  }

  // 设置用户访问趋势
  const setUserTrade = (userAccessData, speedChart) => {
    const xData = (userAccessData || []).map(item => (item.time_unit))
    const xValue = (userAccessData || []).map(item => (item.access_count))
    tradeOption.xAxis.data = xData;
    tradeOption.series[0].data = xValue;
    speedChart.setOption(tradeOption);
  }

  // 获取分析数据
  const getAnalysisDataByTime = async (top5UserChart, speedChart, tradeChart) => {
    try {
      const res = await getAnalysisData({
        appId: appId,
        timeType: time,
      });
      setTabData(res?.basicMetrics);
      setTop5UserData(res?.topUsers, top5UserChart);
      setAvargeSpeed(res?.avgResponseRange, speedChart);
      setUserTrade(res?.userAccessData, tradeChart)
      if (res && res.avgResponseRange) {
        let avgResponseRange = Object.values(res?.avgResponseRange);
        avgResponseRange.sort((start, next) => {
          return order.indexOf(start.range) - order.indexOf(next.range)
        });
        setSpeedList(avgResponseRange);
      }
    } catch (error) {

    }
  }

  return (
    <div className='app-analyzed'>
      <div className='select'>
        <Select
          className='select-time'
          defaultValue={
            time
          }
          style={{ width: 120 }}
          onChange={setTime}
          options={timeOption}
        />
      </div>
      <div className='cards'>
        <AnalyseCard info={graphData.allRequest} />
        <AnalyseCard info={graphData.allActive} />
        <AnalyseCard info={graphData.avgSpeed} />
      </div>
      <div className='chart-cards'>
        <ChartCard info={{ id: 'top5User', title: 'Top5用户', height: 300 }} />
        <Card className='chart-card'>
          <div className='title'>平均响应速度</div>
          <div style={{ display: 'flex' }}>
            <div id='speed' style={{ width: 600, height: 250 }} />
            <div className='speed-number'>
              <div className='number'>{total}</div>
              <div>数据条数</div>
            </div>
          </div>
          <div style={{
            margin: '-169px 600px'
          }}>{
              speedList.map(item => {
                return (<div style={{
                  marginBottom: 8,
                  fontSize: 12
                }}>{item.count}</div>)
              })
            }</div>
        </Card>
      </div>
      <div className='chart-cards' style={{
        width: '100%'
      }}>
        <ChartCard info={{ id: 'trade', height: 350, title: '用户访趋势', width: '100%' }} />
        {/* <ChartCard info={{ id: 'top5Department', title: 'Top5部门', height: 300 }} /> */}
      </div>
    </div>
  );
};

export default AppAnalyse;
