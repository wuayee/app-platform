import React, { forwardRef, useContext, useImperativeHandle, useState, useRef, useCallback, useEffect } from 'react';
import { Tooltip } from 'antd';
import { toClipboard } from '@shared/utils/common';
import { ChatContext } from '../../../aippIndex/context';
import { CopyIcon, PlayIcon, StopIcon } from '@/assets/icon';
import {textToVoice} from '@shared/http/aipp';
import './styles/send-btn.scss'


const SoundBtn = forwardRef((props, ref) => {
  const [active, setActive] = useState(props.active || false);
  const [url, setUrl] = useState(props.url || '');
  useImperativeHandle(ref, () => {
    return {
      active,
      setActive,
      url,
      setUrl,
    }
  })
  function defaultHandleClick() {
    setActive(!active)
  }
  return <>
    <Tooltip title={ active? '停止' : '播放' } color='white' overlayInnerStyle={{color: '#212121' }}>
      <div onClick={props.handleClick || defaultHandleClick}>
        {active? <StopIcon /> : <PlayIcon className='hover-blue-icon'/>} 
      </div>
    </Tooltip>
    {url && <audio></audio>}
  </>
})


const SendBtn = (props) => {
  const { content, sendType,isRecieve } = props;
  const { setShareClass }  = useContext(ChatContext);
  const soundBtnRef = useRef(null);

  // 复制
  function handleCopyQuestion() {
    content && toClipboard(content);
  }
  useEffect(()=>{
    return ()=>{
      if(audioElement){
      audioElement=null
      audioElement.removeEventListener('ended',audioEnded)
      }
    }
  },[])
  let audioElement = null
  // 即将播放的音频数据下标
  let audioIndex=0
  // 一段音频数据播放结束时调用
  function audioEnded(){
    console.log('结束了');
    if(audioIndex<base64Arr.current.length){
      audioElement.src = `data:audio/wav;base64,${base64Arr.current[audioIndex]}`
      audioElement.play()
      audioIndex++
    }else{
      isPlaying.value = false
      soundBtnRef.current.setActive(false);
      // 如果全部播放完了，就重新开始
      if(audioIndex>fragmentArr.length-1){
        audioIndex=1
        audioElement.src = `data:audio/wav;base64,${base64Arr.current[0]}`
      }
    }
  }
  // 获取的音频数据
  let base64Arr=useRef([])
  // 是否正在播放
  let isPlaying=useRef(false)
  // 小段文字容器
  let fragmentArr=[]
  // 文字转语音函数
  function getVioce(){
    fragmentArr=[]
    base64Arr.current=[]
    if(!content) return
    let index=0

    // 按语句分成小段文字
      let arr=content.split(/[,.!?:;。，？；：！]/)
      console.log(arr);
      for(let i=0;i<arr.length;i++){
        if(arr[i]){fragmentArr.push(arr[i])}
        
      }
    // 异步请求
    async function ContinuousRequests(){
      // 如果数据走完了就停止
        // 发起请求
        let res = await textToVoice(fragmentArr[index++],0)
        // 如果没有返回数据，就停止
        if(!res.data)  return
        base64Arr.current.push(res.data)
        if (!audioElement&&soundBtnRef.current.active){
          // 如果是首次拿到音频数据，且当前按钮处于播放中
          audioIndex++
          audioElement = document.createElement('audio')
          audioElement.controls=true
          audioElement.src = `data:audio/wav;base64,${res.data}`
          audioElement.style.display='none'
          audioElement.addEventListener('ended',audioEnded)
          audioElement.play()
          isPlaying.value = true
          soundBtnRef.current.setActive(true);
        }else if(!audioElement&&!soundBtnRef.current.active){
          // 如果是首次拿到音频数据，且用户点击了暂停，保存数据，不用播放
          audioElement = document.createElement('audio')
          audioElement.controls=true
          audioElement.src = `data:audio/wav;base64,${res.data}`
          audioElement.style.display='none'
          audioElement.addEventListener('ended',audioEnded)
        }else if(audioIndex<fragmentArr.length&&!soundBtnRef.current.active){
          // 如果用户之前数据已经播放完毕，但还有数据没回来，等数据回来时就自动播放
          audioIndex++
          audioElement.src = `data:audio/wav;base64,${res.data}`
          audioElement.play()
          isPlaying.value = true
          soundBtnRef.current.setActive(true);
        }
        // else if(soundBtnRef.current.active&&audioElement){
        //   // 如果当前没有处于播放中，直接追加数据即可不用操作
        // }

        // 连续请求所有数据
        if(index<fragmentArr.length) {
          id = setTimeout(()=>{
            ContinuousRequests()
          })
        }
      }
    // 请求语音转文字接口
    ContinuousRequests()
  }
  // 点击播放/暂停
  function handlePlayQuestion() {
    if(!soundBtnRef.current.active&&!audioElement){
      getVioce()
    }else if(!soundBtnRef.current.active&&audioElement){
      audioElement.play()
    }else if(soundBtnRef.current.active&&audioElement){
      audioElement.pause()
    }
    // 播放语音
    soundBtnRef.current.setActive(!soundBtnRef.current.active);
  }
  // tooltip隐藏
  function hideTooltip() {
    const tooltip = document.querySelectorAll('.ant-tooltip-placement-top');
    if (tooltip && tooltip.length) {
      tooltip.forEach(item => {
        item.classList.add('ant-tooltip-hidden')
      });
    }
    setShareClass();
  }
  return <>{(
    <div className='message-tip-box-send'>
      <div className='inner'>
        {/* <Tooltip title='分享' color='white' overlayInnerStyle={{color: '#212121' }} destroyTooltipOnHide>
          <div onClick={ hideTooltip }>
            <ShareIcon />
          </div>
        </Tooltip> */}
        {  sendType === 'text' && 
        <Tooltip title='复制' color='white' overlayInnerStyle={{color: '#212121' }}>
          <div onClick={ handleCopyQuestion }>
            <CopyIcon className='hover-blue-icon'/>
          </div> 
        </Tooltip>
        }
        {  sendType === 'text' && isRecieve === true &&
          <SoundBtn ref={soundBtnRef} handleClick={ useCallback(handlePlayQuestion) }/>
        }
      </div>
    </div>
  )}</>
};

export default SendBtn;
