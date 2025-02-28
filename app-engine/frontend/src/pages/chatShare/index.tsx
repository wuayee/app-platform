/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Button } from 'antd';
import { useParams, useHistory } from 'react-router-dom';
import {getAppInfo, getSharedDialog, aippDebug} from '@/shared/http/aipp';
import ChatMessage from '../chatPreview/components/chat-message';
import { setChatList } from '@/store/chatStore/chatStore';
import { setAppId, setAppInfo } from '@/store/appInfo/appInfo';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setDimension } from '@/store/common/common';
import './index.scss';

const ChatShare = () => {
  const { appId, tenantId, shareId } = useParams();
  const [show, setShow] = useState(false);
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const navigate = useHistory().push;

  useEffect(() => {
    getAippDetails();
    getDialog();
  }, []);

  // 获取aipp详情
  const getAippDetails = async () => {
    const res = await getAppInfo(tenantId, appId);
    if (res.code === 0) {
      dispatch(setAppInfo(res.data));
    }
  }
  // 获取对话详情
  const getDialog = async () => {
    const res = await getSharedDialog(tenantId, shareId);
    if (res.code === 0) {
      const data = JSON.parse(res.data);
      const parsedItem = data.map(item => {
        return JSON.parse(item.query);
      });
      const dimensionShare = parsedItem.filter((ite)=>ite.dimensionValue);
      if(dimensionShare.length > 0){
        dispatch(setDimension(dimensionShare[0].dimensionValue));
      }
      setShow(parsedItem.length > 0);
      dispatch(setChatList(parsedItem));
    }
  }
  const backToChat = async () => {
    const debugRes = await aippDebug(tenantId, appId, appInfo, appInfo.state);
    let { aipp_id, app_id } = debugRes?.data;
    dispatch(setAppId(app_id));   
    const url = aipp_id
      ? `/app/${tenantId}/chat/${appId}/${aipp_id}`
      : `/app/${tenantId}/chat/${appId}`;
      navigate(url);
  }
  return <>{(
    <div className='share-content'>
      <div className='share-inner'>
        { show && <div className='chat-share-content'>
          <div className='top'>
            <div className='head'>
              {(appInfo.attributes?.icon && appInfo.attributes?.icon !== 'null') ?
                <img src={appInfo.attributes?.icon} /> :
                <img src={'./src/assets/images/knowledge/knowledge-base.png'} />
              }
            </div>
            <div className='title'>{ appInfo.name }</div>
            <div className='text'>{appInfo.attributes?.description }</div>
          </div>
        </div> }
        <ChatMessage /> 
        <div className='continue-chat'>
          <Button type="primary" onClick={backToChat}>继续对话</Button>
        </div>
      </div>
    </div>
  )}</>
};

export default ChatShare;
