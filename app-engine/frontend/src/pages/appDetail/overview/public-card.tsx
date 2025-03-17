/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { Button, Input } from 'antd';
import { AppIcons } from '@/components/icons/app';
import { toClipboard } from '@/shared/utils/common';
import { CopyUrlIcon } from '@/assets/icon';
import { useTranslation } from 'react-i18next';
import Empty from '@/components/empty/empty-item';
import IframeModal from './iframe-modal';
import DocumentDrawer from './apiDocument';
import DocumentOmsDrawer from './apiOmsDocument';
import SecretKeyIcon from '@/assets/images/ai/secret_key.png';
import DocumentIcon from '@/assets/images/ai/document.png';

/**
 * 公共访问URL和API卡片
 *
 * @param type 卡片类型.
 * @param url 链接
 * @return {JSX.Element}
 * @constructor
 */
const PublicCard = ({ type, url, detail, auth = false }) => {
  const { t } = useTranslation();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  // 设置公开访问URL
  const setPreviewUrl = (url) => {
    let origin = window.location.origin;
    return type === 'URL' ? `${origin}${process.env.PACKAGE_MODE === 'common' ? '/#' : '/appengine'}${url}` : `${origin}${url}`;
  };

  // 复制
  const copyClick = (url) => {
    toClipboard(url);
  };

  const openClick = (url) => {
    window.open(url);
  };

  // 查看API秘钥
  const openKey = () => {
    window.open(`${window.location.origin}/modellite/apikey/home`);
  };

  // 查看API文档
  const openDocumentation = (url) => {
    setDrawerOpen(true);
  };

  return (
    <div className='detail-card-item'>
      {url ? (
        <div className='item-detail'>
          <div className='item-top'>
            {type === 'URL' ? (
              <div className='title'>{t('public')}URL</div>
            ) : (
              <div className='title'>API{t('access')}</div>
            )}
          </div>
          {detail.attributes?.latest_version || detail.state === 'active' ? (
            <Input
              value={setPreviewUrl(url)}
              readOnly
              suffix={
                <CopyUrlIcon
                  onClick={() => copyClick(setPreviewUrl(url))}
                  style={{ cursor: 'pointer' }}
                />
              }
            />
          ) : (
            <></>
          )}
          {type === 'URL' ? (
            <div className='item-bottom'>
              <Button size='small' onClick={() => openClick(setPreviewUrl(url))}>
                <AppIcons.PreviewIcon />
                <span>{t('preview')}</span>
              </Button>
              <Button size='small' onClick={() => setDeleteOpen(true)}>
                <AppIcons.iframeIcon />
                <span>{t('iframeTip')}</span>
              </Button>
            </div>
          ) : detail.attributes?.latest_version || detail.state === 'active' ? (
            <div className='item-bottom'>
              <Button size='small' disabled={auth} onClick={openKey}>
                <img src={SecretKeyIcon} alt='' />
                <span>{t('ApiKey')}</span>
              </Button>
              <Button size='small' onClick={() => openDocumentation(url)}>
                <img src={DocumentIcon} alt='' />
                <span>{t('ApiDocumentation')}</span>
              </Button>
            </div>
          ) : (
            <Empty iconType='url' text={t('notReleasedYetTip')} />
          )}
        </div>
      ) : (
        <div className='item-detail'>
          <div className='item-top'>
            {type === 'URL' ? (
              <div className='title'>{t('public')}</div>
            ) : (
              <div className='title'>API{t('access')}</div>
            )}
          </div>
          <Empty iconType='url' text={t('notReleasedYetTip')} />
        </div>
      )}
      { process.env.PACKAGE_MODE === 'spa' ? 
      <DocumentOmsDrawer
        drawerOpen={drawerOpen}
        url={setPreviewUrl(url)}
        setDrawerOpen={setDrawerOpen}
      /> : 
      <DocumentDrawer
        drawerOpen={drawerOpen}
        url={setPreviewUrl(url)}
        setDrawerOpen={setDrawerOpen}
      /> }
      <IframeModal deleteOpen={deleteOpen} setDeleteOpen={setDeleteOpen} url={setPreviewUrl(url)} />
    </div>
  );
};

export default PublicCard;
