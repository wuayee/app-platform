import React, { useState } from 'react';
import { Tabs } from 'antd';
import GoBack from '../../components/go-back/GoBack';
import AppOverview from './overview';
import AppAnalyse from './analyse';
import FeedBack from './feedback';
import { useTranslation } from 'react-i18next';

const onChange = (key: string) => {
  console.log(key);
};

const AppDetail: React.FC = () => {
  const { t } = useTranslation();
  const items: TabsProps['items'] = [
    {
      key: '1',
      label: t('overview'),
      children: <AppOverview />
    },
    {
      key: '2',
      label: t('analyse'),
      children: <AppAnalyse />,
    },
    {
      key: '3',
      label: t('feedback'),
      children: <FeedBack />,
    },
    // {
    //   key: '4',
    //   label: t('evaluate'),
    //   disabled: true,
    //   children: <AppEvaluate />,
    // },
  ];
  return (
    <div className='aui-fullpage'>
      <div className='aui-header-1'>
        <div className='aui-title-1'>
          <GoBack path={'/app-develop'} title={t('appDetail')} />
        </div>
      </div>
      <div className='aui-block' style={{ paddingTop: 0 }}>
        <Tabs defaultActiveKey='1' items={items} onChange={onChange} />
        <div />
      </div>
    </div>
  );
};

export default AppDetail;
