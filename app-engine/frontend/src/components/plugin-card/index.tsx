import React from 'react';
import { Dropdown, Flex, MenuProps, Tag } from 'antd';
import { EllipsisOutlined, StarOutlined, UserOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router';
import { Icons } from '../icons';
import { IconMap, PluginCardTypeE } from '@/pages/plugin/helper';
import './style.scoped.scss';

const PluginCard = ({ pluginData, cardType }: any) => {
  const navigate = useHistory().push;
  const operatItems: MenuProps['items'] = [
    {
      label: <div>发布</div>,
      key: 'piblish',
    },
    {
      label: <div>编排</div>,
      key: 'choreography',
    },
    {
      label: <div>删除</div>,
      key: 'delete',
    },
  ];
  return (
    <div className='plugin-card'
      onClick={() => { navigate(`/plugin/detail/${pluginData?.uniqueName}`) }}>
      <div className='plugin-card-header'>
        <img src='./src/assets/images/knowledge/knowledge-base.png' />
        <div>
          <div className='plugin-title'>
            <div className='plugin-head'>
              <span className='text plugin-text' title={pluginData?.name}>{pluginData?.name}</span>
            </div>
          </div>
          <div className='plugin-card-user'>
            <Icons.user />
            <span style={{ marginRight: 8 }}>{pluginData?.creator}</span>
            {pluginData?.tags?.map((tag: string, index: number) => <Tag style={{ margin: 0 }} key={index}>{tag}</Tag>)}
          </div>
        </div>
      </div>
      <div className='card-content'>
        {pluginData?.description}
      </div>
      {/* 卡片底部 */}
      <div className='card-footer'>
        <div hidden>
          <Flex gap={14}>
            <span hidden={cardType === PluginCardTypeE.MARKET}>
              <Tag className='footer-type'>Tag 1</Tag>
            </span>
            <span>
              <UserOutlined style={{ marginRight: 8 }} />
              {pluginData?.downloadCount}
            </span>
            <span>
              <StarOutlined style={{ marginRight: 8 }} />
              {pluginData?.likeCount}
            </span>
          </Flex>
        </div>
        <div hidden={cardType !== PluginCardTypeE.MARKET}>
          <Flex style={{ display: 'flex', alignItems: 'center' }} gap={4} >
            {IconMap[pluginData?.source?.toUpperCase()]?.icon}
            <span style={{ fontSize: 12, fontWeight: 700 }}>{IconMap[pluginData?.source?.toUpperCase()]?.name}</span>
          </Flex>
        </div>
        <div hidden onClick={(e) => { e.stopPropagation(); }}>
          <Dropdown menu={{ items: operatItems }} trigger={['click']}>
            <EllipsisOutlined className='footer-more' />
          </Dropdown>
        </div>
      </div>
    </div >
  )
}

export default PluginCard;
