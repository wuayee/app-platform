import React, { useState, useEffect } from 'react';
import type { MenuProps } from 'antd';
import { Layout, Menu } from 'antd';
import { MenuFoldOutlined } from '@ant-design/icons';
import {
  Route,
  useNavigate,
  Routes,
  useLocation,
  Navigate,
} from 'react-router-dom';
import {
  routeList,
  flattenRoute,
  getRouteByKey,
  getMenus,
} from '../../router/route';
import { Provider } from 'react-redux';
import { Icons, KnowledgeIcons } from '../icons/index';
import { HeaderUser } from '../header-user';
import { store } from '@/store';
import { getUser } from '../../pages/helper';
import './style.scoped.scss';

const { Header, Content, Sider } = Layout;

type MenuItem = Required<MenuProps>['items'][number];
const items: MenuItem[] = getMenus(routeList);
const flattenRouteList = flattenRoute(routeList);

const AppLayout: React.FC = () => {
  // 控制面板的显示与隐藏
  const [showMenu, setShowMenu] = useState(false);

  // 默认的选中的菜单
  const [defaultActive, setDefaultActive] = useState<string[]>([])

  const navigate = useNavigate();

  const location = useLocation();

  /**
   * @description 从后往前遍历路由 父子路由，子路由前缀需要是父路由
   * @pathname 当前路径
   * */
  const getCurrentRoute = (pathname: string) => {

    // 拆开路由
    const pathGroup = pathname.split('/').filter(item => item !== '');
    if (pathGroup?.length) {
      let len = pathGroup?.length - 1;
      while (len >= 0) {
        const key = '/' + pathGroup.slice(0, len + 1).join('/');
        let route = getRouteByKey(flattenRouteList, key);
        if (route && !route?.hidden) {
          setDefaultActive([key]);
          break;
        }
        len--;
      }
    } else {
      // 默认路由为home
      setDefaultActive(['/home']);
    }
  }

  const menuClick = (e: any) => {
    navigate(e.key);
  };

  const colorBgContainer = '#F0F2F4';
  const setClassName = () => {
    if (location.pathname.includes('home')) {
      return 'home-chat'
    } else if (location.pathname.includes('app')) {
      return 'home-app'
    }
    return ''
  }
  useEffect(() => {
    const { pathname } = location;
    const route = getRouteByKey(flattenRouteList, pathname);
    if (pathname.includes('/app-detail/')) {
      setShowMenu(false);
    } else if (!route?.hidden) {
      setShowMenu(true);
    } else {
      setShowMenu(false);
    }
    getCurrentRoute(pathname);

  }, [location]);
  useEffect(() => {
    getUser()
  }, [])
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={false}
        onCollapse={() => setShowMenu(false)}
        trigger={null}
        width={showMenu ? 220 : 0}
        className='layout-sider'
      >
        <div className='layout-sider-header'>
          <div className='layout-sider-content'>
            <Icons.logo />
            <span className='layout-sider-title'>ModelEngine</span>
          </div>
          <MenuFoldOutlined
            style={{ color: '#6d6e72' }}
            onClick={() => setShowMenu(false)}
          />
        </div>
        <Menu
          className='menu'
          theme='dark'
          selectedKeys={defaultActive}
          mode='inline'
          items={items}
          onClick={menuClick}
        />
      </Sider>
      <div className='layout-sider-folder'>
        <KnowledgeIcons.menuFolder onClick={() => setShowMenu(true)} />
      </div>

      <Layout className={setClassName()}>
        <Header
          style={{ padding: 0, background: colorBgContainer, height: '48px' }}
        >
          <HeaderUser />
        </Header>
        <Provider store={store}>
          <Content style={{ padding: '0 16px', background: colorBgContainer }}>
            <Routes>
              <Route path='/' element={<Navigate to='/home' replace />} />
              {flattenRouteList.map((route) => (
                <Route
                  path={route.key}
                  key={route.key}
                  Component={route.component}
                />
              ))}
            </Routes>
          </Content>
        </Provider>
      </Layout>
    </Layout>
  );
};

export default AppLayout;
