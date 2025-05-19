/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'

// function setRem() {
//   const baseSize = 192;  
//   const scale = document.documentElement.clientWidth / 1920;
//     document.documentElement.style.fontSize = (baseSize * Math.min(scale, 2)) + 'px';
// }

// setRem();
// window.onresize = function() {
//     setRem();
// }


createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
