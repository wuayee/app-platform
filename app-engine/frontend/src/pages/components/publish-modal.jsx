
import React, {  useState, useImperativeHandle } from 'react';
import { useParams } from 'react-router-dom';
import { Modal, Button } from 'antd';
import { Message } from '../../shared/utils/message';
import { httpUrlMap } from '../../shared/http/httpConfig';
import { appPublish } from '../../shared/http/aipp'

const PublishModal = (props) => {
  const { modalRef, aippInfo, publishType, modalInfo } = props;
  const { appId, tenantId } = useParams();
  const [ isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const showModal = () => {
    setIsModalOpen(true);
  };
  // 发布
  async function publishClick() {
    setLoading(true);
    try {
      const { PUBLISH_URL } = httpUrlMap[process.env.NODE_ENV];
      aippInfo.publishUrl = `${PUBLISH_URL}/aipp/${tenantId}/chat/${appId}`;
      const res = await appPublish(tenantId, appId, (publishType === 'app' ? aippInfo : modalInfo));
      if (res.code === 0) {
        handleCancel();
        Message({ type: 'success', content: `发布${ publishType === 'app' ? '应用' : '工具流' }成功` });
      }
    } finally {
      setLoading(false)
    }
  }
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  return <>{(
    <Modal 
      title={ publishType === 'app' ? '发布应用' : '发布工具流' }
      width='380px' 
      maskClosable={false}
      centered 
      open={isModalOpen} 
      onOk={publishClick} 
      onCancel={handleCancel}
      footer={[
        <Button key="back" onClick={handleCancel}>
          取消
        </Button>,
        <Button key="submit" type="primary" loading={loading} onClick={publishClick}>
          确定
        </Button>
      ]}>
      <div className='search-list'>
        { publishType === 'app' ? '是否发布应用' : '是否发布工具流'  }
      </div>
    </Modal>
  )}</>
};

export default PublishModal;
