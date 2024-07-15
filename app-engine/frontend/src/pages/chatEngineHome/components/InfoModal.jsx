import React, { useState, useEffect } from 'react';
import Modal from 'antd/es/modal/Modal';
import { Button } from 'antd';
import styles from './infomodal.module.css'
import { NewFeatIcon, RocketIcon, FixIcon } from '@assets/icon'
import { getAnnouncement } from '@shared/http/apps'
import { Spin } from 'antd';

const InfoModal = () => {
  const [showModal, setShowModal] = useState(false)
  const [spinning, setSpining] = useState(true)
  const [lastAnnounceTime, setLastAnnounceTime] = useState(localStorage.getItem('lastAnnounceTime') || '2024-06-19 00:00:00')
  const [infoList, setInfoList] = useState([])

  useEffect(() => {
    const fetchData = async () => {
      const res = await getAnnouncement();
      setLastAnnounceTime(res.latestCreateTime)
      handleData(res.announcementsByType)
      const timeNow = Date.parse(res.latestCreateTime)
      const timeOld = Date.parse(lastAnnounceTime || res.latestCreateTime)
      if(timeNow > timeOld){
        setShowModal(true)
        localStorage.setItem('lastAnnounceTime', res.latestCreateTime)
      }
      setSpining(false)
    }
    fetchData()
  }, [])

  // 将后端数据转换为可展示的数组
  const handleData = (data) => {
    const res = []
    const mapper = {
      "新功能": {
        id: 1,
        icon: <NewFeatIcon/>,
      },
      "优化": {
        id: 2,
        icon: <RocketIcon/>,
      },
      "修复": {
        id: 3,
        icon: <FixIcon/>,
      },
    }
    console.log(data);
    for(const key of Object.keys(data)){
      const item = {
        icon: mapper[key].icon,
        title: key,
        id: mapper[key].id,
        desc: <div>{
            data[key].map((a, idx) => <p key={idx}>{`${idx + 1}. ${a.content}`}</p>)
        }</div>,
      }
      res.push(item)
    }
    res.sort((a, b) => a.id - b.id)
    setInfoList(res)
  }

  const footer = () => <div className={styles.center} style={{width: '100%'}}>
    <Button onClick={() => setShowModal(false)}>
        我知道了
    </Button>
  </div> 

  return <Modal loading={true} title={`更新日志`} open={showModal} footer={spinning ? null : footer} keyboard className={styles.modal} onCancel={() => setShowModal(false)}>
      <div>{spinning ? '' : lastAnnounceTime.split(" ")[0]}</div>
      <hr className={styles.updateDivLine}/>
      <div className={spinning ? styles.contentSpin : styles.updateContent}>
      { 
        spinning ? <Spin spinning={spinning}/> :
          infoList.map(item => <div className={styles.updateItem} key={item.title}>
            <div className={styles.itemTitle}>{item.icon}{item.title}</div>
            <div className={styles.desc}>
              {item.desc}
            </div>
          </div>)
      }
      </div>
    </Modal>

}

export default InfoModal;