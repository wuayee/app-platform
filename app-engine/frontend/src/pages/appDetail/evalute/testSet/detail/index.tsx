import { Button, Space, Table, Drawer, Descriptions } from 'antd';
import React, { useEffect, useState } from 'react';
import type { DescriptionsProps, PaginationProps } from 'antd';
import { formatDateTime } from '../../../../../shared/utils/function';
import CreateSet from '../createTestset/createTestSet';

interface props {
  visible: boolean;
  params: any;
  detailCallback: Function;
}

const showTotal: PaginationProps['showTotal'] = (total) => `共 ${total} 条`;

const SetDetail = ({ params, visible, detailCallback }: props) => {

  const [detailOpen, setDetailOpen] = useState(false);
  const [detailInfo, setDetailInfo] = useState<any[]>([]);
  const [modifyData, setModifyData] = useState<any>({});
  const [items, setItems] = useState<any[]>([]);
  const [modifyOpen, setModifyOpen] = useState(false);

  useEffect(() => {
    generateListData(params);
    setDetailOpen(visible);
    getItems();
    //获取测试集输入输出数据TODO
  }, [params, visible])

  const generateListData = (params: any) => {
    const items: DescriptionsProps['items'] = [
      {
        key: 'name',
        label: '测试集名称',
        children: params?.name
      },
      {
        key: 'desc',
        label: '测试集描述',
        children: params?.desc
      },
      {
        key: 'creator',
        label: '创建人',
        children: params?.creator
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

  const columns = [
    {
      key: 'input',
      dataIndex: 'input',
      title: '输入',
      ellipsis: true
    },
    {
      key: 'output',
      dataIndex: 'output',
      title: '输出',
      ellipsis: true
    },
    {
      key: 'createTime',
      dataIndex: 'createTime',
      title: '创建时间',
      ellipsis: true
    },
    {
      key: 'modifyTime',
      dataIndex: 'modifyTime',
      title: '修改时间',
      ellipsis: true
    },
  ];

  const getItems = () => {
    const dataSource = Array.from({ length: 20 }).fill(null).map((_, index) => ({
      key: index,
      input: `数据集${index}`,
      output: `描述${index}`,
      createTime: formatDateTime(new Date()),
      modifyTime: formatDateTime(new Date())
    }));
    setItems(dataSource);
  }

  const closeDrawer = () => {
    detailCallback();
  }

  const modifyCallback = (type: string, data: any) => {
    setModifyOpen(false);
  }

  const setModifyDrawer = () => {
    let tempData: any = {};
    detailInfo.forEach(item => {
      if (item?.key === 'name' || item?.key === 'desc') {
        tempData[item?.key] = item?.children;
      }
    });
    tempData.data = [...items.map(item => ({
      input: item?.input,
      output: item?.output,
    }))];
    setModifyData(tempData);
    setModifyOpen(true);
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
              <Button style={{ minWidth: 96 }} onClick={closeDrawer}>取消</Button>
              <Button type='primary' style={{ minWidth: 96 }} onClick={setModifyDrawer}>编辑</Button>
            </Space>
          </div>
        }>
        <Descriptions layout='vertical' items={detailInfo} column={2} />
        <Table
          dataSource={items}
          columns={columns}
          pagination={{
            total: 20,
            simple: true,
            size: 'small',
            showTotal,
          }}
        />
        <CreateSet visible={modifyOpen} createCallback={modifyCallback} data={modifyData} title='编辑测试集' />
      </Drawer>
    </>
  )
}

export default SetDetail;
