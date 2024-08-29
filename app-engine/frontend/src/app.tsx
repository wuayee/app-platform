import React from 'react';
import {HashRouter as Router } from 'react-router-dom';
import {ConfigProvider} from 'antd';
import AppLayout from '@/components/layout/index';
import zhCN from 'antd/lib/locale/zh_CN';
import enUS from 'antd/lib/locale/en_US';
import './locale/i18n';
import 'antd/dist/antd.css';
import '__styles/index.scss';
import '__styles/common.scss';
import '__styles/content.scss';
import '__styles/workSpace.scss';
import '__styles/global.scss';
import '__styles/antStyle.scss';
import {getCookie, setCookie} from "./shared/utils/common";

localStorage.getItem('currentUser') || localStorage.setItem('currentUser', '');
localStorage.getItem('currentUserId') || localStorage.setItem('currentUserId', '');
localStorage.getItem('currentUserIdComplete') || localStorage.setItem('currentUserIdComplete', '');
localStorage.getItem('appChatMap') || localStorage.setItem('appChatMap', JSON.stringify({}));
localStorage.getItem('showFlowChangeWarning') || localStorage.setItem('showFlowChangeWarning', 'true');

const locale = getCookie('locale');
if (!locale) {
  setCookie('locale', 'zh-cn');
} else {
  if (locale === 'zh') {
    setCookie('locale', 'zh-cn');
  }
  if (locale === 'en') {
    setCookie('locale', 'en-us');
  }
}

export default function App() {
  return (
    <ConfigProvider locale={getCookie('locale') === 'en-us' ? enUS : zhCN} autoInsertSpace={true}>
      {/* <StoreProvider> */}
        <Router>
          <AppLayout />
        </Router>
      {/* </StoreProvider> */}
    </ConfigProvider>);
};
