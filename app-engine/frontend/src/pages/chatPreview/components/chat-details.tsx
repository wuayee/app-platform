/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, useEffect } from 'react';
import { useLocation, useHistory } from 'react-router-dom';
import EditModal from '../../components/edit-modal';
import { AppBoxIcon, CreateAppIcon } from '@assets/icon';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setOpenStar } from '@/store/chatStore/chatStore';
import { findConfigValue } from '@/shared/utils/common';
import knowledgeBase from '@assets/images/knowledge/knowledge-base.png';
import robot2 from '@assets/images/ai/xiaohai.png';
import { useTranslation } from 'react-i18next';
import '../styles/chat-details.scss';

const ChatDetail = () => {
  const { t } = useTranslation();
  const [modalInfo, setModalInfo] = useState({});
  const [opening, setOpening] = useState('');
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const openStar = useAppSelector((state) => state.chatCommonStore.openStar);
  const navigate = useHistory().push;
  const location = useLocation();
  let modalRef = useRef<any>();
  const isHomepage = appInfo.name === '小海' && !location.pathname.includes('app-detail');
  const addApp = () => {
    setModalInfo(() => {
      modalRef.current.showModal();
      return {
        name: '',
        attributes: {
          description: '',
          icon: '',
          app_type: '',
        }
      }
    })
  }
  function addAippCallBack(appId:string, aippId:string) {
    if (aippId) {
      navigate(`/app-develop/${tenantId}/app-detail/${appId}/${aippId}`);
      return;
    }
    navigate(`/app-develop/${tenantId}/app-detail/${appId}`);
  }

  useEffect(() => {
    const opening = findConfigValue(appInfo, 'opening');
    setOpening(opening || t('hello'));
  }, [appInfo]);
  return <>{(
    <div className='chat-details-content'>
      {appInfo?.name ? (isHomepage ? (
        <div className='home-top'>
          <div className='head-inner'>
            <div className='inner-left'>
              <div className='title'>ModelEngine</div>
              <div className='sub-title'>{t('platformTitle')}</div>
              <div className='desc'>{t('platformSubTitle')}</div>
            </div>
            <div className='inner-right'>
              <div className=''>
                <img src={robot2} />
              </div>
            </div>
            <div className='inner-right-chat'>
              {t('xiaohaiGreet1')} <strong>{t('xiaohai')}</strong>，{t('xiaohaiGreet6')}
            </div>
          </div>
          <div className='head-nav'>
            <div className='nav-left' onClick={addApp}>
              <div className='tag-home-page'>
                <CreateAppIcon />
              </div>
              <div className='nav-title'>{t('createApp')}</div>
              <div className='nav-desc'>{t('createAppDescription')}</div>
            </div>
            <div
              className={`nav-right ${openStar ? 'nav-item-active' : ''}`}
              onClick={() => dispatch(setOpenStar(true))}
            >
              <div className='tag-home-page'>
                <AppBoxIcon />
              </div>
              <div className='nav-title'>{t('appTreasure')}</div>
              <div className='nav-desc'>{t('appTreasureDescription')}</div>
            </div>
          </div>
        </div>
      ) : (
          <div className='top'>
            <div className='head'>
              <Img icon={appInfo.attributes?.icon} />
            </div>
            <div className='title'>{appInfo.name}</div>
            <div className='text'>{appInfo.attributes?.description}</div>
            <div className='bottom'>
              <div className='left'>
                <Img icon={appInfo.attributes?.icon} />
              </div>
              <div className='right'>{opening}</div>
            </div>
          </div>
        )) : ''}
      <EditModal type='add' modalRef={modalRef} appInfo={modalInfo} addAippCallBack={addAippCallBack} />
    </div>
  )}</>;
};

const Img = (props) => {
  const { icon } = props;
  return <>{<span>{icon ? <img src={icon} /> : <img src={knowledgeBase} />}</span>}</>;
};

export default ChatDetail;
