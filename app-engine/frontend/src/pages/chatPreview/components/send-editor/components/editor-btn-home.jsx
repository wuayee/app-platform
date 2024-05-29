
import React, { useEffect, useState, useContext, useRef } from 'react';
import { Tooltip, Dropdown, Space, Modal } from "antd";
import { CloseOutlined } from "@ant-design/icons";
import { 
  LinkIcon, 
  AtIcon, 
  HistoryIcon, 
  ArrowDownIcon, 
  LanguagesIcon, 
  ClearChatIcon } from '@/assets/icon';
import ReferencingApp from './referencing-app';
import UploadFile from './upload-file';
import StarApps from "../../star-apps";
import robot from "@assets/images/ai/robot1.png";

// 操作按钮
const EditorBtnHome = (props) => {
  const { aippInfo, setOpen, clear, fileCallBack } = props;
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ openStar, setOpenStar ] = useState(false);
  const [ showAt, setShowAt ] = useState(false);
  const [ appName, setAppName ] = useState('');
  const [ appIcon, setAppIcon ] = useState(robot);
  const [ isAt, setIsAt ] = useState(false);
  let modalRef = useRef(null);

  useEffect(() => {
    document.body.addEventListener('click', () => {
      setShowAt(false);
    })
    if (aippInfo.attributes?.icon) {
      setAppIcon(aippInfo.attributes.icon);
      setAppName(aippInfo.name || '应用');
    }
  }, [props]);

  // 新聊天
  const handleOk = () => {
    clear('all');
    setIsModalOpen(false);
  };
  // @ 应用点击
  const atClick = (e) => {
    e.stopPropagation();
    setShowAt(!showAt);
  }
  // 取消@应用功能
  const cancleAt = () => {
    setIsAt(false);
  }
  // @应用点击回调
  const atItemClick = (item) => {
    setAppName(item.name);
    setShowAt(false);
    setIsAt(true);
  }
  // 更多应用
  const showMoreClick = () => {
    setOpenStar(true)
  }
  // 开始聊天
  const chatClick = (item) => {
    console.log(item);
    setOpenStar(false);
  }
  // 多模态上传文件
  const uploadClick = () => {
    modalRef.current.showModal();
  }
  // 上传文件回调
  const fileSend = (data, type) => {
    fileCallBack(data, type);
  }
  return <>{(
    <div className="btn-inner">
      <div className="inner-left">
        <div className="inner-item">
          <img src={appIcon} alt="" />
          <div className={['switch-app', isAt ? 'switch-active' : null ].join(' ')} onClick={showMoreClick}>
            { isAt && <span style={{ marginLeft: '6px' }}>正在跟</span> }
            <span className="item-name" title={appName}>{appName}</span>
            <ArrowDownIcon className="arrow-icon" />
            { isAt && <span style={{ marginLeft: '6px' }}>对话</span> }
          </div>
          <LinkIcon onClick={uploadClick} />

          {/* 暂时隐藏@图标 */}
          {/* { !isAt && <AtIcon onClick={atClick} /> } */}
        </div>
      </div>
      <div className="inner-right">
        { 
          isAt ? 
          (
            <div className="inner-item">
              <CloseOutlined className="item-close" onClick={cancleAt}/>
            </div>
          ) : 
          (
            <div className="inner-item">
              <ClearChatIcon style={{ marginTop: '6px' }} onClick={() => setIsModalOpen(true)} />
              <HistoryIcon  onClick={() => setOpen(true)}/>
              <span className="item-clear" onClick={() => clear()}>+ 新聊天</span>
            </div>
          )
        }
        
      </div>
      { showAt && <ReferencingApp atItemClick={atItemClick} atClick={showMoreClick}/> }
      <Modal 
        title="确认清空当前聊天" 
        open={isModalOpen} 
        onOk={handleOk} 
        onCancel={() => setIsModalOpen(false)} 
        centered>
        <span>清空后当前窗口聊天内容将不会被系统保存。</span>
      </Modal>
      <StarApps 
        open={openStar} 
        setOpen={setOpenStar} 
        handleAt={atItemClick}
        chatClick={chatClick}
      />
      <UploadFile  modalRef={modalRef} fileSend={fileSend}/>
    </div>
  )}</>
}


export default EditorBtnHome;
