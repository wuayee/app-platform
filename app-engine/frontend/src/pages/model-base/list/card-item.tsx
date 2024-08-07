import React from 'react';
import { Card, Dropdown, Flex, Tag } from 'antd';
import type { MenuProps } from 'antd';
import { EllipsisOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router';
import { deleteModel } from './delete';
import { AvatarIcon } from '../../../assets/icon';

const CardItem = ({ data, deleteCallback }: any) => {
  
  const navigate = useHistory().push;
  const operatorItems: MenuProps['items'] = [
    {
      key: '1',
      label: (
        <a onClick={() => deleteModel(data, deleteCallback)}>
          删除
        </a>
      ),
    },
  ];

  const gotoDetail = () => {
    navigate(`/model-base/${data.model_name}/detail`);
  }

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
        <div
          title={data.model_name}
          style={{
            fontSize: 20,
            cursor: 'pointer',
            maxWidth: 250,
            textOverflow: 'ellipsis',
            overflow: 'hidden',
          }}
          onClick={gotoDetail}>
          {data.model_name}
        </div>
        <div>
          版本数：{data.version_num}
        </div>
      </div>
      <div style={{
        display: 'flex',
        gap: 4,
        alignItems: 'center',
        flexWrap: 'wrap',
        marginTop: 16,
      }}>
        <Flex gap='small' align='center'>
          <AvatarIcon />
          <span
            title={data.author}
            style={{
              marginRight: 8,
              textOverflow: 'ellipsis',
              overflow: 'hidden',
              maxWidth: 80,
            }}>{data.author}</span>
        </Flex>
        <Tag
          title={data.model_type}
          style={{
            margin: 0,
            maxWidth: 80,
            textOverflow: 'ellipsis',
            overflow: 'hidden'
          }}>{data.model_type}</Tag>
        <Tag
          title={data.series_name}
          style={{
            margin: 0,
            maxWidth: 80,
            textOverflow: 'ellipsis',
            overflow: 'hidden'
          }}>{data.series_name}</Tag>
      </div>
      <div
        title={data.model_description}
        style={{
          display: '-webkit-box',
          textOverflow: 'ellipsis',
          overflow: 'hidden',
          WebkitLineClamp: 3,
          WebkitBoxOrient: 'vertical',
          lineHeight: '22px',
          textAlign: 'justify',
          marginTop: 16,
          height: 66,
        }}>
        {data.model_description}
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
        <Dropdown menu={{ items: operatorItems }} trigger={['click']}>
          <EllipsisOutlined style={{ fontSize: 24, cursor: 'pointer' }} />
        </Dropdown>
      </div>
    </Card >
  )
}

export default CardItem;
