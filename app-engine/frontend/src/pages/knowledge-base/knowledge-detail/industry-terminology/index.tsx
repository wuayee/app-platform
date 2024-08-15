import React, { useEffect, useState } from 'react';
import { Form } from 'antd';
import { Button, Table } from 'antd';
import type { TableProps } from 'antd';
import { useHistory, useLocation } from 'react-router-dom';
import qs from 'qs';
import BreadcrumbSelf from '../../../../components/breadcrumb';
import { KnowledgeIcons } from '../../../../components/icons';
import { getKnowledgeTableById } from '../../../../shared/http/knowledge';
import './index.scoped.scss';
import KnowLedgeTable from '../../../../components/knowledge-detail-table/table-table';
import { ImportTable } from '../../../../components/knowledge-detail-table/import-table';


const IndustryTerminology = () => {
  const searchParams = qs.parse(useLocation().search.replace('?', ''));
  const id = searchParams.id;
  const navigate = useHistory().push;
  const rowid = searchParams.rowid;

  // 获取子组件
  const tableRef = React.useRef<any>(null);

  // 行信息
  const [rowInfo, setRowInfo] = useState<any>(null);

  // 上传文件弹窗开启关闭
  const [open, setOpen] = useState<boolean>(false);



  // 获取知识表信息
  const getTableInfo = async () => {
    try {
      if (!rowid) return;
      let res = await getKnowledgeTableById(rowid);
      setRowInfo(res)
    } catch (error) {

    }
  };

  useEffect(() => {
    if (rowid) {
      getTableInfo();
    }
  }, []);

  const importClick = () => {
    if (rowInfo?.format?.toLowerCase() === 'table') {

      const num = rowInfo?.recordNum ?? 0;
      const col = tableRef?.current?.getColumns();

      // 当导入数量为0且没有列时，跳转导入页面，否则弹出导入弹窗，简化流程
      if (num === 0 && !col?.length) {
        navigate(`/knowledge-base/knowledge-detail/import-data?id=${id}&tableid=${rowid}&tabletype=${(rowInfo?.format || '').toLowerCase()}`);
      }
      setOpen(true);

      return;
    }
    navigate(`/knowledge-base/knowledge-detail/import-data?id=${id}&tableid=${rowid}&tabletype=${(rowInfo?.format || '').toLowerCase()}`);
  }


  return (
    <>
      <div className='aui-fullpage'>
        <div className='aui-header-1'>
          <div className='aui-title-1'>
            <BreadcrumbSelf searchFlag={true} currentLabel={rowInfo?.name}></BreadcrumbSelf>
          </div>
        </div>
        <div className='aui-block' style={{
          display: 'flex',
          flexDirection: 'column',
          gap: 8
        }}>
          <div className='knowledge-detail-header' style={{
            display: 'flex',
            gap: 30,
            alignItems: 'center',
            paddingBottom: 30,
            borderBottom: '1px solid #CDD7E6'
          }}>
            <div className='detail-header-return' style={{
              cursor: 'pointer'
            }} onClick={() => window.history.back()}>
              {<KnowledgeIcons.leftArrow />}
            </div>
            <div className='detail-header-info' style={{
              flex: 1,
              display: 'flex'
            }}>
              {rowInfo?.format === 'TEXT' ? <KnowledgeIcons.tableText width={57} height={57} /> : <KnowledgeIcons.tableXlsx width={57} height={57} />}
              <div className='detail-info-title' style={{
                fontSize: 20,
                height: 30,
                color: 'rgba(5, 5, 5, .96)',
                display: 'flex',
                flexDirection: 'column',
                gap: 10
              }}>
                <span>{rowInfo?.name || ''}</span>

                <div className='detail-info-userinfo' style={{
                  fontSize: 14,
                  color: 'rgba(105, 105, 105, .96)',
                  display: 'flex',
                  gap: 10
                }}>
                  <div className='tag'>{rowInfo?.serviceType || ''}</div>
                  <div className='tag' style={{
                    backgroundColor: '#E6F3FA'
                  }}>{rowInfo?.recordNum ?? 0}</div>
                  {rowInfo?.status ? <div className='tag' style={{
                    backgroundColor: '#FFF5E5'
                  }}>
                    <KnowledgeIcons.loading />
                    <span style={{
                      marginLeft: '4px'
                    }}>
                      {`${rowInfo?.status}个导入任务正在进行中`}
                    </span>

                  </div> : ''}

                </div>
              </div>

            </div>
            <div className='detail-header-add'>
              <Button type="primary" onClick={importClick} style={{
                borderRadius: 4,
                backgroundColor: '#2673E5',
                display: 'flex',
                alignItems: 'center'
              }} disabled={rowInfo ? false : true}>{<KnowledgeIcons.import />} 导入</Button>
            </div>
          </div>
          <div className='knowledge-table' style={{ height: '100%' }}>
            {rowInfo && <KnowLedgeTable ref={tableRef} type={(rowInfo?.format || '').toLowerCase()} reposId={rowInfo.repositoryId} id={rowInfo.id} />}
          </div>
          <div />
        </div>
        <ImportTable open={open} setOpen={setOpen} repositoryId={id} knowledgeTableId={rowid} />
      </div>
    </>
  )
}
export default IndustryTerminology;