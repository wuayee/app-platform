import { Button, Space, Table, Drawer, Descriptions } from 'antd';
import React, { useEffect, useState } from 'react';
import type { DescriptionsProps, PaginationProps } from 'antd';
import { formatDateTime } from '../../../../../shared/utils/function';
import CreateSet from '../createTestset/createTestSet';
import { createDataSetListData, deleteDataSetListData, getDataSetListById, modifyDataSetListData } from '../../../../../shared/http/apps';
import Pagination from '../../../../../components/pagination';
import CreateItem from '../createTestset/createItem';
import { CallbackMethod, CreateType } from '../createTestset/model';

interface props {
  rawData: any;
}

const showTotal: PaginationProps['showTotal'] = (total) => `共 ${total} 条`;

const DetailTable = ({ rawData }: props) => {
  const [items, setItems] = useState<any[]>([]);

  const defaultItemData = {
    input: '',
    output: '',
    datasetId: '',
    index: -1,
  }

  const [params, setParams] = useState(defaultItemData);
  const [itemOpen, setItemOpen] = useState(false);

  // 创建数据
  const createItemDrawer = () => {
    const tempParams = {
      index: -1,
      input: '',
      output: '',
      datasetId: '',
    }
    setParams(tempParams);
    setItemOpen(true);
  };

  // 修改数据
  const modifyItemDrawer = (data: any, index: number) => {
    const tempParams = {
      input: data?.input,
      output: data?.output,
      datasetId: data?.id,
      index,
    }
    setParams(tempParams);
    setItemOpen(true);
  }

  const dataOpera = async (data: any, index: number) => {
    try {
      if (index < 0) {
        // 创建
        await createDataSetListData({
          datasetId: rawData.id,
          input: data?.input ?? '',
          output: data?.output ?? ''
        })
      } else {
        console.log(params)
        // 修改
        await modifyDataSetListData({
          id: params?.datasetId,
          input: data?.input ?? '',
          output: data?.output ?? ''
        })
      }
      refresh();
      setItemOpen(false);
    } catch (error) {

    }

  }

  const callback = (operate: string, data: any, index: number) => {
    if (operate === 'submit') {
      dataOpera(data, index)

    } else {
      setItemOpen(false);
    }
  }

  // 总条数
  const [total, setTotal] = useState(0);

  // 分页
  const [page, setPage] = useState(1);

  // 分页数
  const [pageSize, setPageSize] = useState(10);

  // 分页变化
  const paginationChange = (curPage: number, curPageSize: number) => {
    if (page !== curPage) {
      setPage(curPage);
    }
    if (pageSize != curPageSize) {
      setPageSize(curPageSize);
    }
  }

  useEffect(() => {
    //获取测试集输入输出数据TODO
    refresh();
  }, [page, pageSize]);

  const refresh = async () => {
    try {
      const res = await getDataSetListById({
        datasetId: rawData.id,
        pageIndex: page,
        pageSize: pageSize
      });
      const data = res?.data?.data || [];
      const total = res?.data?.total || 0;
      setTotal(total);
      setItems([...data])
    } catch (error) {

    }
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
    {
      key: 'action',
      title: '操作',
      render(_: any, record: any, index: any) {
        const edit = () => {
          modifyItemDrawer(record, index)
        }
        const delItem = async () => {
          await deleteDataSetListData(record?.id);
          refresh();
        }
        return (
          <Space size='middle'>
            <a onClick={edit}>编辑</a>
            <a onClick={delItem}>删除</a>
          </Space>
        )
      },
      width: 120
    },
  ];

  return (
    <>


      <Button
        type='primary'
        style={{ minWidth: '96px', margin: '8px 0 16px' }}
        onClick={createItemDrawer}
      >创建</Button>

      <Table
        dataSource={items}
        columns={columns}
        pagination={false}
      />
      <Pagination total={total} current={page} onChange={paginationChange} pageSize={pageSize} />
      <CreateItem params={params} visible={itemOpen} callback={callback} />
    </>
  )
}

export default DetailTable;
