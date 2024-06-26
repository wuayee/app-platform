import { Drawer } from 'antd';
import React, { useEffect } from 'react';
import { Input } from 'antd';

const { TextArea } = Input;

interface props {
  visible: boolean,
  callback: Function,
  configData: any
}

const ModelConfig = ({ visible, callback, configData }: props) => {

  return (
    <Drawer
      title='配置详情'
      open={visible}
      width={520}
      onClose={() => callback()}
      destroyOnClose={true}
    >
      <TextArea value={configData} rows={30} />
    </Drawer>
  )
}

export default ModelConfig;
