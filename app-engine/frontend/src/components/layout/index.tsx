import React, { useState, useEffect } from "react";
import type { MenuProps } from "antd";
import { Layout, Menu } from "antd";
import { MenuFoldOutlined } from "@ant-design/icons";
import {
  Route,
  useNavigate,
  Routes,
  useLocation,
  Navigate,
} from "react-router-dom";
import {
  routeList,
  flattenRoute,
  getRouteByKey,
  getMenus,
} from "../../router/route";
import { Icons, KnowledgeIcons } from "../icons/index";
import { HeaderUser } from "../header-user";
import "./style.scoped.scss";

const { Header, Content, Footer, Sider } = Layout;

type MenuItem = Required<MenuProps>["items"][number];
function getItem(
  label: React.ReactNode,
  key: React.Key,
  icon?: React.ReactNode,
  children?: MenuItem[]
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
  // 控制面板的显示与隐藏
  const [showMenu, setShowMenu] = useState(false);

  const navigate = useNavigate();

  const location = useLocation();

  useEffect(() => {
    console.log(location);
    
    const { pathname } = location;
    const route = getRouteByKey(flattenRouteList, pathname);
    if (pathname.includes('/app')) {
      setShowMenu(false);
    } else if (!route?.hidden) {
      setShowMenu(true);
    } else {
      setShowMenu(false);
    }
  }, [location]);

  const menuClick = (e: any) => {
    const route = getRouteByKey(flattenRouteList, e.key);
    navigate(e.key);
  };

  const colorBgContainer = '#F0F2F4';
  const setClassName = () => {
    if ( location.pathname.includes('home')) {
      return 'home-chat'
    } else if (location.pathname.includes('app')) {
      return 'home-app'
    }
    return ''
  }

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Sider
        collapsible
        collapsed={false}
        onCollapse={() => setShowMenu(false)}
        trigger={null}
        width={showMenu ? 220 : 0}
        className="layout-sider"
      >
        <div className="layout-sider-header">
          <div className="layout-sider-content">
            <Icons.logo />
            <span className="layout-sider-title">APP Engine</span>
          </div>
          <MenuFoldOutlined
            style={{ color: "#6d6e72" }}
            onClick={() => setShowMenu(false)}
          />
        </div>
        <Menu
          className="menu"
          theme="dark"
          defaultSelectedKeys={["/home"]}
          mode="inline"
          items={items}
          onClick={menuClick}
        />
      </Sider>
      <div className="layout-sider-folder">
        <KnowledgeIcons.menuFolder onClick={() => setShowMenu(true)} />
      </div>

      <Layout className={setClassName()}>
        <Header
          style={{ padding: 0, background: colorBgContainer, height: "48px" }}
        >
          <HeaderUser />
        </Header>
        <Content style={{ padding: "0 16px", background: colorBgContainer }}>
          <Routes>
            <Route path="/" element={<Navigate to="/home" replace />} />
            {flattenRouteList.map((route) => (
              <Route
                path={route.key}
                key={route.key}
                Component={route.component}
              />
            ))}
          </Routes>
        </Content>
      </Layout>
    </Layout>
  );
};

export default AppLayout;
