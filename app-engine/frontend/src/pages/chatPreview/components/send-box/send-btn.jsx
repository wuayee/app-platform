import React, { forwardRef, useContext, useImperativeHandle, useState, useRef, useCallback, useEffect } from 'react';
import { Tooltip } from "antd";
import { toClipboard } from '@shared/utils/common';
import { ChatContext } from '../../../aippIndex/context';
import { ShareIcon, CopyIcon, DeleteIcon, PlayIcon, StopIcon } from '@/assets/icon';
import './styles/send-btn.scss'
import {textToVoice} from '@shared/http/aipp.js'

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
    <Tooltip title={ active? '停止' : '播放' } color="white" overlayInnerStyle={{color: '#212121' }}>
      <div onClick={props.handleClick || defaultHandleClick}>
        {active? <StopIcon /> : <PlayIcon className='hover-blue-icon'/>} 
      </div>
    </Tooltip>

    {url && <audio>

    </audio>}
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
    // console.log(base64Arr,'base64Arr');
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
  // 是否全部播放完毕
  let isPlaycompleted=useRef(false)
  // 小段文字容器
  let fragmentArr=[]
  // 合并所有音频后的base64字符串
  let allAudioBase64 = useRef('')
  // 文字转语音函数（数据回来了就调用）
  function getVioce(){
    let index=0

    // 按语句分成小段文字
      let arr=content.split(/[,.!?:;。，？；：！]/)
      console.log(arr);
      for(let i=0;i<arr.length;i++){
        if(arr[i]){fragmentArr.push(arr[i])}
        
      }
      console.log(fragmentArr);
    
    
    let id
    console.log(fragmentArr,'fragmentArr');
    // 异步请求
    async function fn(){
      console.log(index);

      // 如果数据走完了就停止
        
        console.log(fragmentArr[index]);
        // 发起请求
        let res = await textToVoice(fragmentArr[index++],0)
        // 如果没有返回数据，就停止
        if(!res.data)  return
        
        base64Arr.current=[...base64Arr.current,res.data]
        // 如果当前没有音频播放，就重新开始播放
        if(!isPlaying.value){
          audioIndex++
          if(audioElement){
            audioElement.removeEventListener('ended',audioEnded)
            audioElement=null
          }
          audioElement = document.createElement('audio')
          audioElement.controls=true
          audioElement.src = `data:audio/wav;base64,${res.data}`
          audioElement.style.display='none'
          audioElement.addEventListener('ended',audioEnded)
          audioElement.play()
          isPlaying.value = true
          soundBtnRef.current.setActive(true);
        }
        if(index<fragmentArr.length) {
          id = setTimeout(()=>{
            fn()
          })
        }
      }
    // 定时请求语音转文字接口
    fn()
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
        {/* <Tooltip title="分享" color="white" overlayInnerStyle={{color: '#212121' }} destroyTooltipOnHide>
          <div onClick={ hideTooltip }>
            <ShareIcon />
          </div>
        </Tooltip> */}
        {  sendType === 'text' && 
        <Tooltip title="复制" color="white" overlayInnerStyle={{color: '#212121' }}>
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
