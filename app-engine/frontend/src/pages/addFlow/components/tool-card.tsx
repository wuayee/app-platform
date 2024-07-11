import React from 'react';
import { Flex, Tag } from 'antd';
import { Icons } from '../../../components/icons';
import { StarOutlined, UserOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router';
import '../styles/tool-card.scss';
import { useAppSelector } from '../../../store/hook';
import { getAppInfoByVersion } from '../../../shared/http/aipp';

const ToolCard = ({ pluginData, tenantId }: any) => {
  const navigate = useNavigate();
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const appId = useAppSelector((state) => state.appStore.appId);

  // 类型处理
  const detailClick = async() => {
   if (pluginData.tags.includes('WATERFLOW')) {
    const res = await getAppInfoByVersion(tenantId,pluginData?.runnables?.APP?.appId);
    navigate(`/app-develop/${tenantId}/app-detail/add-flow/${res?.data?.id}`);
   } else {
    navigate(`/plugin/detail/${pluginData.uniqueName}`)
   }
  }
  return(
  <div className='plugin-card'>
    <div className='plugin-card-header'>
      {
        pluginData.tags.includes('HUGGINGFACE') ? 
          <img src={`/src/assets/images/ai/${2}.png`} /> : 
          <img src='/src/assets/images/knowledge/knowledge-base.png' />
      }
      
      <div>
        <div style={{ display: 'flex' }}>
          <div className="tool-name">
            <span>{pluginData.name}</span> 
            {  pluginData.tags.includes('WATERFLOW') || pluginData.tags.includes('HUGGINGFACE') ? 
              <img src='/src/assets/images/ai/workflow.png' alt='' /> : 
              <img src='/src/assets/images/ai/application.png' alt='' />}
              <span hidden={!pluginData?.version}><Tag className='version' bordered={false} >V{pluginData?.version}</Tag></span>
          </div>
        </div>
        <div className='plugin-card-user'>
          <Icons.user />
          <span style={{ marginRight: 8 }}>{pluginData.creator}</span>
          {pluginData.tags.map((tag: string, index: number) => <Tag style={{ margin: 0 }} bordered={false} key={index}>{tag}</Tag>)}
        </div>
      </div>
    </div>
    <div className='card-content'>
      {pluginData.description === 'null' ? '暂无描述' : pluginData.description}
    </div>
    {/* 卡片底部 */}
    <div className='card-footer'>
      <Flex gap={16}>
        <span>
          <UserOutlined style={{ marginRight: 8 }} />
          {pluginData.downloadCount}
        </span>
        <span>
          <StarOutlined style={{ marginRight: 8 }} />
          {pluginData.likeCount}
        </span>
      </Flex>
    </div>
    <div className="card-detail" onClick={detailClick}>
      查看详情
    </div>
  </div >
)}

export default ToolCard;
