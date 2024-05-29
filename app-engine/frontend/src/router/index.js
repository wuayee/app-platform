import React from 'react';

/**
 *
 * 路由配置js
 * key： 路由组件路径，
 * value: path
 */
export const pages = {
  "pages/aippIndex/index": {
    path: `:tenantId/app-detail/:appId`
  },
  "pages/notFound/index": {
    path: `notfound`
  },
  "pages/addFlow/index": {
    path: `:tenantId/addFlow/:appId`
  },
  "pages/chatRunning/index": {
    path: `:tenantId/chat/:appId`
  },
}

const routes = [];
const routeKeys = Object.keys(pages);

routeKeys.forEach((item) => {
    const {path, exact} = pages[item];
    const component = React.lazy(() => import(`${__dirname}/../${item}.jsx`));
    routes.push({
        path, component, exact
    });
});

export default routes;
