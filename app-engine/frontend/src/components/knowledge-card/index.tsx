import React, { ReactElement } from 'react';
import { Card } from 'antd';
import type { MenuProps } from 'antd';
import { Button, Dropdown, Space } from 'antd';
import { url } from 'inspector';
import { Icons } from '../icons/index';
export interface knowledgeBase {
  name: string;
  createDate: string;
  createBy: string;
  icon: () => ReactElement;

  desc: string;

  id: string;
}

const App = ({knowledge}: {knowledge: knowledgeBase }) => {
const operatorItems: MenuProps['items'] = [
  {
    key: 'delete',
    label: <div style={{
      width: 200,
    }}>删除</div>
  }
]
const clickItem = (info: any) => {
  console.log(info);
}
return (
  <Card style={{ 
    width: 380, 
    background: 'url(/src/assets/images/knowledge/knowledge-background.png)',
    height: 260
   }} >
    {/* 头部区域 */}
    <div className='header' style={{
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
        <div className='createBy' style={{
          fontSize: 14,
          color: 'rgba(105, 105, 105, .9)'
        }}>
          {`${knowledge.createBy}创建于${knowledge.createDate}` }
        </div>
      </div>
    </div>

    {/* 描述 */}
    <div className='content' style={{
      wordBreak: 'break-all',
      marginTop: 16,
      fontSize: '14px',
      lineHeight: '22px',
      textAlign: 'justify'
    }}>
      {knowledge.desc}
    </div>

    {/* 底部 */}
    <div className='footer' style={{
      display: 'flex',
      justifyContent: 'flex-end',
      marginTop: 16,
    }}>
      <div className='operator'>
        <Dropdown menu={{ items: operatorItems, onClick: (info)=> {clickItem(info)} }} placement="bottomLeft" trigger={['click']} >
          <div style={{
            cursor: 'pointer'
          }}>
            <Icons.more width={20} />
          </div>
        </Dropdown>
      </div>
    </div>
  </Card>
);}

export default App;