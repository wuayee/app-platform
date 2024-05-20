import React, { useState, useEffect } from 'react';
import type { MenuProps } from 'antd';
import { Breadcrumb, Layout, Menu, theme, ConfigProvider,  } from 'antd';
import { HashRouter, Route, useNavigate, Routes, useLocation } from 'react-router-dom';
import { routeList, flattenRoute, getRouteByKey, getMenus } from '../../router/route'
import { Icons } from '../icons/index'
import { HeaderUser } from '../header-user';
import { HeaderFolderMenu } from '../header-folder-menu';

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

  // 控制面板的显示与隐藏
  const [showMenu, setShowMenu] = useState(false);

  const navigate = useNavigate();

  const location = useLocation();

  useEffect(()=> {
    const { pathname } = location;
    const route = getRouteByKey(flattenRouteList, pathname)

    if(!route?.hidden) {
      setShowMenu(true)
    } else {
      setShowMenu(false)
    }
  }, [location]);

  const menuFolder = ()=> {
    setShowMenu(!showMenu)
  }


  const menuClick = (e: any) => {
    const route = getRouteByKey(flattenRouteList, e.key);
    setCurrentActivedPage(route?.label || '')
    navigate(e.key)
  }

  const colorBgContainer = '#F0F2F4';
  

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <>
        <Sider collapsible collapsed={false} onCollapse={(value) => setShowMenu(false)}
          style={{
            transition:' all .3s ease',
            visibility: showMenu ? 'visible' : 'hidden',
            opacity: showMenu ? 1 : 0,
            flex: showMenu ? '0 0 200px' : '0'
          }}
        >
        <div style={{
          position: 'static',
          width: '100%',
          height: '48px',
          'display': 'flex',
          'flexDirection': 'row',
          'justifyContent': 'flex-start',
          'alignItems': 'center',
          padding: '0px 24px 0px 24px',
          flex: 'none',
          order: 0,
          'alignSelf': 'stretch',
          'flexGrow': 0,
          margin: '8px 0px',
        }}>
          <Icons.logo/> <span style = {
            {
              color: 'rgb(255, 255, 255)',
              'fontSize': '20px',
              'fontWeight': '400',
              'lineHeight': '24px',
              'letterSpacing': '0px',
              'textAlign': 'left',
              'marginLeft': '8px',
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
      </>
      <>
        <HeaderFolderMenu openMenuFunc={menuFolder} style={{
            transition:' all .3s ease',
            width: !showMenu ? '100%' : '0',
            overflow: 'hidden',
            visibility: !showMenu ? 'visible' : 'hidden',
            opacity: !showMenu ? 1 : 0,
          }}/>
      </>

      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer, height: '48px' }} />
        <HeaderUser/>
        <Content style={{padding: '0 16px', background: colorBgContainer }}>
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