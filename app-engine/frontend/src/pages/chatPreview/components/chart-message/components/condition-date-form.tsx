
import React, { useEffect, useState } from 'react';
import { DatePicker } from 'antd';
import { numToDate } from '../utils/chart-condition';
import locale from 'antd/lib/date-picker/locale/zh_CN';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { getCookie } from '@/shared/utils/common';
import 'dayjs/locale/zh-cn';

dayjs.extend(customParseFormat);
const cLocale = getCookie('locale');
if (cLocale === 'zh') {
  dayjs.locale('zh-cn');
}

const { RangePicker } = DatePicker;
// 日期类型
const DateFilter = (props) => {
  const { filterCurrent, setFilterCurrent } = props;
  const [dateType, setDateType] = useState('');
  const [timeRange, setTimeRange] = useState([dayjs('2015-01', 'YYYY-MM'), dayjs('2015-06', 'YYYY-MM')]);
  const [dateValue, setDateValue] = useState<any>();
  const dateFormat = 'YYYY-MM';

  useEffect(() => {
    let type = 'date'
    if (filterCurrent.value.length > 1) {
      type = 'range';
    }
    dateValueInit(type);
    setDateType(type);
  }, []);
  // 数据初始化处理
  const dateValueInit = (type) => {
    if (type === 'date') {
      let dateItem = dayjs(numToDate(filterCurrent.value[0]), dateFormat);
      setDateValue(dateItem);
    } else {
      let arr = [];
      filterCurrent.value.forEach(item => {
        let dateItem = dayjs(numToDate(item), dateFormat);
        arr.push(dateItem);
      });
      setTimeRange(arr);
    }
  }

  // 数据处理
  const handleChangeDate = (date, dateString) => {
    const arr = [Number(dateString.split('-').join(''))];
    filterCurrent.value = arr;
    setDateValue(date);
    setFilterCurrent(filterCurrent);
  }
  const handleChange = (date, dateString) => {
    setTimeRange(date);
    filterCurrent.value = dateString.map((t) => {
      const arr = t.split('-');
      return Number(arr.join(''));
    });
    setFilterCurrent(filterCurrent);
  }
  return <>
    <div style={{ minWidth: '200px' }}>
      {
        dateType === 'date' ? (
          <DatePicker
            locale={locale}
            onChange={handleChangeDate}
            value={dateValue}
            // minDate={dayjs('2023-01', dateFormat)}
            // maxDate={dayjs('2024-12', dateFormat)}
            picker='month'
          />
        ) : (
            <RangePicker
              locale={locale}
              onChange={handleChange}
              value={timeRange}
              // minDate={dayjs('2023-01', dateFormat)}
              // maxDate={dayjs('2024-12', dateFormat)}
              picker='month'
            />
          )
      }
    </div>
  </>
}

export default DateFilter;
