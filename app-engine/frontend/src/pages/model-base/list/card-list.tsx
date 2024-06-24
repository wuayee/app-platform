
import { Pagination } from 'antd';
import React, { useEffect, useState } from 'react';
import { queryModelbaseList } from '../../../shared/http/model-base';
import CardItem from './card-item';

interface props {
  data: any
}

const ModelBaseCard = () => {

  const [pageNo, setPageNo] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [queryBody, setQueryBody] = useState<any>({ limit: pageSize, offset: pageNo - 1 });
  const [listData, setListData] = useState([]);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    getModelbaseList();
  }, [queryBody])


  //获取模型仓库列表接口
  const getModelbaseList = () => {
    queryModelbaseList(queryBody).then(res => {
      if (res) {
        setListData(res?.modelInfoList);
        setTotal(res?.total);
      }
    });
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
      <div style={{
        height: '100%',
        minHeight: "500px",
        maxHeight: "calc(100% - 200px)",
        display: 'flex',
        gap: '16px',
        flexWrap: 'wrap',
        alignContent: 'flex-start',
        marginBottom: 16
      }}>
        {listData.map((item: any) => <CardItem data={item} />)}
      </div>
      <div style={{
        width: '100%',
        display: 'flex',
        'justifyContent': 'space-between',
        fontSize: '12px'
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
export default ModelBaseCard;
