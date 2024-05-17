import React, { useState, useEffect, ReactElement } from 'react';
import { Button, Input }from 'antd';
import { HashRouter, Route, useNavigate, Routes } from 'react-router-dom';

import Pagination from '../../components/pagination/index';
import { Icons } from '../../components/icons';
import KnowledgeCard, { knowledgeBase } from '../../components/knowledge-card';
const KnowledgeBaseCreate = () => {

  // 路由
  const navigate = useNavigate();

  // 总条数
  const [total, setTotal] = useState(100);

  // 数据
  const [knowledgeData, setKnowledgeData] = useState<knowledgeBase[]>([
    {
      name: 'testName',
      createDate: '2024-05-17',
      createBy: 'hzw_test',
      icon: ()=> (<>
        <img src='/src/assets/images/knowledge/knowledge-base.png'/>
      </>),
      desc: '管理储存数据，抽取1111dtydtyuftytbyusdcftbuyiyhudfgyhubdfrgbhuidfcgbyuierfdgbyuieyhugierdfhujierhujriefgheiryujdfwgyhuedfirwedrfgbhiuyedrfgyhiugyhu',
      id: 'etgyjdvghsfvgyh'
    },
    {
      name: 'testName',
      createDate: '2024-05-17',
      createBy: 'hzw_test',
      icon: ()=> (<>
        <img src='/src/assets/images/knowledge/knowledge-base.png'/>
      </>),
      desc: '管理储存数据，抽取1111dtydtyuftytbyusdcftbuyiyhudfgyhubdfrgbhuidfcgbyuierfdgbyuieyhugierdfhujierhujriefgheiryujdfwgyhuedfirwedrfgbhiuyedrfgyhiugyhu',
      id: 'etgyjdvghsfvgyh'
    },
    {
      name: 'testName',
      createDate: '2024-05-17',
      createBy: 'hzw_test',
      icon: ()=> (<>
        <img src='/src/assets/images/knowledge/knowledge-base.png'/>
      </>),
      desc: '管理储存数据，抽取1111dtydtyuftytbyusdcftbuyiyhudfgyhubdfrgbhuidfcgbyuierfdgbyuieyhugierdfhujierhujriefgheiryujdfwgyhuedfirwedrfgbhiuyedrfgyhiugyhu',
      id: 'etgyjdvghsfvgyh'
    },
    {
      name: 'testName',
      createDate: '2024-05-17',
      createBy: 'hzw_test',
      icon: ()=> (<>
        <img src='/src/assets/images/knowledge/knowledge-base.png'/>
      </>),
      desc: '管理储存数据，抽取1111dtydtyuftytbyusdcftbuyiyhudfgyhubdfrgbhuidfcgbyuierfdgbyuieyhugierdfhujierhujriefgheiryujdfwgyhuedfirwedrfgbhiuyedrfgyhiugyhu',
      id: 'etgyjdvghsfvgyh'
    },
    {
      name: 'testName',
      createDate: '2024-05-17',
      createBy: 'hzw_test',
      icon: ()=> (<>
        <img src='/src/assets/images/knowledge/knowledge-base.png'/>
      </>),
      desc: '管理储存数据，抽取1111dtydtyuftytbyusdcftbuyiyhudfgyhubdfrgbhuidfcgbyuierfdgbyuieyhugierdfhujierhujriefgheiryujdfwgyhuedfirwedrfgbhiuyedrfgyhiugyhu',
      id: 'etgyjdvghsfvgyh'
    },
    {
      name: 'testName',
      createDate: '2024-05-17',
      createBy: 'hzw_test',
      icon: ()=> (<>
        <img src='/src/assets/images/knowledge/knowledge-base.png'/>
      </>),
      desc: '管理储存数据，抽取1111dtydtyuftytbyusdcftbuyiyhudfgyhubdfrgbhuidfcgbyuierfdgbyuieyhugierdfhujierhujriefgheiryujdfwgyhuedfirwedrfgbhiuyedrfgyhiugyhu',
      id: 'etgyjdvghsfvgyh'
    },
    {
      name: 'testName',
      createDate: '2024-05-17',
      createBy: 'hzw_test',
      icon: ()=> (<>
        <img src='/src/assets/images/knowledge/knowledge-base.png'/>
      </>),
      desc: '管理储存数据，抽取1111dtydtyuftytbyusdcftbuyiyhudfgyhubdfrgbhuidfcgbyuierfdgbyuieyhugierdfhujierhujriefgheiryujdfwgyhuedfirwedrfgbhiuyedrfgyhiugyhu',
      id: 'etgyjdvghsfvgyh'
    },
    {
      name: 'testName',
      createDate: '2024-05-17',
      createBy: 'hzw_test',
      icon: ()=> (<>
        <img src='/src/assets/images/knowledge/knowledge-base.png'/>
      </>),
      desc: '管理储存数据，抽取1111dtydtyuftytbyusdcftbuyiyhudfgyhubdfrgbhuidfcgbyuierfdgbyuieyhugierdfhujierhujriefgheiryujdfwgyhuedfirwedrfgbhiuyedrfgyhiugyhu',
      id: 'etgyjdvghsfvgyh'
    }
  ])

  // 创建知识库
  const createKnowledge = () => {
    navigate('/knowledge-base/create')
  }

  useEffect(()=> {
    const index = 1;
    setInterval(()=> {
      setTotal(Math.floor(Math.random() * 1000))
    }, 1000)
  }, [])
  return (
    <>
      12324
    </>
  )
}
export default KnowledgeBaseCreate;