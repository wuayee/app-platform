
import React, { useEffect, useState } from 'react';
import './index.scss'

const NotFound = () => {
  useEffect(() => {
    urlLoad();
  })
  // 页面调转
  function urlLoad(id) {
    let mode = process.env.NODE_ENV;
    let env = '-alpha';
    if (mode === 'dev') {
      let url = `http://localhost.huawei.com:3300/#/tenant/727d7157b3d24209aefd59eb7d1c49ff/aipp`;
      window.open(url);
      return
    }
    if (mode === 'beta') {
      env = '-beta'
    }
    if (mode === 'gamma') {
      env = '-gamma'
    }
    if (mode === 'prop') {
      env = ''
    }
    let url = `https://jane${env}.huawei.com/tenant/727d7157b3d24209aefd59eb7d1c49ff/aipp`;
    window.open(url);
  }
  return <>
    <div className="app-test">
      {/* <div className="inner">
        <div className="left">
          <span className="opration">And</span>
        </div>
        <div className="right">
          <div className="item"></div>
          <div className="item"></div>
          <div className="item"></div>
          <div className="item"></div>
        </div>
      </div> */}
    </div>
  </>
};


export default NotFound;
