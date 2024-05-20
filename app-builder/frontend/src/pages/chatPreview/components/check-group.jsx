
import React, { useEffect, useState } from 'react';
import { Button } from 'antd';
import '../styles/check-group.scss'
import {shareDialog} from "../../../shared/http/aipp";
import {useNavigate} from "react-router-dom";

const CheckGroup = (props) => {
  const {
    type,
    totalNum,
    confirmText,
    shareWelink,
    setEditorShow,
    checkedList,
    selectAllClick,
    chatList,
    tenantId,
    appId
  } = props;
  const navigate = useNavigate();
  const [ checkAll, setCheckAll ] = useState(false);
  const btnStyle = {
    marginLeft: '10px'
  }
  useEffect(() => {
    setCheckAll((totalNum === 2*checkedList.length))
  }, [props.checkedList])
  // 取消
  function cancle() {
    setEditorShow(false)
  }
  // 全选反选
  function checkAllClick() {
    setCheckAll(() => {
      let all = !checkAll;
      selectAllClick(all);
      return all
    })
  }
  // 处理点击分享
    const handleShare = (e) => {
        const ids = checkedList.map(item => item.logId);
        const result = [];
            chatList.map((item,index) => {
            if (ids.includes(item.logId)) {
                result.push({query: JSON.stringify(item)});
                result.push({query: JSON.stringify(chatList[index+1])});
            }
        })
        shareDialog(tenantId, result).then(res => {
            if (res.code === 0) {
                navigate(`/aipp/${tenantId}/chatShare/${appId}/${res.data}`);
            }
        })
    }
  return <>{(
    <div className='message-check-toolbox-wrap'>
      <div className="message-check-toolbox">
        <div className="message-check-toolbox-left">
          <div className="message-check-toolbox__title">{ `请选择需要${ type === 'share' ? '分享' : '删除'}的问答组` }</div>
          <div className="message-check-toolbox__tips">点击问答组可多选</div>
        </div>
        <div className="message-check-toolbox-right">
          <div className="message-check-toolbox__num">已选择：{ checkedList.length }组</div>
          <Button style={btnStyle} type="primary" onClick={checkAllClick}>{ checkAll ? '取消全选' : '全选' }</Button>
          <Button onClick={cancle}>取消</Button>
          <Button type={ checkedList.length === 0 ? 'default' : 'primary' } disabled={checkedList.length === 0} onClick={(e) => handleShare(e)}>复制分享链接</Button>
          {/*{ <Button  type={ checkedList.length === 0 ? 'default' : 'primary' } disabled={checkedList.length === 0}>分享到weLink</Button> }*/}
        </div>
      </div>
    </div>
  )}</>
};


export default CheckGroup;
