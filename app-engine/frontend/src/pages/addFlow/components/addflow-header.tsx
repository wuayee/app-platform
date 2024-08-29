
import React, { useState, useRef, useContext } from 'react';
import { useHistory, useParams } from 'react-router-dom';
import { Button } from 'antd';
import { LeftArrowIcon, UploadIcon } from '@/assets/icon';
import { updateAppInfo } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { FlowContext } from '../../aippIndex/context';
import EditTitleModal from '../../components/edit-title-modal';
import PublishModal from '../../components/publish-modal';
import TestModal from '../../components/test-modal';
import TestStatus from '../../components/test-status';
import TimeLineDrawer from '@/components/timeLine';
import { useTranslation } from 'react-i18next';
import { setTestStatus, setTestTime } from "@/store/flowTest/flowTest";
import { useAppSelector, useAppDispatch } from "@/store/hook";

const AddHeader = (props) => {
  const dispatch = useAppDispatch();
  const { t } = useTranslation();
  const { handleDebugClick } = props;
  const { appInfo, showTime, setFlowInfo } = useContext(FlowContext);
  const [open, setOpen] = useState(false);
  const { tenantId, appId } = useParams();
  let editRef: any = useRef(null);
  let modalRef: any = useRef(null);
  let testRef: any = useRef(null);
  const testStatus = useAppSelector((state) => state.flowTestStore.testStatus);

  const navigate = useHistory().push;
  // 发布工具流
  const handleUploadFlow = () => {
    if (testStatus !== 'Finished') {
      testRef.current.showModal();
      return;
    }
    modalRef.current.showModal();
  }
  // 编辑工具流
  const handleEditClick = () => {
    editRef.current.showModal();
  }
  // 返回上一页
  const handleBackClick = () => {
    if (testStatus) {
      dispatch(setTestStatus(null));
      dispatch(setTestTime(0));
    }
    window.history.back();
  }
  const getCurrentTime = () => {
    let str = new Date().toTimeString().substring(0, 8);
    return str
  }
  // 保存回调
  function onFlowNameChange(params) {
    appInfo.name = params.name;
    appInfo.attributes.description = params.description;
    updateAppWorkFlow('waterFlow');
  }
  // 创建更新应用
  async function updateAppWorkFlow(optionType = '') {
    const res = await updateAppInfo(tenantId, appId, appInfo);
    if (res.code === 0) {
      Message({ type: 'success', content: t('editSuccess') });
      optionType && editRef.current.handleCancel();
      setFlowInfo(JSON.parse(JSON.stringify(appInfo)));
    } else {
      optionType && editRef.current.handleLoading();
    }
  }
  const versionDetail = () => {
    setOpen(true);
  }
  return <>{(
    <div>
      <div className='app-header'>
        <div className='logo'>
          <LeftArrowIcon className='back-icon' onClick={handleBackClick} />
          {(appInfo.attributes?.icon && appInfo.attributes?.icon !== 'null') ?
            <img src={appInfo.attributes?.icon} /> :
            <img src='./src/assets/images/knowledge/knowledge-base.png' />
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
          {showTime && <span>{t('autoSave')}：{getCurrentTime()}</span>}
          <TestStatus />
        </div>
        <div className='header-grid'>
          {
            (appInfo.attributes?.latest_version || appInfo.state === 'active') &&
            <span className='history' onClick={versionDetail}>
              <img src='./src/assets/images/ai/time.png' />
            </span>
          }
          <Button
            className='header-btn test-btn'
            onClick={handleDebugClick}
            disabled={testStatus === 'Running'}
          >
            {t('debug')}
          </Button>
          <Button
            type='primary'
            className='header-btn'
            onClick={handleUploadFlow}
            disabled={testStatus === 'Running'}
          >
            <UploadIcon />{t('publish')}
          </Button>
        </div>
      </div>
      <PublishModal
        modalRef={modalRef}
        appInfo={appInfo}
        publishType='waterflow'
      />
      <TestModal
        testRef={testRef}
        handleDebugClick={handleDebugClick}
      />
      <EditTitleModal
        modalRef={editRef}
        onFlowNameChange={onFlowNameChange}
        appInfo={appInfo}
      />
      <TimeLineDrawer open={open} setOpen={setOpen} />
    </div>
  )}</>
};


export default AddHeader;
