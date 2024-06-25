
import { Pagination, Space, Table } from 'antd';
import React, { useEffect, useState } from 'react';
import type { TableColumnsType } from 'antd';
import TableTextSearch from '../../../components/table-text-search';
import { useNavigate } from 'react-router';
import { queryModelbaseList } from '../../../shared/http/model-base';
import { deleteModel } from './delete';

const ModelBaseTable = () => {

  const [pageNo, setPageNo] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [queryBody, setQueryBody] = useState<any>({ limit: pageSize, offset: pageNo - 1 });
  const [listData, setListData] = useState([]);
  const [total, setTotal] = useState(0);

  const navigate = useNavigate();

  useEffect(() => {
    getModelbaseList();
  }, [queryBody])

  const typeOptions = [
    {
      text: '语言模型',
      value: 'llm',
    },
  ];

  const seriesOptions = [
    {
      text: '千问',
      value: 'qwen'
    },
    {
      text: 'Llama2',
      value: 'llama2'
    }
  ];

  //获取模型仓库列表接口
  const getModelbaseList = () => {
    queryModelbaseList(queryBody).then(res => {
      if (res) {
        setListData(res?.modelInfoList);
        setTotal(res?.total);
      }
    });
  }

  const columns: TableColumnsType = [
    {
      key: 'model_id',
      dataIndex: 'model_id',
      title: 'ID',
      hidden: true
    },
    {
      key: 'model_name',
      dataIndex: 'model_name',
      title: '模型',
      sorter: true,
      ...TableTextSearch('model_name', true),
      render: (_, record) => (
        <a onClick={() => { navigate(`/model-base/${record.model_id}/detail`); }}>
          {record.model_name}
        </a>
      ),
    },
    {
      key: 'model_type',
      dataIndex: 'model_type',
      title: '类型',
      filters: typeOptions,
      sorter: true,
    },
    {
      key: 'author',
      dataIndex: 'author',
      title: '作者',
      sorter: true,
      ...TableTextSearch('creator', true),
    },
    {
      key: 'series_name',
      dataIndex: 'series_name',
      title: '模型系列',
      filters: seriesOptions,
      sorter: true,
    },
    {
      key: 'model_description',
      dataIndex: 'model_description',
      title: '描述',
      ellipsis: true,
    },
    {
      key: 'version_num',
      dataIndex: 'version_num',
      title: '版本数量',
      sorter: true,
    },
    {
      key: 'model_size',
      dataIndex: 'model_size',
      title: '模型大小',
      sorter: true,
    },
    {
      key: 'action',
      title: '操作',
      render(_, record) {
        const deleteConfirm = () => {
          deleteModel(record);
        }
        return (
          <Space size='middle'>
            <a onClick={deleteConfirm}>删除</a>
          </Space>
        )
      },
    }
  ];

  //TODO：筛选和排序项变更时的回调方法，触发数据调用方法
  const fetchData = (_, filters, sorter) => {
    let params: any = {
      offset: pageNo - 1,
      limit: pageSize
    };
    if (filters?.author && filters.author.length > 0) {
      params.author = filters.author[0];
    }
    if (filters?.model_name && filters.model_name.length > 0) {
      params.model_name = filters.model_name[0];
    }
    if (filters?.model_type) {
      params.model_type = filters.model_type;
    }
    if (filters?.series_name) {
      params.series_name = filters.series_name;
    }
    if (sorter?.order) {
      params.sort = [sorter?.field];
      params.direction = [sorter?.order.slice(0, -3)];
    }
    setQueryBody(params);
  }

  const pageChange = (page: number, pageSize: number) => {
    setPageNo(page);
    setPageSize(pageSize);
    setQueryBody({
      ...queryBody,
      offset: page - 1,
      limit: pageSize
    });
  }

  return (
    <>
      <Table
        dataSource={listData}
        columns={columns}
        scroll={{ y: '800px' }}
        pagination={false}
        onChange={fetchData}
      />
      <div style={{
        width: '100%',
        display: 'flex',
        'justifyContent': 'space-between',
        fontSize: '12px',
        marginTop: 16,
      }}>
        <span>Total: {total}</span>
        <Pagination
          size='small'
          total={total}
          showSizeChanger
          showQuickJumper
          pageSize={pageSize}
          current={pageNo}
          onChange={pageChange}
        />
      </div>
    </>
  );
};
export default ModelBaseTable;
