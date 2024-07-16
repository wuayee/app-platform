import React, { ReactElement } from 'react';
import { Card, Tooltip, Dropdown } from 'antd';
import type { MenuProps } from 'antd';
import { url } from 'inspector';
import { Icons, KnowledgeIcons } from '../icons/index';
import { useNavigate } from 'react-router-dom';

const DetailCard = ({knowledge, clickMore, currentIndex}: {knowledge: string, currentIndex: number, clickMore: (type: 'delete') => void }) => {
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

return (
  <Card style={{ 
    width: 440, 
    background: 'url(/src/assets/images/knowledge/knowledge-background.png)',
    height: 230,
    backgroundRepeat: 'no-repeat',
    backgroundSize: 'cover',
    marginBottom:10
   }} >
    {/* 头部区域 */}
    <div  style={{
      display: 'flex',
      gap: '16px',
      height: 22,
    }}>
      <span style={
        {
          border: '1px solid #e6e6e6',
          borderRadius: 5,
          height: 22,
          width: 'auto',
          padding: '0 4px',
          backgroundColor: '#fff'
        }
      }>#{currentIndex.toString().padStart(3, '0')}</span>

    </div>

    {/* 描述 */}
    <div  style={{
      wordBreak: 'break-all',
      marginTop: 16,
      fontSize: '14px',
      lineHeight: '22px',
      textAlign: 'justify',
      minHeight: 100
    }}>
      <Tooltip placement="top" title={knowledge} overlayStyle={{ minWidth: '25%' }}>
          {knowledge?.substring(0, 100)}{knowledge?.length>=100 ? '...': ''}
      </Tooltip>
    </div>

    {/* 底部 */}
    <div  style={{
      display: 'flex',
      justifyContent: 'space-between',
      marginTop: 16,
    }}>
      <div style={{
        display: 'flex',
        alignItems: 'center'
      }}>
        <KnowledgeIcons.t/> 
        
        <span style={{
          marginLeft: 2,
          fontSize: 12,
          color: '#444'
        }}>
          {`${knowledge.length ?? 0} 字符`}
        </span>
      </div>
      <div >
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

export default DetailCard;
