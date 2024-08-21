
import React, { useState } from 'react';
import { Button, Modal } from 'antd';
import { shareDialog } from '@shared/http/aipp';;
import { toClipboard } from '@shared/utils/common';
import { useAppSelector } from '@/store/hook';
import { useTranslation } from 'react-i18next';
import '../styles/check-group.scss';

const CheckGroup = (props) => {
  const { t } = useTranslation();
  const {
    type,
    setEditorShow,
    checkedList,
    deleteChat,
    reportClick
  } = props;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [shareUrl, setShareUrl] = useState('');
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const textMap = {
    'share': t('share'),
    'delete': t('delete'),
    'report': '',
  }

  // 取消
  function cancle() {
    setEditorShow(false)
  }
  function handleCancel() {
    setIsModalOpen(false);
  }
  function copyLink() {
    toClipboard(shareUrl);
  }
  // 处理点击
  const handleShare = (e) => {
    const resultArr = [];
    const logIdArr = [];
    checkedList.forEach((item, index) => {
      resultArr.push({ query: JSON.stringify(item) });
      logIdArr.push(item.logId);
    });
    if (type === 'share') {
      shareConfirm(resultArr);
    } else if (type === 'delete') {
      deleteChat(logIdArr);
    } else {
      reportClick(resultArr);
    }
  }
  // 分享
  function shareConfirm(result) {
    shareDialog(tenantId, result).then(res => {
      if (res.code === 0) {
        setIsModalOpen(true);
        setShareUrl(window.location.href.split('#')[0] + `#/${tenantId}/chatShare/${appId}/${res.data}`);
      }
    })
  }
  return <>{(
    <div className='message-check-toolbox-wrap'>
      <div className='message-check-toolbox'>
        <div className='message-check-toolbox-left'>
          <div className='message-check-toolbox__num'>{t('isSelected')}：{checkedList.length} </div>
        </div>
        <div className='message-check-toolbox-right'>
          <Button onClick={cancle}>{t('cancel')}</Button>
          <Button
            type={checkedList.length === 0 ? 'default' : 'primary'}
            disabled={checkedList.length === 0}
            onClick={(e) => handleShare(e)}>
            {t('ok')}{textMap[type] || ''}
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
        ]}>
        <div className='modal-share'>
          <span className='share-text'>{shareUrl}</span>
          <span className='link' onClick={copyLink}>{t('copiedLink')}</span>
        </div>
      </Modal>
    </div>
  )}</>
};


export default CheckGroup;
