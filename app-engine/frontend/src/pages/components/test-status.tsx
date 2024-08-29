import React from 'react';
import { CheckCircleFilled, CloseCircleFilled, LoadingOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import './styles/test-status.scss';
import { useAppSelector } from "@/store/hook";

const TestStatus = (props) => {
  const { t } = useTranslation();
  const testTime = useAppSelector((state) => state.flowTestStore.testTime);
  const testStatus = useAppSelector((state) => state.flowTestStore.testStatus);
  return <>
    { testStatus && <span className={[
      'header-time',
      testStatus === 'Running' ? 'running' : '',
      testStatus === 'Finished' ? 'finished' : '',
      testStatus === 'Error' ? 'error' : '',
    ].join(' ').trim()}>
      {testStatus === 'Running' && <div><LoadingOutlined className='test-icon' /><span>{t('runningTip')} {testTime}s</span></div>}
      {testStatus === 'Finished' && <div><CheckCircleFilled className='test-icon' /><span>{t('runningTip2')}</span></div>}
      {testStatus === 'Error' && <div><CloseCircleFilled className='test-icon' /><span>{t('runningTip3')} {testTime}s</span></div>}
    </span>}
  </>
};

export default TestStatus;
