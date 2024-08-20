
import React from 'react';

// 单选列表类型dom
const ListFilter = (props) => {
  const { filterCurrent, setFilterCurrent } = props;
  const setClassName = (item) => {
    return item.value === filterCurrent.value || item.value === filterCurrent.value[0];
  }
  const handleCLick = (item) => {
    filterCurrent.value = item.value;
    let item2 = JSON.parse(JSON.stringify(filterCurrent))
    setFilterCurrent(item2);
  }
  return <>
    <div className='filter-list-content'>
      {filterCurrent.options?.map(item => {
        return (
          <div key={item.value} className={
            setClassName(item) ? 'active list-filter-item' : 'list-filter-item'}
            onClick={() => handleCLick(item)}
          >
            { item.label}
          </div>
        )
      })
      }
    </div>
  </>
}

export default ListFilter;
