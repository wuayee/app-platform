import React, { useState } from 'react';
import { Dropdown, Flex, MenuProps, Tag, Button, Popconfirm, message, Drawer } from 'antd';
import { EllipsisOutlined, StarOutlined, UserOutlined } from '@ant-design/icons';
import { useHistory } from 'react-router';
import { Icons } from '../icons';
import { IconMap, PluginCardTypeE } from '@/pages/plugin/helper';
import { deletePluginAPI } from '../../shared/http/plugin';
import Detail from '../../pages/plugin/detail/detail';
import './style.scss';

const PluginCard = ({ pluginData, cardType, getPluginList, pluginId }: any) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isShow, setIsShow] = useState(false);
  const navigate = useHistory().push;
  // 插件点击详情
  const pluginCardClick = () => {
    pluginData?.pluginToolDataList === null
      ? navigate(`/plugin/detail/${pluginId}`)
      : setIsShow(true);
  };
  const onClose = () => {
    setIsShow(false);
  };

  return (
    <div className='page-plugin-card' onClick={pluginCardClick}>
      <div className='plugin-card-header'>
        <img src='/src/assets/images/knowledge/knowledge-base.png' />
        <div>
          <div style={{ display: 'flex' }}>
            <div style={{ fontSize: 20, marginBottom: 8 }}>
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
          <Flex style={{ display: 'flex', alignItems: 'center' }} gap={4}>
            {IconMap[pluginData?.source?.toUpperCase()]?.icon}
            <span style={{ fontSize: 12, fontWeight: 700 }}>
              {IconMap[pluginData?.source?.toUpperCase()]?.name}
            </span>
          </Flex>
        </div>
        <div
          hidden={pluginData?.pluginToolDataList !== null}
          onClick={(e) => {
            e.stopPropagation();
            !isOpen ? setIsOpen(true) : setIsOpen(false);
          }}
          style={{ width: 60 }}
        >
          <Flex justify='flex-end'>
            <EllipsisOutlined className='footer-more' />
          </Flex>
        </div>
        {isOpen && (
          <div style={{ position: 'absolute', right: '-20px', top: '-20px' }}>
            <Button
              style={{ width: 62 }}
              onClick={(e) => {
                e.stopPropagation();
                setIsOpen(false);
                deletePluginAPI(pluginId)
                  .then((res) => {
                    if (res.code === 0) {
                      getPluginList();
                      message.success('删除成功！');
                    }
                  })
                  .catch(() => {
                    message.error('删除失败！');
                  });
              }}
            >
              删除
            </Button>
          </div>
        )}
      </div>
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
};

export default PluginCard;
