import { Button, Divider, Flex, Input, Switch, Tag } from 'antd';
import React, { useEffect, useState } from 'react';
import './style.scoped.scss';
import { queryAppDetail } from '../../../shared/http/app';
import { Message } from '../../../shared/utils/message';

const AppOverview: React.FC = () => {

  const [detail, setDetail] = useState({});
  const appId = '9455a208e1564cb592f084b851fa46d2';
  const [appIcon, setAppIcon] = useState('/src/assets/svg/app-default.svg');

  useEffect(() => {
    queryAppDetail(appId).then(res => {
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

  return (
    <div className='tab-content'>
      <Flex vertical gap={20}>
        <Flex justify={'space-between'}>
          <Flex gap='middle'>
            <img width={100} height={100} src={appIcon} />
            <Flex vertical gap='middle'>
              <h3>{detail?.name}</h3>
              <Flex gap={20}>
                <Flex gap='small' align='center'>
                  <img width={16} height={16} src='/src/assets/images/avatar-default.png' />
                  <span>{detail?.createBy}</span>
                </Flex>
                <Flex gap='small'>
                  <span>发布于</span>
                  <span>{detail?.createAt}</span>
                </Flex>
              </Flex>
              <Flex gap={20}>
                <Flex gap={4}>
                  <img src='/src/assets/svg/user.svg' />
                  <span>2.36k</span>
                </Flex>
                <Flex gap={4}>
                  <img src='/src/assets/svg/star.svg' />
                  <span>123</span>
                </Flex>
                <Flex gap={4}>
                  <img src='/src/assets/svg/like.svg' />
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
            <Divider type='vertical' style={{ backgroundColor: '#D7D8DA' }} />
            <Flex vertical align={'center'}>
              <span className='font-size-24'>2</span>
              <span>插件</span>
            </Flex>
            <Divider type='vertical' />
            <Flex vertical align={'center'}>
              <span className='font-size-24'>5</span>
              <span>创意灵感</span>
            </Flex>
          </Flex>
        </Flex>
        <div>
          {detail?.attributes?.description}
        </div>
        <Button type='primary' style={{
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
              <span>{detail?.attributes?.greeting}</span>
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
                <Button type='primary' size='small'><Flex align={'center'}><img src='/src/assets/svg/preview.svg' />预览</Flex></Button>
                <Button size='small'><Flex align={'center'}><img src='/src/assets/svg/flip.svg' />自动生成</Flex></Button>
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
                <Button size='small'><Flex align={'center'}><img src='/src/assets/svg/flip.svg' />API秘钥</Flex></Button>
                <Button size='small'><Flex align={'center'}><img src='/src/assets/svg/flip.svg' />查阅API文档</Flex></Button>
                <Button size='small'><Flex align={'center'}><img src='/src/assets/svg/flip.svg' />自动生成</Flex></Button>
              </Flex>
            </Flex>
          </div>
        </div>
      </Flex>
    </div>
  )
}

export default AppOverview;
