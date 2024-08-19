import { Button, Divider, Flex, Input, Switch, Tag } from 'antd';
import React, { useEffect, useState } from 'react';
import './style.scoped.scss';
import { getAppInfo, getAppInfoByVersion } from '@shared/http/aipp';
import { Message } from '../../../shared/utils/message';
import { useHistory, useParams } from 'react-router';
import { AppIcons } from '../../../components/icons/app';
import { useAppDispatch } from '../../../store/hook';
import { setAppInfo } from "../../../store/appInfo/appInfo";
import { useTranslation } from "react-i18next";

const AppOverview: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useHistory().push;
  const { appId, tenantId } = useParams();
  const [detail, setDetail] = useState({});
  const [appIcon, setAppIcon] = useState('');
  const dispatch = useAppDispatch();

  useEffect(() => {
    getAppInfo(tenantId, appId).then(res => {
      if (res.code === 0) {
        setDetail({ ...res.data });
        if (res.data?.attributes?.icon) {
          setAppIcon(res.data?.attributes?.icon);
        }
      } else {
        Message({ type: 'error', content: res.message || t('getDetailFail') });
      }
    });
  }, [])

  const gotoArrange = () => {
    getAppInfoByVersion(tenantId, appId).then(res => {
      if (res.code === 0) {
        dispatch(setAppInfo({}));
        const newAppId = res.data.id;
        navigate(`/app-develop/${tenantId}/app-detail/${newAppId}`);
      }
    })
  }

  return (
    <div className='tab-content'>
      <Flex vertical gap={20}>
        <Flex justify={'space-between'}>
          <Flex className='details-content' gap='middle'>
            {appIcon ?
              <img width={100} height={100} src={appIcon} />
              :
              <img width={100} height={100} src='./src/assets/images/knowledge/knowledge-base.png' />
            }

            <Flex className='details-content' vertical gap='middle'>
              <div className='detail-name'>
                <span className='text'>{detail?.name || ''}</span>
                {
                  (detail.attributes?.latest_version || detail.state === 'active') ?
                    (
                      <div className="status-tag">
                        <img src='./src/assets/images/ai/complate.png' />
                        <span>{t('published')}</span>
                      </div>
                    ) :
                    (
                      <div className="status-tag">
                        <img src='./src/assets/images/ai/publish.png' />
                        <span>{t('unPublished')}</span>
                      </div>
                    )
                }
              </div>
              <Flex gap={20}>
                <Flex gap='small' align='center'>
                  <AppIcons.UserIcon />
                  <span>{detail?.createBy || 'Admin'}</span>
                </Flex>
                <Flex gap='small'>
                  <span>{t('createAt')}</span>
                  <span>{detail?.createAt}</span>
                </Flex>
              </Flex>
              <Flex gap={20}>
                <Flex gap={4} align='center'>
                  <span>{t('app')}ID：</span>
                  <span>{detail?.id}</span>
                </Flex>
              </Flex>
            </Flex>
          </Flex>
        </Flex>
        <div className='app-desc' title={detail?.attributes?.description}>
          {detail?.attributes?.description}
        </div>
        <Button type='primary' onClick={gotoArrange} style={{
          width: '96px',
          height: '32px',
        }}>{t('toArrange')}</Button>
        <Divider style={{ margin: 0, backgroundColor: '#D7D8DA' }} />
        <div>
          <Flex gap='large'>
            <div className='remarks'>
              <span className='left'>{t('prologue')}：</span>
              <span className='right'>{detail?.attributes?.greeting || '-'}</span>
            </div>
          </Flex>
        </div>
      </Flex>
    </div>
  )
}

export default AppOverview;
