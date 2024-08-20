
import React, { useEffect, useState } from 'react';
import { Button, Modal } from 'antd';
import { useParams, useHistory } from 'react-router-dom';
import CommonChat from '../chatPreview/chatComminPage';
import { getAppInfo } from '@/shared/http/aipp';
import { setAppId, setAppInfo } from '@/store/appInfo/appInfo';
import { setHistorySwitch } from '@/store/common/common';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { storage } from '@/shared/storage';
import { useTranslation } from 'react-i18next';
import './index.scoped..scss';

const ChatRunning = () => {
  const { t } = useTranslation();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [notice, setNotice] = useState('');
  const { appId, tenantId } = useParams();
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);

  // 获取aipp详情
  const getAippDetails = async () => {
    const res = await getAppInfo(tenantId, appId);
    if (res.code === 0) {
      res.data.notShowHistory = false;
      dispatch(setAppInfo(res.data));
      setNotice('');
      announcements(res.data);
    }
  }

  /**
   * 从appInfo中获取是否要展示多轮对话开关
   * @param data appInfo
   * @Return bool 是否
   */
  const getHistorySwitchValue = (data) => {
    return data.config?.form.properties.filter(item => item.name === 'memory')[0].defaultValue?.memorySwitch || false;
  }

  // 公告弹层
  const announcements = (data) => {
    const { id, version, attributes } = data;
    let chatVersionListMap = storage.get('chatVersionMap');
    if (chatVersionListMap) {
      try {
        let versionItem = chatVersionListMap.filter(item => item.id === id)[0];
        if (!versionItem) {
          chatVersionListMap.push({ id, version });
          setModalContent(data, chatVersionListMap);
        } else if (versionItem && versionItem.version !== version) {
          let index = chatVersionListMap.findIndex(item => item.id === id);
          chatVersionListMap[index].version = version;
          setModalContent(data, chatVersionListMap);
        }
      } catch {
        setIsModalOpen(false);
      }
    } else {
      setModalContent(data, [{ id, version }]);
    }
  }
  // 保存并显示弹层
  const setModalContent = (data, arr) => {
    let { publishedUpdateLog } = data.attributes;
    if (publishedUpdateLog && publishedUpdateLog.length) {
      setNotice(publishedUpdateLog);
      setIsModalOpen(true);
      storage.set('chatVersionMap', arr);
    }
  }
  // 点击显示弹层
  useEffect(() => {
    dispatch(setAppId(appId));
    getAippDetails();
  }, []);

  useEffect(() => {
    dispatch(setHistorySwitch(getHistorySwitchValue(appInfo)));
  }, [appInfo.id]);

  return (
    <div className='chat-running-container'>
      <div className='chat-running-chat'>
        <Button type='text' onClick={() => { window.history.back() }}>{t('return')}</Button>
        <span className='running-app-name'>{appInfo.name}</span>
      </div>
      <CommonChat chatType='active' />
      <Modal
        title={t('updateLog')}
        width={800}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        centered
        footer={null}>
        <div style={{ maxHeight: '400px', overflow: 'auto' }}>
          <div dangerouslySetInnerHTML={{ __html: notice }}></div>
        </div>
        <div style={{ display: 'flex', justifyContent: 'center' }}>
          <Button onClick={() => setIsModalOpen(false)}>{t('gotIt')}</Button>
        </div>
      </Modal>
    </div>
  )
};


export default ChatRunning;
