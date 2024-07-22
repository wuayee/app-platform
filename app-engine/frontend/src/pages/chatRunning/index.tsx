
import React, { useEffect, useState } from 'react';
import { Button, Modal } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import CommonChat from '../chatPreview/chatComminPage';
import { getAppInfo } from '@/shared/http/aipp';
import { setAppId, setAppInfo } from '@/store/appInfo/appInfo';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { storage } from '@/shared/storage';
import './index.scoped..scss';

const ChatRunning = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [notice, setNotice] = useState('');
  const { appId, tenantId } = useParams();
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const navigate = useNavigate();
  
  // 获取aipp详情
  const getAippDetails = async () => {
    const res = await getAppInfo(tenantId, appId);
    if (res.code === 0) {
      res.data.notShowHistory = true;
      dispatch(setAppInfo(res.data));
      setNotice('');
      announcements(res.data);
    }
  }

  // 公告弹层
  const announcements = ({ id, version, attributes }) => {
    let chatVersionListMap = storage.get('chatVersionMap');
    if (chatVersionListMap) {
      try {
        let versionItem = chatVersionListMap.filter(item => item.id === id)[0];
        if (!versionItem) {
          chatVersionListMap.push({ id, version });
          setModalContent(attributes, chatVersionListMap);
        } else if (versionItem && versionItem.version !== version){
          let index = chatVersionListMap.findIndex(item => item.id === id);
          chatVersionListMap[index].version = version;
          setModalContent(attributes, chatVersionListMap);
        }
      } catch {
        setIsModalOpen(false);
      }
    } else {
      setModalContent(attributes, [{ id, version }]);
    }
  }
  // 保存并显示弹层
  const setModalContent = (attributes, arr) => {
    let { remark } = attributes;
    if (remark && remark.length) {
      setNotice(remark);
      setIsModalOpen(true);
      storage.set('chatVersionMap', arr);
    }
  }
  useEffect(() => {
    dispatch(setAppId(appId));
    getAippDetails();
  }, []);
  return (
    <div className='chat-running-container'>
      <div className='chat-running-chat'>
        <Button type='text' onClick={()=> { navigate(-1)}}>返回</Button>
        { appInfo.name }
      </div>
      <CommonChat /> 
      <Modal 
        title='更新日志' 
        width={800}
        open={isModalOpen} 
        onCancel={() => setIsModalOpen(false)}
        centered
        footer={null}>
        <div style={{ maxHeight: '400px', overflow: 'auto', padding: '12px' }}>
          公告
        </div>
        <div style={{ display: 'flex', justifyContent: 'center' }}>
          <Button onClick={() => setIsModalOpen(false)}>我知道了</Button>
        </div>
      </Modal>
    </div>
  )
};


export default ChatRunning;
