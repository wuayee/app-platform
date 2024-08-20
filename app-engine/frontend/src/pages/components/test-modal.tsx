import React, { useState, useImperativeHandle } from 'react';
import { Modal, Button } from 'antd';
import { useTranslation } from 'react-i18next';

const TestModal = (props) => {
  const { t } = useTranslation();
  const { testRef, handleDebugClick, type } = props;
  const [isModalOpen, setIsModalOpen] = useState(false);

  const showModal = () => {
    setIsModalOpen(true);
  };

  const handleCancel = () => {
    setIsModalOpen(false);
  };

  const handleOK = () => {
    setIsModalOpen(false);
    handleDebugClick();
  }

  useImperativeHandle(testRef, () => {
    return {
      'showModal': showModal
    }
  })
  return <>{(
    <Modal
      title={type ? t('debugTip') : t('debugTip2')}
      width='380px'
      maskClosable={false}
      centered
      open={isModalOpen}
      onOk={handleOK}
      onCancel={handleCancel}
      footer={[
        <Button key='back' onClick={handleCancel}>
          {t('cancel')}
        </Button>,
        <Button key='test' type='primary' onClick={handleOK}>
          {t('ok')}
        </Button>
      ]}>
      { type ? <p>{t('testTip')}</p> : <p>{t('testTip2')}</p>}


    </Modal>
  )}</>
};

export default TestModal;
