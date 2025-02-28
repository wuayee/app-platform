/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Dropdown, Tooltip, Button } from 'antd';
import { Icons } from '../icons';
import { useTranslation } from 'react-i18next';
import chatbotImg from '@/assets/images/ai/app_chatbot.svg';
import workflowImg from '@/assets/images/ai/app_workflow.svg';
import agentImg from '@/assets/images/ai/app_agent.svg';
import './style.scoped.scss';

/**
 * 应用卡片组件
 *
 * @return {JSX.Element}
 * @param cardInfo  应用详情
 * @param clickMore  点击更多按钮回调
 * @param showOptions  是否显示操作按钮
 * @param isTemplate  是否为应用模板卡片
 * @param isCurrentHover  鼠标移入显示使用模板按钮
 * @param openTemplateModal  打开模板选择弹窗方法
 * @param readOnly  是否只读
 * @constructor
 */

const AppCard = ({ cardInfo, clickMore, showOptions = true, isTemplate = false, isCurrentHover = false, openTemplateModal, readOnly }: any) => {
  const { t } = useTranslation();
  const [count, setCount] = useState(0);
  const [menu, setMenu] = useState<any>([]);
  const clickItem = (info: any) => {
    clickMore(info.key, cardInfo);
  };
  const getAppIcon = (category) => {
    switch (category) {
      case 'chatbot':
        return chatbotImg;
      case 'workflow':
        return workflowImg;
      case 'agent':
        return agentImg;
      default:
        break;
    }
  }
  useEffect(() => {
    let { likeCount } = cardInfo;
    setCount(likeCount || 0);
  }, [cardInfo]);

  // 基于用户角色动态设置操作按钮
  useEffect(() => {
    if (readOnly) {
      setMenu([
        {
          key: 'export',
          label: <div>{t('export')}</div>,
        },
      ]);
    } else {
      setMenu([
        {
          key: 'delete',
          label: <div>{t('delete')}</div>,
        },
        {
          key: 'export',
          label: <div>{t('export')}</div>,
        },
      ]);
    }
  }, [readOnly]);
  return (
    <div className='app_card_root'>
      {/* 头部区域 */}
      <div className='app_card_header'>
        <div className='img_box'>
          {cardInfo.icon && <img width={'100%'} src={cardInfo.icon} alt='' />}
          {!cardInfo.icon && (
            <img width={'100%'} src='./src/assets/images/knowledge/knowledge-base.png' alt='' />
          )}
        </div>
        <div className='infoArea'>
          <Tooltip title={cardInfo?.name}>
            <div className='headerTitle'>
              <span className='header-text'>{cardInfo?.name}</span>
              {
                showOptions && <span className='header-tag'>
                  <img src={getAppIcon(cardInfo.appCategory)} alt="" />
                </span>
              }
            </div>
          </Tooltip>
          <div className='title_info'>
            <img width={16} height={16} src='./src/assets/images/ai/people.png' alt='' />
            <div className='createBy'>{cardInfo.createBy || cardInfo.creator}</div>
          </div>
        </div>
      </div>

      {/* 描述 */}
      <div className='app_card_body'>{cardInfo.description}</div>

      {/* 底部 */}
      {
        !isTemplate && <div className='app_card_footer'>
          {
            showOptions && (
              <div className='left'>
                <div className={`status ${cardInfo.attributes?.latest_version || cardInfo.state === 'active' ? 'published' : 'unpublished'}`}>{cardInfo.attributes?.latest_version || cardInfo.state === 'active' ? t('published') : t('unPublished')}</div>
              </div>)
          }
          <div style={{ flex: 1 }}></div>
          {showOptions && (
            <div className='operator'>
              <Dropdown
                menu={{
                  items: menu,
                  onClick: (info) => {
                    clickItem(info);
                    info.domEvent.stopPropagation();
                  },
                }}
                placement='bottomLeft'
                trigger={['click']}
              >
                <div
                  style={{ cursor: 'pointer' }}
                  onClick={(e) => {
                    e.stopPropagation();
                  }}
                >
                  <Icons.more width={20} />
                </div>
              </Dropdown>
            </div>
          )}
        </div>
      }
      {/* 模板卡片的hover */}
      {isCurrentHover && <Button type='primary' onClick={() => openTemplateModal(cardInfo)}>使用该模板</Button>}
    </div>
  );
};

export default AppCard;
