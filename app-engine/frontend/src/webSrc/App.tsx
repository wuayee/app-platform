/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import './App.scss';
import './styles/global.scss';
import { HashRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Header from './pages/Home/components/Header';
import routes from './routes';

function App() {

  return (
      <>
          <Router>
              <Header/>
              <Routes>
                  {routes.map((route) => (
                      <Route key={route.path} path={route.path} Component={route.element} />
                  ))}
                  <Route path="/" element={<Navigate to='/home' replace />} />
              </Routes>
          </Router>
      </>
  )
}

export default App
