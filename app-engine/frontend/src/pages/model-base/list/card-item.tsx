import React from 'react';
import { Card, Dropdown, Tag } from 'antd';
import type { MenuProps } from 'antd';
import { EllipsisOutlined } from '@ant-design/icons';

const CardItem = ({ data }: any) => {

  const items: MenuProps['items'] = [
    {
      key: '1',
      label: (
        <a>
          删除
        </a>
      ),
    },
  ];

  return (
    <Card
      style={{
        width: 376,
        background: 'url(/src/assets/images/knowledge/knowledge-background.png)',
        height: 240,
      }}
    >
      <div style={{
        width: '100%',
        display: 'flex',
        justifyContent: 'space-between'
      }}>
        <div style={{ fontSize: 20 }}>
          {data.name}
        </div>
        <div>
          版本数：{data.versionNum}
        </div>
      </div>
      <div style={{
        display: 'flex',
        gap: 4,
        alignItems: 'center',
        flexWrap: 'wrap',
        marginTop: 16,
      }}>
        <span style={{ marginRight: 8 }}>{data.creator}</span>
        {data.tags.map((tag: string) => <Tag style={{ margin: 0 }}>{tag}</Tag>)}
      </div>
      <div
        title={data.description}
        style={{
          display: '-webkit-box',
          wordBreak: 'break-all',
          textOverflow: 'ellipsis',
          overflow: 'hidden',
          WebkitLineClamp: 3,
          WebkitBoxOrient: 'vertical',
          lineHeight: '22px',
          textAlign: 'justify',
          marginTop: 16,
          height: 66,
        }}>
        {data.description}
      </div>
      <div style={{
        padding: '12px 0',
        display: 'flex',
        borderTop: '1px solid #ebebeb',
        color: '#808080',
        alignItems: 'center',
        justifyContent: 'end',
        marginTop: 16,
      }}>
        <Dropdown menu={{ items }} trigger={['click']}>
          <EllipsisOutlined style={{ fontSize: 24, cursor: 'pointer' }} />
        </Dropdown>
      </div>
    </Card>
  )
}

export default CardItem;
