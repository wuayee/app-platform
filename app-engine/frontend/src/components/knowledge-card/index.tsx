import React, { ReactElement } from 'react';
import { Card } from 'antd';
import type { MenuProps } from 'antd';
import { Button, Dropdown, Space } from 'antd';
import { url } from 'inspector';
import { Icons } from '../icons/index';
import { useNavigate } from 'react-router-dom';
export interface knowledgeBase {
  name: string;
  createdAt: string;
  ownerName: string;
  icon: () => ReactElement;

  description: string;

  id: string;

}

const App = ({knowledge, clickMore}: {knowledge: knowledgeBase, clickMore: (type: 'delete') => void }) => {
  // 路由
  const navigate = useNavigate();
  const operatorItems: MenuProps['items'] = [
  {
    key: 'modify',
    label: <div style={{
      width: 150,
    }}>修改</div>
  },
  {
    key: 'delete',
    label: <div style={{
      width: 150,
    }}>删除</div>
  },
]
const clickItem = (info: any) => {
  clickMore(info.key)
}
  // 跳转至详情
  const jumpDetail = (id: string) => {
    navigate(`/knowledge-base/knowledge-detail?id=${id}`)
  }

  // 格式化时间
  const formateTime = (dateStr: Date)=> {
    const date = new Date(dateStr);
    const y = date.getFullYear();
    const m = date.getMonth() + 1;
    const d = date.getDate();
    const hh = date.getHours();
    const mm = date.getMinutes();
    const ss = date.getSeconds();
    return `${y}.${m}.${d}`;
  }
return (
  <Card style={{ 
    width: 380, 
    background: 'url(/src/assets/images/knowledge/knowledge-background.png)',
    height: 260
   }} onClick={()=> {jumpDetail(knowledge.id)}}>
    {/* 头部区域 */}
    <div style={{
      display: 'flex',
      gap: '16px',
      height: 57,
    }}>
      <div className='iconArea'><knowledge.icon/></div>
      <div className='infoArea'>
        <div className='headerTitle' style={{
          fontSize: 20,
          color: 'rgba(5, 5, 5, .96)'
        }}>
          {knowledge.name}
        </div>
        <div className='ownerName' style={{
          fontSize: 14,
          color: 'rgba(105, 105, 105, .9)'
        }}>
          {`${knowledge.ownerName}创建于${formateTime(knowledge.createdAt as any as Date)}` }
        </div>
      </div>
    </div>

    {/* 描述 */}
    <div className='content' style={{
      wordBreak: 'break-all',
      marginTop: 16,
      fontSize: '14px',
      lineHeight: '22px',
      textAlign: 'justify',
      minHeight: 100
    }}>
      {knowledge.description}
    </div>

    {/* 底部 */}
    <div className='footer' style={{
      display: 'flex',
      justifyContent: 'flex-end',
      marginTop: 16,
    }}>
      <div className='operator'>
        <Dropdown menu={{ items: operatorItems, onClick: (info)=> {clickItem(info); info.domEvent.stopPropagation()} }} placement="bottomLeft" trigger={['click']} >
          <div style={{
            cursor: 'pointer'
          }} onClick={(e)=> {e.stopPropagation()}}>
            <Icons.more width={20} />
          </div>
        </Dropdown>
      </div>
    </div>
  </Card>
);}

export default App;