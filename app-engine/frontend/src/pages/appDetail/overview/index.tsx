import { Button, Divider, Flex, Input, Switch, Tag } from 'antd';
import React, { useEffect, useState } from 'react';
import './style.scoped.scss';
import { getAippInfo } from '../../../shared/http/aipp';
import { Message } from '../../../shared/utils/message';
import { useNavigate, useParams } from 'react-router';
import { AppIcons } from '../../../components/icons/app';
import { AvatarIcon } from '../../../assets/icon';
import { AppDefaultIcon } from '../../../assets/icon';

const AppOverview: React.FC = () => {

  const navigate = useNavigate();
  const { appId, tenantId } = useParams();
  const [detail, setDetail] = useState({});
  const [appIcon, setAppIcon] = useState('');

  useEffect(() => {
    getAippInfo(tenantId, appId).then(res => {
      if (res.code === 0) {
        setDetail({ ...res.data });
        if (res.data?.attributes?.icon) {
          setAppIcon(res.data?.attributes?.icon);
        }
      } else {
        Message({ type: 'error', content: res.message || '获取详情数据失败' })
      }
    });
  }, [])

  const gotoArrange = () => {
    navigate(`/app-develop/${tenantId}/app-detail/${appId}`);
  }

  return (
    <div className='tab-content'>
      <Flex vertical gap={20}>
        <Flex justify={'space-between'}>
          <Flex gap='middle'>
            {appIcon ?
              <img width={100} height={100} src={appIcon} />
              :
              <AppDefaultIcon />
          }

            <Flex vertical gap='middle'>
              <h3>{detail?.name || 'Test AppName'}</h3>
              <Flex gap={20}>
                <Flex gap='small' align='center'>
                  <AvatarIcon />
                  <span>{detail?.createBy || 'Admin'}</span>
                </Flex>
                <Flex gap='small'>
                  <span>发布于</span>
                  <span>{detail?.createAt}</span>
                </Flex>
              </Flex>
              <Flex gap={20}>
                <Flex gap={4} align='center'>
                  <AppIcons.UserIcon />
                  <span>2.36k</span>
                </Flex>
                <Flex gap={4} align='center'>
                  <AppIcons.StarIcon />
                  <span>123</span>
                </Flex>
                <Flex gap={4} align='center'>
                  <AppIcons.AppLikeIcon />
                  <span>123</span>
                </Flex>
              </Flex>
            </Flex>
          </Flex>
          <Flex gap='middle'>
            <Flex vertical align={'center'}>
              <span className='font-size-24'>4</span>
              <span>知识库</span>
            </Flex>
            <Divider type='vertical' style={{ backgroundColor: '#D7D8DA', height: '60px' }} />
            <Flex vertical align={'center'}>
              <span className='font-size-24'>2</span>
              <span>插件</span>
            </Flex>
            <Divider type='vertical' style={{ backgroundColor: '#D7D8DA', height: '60px' }} />
            <Flex vertical align={'center'}>
              <span className='font-size-24'>5</span>
              <span>创意灵感</span>
            </Flex>
          </Flex>
        </Flex>
        <div>
          {detail?.attributes?.description || 'Test Desc'}
        </div>
        <Button type='primary' onClick={gotoArrange} style={{
          width: '96px',
          height: '32px',
        }}>去编排</Button>
        <Divider style={{ margin: 0, backgroundColor: '#D7D8DA' }} />
        <div>
          <Flex gap='large'>
            <Flex vertical gap={20}>
              <span>对话开场白</span>
            </Flex>
            <Flex vertical gap={20}>
              <span>{detail?.attributes?.greeting || 'Test Greeting'}</span>
            </Flex>
          </Flex>
        </div>
        <div>
          <div style={{
            border: '1px solid rgb(230, 230, 230)',
            borderRadius: '8px',
            padding: '24px',
            width: '50%'
          }}>
            <Flex vertical gap={20}>
              <Flex justify={'space-between'}>
                <div style={{
                  fontSize: '20px',
                  fontWeight: '500',
                  lineHeight: '23px'
                }}>公开访问URL</div>
                <Flex align={'center'} gap='middle'>
                  <Tag color='#eee' style={{
                    color: '#999',
                    borderRadius: '10px',
                    padding: '0 8px'
                  }}>未运行</Tag>
                  <Switch />
                </Flex>
              </Flex>
              <Input placeholder='https://octo-cd.hdesign.huawei.com/app/editor/UcmfDrFl0JHBFRBeGgfj2Q?' />
              <Flex gap='small'>
                <Button type='primary' size='small'><Flex align={'center'}><AppIcons.PreviewIcon />预览</Flex></Button>
                <Button size='small'><Flex align={'center'}><AppIcons.FlipIcon />自动生成</Flex></Button>
              </Flex>
            </Flex>
          </div>
        </div>
        <div>
          <div style={{
            border: '1px solid rgb(230, 230, 230)',
            borderRadius: '8px',
            padding: '24px',
            width: '50%'
          }}>
            <Flex vertical gap={20}>
              <Flex justify={'space-between'}>
                <div style={{
                  fontSize: '20px',
                  fontWeight: '500',
                  lineHeight: '23px'
                }}>API访问凭证</div>
                <Flex align={'center'} gap='middle'>
                  <Tag color='#eee' style={{
                    color: '#999',
                    borderRadius: '10px',
                    padding: '0 8px'
                  }}>未运行</Tag>
                  <Switch />
                </Flex>
              </Flex>
              <Input placeholder='https://octo-cd.hdesign.huawei.com/app/editor/UcmfDrFl0JHBFRBeGgfj2Q?' />
              <Flex gap='small'>
                <Button size='small'><Flex align={'center'}><AppIcons.FlipIcon />API秘钥</Flex></Button>
                <Button size='small'><Flex align={'center'}><AppIcons.FlipIcon />查阅API文档</Flex></Button>
                <Button size='small'><Flex align={'center'}><AppIcons.FlipIcon />自动生成</Flex></Button>
              </Flex>
            </Flex>
          </div>
        </div>
      </Flex>
    </div>
  )
}

export default AppOverview;
