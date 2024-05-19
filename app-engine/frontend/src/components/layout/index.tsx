import React, { useState, useEffect } from 'react';
import type { MenuProps } from 'antd';
import { Breadcrumb, Layout, Menu, theme, ConfigProvider,  } from 'antd';
import { HashRouter, Route, useNavigate, Routes, useLocation } from 'react-router-dom';
import { routeList, flattenRoute, getRouteByKey, getMenus } from '../../router/route'
import { Icons } from '../icons/index'
import KnowledgeBase from '../../pages/knowledge-base';

const { Header, Content, Footer, Sider } = Layout;

type MenuItem = Required<MenuProps>['items'][number];

function getItem(
  label: React.ReactNode,
  key: React.Key,
  icon?: React.ReactNode,
  children?: MenuItem[],
): MenuItem {
  return {
    key,
    icon,
    children,
    label,
  } as MenuItem;
}

const items: MenuItem[] = getMenus(routeList);
const flattenRouteList = flattenRoute(routeList);


const AppLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const [currentActivedPage, setCurrentActivedPage] = useState('首页');

  const navigate = useNavigate();
  const menuClick = (e: any) => {
    const route = getRouteByKey(flattenRouteList, e.key);
    setCurrentActivedPage(route?.label || '')
    navigate(e.key)
  }

  const colorBgContainer = '#F0F2F4';
  

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>
        <div style={{
          position: 'static',
          width: '100%',
          height: '48px',
          'display': 'flex',
          'flex-direction': 'row',
          'justify-content': 'flex-start',
          'align-items': 'center',
          padding: '0px 24px 0px 24px',
          flex: 'none',
          order: 0,
          'align-self': 'stretch',
          'flex-grow': 0,
          margin: '8px 0px',
        }}>
          <Icons.logo/> <span style = {
            {
              color: 'rgb(255, 255, 255)',
              'font-size': '20px',
              'font-weight': '400',
              'line-height': '24px',
              'letter-spacing': '0px',
              'text-align': 'left',
              'margin-left': '8px',
            }
          }>APP Engine</span>
        </div>
        <ConfigProvider theme={{
          components: {

          }
        }}>
          <Menu className='menu'  theme="dark" defaultSelectedKeys={['/home']} mode="inline" items={items} onClick={menuClick}/>
        </ConfigProvider>
      </Sider>
      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer, height: '48px' }} />
        <Content style={{padding: '0 16px', background: colorBgContainer }}>
          {/* <Breadcrumb style={{ margin: '16px 0' }}>
            <Breadcrumb.Item>User</Breadcrumb.Item>
            <Breadcrumb.Item>Bill</Breadcrumb.Item>
          </Breadcrumb> */}

            <Routes>
              {flattenRouteList.map(route=> {
                if(route.component) {
                  return (<>
                
                    <Route path={route.key} Component={route.component}/>
                  </>)
                }
            })}
              
            </Routes>
        </Content>
        {/* <Footer style={{ textAlign: 'center' }}>
        </Footer> */}
      </Layout>
    </Layout>
  );
};

export default AppLayout;