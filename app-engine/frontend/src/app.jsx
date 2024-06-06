import React from "react";
import {HashRouter as Router, Navigate, Route, Routes} from "react-router-dom";
// import {StoreProvider} from "__store";
import {ConfigProvider, Layout} from "antd";
const { Header, Content, Footer, Sider } = Layout;
import AppLayout from '@/components/layout/index'

const layoutStyle = {
  borderRadius: 8,
  overflow: 'hidden',
  width: 'calc(50% - 8px)',
  maxWidth: 'calc(50% - 8px)',
};
// 由于 antd 组件的默认文案是英文，所以需要修改为中文
import zhCN from "antd/lib/locale/zh_CN";
import routes from './router';
import '__styles/index.scss';
import "__styles/common.scss";
import "__styles/content.scss";
import "__styles/workSpace.scss";
import '__styles/global.scss';



localStorage.getItem("currentUser") || localStorage.setItem('currentUser', '');
localStorage.getItem("currentUserId") || localStorage.setItem('currentUserId', '');
localStorage.getItem("currentUserIdComplete") || localStorage.setItem('currentUserIdComplete', '');

const Home = () => {
    return (<Router hashType="hash">
              <AppLayout/>
        {/* <Layout>
          <Layout>
            <Sider>left sidebar</Sider>
            <Content>main content</Content>
            <Sider>right sidebar</Sider>
          </Layout>
          <Footer>footer</Footer>
        </Layout> */}
    {/* <Layout style={layoutStyle}>
      <Sider width="25%" style={siderStyle}>
        Sider
      </Sider>
      <Layout>
        <Header style={headerStyle}>Header</Header>
        <Content style={contentStyle}>Content</Content>
        <Footer style={footerStyle}>Footer</Footer>
      </Layout>
    </Layout>
      <Layout className="layout">
        <Layout className="main">
          11111
          <Routes>
            <Route path="/aipp">
              {(routes.map((route, index) => {
                  const {path, component: Component} = route;
                  return <Route key= {index} path={path} element={React.createElement(Component)}/>;
              }))}
            </Route>
            <Route path="/*" element={<Navigate to="/aipp/notfound" />}/>
          </Routes>
        </Layout>
      </Layout> */}
    </Router>);
};

export default function App() {
  return (
    <ConfigProvider locale={zhCN} autoInsertSpace={true}>
      {/* <StoreProvider> */}
          <Home/>
      {/* </StoreProvider> */}
    </ConfigProvider>);
};
