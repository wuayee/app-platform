
import { Button, Space, Drawer } from 'antd';
import React, { useEffect, useState } from 'react';

interface props {
  visible: boolean;
  params: any;
  detailCallback: Function;
}

const SetDetail = ({ params, visible, detailCallback }: props) => {

  const [detailOpen, setDetailOpen] = useState(false);

  useEffect(() => {
    setDetailOpen(visible);
  })

  const closeDrawer = () => {
    detailCallback();
  }

  return (
    <Drawer
      title='测试集详情'
      width={800}
      open={detailOpen}
      onClose={closeDrawer}
      maskClosable={false}
      destroyOnClose={true}
      footer={
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Space>
            <Button style={{ minWidth: 96 }} onClick={closeDrawer}>取消</Button>
            <Button type='primary' style={{ minWidth: 96 }}>编辑</Button>
          </Space>
        </div>
      }
    >
      asd
    </Drawer>
  )
}

export default SetDetail;
