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
import { getEvalReport, getEvalReportTrace, getEvalTaskReport } from '../../../../shared/http/apps';

const numOriginInfos = {
  all: { id: 'allInput', num: 0, des: '全部用例', icon: <AppIcons.EvalueateAllIcon /> },
  success: {
    id: 'passInput',
    num: 0,
    des: '成功用例',
    icon: <AppIcons.EvalueateSuccessIcon />,
  },
  fail: {
    id: 'failureInput',
    num: 0,
    des: '失败用例',
    icon: <AppIcons.EvalueateFailIcon />, 
  },
};

const EvaluationDrawer: React.FC<{ openSignal: number; taskRecord: object }> = ({
  openSignal,
  taskRecord,
}) => {
  const [open, setOpen] = useState(false);
  const [traceDrawer, setTraceDrawer] = useState(false);
  const [numInfos, setNumInfos] = useState(numOriginInfos);
  const [row, setRow] = useState(null);
  const [selectCard, setSelectCard] = useState('allInput');
  const [reportData,setReportData]=useState(null);
  const [inputList,setInputList]=useState([]);
  const [traceData,setTraceData]=useState([]);

  const onGetReport = async () => {
    const reportRes = await getEvalTaskReport(taskRecord?.id);

    // header的三张卡片
    numInfos.all.num = reportRes?.failureInput?.length + reportRes?.passInput?.length;
    numInfos.fail.num = reportRes?.failureInput?.length;
    numInfos.success.num = reportRes?.passInput?.length;
    setNumInfos({ ...numInfos });

    const allInput=[...reportRes.failureInput,...reportRes.passInput]
    setReportData({...reportRes,allInput});
    setInputList(allInput);
    setRow(allInput?.[0]);
  };

  const onGetTrace=async(id)=>{
  const traceRes=  await getEvalReportTrace(id);
  setTraceData(traceRes?.trace);
  }
  useEffect(() => {
    if (openSignal > 0) {
      setOpen(true);
      onGetReport();
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
          {Object.values(numInfos).map((info) => (
            <div
              id={info.id}
              className='evaluateCard'
              style={{ borderColor: selectCard === info.id ? '#2673e5' : '#f0f2f4' }}
              onClick={() => {
                setSelectCard(info.id);
                setInputList(reportData?.[info.id]);
                setRow(reportData?.[info.id]?.[0]);
                ['allInput','passInput','failureInput'].forEach((key) => {
                  document.getElementById(key).style.borderColor =
                    key === info.id ? '#2673e5' : '#f0f2f4';
                });
              }}
            >
              {info.icon}
              <div className='info'>
                <div className='num'>{info.num}</div>
                <div>{info.des}</div>
              </div>
            </div>
          ))}
        </div>

        <div style={{marginTop:'18px'}}>算法评估</div>
        <div>{reportData?.algorithm}</div>
        <div style={{ display: 'flex', marginTop: '14px', width: '100%' }}>
          <Table
            virtual
            style={{ width: 300, overflow: 'auto', overflowY: 'hidden' }}
            dataSource={inputList}
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
                  onGetTrace(row?.id);
                }}
              >
                调用轨迹
              </a>
            </div>
            <div style={{ display: 'flex' }} className='block'>
              <div style={{ width: '50%' }}>
                <div className='block-title'>得分</div>
                <div className='block-num'>{row?.score}</div>
              </div>
              <div style={{ width: '50%' }}>
                <div className='block-title'>耗时</div>
                <div className='block-num'>{`${row?.latency}ms`}</div>
              </div>
            </div>
            <div className='block'>
              <div className='block-title'>输入</div>
              <div className='block-num'>
              {row?.input}
              </div>
            </div>
            <Table
              rowHoverable={false}
              style={{ marginTop: '20px', height: '200px' }}
              virtual
              dataSource={[{output:row?.output,expectedOutput:row?.expectedOutput}]}
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
          rowKey='time'
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
                  dataSource={[record] }
                  columns={inOutColumns}
                  pagination={false}
                />
              </ConfigProvider>
            ),
          }}
          dataSource={traceData}
          pagination={false}
        />
      </Drawer>
    </div>
  );
};

export default EvaluationDrawer;
