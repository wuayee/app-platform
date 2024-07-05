import { Col, Flex, message, Modal, Row, Space, Table } from 'antd';
import React, { useEffect, useState } from 'react';
import GoBack from '../../../components/go-back/GoBack';
import type { PaginationProps, TableColumnsType } from 'antd';
import ModelConfig from './config';
import { useParams } from 'react-router';
import { deleteModelbaseVersion, queryModelDetail } from '../../../shared/http/model-base';
import { ExclamationCircleFilled } from '@ant-design/icons';

const showTotal: PaginationProps['showTotal'] = (total) => `Total: ${total}`;

const ModelBaseDetail: React.FC = () => {

  const { name } = useParams();
  const [configOpen, setConfigOpen] = useState(false);
  const [data, setData] = useState<any>({});
  const [versionData, setVersionData] = useState<any[]>([]);
  const [config, setConfig] = useState('');

  const baseInfoKey = [
    {
      label: '作者',
      key: 'author'
    },
    {
      label: '模型系列',
      key: 'series_name'
    },
    {
      label: '最大序列长度',
      key: 'max_seq_length'
    },
    {
      label: '模型大小',
      key: 'model_size'
    },
    {
      label: '创建时间',
      key: 'create_time'
    },
    {
      label: '更新时间',
      key: 'update_time'
    }
  ];

  useEffect(() => {
    getDetail();
  }, [name]);

  const getDetail = () => {
    if (name) {
      queryModelDetail(name).then(res => {
        if (res) {
          setData(res?.modelInfo);
          setConfig(JSON.stringify(JSON.parse(res?.config),null,2));  // 格式化返回值
          setVersionData(res?.versionInfo);
        }
      });
    }
  }

  const deleteVersion = (item: any) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleFilled />,
      content: `确认删除模型版本 ${item?.version_no} ?`,
      okType: 'danger',
      onOk() {
        //删除逻辑
        deleteModelbaseVersion(item?.version_no, item?.model_name).then(res => {
          if (res && (res?.code === 0 || res?.code === 200)) {
            message.success('删除成功');
            getDetail();
          } else {
            message.error('删除失败');
          }
        })
      }
    })
  }

  const columns: TableColumnsType = [
    {
      key: 'version_id',
      dataIndex: 'version_id',
      title: 'ID',
      hidden: true
    },
    {
      key: 'version_no',
      dataIndex: 'version_no',
      title: '版本号',
    },
    {
      key: 'version_description',
      dataIndex: 'version_description',
      title: '版本说明',
      ellipsis: true
    },
    {
      key: 'update_time',
      dataIndex: 'update_time',
      title: '更新时间',
      sorter: (a, b) => a?.['update_time'].localeCompare(b?.['update_time'])
    },
    {
      key: 'train_frame',
      dataIndex: 'train_frame',
      title: '训练框架',
      sorter: (a, b) => a?.['train_frame'].localeCompare(b?.['train_frame'])
    },
    {
      key: 'train_type',
      dataIndex: 'train_type',
      title: '训练类型',
      sorter: (a, b) => a?.['train_type'].localeCompare(b?.['train_type'])
    },
    {
      key: 'train_strategy',
      dataIndex: 'train_strategy',
      title: '训练策略',
      sorter: (a, b) => a?.['train_strategy'].localeCompare(b?.['train_strategy'])
    },
    {
      key: 'train_time',
      dataIndex: 'train_time',
      title: '训练时长',
      sorter: (a, b) => a?.['train_time'].localeCompare(b?.['train_time'])
    },
    {
      key: 'final_loss',
      dataIndex: 'final_loss',
      title: '最终loss',
      sorter: (a, b) => a?.['final_loss'].localeCompare(b?.['final_loss'])
    },
    {
      key: 'action',
      title: '操作',
      render(_, record) {
        const deleteConfirm = () => {
          deleteVersion(record);
        }
        return (
          <Space size='middle'>
            <a onClick={deleteConfirm}>删除</a>
          </Space>
        )
      },
    }
  ];

  const configCallback = () => {
    setConfigOpen(false);
  }

  return (
    <div className='aui-fullpage'>
      <div className='aui-header-1'>
        <div className='aui-title-1' style={{ alignItems: 'center' }}>
          <GoBack path={'/model-base'} title='模型详情' />
        </div>
      </div>
      <div className='aui-block'>
        <Flex gap={16} vertical>
          {/* 模型名称头部 */}
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <Flex gap={16} style={{ alignItems: 'center' }}>
              <h2 style={{ fontSize: 20, fontWeight: 400 }}>{data?.model_name}</h2>
              <span>版本数：{data?.version_num}</span>
            </Flex>
            <a onClick={() => { setConfigOpen(true) }}>配置详情</a>
          </div>
          {/* 模型描述 */}
          <div title={data?.model_description}
            style={{
              display: '-webkit-box',
              textOverflow: 'ellipsis',
              overflow: 'hidden',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
            }}>
            {data?.model_description}
          </div>
          {/* 模型其他基础信息 */}
          <div>
            <Row>
              {baseInfoKey.map(item => (
                <Col span={4}>
                  <Flex vertical>
                    <span style={{ fontSize: 12, color: '#4D4D4D' }}>{item.label}</span>
                    <span style={{ fontSize: 14, color: '#1A1A1A' }}>{data?.[item.key]}</span>
                  </Flex>
                </Col>
              ))}
            </Row>
          </div>
          {/* 版本列表*/}
          <div>
            <Table
              columns={columns}
              dataSource={versionData}
              pagination={{
                size: 'small',
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: showTotal,
              }}
            />
          </div>
        </Flex>
      </div>
      <ModelConfig visible={configOpen} callback={configCallback} configData={config} />
    </div>
  );
};

export default ModelBaseDetail;
