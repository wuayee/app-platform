
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useRef } from 'react';
import { Button, Modal } from 'antd';
import { shareDialog } from '@/shared/http/aipp';
import { toClipboard } from '@/shared/utils/common';
import { useTranslation } from 'react-i18next';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setChatList } from '@/store/chatStore/chatStore';
import { deepClone } from '../utils/chat-process';
import '../styles/check-group.scss';

const CheckGroup = (props) => {
  const { t } = useTranslation();
  const { type, setEditorShow, deleteChat, reportClick, checkedChat, display } = props;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [shareUrl, setShareUrl] = useState('');
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [btnLoading, setBtnLoading] = useState(false);
  const [checkedList, setCheckedList] = useState([]);
  const currentLogId = useRef([]);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const dimension = useAppSelector((state) => state.commonStore.dimension);
  const dispatch = useAppDispatch();
  const textMap = {
    share: t('share'),
    delete: t('delete'),
    report: '',
  };
  // 全选
  const selectAll = (checkAll: boolean) => {
    const list = deepClone(chatList);
    list.forEach((item) => (item.checked = checkAll));
    dispatch(setChatList(deepClone(list)));
    checkAll ? setCheckedList(list) : setCheckedList([]);
  };
  // 取消
  function cancle() {
    setEditorShow(false);
  }
  function handleCancel() {
    setEditorShow(false);
    setIsModalOpen(false);
  }
  function copyLink() {
    toClipboard(shareUrl);
  }
  // 处理点击
  const handleShare = (e) => {
    const resultArr = [];
    const logIdArr = [];
    const currentUser = localStorage.getItem('currentUser') || '';
    checkedList.forEach((item, index) => {
      item.shareUser = currentUser;
      item.mode = 'share';
      item.dimension = dimension.name;
      if (dimension.id) {
        item.dimensionValue = dimension;
      }
      resultArr.push({ query: JSON.stringify(item) });
      logIdArr.push(Number(item.logId));
    });
    if (type === 'share') {
      shareConfirm(resultArr);
    } else if (type === 'delete') {
      currentLogId.current = logIdArr;
      setDeleteOpen(true);
    } else {
      reportClick(resultArr);
    }
  };
  const confirm = () => {
    setLoading(false);
    deleteChat(currentLogId.current);
  };
  // 分享
  async function shareConfirm(result) {
    setBtnLoading(true);
    try {
      const res = await shareDialog(tenantId, result);
      if (res.code === 0) {
        setIsModalOpen(true);
        setShareUrl(
          window.location.href.split('#')[0] + `#/${tenantId}/chatShare/${appId}/${res.data}`
        );
      }
    } finally {
      setBtnLoading(false);
    }
  }
  useEffect(() => {
    setCheckedList(checkedChat);
  }, [checkedChat]);
  return (
    <>
      {
        <div className='message-check-toolbox-wrap' style={{display: display ? 'block' : 'none'}}>
          <div className='message-check-toolbox'>
            <div className='message-check-toolbox-left'>
              <div className='message-check-toolbox__num'>
                {t('isSelected')}：{checkedList.length}{' '}
              </div>
            </div>
            <div className='message-check-toolbox-right'>
              {checkedList.length !== chatList.length && (
                <Button type='primary' onClick={() => selectAll(true)}>
                  {t('selectAll')}
                </Button>
              )}
              {checkedList.length === chatList.length && (
                <Button type='primary' onClick={() => selectAll(false)}>
                  {t('unselectAll')}
                </Button>
              )}
              <Button onClick={cancle}>{t('cancel')}</Button>
              <Button
                type={checkedList.length === 0 ? 'default' : 'primary'}
                disabled={checkedList.length === 0}
                loading={btnLoading}
                onClick={(e) => handleShare(e)}
              >
                {type === 'share' && '复制分享链接'}
                {(type !== 'share' && t('ok') + textMap[type]) || ''}
              </Button>
            </div>
          </div>
          <Modal
            title={t('copiedLinkTitle')}
            open={isModalOpen}
            onCancel={handleCancel}
            footer={[
              <Button key='back' onClick={handleCancel}>
                {t('close')}
              </Button>,
            ]}
          >
            <div className='modal-share'>
              <span className='share-text'>{shareUrl}</span>
              <span className='link' onClick={copyLink}>
                {t('copiedLink')}
              </span>
            </div>
          </Modal>
          <Modal
            title='提示'
            open={deleteOpen}
            centered
            width='380px'
            okText={t('ok')}
            cancelText={t('cancel')}
            okButtonProps={{ loading }}
            onOk={() => confirm()}
            onCancel={() => setDeleteOpen(false)}
          >
            <div style={{ margin: '8px 0' }}>
              <span>{t('deleteKnowledgeTips')}</span>
            </div>
          </Modal>
        </div>
      }
    </>
  );
};

export default CheckGroup;
