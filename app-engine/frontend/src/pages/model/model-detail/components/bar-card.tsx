import React, { ReactElement } from 'react';
import { Card } from 'antd';

import { Button, Dropdown, Space } from 'antd';
import { Column } from '@ant-design/charts';
interface BarItem {
  title: string;
  content: object;
}

const BarCard = ({ data }: { data: BarItem }) => {
  const leftBarData = [
    {
      index: '请求数',
      数量: data.content.requests,
    },
    {
      index: '回答数',
      数量: data.content.responses,
    },
    {
      index: '异常数',
      数量: data.content.exceptions,
    },
    {
      index: '吞吐量',
      数量: data.content.throughput,
    },
  ];
  const rightBarData = [
    {
      index: '输入token',
      数量: data.content.total_input_tokens,
    },
    {
      index: '输出token',
      数量: data.content.total_output_tokens,
    },
  ];
  const leftConfig = {
    forceFit: true,
    leftBarData,
    padding: 'auto',
    xField: 'index',
    yField: '数量',
  };
  const rightConfig = {
    forceFit: true,
    rightBarData,
    padding: 'auto',
    xField: 'index',
    yField: '数量',
  };
  return (
    <Card
      style={{
        width: '49%',
        height: 526,
        padding: 0,
      }}
    >
      <div
        style={{
          height: 40,
        }}
      >
        <div>
          <div
            style={{
              fontSize: 20,
              color: 'rgba(5, 5, 5, .96)',
            }}
          >
            {data.title}
          </div>
        </div>
      </div>
      <div
        style={{
          fontSize: 12,
          color: 'rgb(113, 117, 127)',
        }}
      >
        数量
      </div>
      {data.title === '数据表现' && <Column data={leftBarData} {...leftConfig} />}
      {data.title === 'Token' && <Column data={rightBarData} {...rightConfig} />}
    </Card>
  );
};

export default BarCard;
