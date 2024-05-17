import { Button, Divider, Flex, Input, Switch, Tag } from 'antd';
import React, { useState } from 'react';
import './style.scss';

const AppOverview: React.FC = () => {

  const tags = ['财经', '金融', '投资'];

  return (
    <div className='tab-content'>
      <Flex vertical gap={20}>
        <Flex justify={'space-between'}>
          <Flex gap='middle'>
            <img width={100} height={100} />
            <Flex vertical gap='middle'>
              <h3>经营小魔方</h3>
              <Flex gap={20}>
                <Flex gap='small'>
                  <span>张晓明 00123456</span>
                </Flex>
                <Flex gap='small'>
                  <span>发布于</span>
                  <span>2024-01-31 17:46:41</span>
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
              <span>灵感大全</span>
            </Flex>
          </Flex>
        </Flex>
        <div>
          微软的笔记插件，可以帮助用户整理和记录灵感和想法，提高工作和学习效率。这些插件可以根据用户的需求和工作习惯进行选择，提高办公效率。
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
              <span>分类</span>
            </Flex>
            <Flex vertical gap={20}>
              <span>你好，请输入你的需求询问我。我会帮你提供预算制定、储蓄计划、退休规划、教育基金规划等功能。</span>
              <span>
                {tags.map(item => (
                  <Tag style={{
                    padding: '4px 16px',
                    background: 'rgb(230, 241, 253)',
                    fontSize: '14px',
                    border: 'none',
                    borderRadius: '4px',
                    marginRight: '16px'
                  }}>{item}</Tag>
                ))}
              </span>
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
                  <Tag color='#EDFFF9' style={{
                    color: '#50D4AB',
                    borderRadius: '10px',
                    padding: '0 8px'
                  }}>运行中</Tag>
                  <Switch defaultChecked />
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
                  <Tag color='#EDFFF9' style={{
                    color: '#50D4AB',
                    borderRadius: '10px',
                    padding: '0 8px'
                  }}>运行中</Tag>
                  <Switch defaultChecked />
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
