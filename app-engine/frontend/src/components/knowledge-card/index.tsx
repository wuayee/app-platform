import React from 'react';
import type { ReactElement } from 'react';
import { Dropdown } from 'antd';
import type { MenuProps } from 'antd';
import { useHistory } from 'react-router-dom';
import { Icons } from '../icons/index';
import './index.scoped.scss';
import { useTranslation } from 'react-i18next';

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
  const { t } = useTranslation();
  // 路由
  const navigate = useHistory().push;
  const operatorItems: MenuProps['items'] = [
    {
      key: 'modify',
      label: t('modify'),
    },
    {
      key: 'delete',
      label: t('delete'),
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
    return `${y}.${m}.${d}`;
  };
  return (
    <div className='knowledge-card' onClick={() => { jumpDetail(knowledge.id) }}>
      <div className='card-head'>
        <span className='card-icon'>
          <knowledge.icon />
        </span>
        <div className='card-title'>
          <div className='card-name'>{knowledge.name}</div>
          <div className='card-create'>
            {`${knowledge.ownerName} ${t('createAt')}${formateTime(knowledge.createdAt as any as Date)}`}
          </div>
        </div>
      </div>
      <div className='card-desc'>
        {knowledge.description}
      </div>
      <div className='card-footer'>
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
    </div>
  );
};

export default App;
