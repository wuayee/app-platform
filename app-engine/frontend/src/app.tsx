import React from 'react';
import {HashRouter as Router } from 'react-router-dom';
import {ConfigProvider} from 'antd';
import AppLayout from '@/components/layout/index';
import zhCN from 'antd/lib/locale/zh_CN';
import '__styles/index.scss';
import '__styles/common.scss';
import '__styles/content.scss';
import '__styles/workSpace.scss';
import '__styles/global.scss';

localStorage.getItem('currentUser') || localStorage.setItem('currentUser', '');
localStorage.getItem('currentUserId') || localStorage.setItem('currentUserId', '');
localStorage.getItem('currentUserIdComplete') || localStorage.setItem('currentUserIdComplete', '');
localStorage.getItem('appChatMap') || localStorage.setItem('appChatMap', JSON.stringify({}));
localStorage.getItem('showFlowChangeWarning') || localStorage.setItem('showFlowChangeWarning', 'true');

export default function App() {
  return (
    <ConfigProvider locale={zhCN} autoInsertSpace={true}>
      {/* <StoreProvider> */}
        <Router hashType='hash'>
          <AppLayout />
        </Router>
      {/* </StoreProvider> */}
    </ConfigProvider>);
};
