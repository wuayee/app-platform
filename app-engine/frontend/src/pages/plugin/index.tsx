import React, { useState } from 'react';
import { Drawer, Button } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import MarketItems from './market';
import DeployMent from './deployment';

const Plugin = () => {
  const [open, setOpen] = useState(false);
  const onClose = () => {
    setOpen(false);
  };
  return (
    <div className='aui-fullpage' style={{ display: 'flex', flexDirection: 'column' }}>
      <div className='aui-header-1 '>
        <div className='aui-title-1'>插件管理</div>
        {/* <Button size='small' onClick={() => setOpen(true)}>部署</Button> */}
      </div>
      <div>
        <MarketItems />
      </div>
      <Drawer
        title='部署插件'
        width={900}
        onClose={onClose}
        closeIcon={false}
        open={open}
        extra={
          <CloseOutlined onClick={() => setOpen(false)} />
        }
        footer={[
          <Button key="back" onClick={() => setOpen(false)}>
            关闭
          </Button>
        ]}
      >
        <DeployMent />
      </Drawer>
    </div>
  )
}
export default Plugin;
