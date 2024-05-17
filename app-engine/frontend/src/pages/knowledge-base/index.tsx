import React, { useState, useEffect, ReactElement } from 'react';
import { Button, Input }from 'antd';
import { HashRouter, Route, useNavigate, Routes } from 'react-router-dom';

import Pagination from '../../components/pagination/index';
import { Icons } from '../../components/icons';
import KnowledgeCard, { knowledgeBase } from '../../components/knowledge-card';
import '../../index.scss'
const KnowledgeBase = () => {

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
    <div className='aui-fullpage'>
    <div className='aui-header-1'>
      <div className='aui-title-1'>header</div>
    </div>
    <div className='aui-block'>
        <div className='operatorArea' style={{
          display: 'flex',
          gap: '16px'
        }}>
          <Button type="primary" style={{
            background: '#2673E5',
            width: '96px',
            height: '32px',
            fontSize: '14px',
            borderRadius: '4px',
            letterSpacing: '0',
          }} onClick={createKnowledge}>创建</Button>
          <Input placeholder="搜索"  style={{
            width: '200px',
            borderRadius: '4px',
            border: '1px solid rgb(230, 230, 230)',
          }} prefix={<Icons.search color = {'rgb(230, 230, 230)'}/>}/>

        </div>
        <div className='containerArea' style={{
          width: '100%',
          minHeight: '800px',
          maxHeight: 'calc(100% - 200px)',
          boxSizing: 'border-box',
          paddingTop: '20px',
          paddingBottom: '20px',
          display:'flex',
          gap: '17px',
          flexWrap: 'wrap'
        }}>
            {knowledgeData.map(knowledge=> (<>
              <KnowledgeCard key={knowledge.id} knowledge={knowledge} style={{
                flex: '0'
              }}/>
            </>))}

        </div>
        <Pagination total = {total}/>
    </div>
  </div>
     
  )
}
export default KnowledgeBase;