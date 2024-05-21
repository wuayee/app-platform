
import React, {  useState, useImperativeHandle } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Modal, Button } from 'antd';
import { Message } from '../../shared/utils/message';
import { httpUrlMap } from '../../shared/http/httpConfig';
import { appPublish, updateFlowInfo } from '../../shared/http/aipp'

const PublishModal = (props) => {
  const { modalRef, aippInfo, publishType, modalInfo, waterFlowName, addId } = props;
  const { appId, tenantId } = useParams();
  const [ isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const showModal = () => {
    setIsModalOpen(true);
  };
  // 发布点击
  function publishClick() {
    const { PUBLISH_URL } = httpUrlMap[process.env.NODE_ENV];
    aippInfo.publishUrl = `${PUBLISH_URL}/aipp/${tenantId}/chat/${appId}`;
    publishType !== 'app' ? updateAppRunningFlow() : publishApp();
  }
  // 发布应用
  async function publishApp() {
    setLoading(true);
    try {
      const res = await appPublish(tenantId, appId, aippInfo);
      if (res.code === 0) {
        Message({ type: 'success', content: `发布应用成功` });
        setIsModalOpen(false);
      }
    } finally {
      setLoading(false)
    }
  }
  // 发布工具流
  async function publishWaterFlow() {
    try {
      const res = await appPublish(tenantId, appId, modalInfo);
      if (res.code === 0) {
        Message({ type: 'success', content: `发布工具流成功` });
        sessionStorage.setItem('uniqueName', res.data.tool_unique_name);
        navigate(`/app/${tenantId}/detail/${appId}`);
      }
    } finally {
      setLoading(false)
    }
  }
  // 编辑更新应用
  async function updateAppRunningFlow() {
    if (waterFlowName === '无标题') {
      Message({ type: 'warning', content: '工具流标题不能为空' });
      return
    }
    setLoading(true);
    let params = modalInfo.flowGraph;
    const res = await updateFlowInfo(tenantId, addId, params);
    if (res.code === 0) {
      publishWaterFlow();
    } else {
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
