
import React, { useEffect, useState } from 'react';
import { Drawer, Timeline } from 'antd';
import { CloseOutlined } from '@ant-design/icons';

const TimeLine = (props) => {
  const { open, setOpen, list } = props;
  const [ timeList, setTimeList ] = useState([])
  useEffect(() => {
    let arr = [];
    arr = list.map(item => {
      return {
        color: '#000000',
        children: <div className="time-line-inner" style={{ color: 'rgb(77, 77, 77)' }}>
          <div style={{ fontWeight: '700' }}>{item.name}</div>
          <div style={{ margin: '8px 0' }}>{item.desc}</div>
          <div>{item.user}</div>
          <div>{item.time}</div>
        </div>
      }
    })
    setTimeList(arr);
  }, [list])
  return <>
    <Drawer
      title='发布历史'
      placement='right'
      width='420px'
      closeIcon={false}
      onClose={() => setOpen(false)}
      open={open}
      footer={null }
      extra={
        <CloseOutlined onClick={() => setOpen(false)}/>
      }>
      <div style={{ paddingTop: '12px' }}>
        <Timeline
          items={timeList}
        />
      </div>
    </Drawer>
  </>
};


export default TimeLine;
