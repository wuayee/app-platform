import { CloseOutlined } from '@ant-design/icons';
import { Button, Card, ConfigProvider, Drawer, Table } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { AppIcons } from '../../../../components/icons/app';
import './index.scss';
import {
  compareColumns,
  compareData,
  inOutColumns,
  listColumns,
  listData,
  traceColumns,
  traceData,
} from './model';

const numOriginInfos = {
  all: { id: 'all', num: 0, des: '全部用例', icon: <AppIcons.EvalueateAllIcon />, selected: true },
  success: {
    id: 'success',
    num: 0,
    des: '成功用例',
    icon: <AppIcons.EvalueateSuccessIcon />,
    selected: false,
  },
  fail: {
    id: 'fail',
    num: 0,
    des: '失败用例',
    icon: <AppIcons.EvalueateFailIcon />,
    selected: false,
  },
};

const NumCard: React.FC<{ info: any }> = ({ info }) => (
  <div
    id={info.id}
    className='evaluateCard'
    onClick={() => {
      ['all', 'fail', 'success'].forEach((key) => {
        document.getElementById(key).style.borderColor = key === info.id ? '#2673e5' : '#f0f2f4';
      });
    }}
  >
    {info.icon}
    <div className='info'>
      <div className='num'>{info.num}</div>
      <div>{info.des}</div>
    </div>
  </div>
);

const EvaluationDrawer: React.FC<{ openSignal: number }> = ({ openSignal }) => {
  const [open, setOpen] = useState(false);
  const [traceDrawer, setTraceDrawer] = useState(false);
  const [numInfos, setNumInfos] = useState(numOriginInfos);
  const [row, setRow] = useState(null);
  useEffect(() => {
    if (openSignal > 0) {
      setOpen(true);
    }
  }, [openSignal]);

  return (
    <div>
      <Drawer
        title='应用测评'
        width={1400}
        style={{ minWidth: 1200 }}
        placement='right'
        closeIcon={false}
        onClose={false}
        open={open}
        extra={
          <CloseOutlined
            onClick={() => {
              setOpen(false);
            }}
          />
        }
        footer={
          <Button
            style={{ float: 'right', width: 90 }}
            onClick={() => {
              setOpen(false);
            }}
          >
            关闭
          </Button>
        }
      >
        <div className='evaluateCards'>
          <NumCard info={numInfos.all} />
          <NumCard info={numInfos.success} />
          <NumCard info={numInfos.fail} />
        </div>
        <div>算法评估</div>
        <div>xxxxxxxxxxxxxxxxxxx</div>
        <div style={{ display: 'flex', marginTop: '14px', width: '100%' }}>
          <Table
            virtual
            style={{ width: 300, overflow: 'auto', overflowY: 'hidden' }}
            dataSource={listData}
            columns={listColumns}
            pagination={false}
            scroll={{ y: 'calc(100vh - 280px)' }}
            onRow={(record) => ({
              onClick: (event) => {
                setRow(record);
              }, // 点击行
            })}
            rowClassName={(record) => (row?.id === record.id ? 'clickRowStyl' : '')}
          />
          <div style={{ width: 'calc(100% - 355px)', marginLeft: 25 }}>
            <div className='progress-card'>
              <a
                className='call-track-button'
                onClick={() => {
                  setTraceDrawer(true);
                }}
              >
                调用轨迹
              </a>
            </div>
            <div style={{ display: 'flex' }} className='block'>
              <div style={{ width: '50%' }}>
                <div className='block-title'>得分</div>
                <div className='block-num'>20分</div>
              </div>
              <div style={{ width: '50%' }}>
                <div className='block-title'>耗时</div>
                <div className='block-num'>200ms</div>
              </div>
            </div>
            <div className='block'>
              <div className='block-title'>输入</div>
              <div className='block-num'>
                啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊
              </div>
            </div>
            <Table
              rowHoverable={false}
              style={{ marginTop: '20px', height: '200px' }}
              virtual
              dataSource={compareData}
              columns={compareColumns}
              pagination={false}
            />
          </div>
        </div>
      </Drawer>
      <Drawer
        title='调用轨迹'
        placement='right'
        size='large'
        closeIcon={false}
        onClose={false}
        open={traceDrawer}
        extra={
          <CloseOutlined
            onClick={() => {
              setTraceDrawer(false);
            }}
          />
        }
        footer={
          <Button
            style={{ float: 'right', width: 90 }}
            onClick={() => {
              setTraceDrawer(false);
            }}
          >
            关闭
          </Button>
        }
      >
        <Table
          rowKey='name'
          columns={traceColumns}
          expandable={{
            expandedRowRender: (record) => (
              <ConfigProvider
                theme={{
                  components: {
                    Table: {
                      headerBg: '#ffffff',
                    },
                  },
                }}
              >
                <Table
                  rowHoverable={false}
                  style={{ margin: '20px 0px', width: '640px' }}
                  dataSource={compareData}
                  columns={inOutColumns}
                  pagination={false}
                />
              </ConfigProvider>
            ),
          }}
          dataSource={traceData}
        />
      </Drawer>
    </div>
  );
};

export default EvaluationDrawer;
