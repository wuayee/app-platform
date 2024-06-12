import React from 'react';
import { Flex, Tag } from 'antd';
import { Icons } from '../icons';
import { PluginIcons } from '../icons/plugin';
import { StarOutlined, UserOutlined } from '@ant-design/icons';

const PluginCard = ({ pluginData }: any) => (
  <div style={{
    padding: '24px 24px 0',
    width: 380,
    height: 260,
    background: 'url(/src/assets/images/knowledge/knowledge-background.png)',
    backgroundPosition: 'center',
    display: 'flex',
    flexDirection: 'column',
    gap: 16,
    border: '1px solid #ebebeb',
    borderRadius: 8
  }}>
    <div style={{
      display: 'flex',
      gap: 16,
      alignItems: 'flex-start'
    }}>
      <img src='/src/assets/images/knowledge/knowledge-base.png' />
      <div>
        <div style={{ display: 'flex' }}>
          <div style={{ fontSize: 20, marginBottom: 8 }}>
            {pluginData.name}
          </div>
          <div hidden={!pluginData.tags.includes('workflow')} style={{ alignContent: 'center', marginLeft: '12px' }}>
            <PluginIcons.ButterFlydate />
          </div>
        </div>
        <div style={{
          display: 'flex',
          gap: 4,
          alignItems: 'center',
          flexWrap: 'wrap'
        }}>
          <Icons.user />
          <span style={{ marginRight: 8 }}>{pluginData.creator}</span>
          {pluginData.tags.map((tag: string, index: number) => <Tag style={{ margin: 0 }} key={index}>{tag}</Tag>)}
        </div>
      </div>
    </div>
    <div className='content' style={{
      wordBreak: 'break-all',
      lineHeight: '22px',
      textAlign: 'justify',
      height: '100%',
      overflowY: 'auto'
    }}>
      {pluginData.description}
    </div>
    {/* 卡片底部 */}
    <div style={{
      padding: '16px 0',
      display: 'flex',
      justifyContent: 'space-between',
      gap: 32,
      borderTop: '1px solid #ebebeb',
      color: '#808080',
      alignItems: 'center'
    }}>
      <Flex gap={16}>
        <span>
          <UserOutlined style={{ marginRight: 8 }} />
          2.36k
        </span>
        <span>
          <StarOutlined style={{ marginRight: 8 }} />
          126
        </span>
      </Flex>
      <Flex style={{ display: 'flex', alignItems: 'center' }} gap={4}>
        <PluginIcons.HuggingFaceIcon />
        <span style={{ fontSize: 12, fontWeight: 700 }}>HuggingFace</span>
      </Flex>
    </div>
  </div >
)

export default PluginCard;
