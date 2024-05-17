import React from 'react';
import type { PaginationProps } from 'antd';
import { Pagination } from 'antd';
import { number } from 'prop-types';

interface paginationProps {
  // 当前页
  current?: number;

  // 总条数
  total?: 0;

  // 默认页
  defaultCurrent?: number;

  // 默认分页
  defaultPageSize?: number;

  // 每页条数
  pageSize?: number;

  // 分页设置
  pageSizeOptions?: number[] | string[];

  // 分页是否显示
  showSizeChanger?: boolean;

  // 快速跳转
  showQuickJumper?: boolean;

  // 禁用分页
  disabled?: boolean;

  // 只有一页是否隐藏分页
  hideOnSinglePage?: boolean;

  // 页码或pagesize改变的回调
  onChange?: (page: number, pageSize: number) => any;

}

// 默认参数
const defaultConfig: paginationProps = {
  current: 1,
  pageSizeOptions: [10, 20, 50, 100],
  pageSize: 10,
  showSizeChanger: true,
  showQuickJumper: true,
  hideOnSinglePage: false,
  total: 0,
}

const App: React.FC = (props: paginationProps = defaultConfig) => {
  const config = {...defaultConfig, ...props }
return (
  <>
    <div style={{
      width: '100%',
      display: 'flex',
      'justifyContent': 'space-between',
      fontSize: '12px'

    }}>
      <span>Total:{props.total}</span>
      <Pagination {...config} />
    </div>
  </>
)};

export default App;