/*
 * 版权所有 (c) 华为技术有限公司 2024
 */

import React, { useEffect, useState, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { useParams } from 'react-router-dom';
import { toClipboard } from '@/shared/utils/common';
import { Drawer } from 'antd';
import { getApiDocument } from '@/shared/http/aipp';
import { CloseOutlined } from '@ant-design/icons';
import { markedProcess } from '@/pages/chatPreview/utils/marked-process';
import './styles/apiOms.scss';
import copyCodeIcon from '@/assets/images/ai/copy_code.png';

/**
 * API文档
 *
 * @param drawerOpen 抽屉是否打开.
 * @param url apiUrl.
 * @param setDrawerOpen 抽屉是否打开的方法.
 * @return {JSX.Element}
 * @constructor
 */
const DocumentDrawer = ({ drawerOpen, url, setDrawerOpen }) => {
  const { t } = useTranslation();
  const { appId } = useParams();
  const [documentContent, setCocumentContent] = useState([]);
  const jumpA = useRef();

  // 解析未识别的``符号
  const translateSymbol = (content) => {
    const regex = /`(.*?)`/g;
    return content.replace(regex, '<code>$1</code>');
  };

  // 转换代码格式
  const tranlateCode = (content) => {
    const regex = /<code>(.*?)<\/code>/g;
    return content.replace(regex, `<div class='inner-code'>
      <div class='inner-code-title'>Code</div>
      <div class='inner-code-content'>$1</div>
    </div>`);
  };

  // 展示当前appId
  const displayAppId = (content) => {
    const regex = /<placeholder>(.*?)<\/placeholder>/g;
    return content.replace(regex, `<span>${appId}</span>`);
  };


  // 获取请求方法代码
  const getMethodCode = (codeContent, index) => {
    return <div className='inner-code' key={index}>
      <div className='inner-code-title'>{codeContent.title}</div>
      {codeContent.description && getHighLightMethod(codeContent.description)}
      <div className='inner-code-content' dangerouslySetInnerHTML={{ __html: codeContent.code }}></div>
    </div>
  };

  // 高亮请求方法
  const getHighLightMethod = (content) => {
    const index = content.indexOf(' ');
    return <div className='inner-code-menthod'>
      <span style={{ color: '#40acff', fontWeight: 700 }}>{content.substring(0, index)}</span>
      <span>{content.substring(index)}</span>
    </div>
  };

  // 获取错误码
  const getErrorContent = (errArr) => {
    return errArr.map(err =>
      <div className='error-item'>
        <div className='circle-item'></div>
        <div dangerouslySetInnerHTML={{ __html: markedProcess(err.field) }}></div>
        <div>&nbsp;&nbsp;，</div>
        <div dangerouslySetInnerHTML={{ __html: markedProcess(err.description) }}></div>
      </div>
    )
  };

  // 获取请求参数的内容
  const getParamsContent = (params, type = 'param', hierarchy = 1) => {
    return params.map((param, index) => <div key={index} className={`${hierarchy > 1 ? 'meta-item' : 'param-item'}`}>
      {param.title ? <div className='body-title'>{param.title}</div> : <div>
        {
          (param.field || param.type) &&
          <div className='param-top'>
            {type === 'meta' && <div className='circle-item'></div>}
            {param.field && <span className={`${type === 'meta' ? 'meta' : ''}`} dangerouslySetInnerHTML={{ __html: markedProcess(param.field) }}></span>}
            {param.type && <span dangerouslySetInnerHTML={{ __html: markedProcess(param.type) }}></span>}
          </div>
        }
        {param.description && <div className='param-content' dangerouslySetInnerHTML={{ __html: translateSymbol(markedProcess(displayAppId(param.description))) }}></div>}
      </div>
      }
      {param.children?.length > 0 && <div style={{ padding: hierarchy > 1 ? '0 30px' : '0 16px' }}>{getParamsContent(param.children, 'meta', hierarchy + 1)}</div>}
    </div>
    )
  };

  // 获取文档内容
  const getDocument = async () => {
    const res = await getApiDocument();
    if (res) {
      setCocumentContent(res);
    }
  };

  // 滚动到到wss部分
  const jumpToWss = () => {
    document.querySelector('#wss-target')?.scrollIntoView(true);
  };

  useEffect(() => {
    if (documentContent.length) {
      document.querySelector('#jump-wss')?.addEventListener('click', jumpToWss);
    }
  }, [documentContent])

  useEffect(() => {
    if (drawerOpen) {
      getDocument();
    } else {
      setCocumentContent([]);
    }
  }, [drawerOpen]);

  useEffect(() => {
    return () => {
      document.querySelector('#jump-wss')?.removeEventListener('click', jumpToWss);
    }
  }, []);

  return (
    <Drawer
      title={t('ApiDocument')}
      width={1000}
      open={drawerOpen}
      onClose={() => setDrawerOpen(false)}
      closable={false}
      extra={
        <CloseOutlined
          onClick={() => {
            setDrawerOpen(false);
          }}
        />
      }
    >
      <div className='document-markdown'>
        <div>
          <div className='content-title'>{t('basic')}URL</div>
          <div className='inner-code'>
            <div className='inner-code-title'>Code</div>
            <div className='inner-code-content'>
              <span>{url}</span>
              <div style={{ cursor: 'pointer' }} onClick={() => toClipboard(url)}>
                <img src={copyCodeIcon} style={{ marginRight: 8 }} alt="" />
                <span>Copy</span>
              </div>
            </div>
          </div>
        </div>
        {
          documentContent.map((item, index) =>
            <div key={index} className={`item-content ${item.method ? 'method-item' : ''}`}>
              <div style={{ width: item.codes?.length ? 'calc(50% - 10px)' : '100%' }}>
                {
                  item.method && <div className='method-title'>
                    <div className='method-name'>{item.method}</div>
                    <span dangerouslySetInnerHTML={{ __html: item.url }}></span>
                  </div>
                }
                <div className='content-title' dangerouslySetInnerHTML={{ __html: markedProcess(item.title) }}></div>
                <div className='content-description' dangerouslySetInnerHTML={{ __html: translateSymbol(markedProcess(tranlateCode(item.content))) }}></div>
                {
                  item.children?.length > 0 && item.children.map((body, i) =>
                    <div key={i} className='method-body'>
                      <div className='body-title'>{body.title}</div>
                      {body.content && <div className='body-description' dangerouslySetInnerHTML={{ __html: translateSymbol(markedProcess(tranlateCode(body.content))) }}></div>}
                      {body.children?.length > 0 && (body.isError ? getErrorContent(body.children) : getParamsContent(body.children))}
                    </div>
                  )
                }
              </div>
              {
                item.codes?.length > 0 && <div className='content-right'>
                  {
                    item.codes.map((code, idx) =>
                      getMethodCode(code, idx)
                    )
                  }
                </div>
              }
            </div>
          )
        }
      </div>
    </Drawer>
  );
};

export default DocumentDrawer;
