import type { ReactElement } from 'react';
import React from 'react';
import { Card, Button, Dropdown, Space } from 'antd';
import type { MenuProps } from 'antd';
import { url } from 'inspector';
import { useNavigate } from 'react-router-dom';
import { Icons } from '../icons/index';

export interface knowledgeBase {
  name: string;
  createdAt: string;
  ownerName: string;
  icon: () => ReactElement;
  description: string;
  id: string;
}

const App = ({
  knowledge,
  clickMore,
}: {
  knowledge: knowledgeBase;
  clickMore: (type: 'delete') => void;
}) => {
  // 路由
  const navigate = useNavigate();
  const operatorItems: MenuProps['items'] = [
    {
      key: 'modify',
      label: '修改',
    },
    {
      key: 'delete',
      label:'删除',
    },
  ];
  const clickItem = (info: any) => {
    clickMore(info.key);
  };
  // 跳转至详情
  const jumpDetail = (id: string) => {
    navigate(`/knowledge-base/knowledge-detail?id=${id}`);
  };

  // 格式化时间
  const formateTime = (dateStr: Date) => {
    if (!dateStr) {
      return '';
    }
    const date = new Date(dateStr);
    const y = date.getFullYear();
    const m = date.getMonth() + 1;
    const d = date.getDate();
    const hh = date.getHours();
    const mm = date.getMinutes();
    const ss = date.getSeconds();
    return `${y}.${m}.${d}`;
  };
  return (
    <Card
      style={{
        background: 'url(/src/assets/images/knowledge/knowledge-background.png)',
        backgroundRepeat:'no-repeat',
        backgroundSize:'cover',
        height: 280,
        marginBottom:14,
      }}
      onClick={() => {
        jumpDetail(knowledge.id);
      }}
    >
      {/* 头部区域 */}
        <div>
          <div style={{display:'flex'}}>
            <span style={{width:48}}>
            <knowledge.icon/>
            </span>
            <span style={{fontSize:'20px',marginLeft:8}}>{knowledge.name}</span>
          </div>
          <div
            style={{
              fontSize: 14,
              color: 'rgba(105, 105, 105, .9)',
            }}
          >
            {`${knowledge.ownerName}创建于${formateTime(knowledge.createdAt as any as Date)}`}
          </div>
        </div>

      {/* 描述 */}
      <div
        style={{
          wordBreak: 'break-all',
          marginTop: 16,
          fontSize: '14px',
          lineHeight: '22px',
          textAlign: 'justify',
          height: 120,
          overflowY: 'auto',
        }}
      >
        {knowledge.description}
      </div>

      {/* 底部 */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'flex-end',
          marginTop:8,
        }}
      >
        <div>
          <Dropdown
            menu={{
              items: operatorItems,
              onClick: (info) => {
                clickItem(info);
                info.domEvent.stopPropagation();
              },
            }}
            placement='bottomLeft'
            trigger={['click']}
          >
            <div
              style={{
                cursor: 'pointer',
              }}
              onClick={(e) => {
                e.stopPropagation();
              }}
            >
              <Icons.more width={20} />
            </div>
          </Dropdown>
        </div>
      </div>
    </Card>
  );
};

export default App;
