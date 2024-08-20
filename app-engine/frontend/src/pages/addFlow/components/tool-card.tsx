import React from 'react';
import { Tag } from 'antd';
import { Icons } from '../../../components/icons';
import { useHistory } from 'react-router';
import '../styles/tool-card.scss';
import { useAppSelector } from '../../../store/hook';
import { getAppInfoByVersion } from '../../../shared/http/aipp';
import { useTranslation } from "react-i18next";

const ToolCard = ({ pluginData, tenantId }: any) => {
  const { t } = useTranslation();
  const navigate = useHistory().push;
  const appId = useAppSelector((state) => state.appStore.appId);

  // 类型处理
  const detailClick = async () => {
    if (pluginData.tags.includes('WATERFLOW')) {
      const res = await getAppInfoByVersion(tenantId, pluginData?.runnables?.APP?.appId);
      if (res.data.id) {
        sessionStorage.setItem('appId', appId);
        navigate(`/app-develop/${tenantId}/app-detail/add-flow/${res?.data?.id}`);
      }
    } else {
      navigate(`/plugin/detail/${pluginData.uniqueName}`)
    }
  }
  return (
    <div className='plugin-card'>
      <div className='plugin-card-header'>
        {
          pluginData.tags.includes('HUGGINGFACE') ?
            <img src={`./src/assets/images/ai/${2}.png`} /> :
            <img src='./src/assets/images/knowledge/knowledge-base.png' />
        }

        <div>
          <div style={{ display: 'flex' }}>
            <div className='tool-name'>
              <span className='text'>{pluginData.name}</span>
              {pluginData.tags.includes('WATERFLOW') || pluginData.tags.includes('HUGGINGFACE') ?
                <img src='./src/assets/images/ai/workflow.png' alt='' /> :
                <img src='./src/assets/images/ai/application.png' alt='' />}
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
        {pluginData.description === 'null' ? t('noDescription') : pluginData.description}
      </div>
      {/* 卡片底部 */}
      <div className='card-footer'></div>
      <div className='card-detail' onClick={detailClick}>
        {t('checkMore')}
      </div>
    </div >
  )
}

export default ToolCard;
