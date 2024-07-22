import React, {  useState, useImperativeHandle } from 'react';
import { Modal, Button } from 'antd';

const TestModal = (props) => {
  const { testRef, handleDebugClick, type } = props;
  const [ isModalOpen, setIsModalOpen] = useState(false);

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
      title={type ? '需要测试成功才能发布应用' : '需要测试成功才能发布工具流'}
      width='380px'
      maskClosable={false}
      centered
      open={isModalOpen}
      onOk={handleOK}
      onCancel={handleCancel}
      footer={[
        <Button key='back' onClick={handleCancel}>
          取消
        </Button>,
        <Button key='test' type='primary' onClick={handleOK}>
          调试
        </Button>
      ]}>
      <p>为了保证{type ? '应用' : '工作流'}运行正常，必须进行测试</p>
    </Modal>
  )}</>
};

export default TestModal;
