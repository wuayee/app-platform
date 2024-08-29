import React, { useState, useEffect } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import { Button } from 'antd';
import { LeftArrowIcon, UploadIcon } from '@assets/icon';
import TimeLineDrawer from '@/components/timeLine';
import PublishModal from './publish-modal';
import EditModal from './edit-modal';
import TestStatus from './test-status';
import TestModal from './test-modal';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { updateChatId } from '@/shared/utils/common';
import { setTestStatus, setTestTime } from "@/store/flowTest/flowTest";
import { setChatId, setChatList } from '@/store/chatStore/chatStore';
import { useTranslation } from 'react-i18next';
import './styles/header.scss'

const ChoreographyHead = (props) => {
  const { t } = useTranslation();
  const {
    showElsa,
    appInfo,
    updateAippCallBack,
    mashupClick,
    openDebug,
    showTime
  } = props;
  const testStatus = useAppSelector((state) => state.flowTestStore.testStatus);
  const [currentTime, setCurrentTime] = useState('');
  const [open, setOpen] = useState(false);
  let modalRef = React.createRef();
  let editRef = React.createRef();
  let testRef = React.createRef();
  const { tenantId, appId } = useParams();
  const navigate = useHistory().push;
  const dispatch = useAppDispatch();
  useEffect(() => {
    showTime && getCurrentTime();
  }, [appInfo])
  // 编辑名称
  const handleEditClick = () => {
    editRef.current.showModal();
  }

  // 点击应用发布按钮
  function handleUploadApp() {
    if (testStatus !== 'Finished') {
      testRef.current.showModal();
      return;
    }
    modalRef.current.showModal();
  }
  // 返回编排页面
  const backClick = () => {
    if (testStatus) {
      dispatch(setTestStatus(null));
      dispatch(setTestTime(0));
    }
    if (showElsa) {
      mashupClick();
    } else {
      navigate(`/app-develop/${tenantId}/appDetail/${appId}`);
    }
  }
  // 打开调试抽屉
  const handleOpenDebug = () => {
    openDebug();
  }

  const getCurrentTime = () => {
    let str = new Date().toTimeString().substring(0, 8);
    setCurrentTime(str);
  }
  const chatClick = () => {
    updateChatId(null, appId);
    dispatch(setChatId(null));
    dispatch(setChatList([]));
    navigate(`/app/${tenantId}/chat/${appId}`);
  }
  const versionDetail = () => {
    setOpen(true);
  }
  return <>{(
    <div className='app-header'>
      <div className='logo'>
        <LeftArrowIcon className='back-icon' onClick={backClick} />
        {appInfo?.attributes?.icon ?
          <img src={appInfo.attributes?.icon} onClick={backClick} /> :
          <img src='./src/assets/images/knowledge/knowledge-base.png' onClick={backClick} />
        }
        <span className='header-text' title={appInfo?.name}>{appInfo?.name}</span>
        <img className='edit-icon' src='./src/assets/images/ai/edit.png' onClick={handleEditClick} />
        {
          (appInfo.attributes?.latest_version || appInfo.state === 'active') ?
            (
              <div className='status-tag'>
                <img src='./src/assets/images/ai/complate.png' />
                <span>{t('published')}</span>
              </div>
            ) :
            (
              <div className='status-tag'>
                <img src='./src/assets/images/ai/publish.png' />
                <span>{t('unPublished')}</span>
              </div>
            )
        }
        {showTime && <span>{t('autoSave')}：{currentTime}</span>}
        {showElsa && <TestStatus />}
      </div>
      <div className='header-grid'>
        {
          (appInfo.attributes?.latest_version || appInfo.state === 'active') &&
          <div className='header-grid-btn'>
            <span className='history' onClick={versionDetail}>
              <img src='./src/assets/images/ai/time.png' />
            </span>
            <span className='history robot' onClick={chatClick}>
              <img src='./src/assets/images/ai/robot.png' />
              <span>{t('toTalk')}</span>
            </span>
          </div>
        }
        {showElsa && <Button
          className='header-btn test-btn'
          disabled={testStatus === 'Running'}
          onClick={handleOpenDebug}>
          {t('debug')}
        </Button>}
        <Button
          type='primary'
          className='header-btn publish-btn'
          disabled={testStatus === 'Running'}
          onClick={handleUploadApp}>
          <UploadIcon />{t('publish')}
        </Button>
      </div>
      <PublishModal modalRef={modalRef} appInfo={appInfo} publishType='app' />
      <EditModal modalRef={editRef} appInfo={appInfo} updateAippCallBack={updateAippCallBack} />
      <TimeLineDrawer open={open} setOpen={setOpen} />
      <TestModal testRef={testRef} handleDebugClick={openDebug} type='edit' />
    </div>
  )} </>;
};

export default ChoreographyHead;
