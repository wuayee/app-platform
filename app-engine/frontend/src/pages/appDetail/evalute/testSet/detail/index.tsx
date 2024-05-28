import { Button, Space, Table, Drawer, Descriptions, Input } from 'antd';
import React, { useEffect, useState } from 'react';
import type { DescriptionsProps, PaginationProps } from 'antd';
import DetailTable from './detail-table';
import ModifyBaseInfo from './modify-base-info';
import { modifyDataSetBaseInfo } from '../../../../../shared/http/apps';

interface props {
  visible: boolean;
  params: any;
  detailCallback: Function;
}

const SetDetail = ({ params, visible, detailCallback }: props) => {

  const [detailOpen, setDetailOpen] = useState(false);
  const [detailInfo, setDetailInfo] = useState<any[]>([]);
  const [detailData, setDetailData] = useState(params);
  useEffect(() => {
    generateListData(detailData);
    setDetailOpen(visible);
  }, [params, visible]);

  const modifyBaseInfo = async (key: string, data: any) => {
    try {
      await modifyDataSetBaseInfo({
        id: params.id,
        datasetName: params.datasetName,
        description: params.description,
        [key]: data.data
      } as any)
      setDetailData({...detailData, [key]: data.data});
      generateListData({...detailData, [key]: data.data});
      
    } catch (error) {
      
    }
  }


  const generateListData = (params: any) => {
    const items: DescriptionsProps['items'] = [
      {
        key: 'datasetName',
        label: '测试集名称',
        children: <ModifyBaseInfo data={params.datasetName} dataKey={'datasetName'} saveCallback={modifyBaseInfo}/>
      },
      {
        key: 'description',
        label: '测试集描述',
        children: <ModifyBaseInfo data={params.description} dataKey={'description'} saveCallback={modifyBaseInfo}/>
      },
      {
        key: 'author',
        label: '创建人',
        children: params?.author
      },
      {
        key: 'createTime',
        label: '创建时间',
        children: params?.createTime
      },
      {
        key: 'modifyTime',
        label: '修改时间',
        children: params?.modifyTime
      }
    ];
    setDetailInfo(items);
  }

  const closeDrawer = () => {
    detailCallback();
  }

  return (
    <>
      <Drawer
        title='测试集详情'
        width={800}
        open={detailOpen}
        onClose={closeDrawer}
        maskClosable={false}
        destroyOnClose={true}
        footer={
          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Space>
              <Button style={{ minWidth: 96 }} onClick={closeDrawer}>关闭</Button>
            </Space>
          </div>
        }>
        <Descriptions layout='vertical' items={detailInfo} column={2} />
        <DetailTable rawData ={params}/>
      </Drawer>
    </>
  )
}

export default SetDetail;
