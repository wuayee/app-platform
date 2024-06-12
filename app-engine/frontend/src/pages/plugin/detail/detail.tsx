import React, { useEffect, useState } from 'react';
import { Divider, Table, Tag } from 'antd';
import GoBack from '../../../components/go-back/GoBack';
import { PluginIcons } from '../../../components/icons/plugin';
import { Icons } from '../../../components/icons';
import { useParams } from 'react-router';
import { getPluginDetail } from '../../../shared/http/plugin';
import '../style.scoped.scss';

const tags = ['fit', 'http'];
const columns = [
  {
    title: '参数名',
    dataIndex: 'name',
    key: 'name',
    width: 300,
  },
  {
    title: '参数类型',
    dataIndex: 'type',
    key: 'type',
    width: 300,
  },
  {
    title: '参数说明',
    dataIndex: 'des',
    key: 'des',
    ellipsis: true,
  },
];
const data0 = [{ name: 1, type: 2, des: 3 }];
const PlugeDetail: React.FC = () => {
  const { pluginId } = useParams();
const [data,setData]=useState(null);
 const refreshDetail=async()=>{
    const res= await getPluginDetail(pluginId);
    console.log(res);
    setData(res?.data)
  }
  useEffect(()=>{
    if(pluginId){
    refreshDetail();
    }
  },[pluginId])
  return(
  <div className='aui-fullpage'>
    <div className='aui-header-1'>
      <div className='aui-title-1'>
        <GoBack path='/plugin' title='插件详情' />
      </div>
    </div>
    <div className='aui-block'>
      <div className='detail-header'>
        <img src='/src/assets/images/knowledge/knowledge-base.png' />
        <div>
          <div style={{ display: 'flex' }}>
            <div className='detail-header-name'>{data?.name}</div>
            <div className='version-div'>
              <Tag className='version'>v1.1.0</Tag>
            </div>
            <div style={{ alignContent: 'center'}}>
              <PluginIcons.ButterFlydate style={{ marginLeft: '8px' }}/>
              <PluginIcons.HuggingFaceIcon style={{marginLeft: '8px' }}/>
            </div>
            <div className='header-tag'>
              {data?.tags.map((tag: string, index: number) => (
                <Tag key={index}>{tag}</Tag>
              ))}
            </div>
          </div>
          <div className='user-info'>
            <Icons.user />
            <span>{data?.creator}</span>
            <span className='header-time'>创建于2021</span>
            <span className='header-time'>引用数:250</span>
          </div>
        </div>
      </div>
      <div style={{marginTop:14}}>{data?.description}</div>
      <Divider />
      <div className='param-title'>输入参数</div>
      <Table dataSource={data0} columns={columns} pagination={false} />
      <div className='param-title' style={{marginTop:14}}>输出参数</div>
      <Table dataSource={data0} columns={columns} pagination={false} />
    </div>
  </div>
)};

export default PlugeDetail;
