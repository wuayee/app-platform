import React from 'react';
import { Avatar, Space, Tabs, Tag } from 'antd';
import data from './data';
import './style.scoped.scss';
import { AppDefaultIcon } from '../../assets/icon';
import { StarFilled, UserOutlined } from '@ant-design/icons';

const PluginMarket = () => {
  return (
    <div className='aui-fullpage'>
      <div className='aui-header-1'>
        <div className='aui-title-1'>插件市场</div>
      </div>
      <div className='aui-block plugin-market'>
        <Tabs
          defaultActiveKey='1'
          items={[
            {
              label: '市场',
              key: '1',
            },
            {
              label: '我的',
              key: '2',
            },
          ]}
        />
        <div className='plugin-wrapper'>
          {data.map((item) => (
            <div className='plugin-item'>
              <div className='plugin-item-header'>
                <div className='plugin-item-header-icon'>
                  <AppDefaultIcon />
                </div>
                <div className='plugin-item-header-title'>
                  <div className='plugin-item-title-1'>{item.name}</div>
                  <div className='plugin-item-title-2'>
                    <Avatar size={16} src='https://api.dicebear.com/7.x/miniavs/svg?seed=1' />
                    <Space>
                      张小明
                      <div className='plugin-item-tags'>
                        {item.tags.map((item) => (
                          <Tag>{item}</Tag>
                        ))}
                      </div>
                    </Space>
                  </div>
                </div>
              </div>
              <div className='plugin-item-content'>{item.description}</div>
              <div className='plugin-item-footer'>
                <div className='plugin-item-footer-left'>
                  <Space className='plugin-item-footer-icons'>
                    <UserOutlined /> 2.36k
                  </Space>
                  <Space className='plugin-item-footer-icons'>
                    <StarFilled /> 126
                  </Space>
                </div>
                <div className='plugin-item-footer-right'></div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default PluginMarket;
