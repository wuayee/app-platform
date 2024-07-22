import React from 'react';
import {CheckCircleFilled, CloseCircleFilled, LoadingOutlined} from '@ant-design/icons';
import './styles/test-status.scss';
const TestStatus = (props) => {
  const { testStatus, testTime} = props;

  return <>
    { testStatus && <span className={[
      'header-time',
      testStatus === 'Running' ? 'running' : '',
      testStatus === 'Finished' ? 'finished' : '',
      testStatus === 'Error' ? 'error' : '',
    ].join(' ').trim()}>
      {testStatus === 'Running' && <div><LoadingOutlined className='test-icon'/><span>试运行中 {testTime}s</span></div>}
      {testStatus === 'Finished' && <div><CheckCircleFilled className='test-icon'/><span>运行成功</span></div>}
      {testStatus === 'Error' && <div><CloseCircleFilled className='test-icon'/><span>运行失败 {testTime}s</span></div>}
       </span>}
  </>
};

export default TestStatus;
