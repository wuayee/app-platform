import React from 'react';
import { Dropdown, Flex, MenuProps, Tag } from 'antd';
import { EllipsisOutlined, StarOutlined, UserOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router';
import { Icons } from '../icons';
import { useAppSelector } from '@/store/hook';
import { getAppInfoByVersion } from '@/shared/http/aipp';
import './style.scoped.scss';

const WorkflowCard = ({ pluginData }: any) => {
  const navigate = useNavigate();
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const operatItems: MenuProps['items'] = [
    {
      label: <div>编排</div>,
      key: 'choreography',
    },
  ];
  return(
  <div className='plugin-card'
   onClick={async()=>{
    let id=pluginData?.id;
    if(pluginData?.state==='active')
    {
      const res= await getAppInfoByVersion(tenantId,id);
      id=res?.data?.id;
    }
    navigate(`/app-develop/${tenantId}/app-detail/add-flow/${id}`);
  }}
    >
    <div className='plugin-card-header'>
      <img src='/src/assets/images/knowledge/knowledge-base.png' />
      <div>
        <div className='plugin-title'>
          <div className='plugin-head'>
            <span className='text' title={pluginData?.name}>{pluginData?.name}</span>
            <Tag className='version'>V{pluginData?.version}</Tag>
          </div>
        </div>
        <div className='plugin-card-user'>
          <Icons.user />
          <span style={{ marginRight: 8 }}>{pluginData?.createBy}</span>
          {pluginData?.tags?.map((tag: string, index: number) => <Tag style={{ margin: 0 }} key={index}>{tag}</Tag>)}
        </div>
      </div>
    </div>
    <div className='card-content'>
      {pluginData?.attributes?.description}
    </div>
    {/* 卡片底部 */}
    <div className='card-footer'>
      <div>
      <Flex gap={14}>
        <span>
          {
            pluginData?.state==='active' ? 
            <Tag bordered={false} color="processing" className='footer-type'>已发布</Tag> :
            <Tag bordered={false} className='footer-type'>草稿</Tag>
          }
        </span>
        <span hidden>
          <UserOutlined style={{ marginRight: 8 }} />
          {pluginData?.downloadCount}
        </span>
        <span hidden>
          <StarOutlined style={{ marginRight: 8 }} />
          {pluginData?.likeCount}
        </span>
      </Flex>
      </div>
      <div onClick={(e)=>{e.stopPropagation();}}>
        <Dropdown menu={{items:operatItems}} trigger={['click']}>
           <EllipsisOutlined className='footer-more'/>
        </Dropdown>
      </div>
    </div>
  </div >
)}

export default WorkflowCard;
