import { Button } from 'antd';
import React, { useEffect, useState } from 'react';
import type { PaginationProps } from 'antd';
import { Table, Space } from 'antd';
import { formatDateTime } from '../../../../shared/utils/function';
import CreateSet from './createTestset/createTestSet';
import SetDetail from './detail';
import { useParams } from 'react-router';
import TableTextSearch from '../../../../components/table-text-search';
import TableCalendarSearch from '../../../../components/table-calendar-search';
import { createEvalDataset, getEvalDatasetList } from '../../../../shared/http/apps';
import { Message } from '../../../../shared/utils/message';

const showTotal: PaginationProps['showTotal'] = (total) => `共 ${total} 条`;

const TestSet: React.FC = () => {

  const { appId } = useParams();
  const [open, setOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [detailInfo, setDetailInfo] = useState({});

  useEffect(() => {
    getEvalDatasetList({ appId, pageNum: 1, pageSize: 10 }).then(res => {
      console.log(res);
    });
  })

  const dataSource = Array.from({ length: 30 }).fill(null).map((_, index) => ({
    key: index,
    id: index,
    name: `数据集${index}`,
    desc: `描述${index}`,
    creator: `admin${index}`,
    createTime: formatDateTime(new Date()),
    modifyTime: formatDateTime(new Date())
  }));

  const columns = [
    {
      key: 'id',
      dataIndex: 'id',
      title: 'ID'
    },
    {
      key: 'name',
      dataIndex: 'name',
      title: '测试集名称',
      ...TableTextSearch('name', false),
    },
    {
      key: 'desc',
      dataIndex: 'desc',
      title: '测试集描述',
      ...TableTextSearch('desc'),
    },
    {
      key: 'creator',
      dataIndex: 'creator',
      title: '创建人',
      ...TableTextSearch('creator'),
    },
    {
      key: 'createTime',
      dataIndex: 'createTime',
      title: '创建时间',
      ...TableCalendarSearch('createTime')
    },
    {
      key: 'modifyTime',
      dataIndex: 'modifyTime',
      title: '修改时间'
    },
    {
      key: 'action',
      title: '操作',
      render(_: any, record: any) {
        const viewDetail = () => {
          setDetailInfo(record);
          setDetailOpen(true);
        }
        return (
          <Space size='middle'>
            <a onClick={viewDetail}>查看</a>
            <a>删除</a>
          </Space>
        )
      },
    }
  ];

  const showDrawer = () => {
    setOpen(true);
  };

  const callback = (type: string, data: any) => {
    //创建&编辑面板的回调，根据type进行业务处理
    //submit：提交后请求接口处理逻辑；cancel：关闭面板（可添加二次确认
    if (type === 'submit') {
      const requestBody = {
        author: 'test',
        appId,
        ...data
      };
      console.log(requestBody);
      createEvalDataset(requestBody).then(res => {
        console.log(res);
      });
    }
    setOpen(false);
  }

  const detailCallback = () => {
    setDetailOpen(false);
  }
  const handleChange = (pagination: any, filters: any) => {
    console.log('Various parameters', pagination, filters);
  };
  return (
    <div>
      <div className='margin-bottom-standard test'>
        <Button className='margin-right-standard' type='primary' style={{ width: '88px' }} onClick={showDrawer}>创建</Button>
        <Button>应用评估</Button>
      </div>
      <Table
        dataSource={dataSource}
        columns={columns}
        onChange={handleChange}
        rowSelection={{
          type: 'checkbox',
          columnWidth: 60
        }}
        virtual
        scroll={{ y: 800 }}
        pagination={{
          size: 'small',
          total: 50,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal,
        }}
      />
      <CreateSet visible={open} createCallback={callback} />
      <SetDetail visible={detailOpen} params={detailInfo} detailCallback={detailCallback} />
    </div>
  )
}

export default TestSet;
