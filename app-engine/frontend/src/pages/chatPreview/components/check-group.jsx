
import React, { useState, useContext } from 'react';
import { Button, Modal } from 'antd';
import '../styles/check-group.scss'
import { shareDialog } from "@shared/http/aipp";
import { AippContext } from '@/pages/aippIndex/context';
import { toClipboard } from "@shared/utils/common";

const CheckGroup = (props) => {
  const {
    type,
    setEditorShow,
    checkedList,
    reportClick
  } = props;
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ shareUrl, setShareUrl ] = useState('');
  const { appId,tenantId } = useContext(AippContext);

  // 取消
  function cancle() {
    setEditorShow(false)
  }
  function handleCancel() {
    setIsModalOpen(false);
  }
  function copyLink() {
    toClipboard(shareUrl);
  }
  // 处理点击
  const handleShare = (e) => {
    const result = [];
    checkedList.map((item,index) => {
      result.push({query: JSON.stringify(item)});
    })
    type === 'share' ? shareConfirm(result) : reportClick(result);
  }
  // 分享
  function shareConfirm(result) {
    shareDialog(tenantId, result).then(res => {
      if (res.code === 0) {
        setIsModalOpen(true);
        setShareUrl(window.location.href.split('#')[0] + `#/${tenantId}/chatShare/${appId}/${res.data}`);
      }
    })
  }
  return <>{(
    <div className='message-check-toolbox-wrap'>
      <div className="message-check-toolbox">
        <div className="message-check-toolbox-left">
          <div className="message-check-toolbox__num">已选择：{ checkedList.length } </div>
        </div>
        <div className="message-check-toolbox-right">
          {/* <Button style={btnStyle} type="primary" onClick={checkAllClick}>{ checkAll ? '取消全选' : '全选' }</Button> */}
          <Button onClick={cancle}>取消</Button>
          <Button 
            type={ checkedList.length === 0 ? 'default' : 'primary' } 
            disabled={checkedList.length === 0} 
            onClick={(e) => handleShare(e)}>
              确认{ type === 'share' ? '分享' : ''}
            </Button>
          {/*{ <Button  type={ checkedList.length === 0 ? 'default' : 'primary' } disabled={checkedList.length === 0}>分享到weLink</Button> }*/}
        </div>
      </div>
      <Modal 
        title="分享链接已复制，快发给朋友们吧～" 
        open={isModalOpen} 
        onCancel={handleCancel}
        footer={[
          <Button key="back" onClick={handleCancel}>
            关闭
          </Button>,
        ]}>
        <div className="modal-share">
          <span className="share-text">{shareUrl}</span>
          <span className="link" onClick={copyLink}>复制链接</span>
        </div>
      </Modal>
    </div>
  )}</>
};


export default CheckGroup;
