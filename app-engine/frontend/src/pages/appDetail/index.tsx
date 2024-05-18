import React from 'react';
import GoBack from '../../components/go-back/GoBack';

const AppDetail: React.FC = () => (
  <div className='aui-fullpage'>
    <div className='aui-header-1'>
      <div className='aui-title-1'><GoBack path={'/app'} title='应用详情'/></div>
    </div>
    <div className='aui-block'>
      <div className='dc-table-operate'>
        <div>ops-left</div>
        <div>ops-right</div>
      </div>
      <div />
    </div>
  </div>
);

export default AppDetail;
