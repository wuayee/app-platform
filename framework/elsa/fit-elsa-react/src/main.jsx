import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'

ReactDOM.createRoot(document.getElementById('root')).render(
  // 打开strictMode会导致每个组件被加载两次，测试某些功能时可以打开
  // <React.StrictMode>
    <App />
  // </React.StrictMode>,
)
