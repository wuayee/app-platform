
import React, { useEffect, useState } from 'react';
import { Drawer, Timeline, Empty } from 'antd';
import { useParams } from 'react-router-dom';
import { CloseOutlined } from '@ant-design/icons';
import { getVersion } from '@shared/http/aipp';

const TimeLine = (props) => {
  const { open, setOpen } = props;
  const [ timeList, setTimeList ] = useState([]);
  const { tenantId, appId } = useParams();

  useEffect(() => {
    open && getVersion(tenantId, appId).then(res => {
      if(res.code === 0) {
        dataProcess(res.data);
      }
    })
  }, [open]);
  const dataProcess = (list) => {
    let arr = [];
    arr = list.map(item => {
      return {
        color: '#000000',
        children: <div className="time-line-inner" style={{ color: 'rgb(77, 77, 77)' }}>
          <div style={{ fontWeight: '700' }}>{item.appVersion}</div>
          <div style={{ margin: '8px 0' }}>{item.publishedDescription || '-'}</div>
          <div>{item.publishedBy}</div>
          <div>{item.publishedAt}</div>
        </div>
      }
    })
    setTimeList(arr);
  }
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
      <div>
        <div style={{ marginBottom: '18px', display: 'flex', alignItems: 'center' }}>
          <img src='/src/assets/images/ai/tag.png'  />
          <span style={{ marginLeft: '12px' }}>不可恢复历史版本</span>
        </div>
        { timeList.length > 0 ? <Timeline  items={timeList} /> : 
          <div style={{ marginTop: '300px' }}><Empty description="暂无数据" /></div>  
        }
      </div>
    </Drawer>
  </>
};


export default TimeLine;
