import React, { useState, useRef } from 'react';
import { Tag, Button, message, Modal, Drawer } from 'antd';
import { EllipsisOutlined, StarOutlined, UserOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router';
import { Icons } from '../icons';
import { IconMap, PluginCardTypeE, PluginStatusTypeE, PluginCnType } from '@/pages/plugin/helper';
import { deletePluginAPI } from '../../shared/http/plugin';
import Detail from '../../pages/plugin/detail/detail';
import { useTranslation } from 'react-i18next';
import './style.scss';

const PluginCard = ({ pluginData, cardType, getPluginList, pluginId, cardStatus }: any) => {
  const { t } = useTranslation();
  const [isOpen, setIsOpen] = useState(false);
  const [isShow, setIsShow] = useState(false);
  const [loading, setLoading] = useState(false);
  const modalRef = useRef()
  const navigate = useHistory().push;
  // 插件点击详情
  const pluginCardClick = () => {
    pluginData?.pluginToolDataList === null
      ? navigate(`/plugin/detail/${pluginId}`)
      : setIsShow(true);
  };
  // 删除
  const deletePlugin = (e) => {
    e.stopPropagation();
    setIsOpen(false);
    modalRef.current = Modal.warning({
      title: t('deletePlugin'),
      centered: true,
      okText: t('ok'),
      footer: (
        <div className='drawer-footer'>
          <Button onClick={() => modalRef.current.destroy()}>{t('cancel')}</Button>
          <Button type="primary" loading={loading} onClick={confirm}>{t('ok')}</Button>
        </div>
      ),
      content: (
        <div style={{ margin: '8px 0' }}>
          <span>{t('deleteKnowledgeTips')}</span>
        </div>
      )
    });
  }
  const confirm = () => {
    setLoading(true);
    deletePluginAPI(pluginId).then((res) => {
      setLoading(false);
      if (res.code === 0) {
        getPluginList();
        modalRef.current.destroy();
        message.success(t('deleteSuccess'));
      }
    }).catch(() => {
      setLoading(false);
    });
  }
  const onClose = () => {
    setIsShow(false);
  };

  return (
    <div className='page-plugin-card' onClick={pluginCardClick}>
      <div className='plugin-card-header'>
        <img src='./src/assets/images/knowledge/knowledge-base.png' />
        <div className='header-content'>
          <div className='header-name'>
            <div className='text' title={pluginData?.pluginToolDataList === null ? pluginData.pluginName : pluginData?.name}>
              {pluginData?.pluginToolDataList === null ? pluginData.pluginName : pluginData?.name}
            </div>
          </div>
          <div className='plugin-card-user'>
            <Icons.user />
            <span style={{ marginRight: 8 }}>{pluginData.creator}</span>
            {pluginData?.tags?.map((tag: string, index: number) => (
              <Tag style={{ margin: 0 }} key={index}>
                {tag}
              </Tag>
            ))}
          </div>
        </div>
      </div>
      <div className='card-content'>
        {pluginData?.pluginToolDataList === null
          ? pluginData.extension.description
          : pluginData?.description}
      </div>
      {/* 卡片底部 */}
      <div className='card-footer' style={{ position: 'relative' }}>
        <div hidden>
          <div className='card-footer-content'>
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
          </div>
        </div>
        <div hidden={cardType !== PluginCardTypeE.MARKET}>
          <div className='card-footer-right'>
            {IconMap[pluginData?.source?.toUpperCase()]?.icon}
            <span>
              {IconMap[pluginData?.source?.toUpperCase()]?.name}
            </span>
          </div>
        </div>
        <div
          hidden={pluginData?.pluginToolDataList !== null}
          onClick={(e) => {
            e.stopPropagation();
            !isOpen ? setIsOpen(true) : setIsOpen(false);
          }}
          style={{ width: 60 }}
        >
          <div className='footer-icon'>
            <EllipsisOutlined className='footer-more' />
          </div>
        </div>
        {isOpen && (
          <div style={{ position: 'absolute', right: '-20px', top: '-20px' }}>
            <Button style={{ width: 62 }} onClick={deletePlugin}>
              {t('delete')}
            </Button>
          </div>
        )}
      </div>
      {/* 卡片状态 */}
      { pluginData.deployStatus && <span className={['plugin-tag', PluginStatusTypeE[pluginData.deployStatus]].join(' ')}>
        {PluginCnType[pluginData.deployStatus]}
      </span>}
      <Drawer
        width={800}
        open={isShow}
        onClose={() => {
          setTimeout(() => {
            setIsShow(false);
          }, 500);
        }}
      >
        <Detail pluginData={pluginData} />
      </Drawer>
    </div>
  );
}
export default PluginCard;
