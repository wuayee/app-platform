import React from 'react';
import { Flex, Tag } from 'antd';
import { Icons } from '../icons';
import { PluginIcons } from '../icons/plugin';
import { StarOutlined, UserOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router';
import './style.scoped.scss';

const PluginCard = ({ pluginData }: any) => {
  const navigate = useNavigate();
  return(
  <div className='plugin-card'
   onClick={()=>{navigate(`/plugin/detail/${pluginData.uniqueName}`)}}>
    <div className='plugin-card-header'>
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
        <div className='plugin-card-user'>
          <Icons.user />
          <span style={{ marginRight: 8 }}>{pluginData.creator}</span>
          {pluginData.tags.map((tag: string, index: number) => <Tag style={{ margin: 0 }} key={index}>{tag}</Tag>)}
        </div>
      </div>
    </div>
    <div className='card-content'>
      {pluginData.description}
    </div>
    {/* 卡片底部 */}
    <div className='card-footer'>
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
)}

export default PluginCard;
