import React, { useState } from 'react';
import { Drawer, Button } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import MarketItems from './market';
import DeployMent from './deployment';
import { useTranslation } from 'react-i18next';

const Plugin = () => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [reload, setReload] = useState(false);
  const onClose = () => {
    setOpen(false);
  };
  const confirm = () => {
    setOpen(false);
    setReload(!reload);
  }
  return (
    <div className='aui-fullpage' style={{ display: 'flex', flexDirection: 'column' }}>
      <div className='aui-header-1 '>
        <div className='aui-title-1'>{t('pluginManagement')}</div>
        <Button size='small' onClick={() => setOpen(true)}>{t('deploying')}</Button>
      </div>
      <div>
        <MarketItems reload={reload} />
      </div>
      <Drawer
        title={t('pluginDeploying')}
        width={900}
        onClose={onClose}
        closeIcon={false}
        open={open}
        destroyOnClose
        extra={
          <CloseOutlined onClick={() => setOpen(false)} />
        }
        footer={[
          <Button key="back" onClick={() => setOpen(false)}>
            {t('close')}
          </Button>
        ]}
      >
        <DeployMent cancle={() => setOpen(false)} confirm={confirm} />
      </Drawer>
    </div>
  )
}
export default Plugin;
