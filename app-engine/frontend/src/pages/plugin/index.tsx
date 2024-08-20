import React, { useState } from 'react';
import { Drawer, Button } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import MarketItems from './market';
import DeployMent from './deployment';
import { useTranslation } from 'react-i18next';

const Plugin = () => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const onClose = () => {
    setOpen(false);
  };
  return (
    <div className='aui-fullpage' style={{ display: 'flex', flexDirection: 'column' }}>
      <div className='aui-header-1 '>
        <div className='aui-title-1'>{t('pluginManagement')}</div>
        {/* <Button size='small' onClick={() => setOpen(true)}>{t('deploying')}</Button> */}
      </div>
      <div>
        <MarketItems />
      </div>
      <Drawer
        title={t('pluginDeploying')}
        width={900}
        onClose={onClose}
        closeIcon={false}
        open={open}
        extra={
          <CloseOutlined onClick={() => setOpen(false)} />
        }
        footer={[
          <Button key="back" onClick={() => setOpen(false)}>
            {t('close')}
          </Button>
        ]}
      >
        <DeployMent />
      </Drawer>
    </div>
  )
}
export default Plugin;
