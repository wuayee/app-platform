import React from "react";
import {HashRouter as Router, Navigate, Route, Routes} from "react-router-dom";
// import {StoreProvider} from "__store";
import {ConfigProvider, Layout} from "antd";
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

const Home = () => {
    return (<Router hashType="hash">
      <Layout className="layout">
        <Layout className="main">
          <Routes>
            <Route path="/aipp">
              {(routes.map((route, index) => {
                  const {path, component: Component} = route;
                  return <Route key= {index} path={path} element={React.createElement(Component)}/>;
              }))}
            </Route>
            <Route path="/*" element={<Navigate to="/aipp/home" />}/>
          </Routes>
        </Layout>
      </Layout>
    </Router>);
};

export default function App() {
  return (
    <ConfigProvider locale={zhCN} autoInsertSpaceInButton={true}>
      {/* <StoreProvider> */}
          <Home/>
      {/* </StoreProvider> */}
    </ConfigProvider>);
};
