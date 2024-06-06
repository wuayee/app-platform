
import React, { useEffect, useState, useImperativeHandle, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { EditIcon, LeftArrowIcon, UploadIcon } from '@assets/icon';

const AddHeader = () => {
  const [ timestamp, setTimestamp ] = useState(new Date());
  let editRef = useRef(null);
  let modalRef = useRef(null);
  let testRef = useRef(null);
  
  const navigate = useNavigate();
  // 发布工具流
  const handleUploadFlow = () => {
    if (!isTested) {
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
    navigate(-1);
  }
  const formatTimeStamp = (now) => {
    let hours = now.getHours().toString().padStart(2, '0');
    let minutes = now.getMinutes().toString().padStart(2, '0');
    let seconds = now.getSeconds().toString().padStart(2, '0');
    // 返回格式化后的时间字符串
    return `${hours}:${minutes}:${seconds}`;
  }
  return <>{(
    <div className='header'>
      <div className='header-left'>
        <LeftArrowIcon className="icon-back" onClick={ handleBackClick } />
        <span className='header-text'>{ waterFlowName }</span>
        <span className='header-edit'>
          <EditIcon onClick={ handleEditClick } />
        </span>
          { added && <span className='header-last-saved'>自动保存于 { formatTimeStamp(timestamp) }</span> }
        < TestStatus isTested={isTested} isTesting={isTesting} testTime={testTime} testStatus={testStatus}/>
      </div>
      <div className='header-grid'>
        <span className="header-btn test-btn" onClick={handleDebugClick}>测试</span>
        <span className="header-btn" onClick={handleUploadFlow}><UploadIcon />发布</span>
      </div>
    </div>
  )}</>
};


export default AddHeader;
