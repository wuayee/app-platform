
import React, { useEffect, useState, useRef } from 'react';
import { Table } from 'antd';
import screenfull from 'screenfull';
import { getChartWidth, exportTableData } from '../utils/table-utils';
import { DownLoadIcon, FullScreenIcon } from '@assets/icon';

// 表格组件
const ChartTable = (props) => {
  const { chartTitle, tableData, headers } = props.chatItem;
  const [isFullscreen, setIsFullScreen] = useState(false);
  const tableRef = useRef(null);
  useEffect(() => {
    getChartWidth(headers, tableData);
    screenfull.on('change', () => {
      setIsFullScreen(screenfull.isFullscreen);
    })
  }, [props]);

  // 全屏
  function fullScreenTable() {
    if (screenfull.isFullscreen) {
      screenfull.exit();
      return;
    }
    screenfull.request(tableRef.current);
  }
  return <>{(
    <div className={['table-dom', isFullscreen ? 'table-full-dom' : null].join(' ')} ref={tableRef}>
      <div className='tool-box'>
        <div className='tool-text'>{chartTitle || ''}</div>
        <div className='table-tool'>
          <DownLoadIcon onClick={() => exportTableData(headers, tableData, chartTitle)} />
          <FullScreenIcon onClick={fullScreenTable} />
        </div>
      </div>
      <Table
        dataSource={tableData}
        columns={headers}
        scroll={{ y: isFullscreen ? 900 : 240 }}
        bordered
        size='small'
        pagination={false}
        rowKey={record => record.rowIndex} />
    </div>
  )}</>
}

export default ChartTable;
