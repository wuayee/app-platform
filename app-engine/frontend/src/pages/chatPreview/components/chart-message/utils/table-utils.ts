import i18n from '@/locale/i18n';

// 设置表格宽度
export const getChartWidth = (headers, tableData) => {
  const messageDiv = document.querySelector('.receive-box');
  if (messageDiv) {
    const width = messageDiv?.clientWidth - 80;
    let tableWidth = 0;
    headers.forEach((a, b) => {
      tableWidth += a.width;
    });
    let reg = /^\d+$/;
    const isPureNumber = tableData.every((element) => {
      return reg.test(parseInt(element[headers[0].key]));
    });
    if (tableWidth > width) {
      !isPureNumber && (headers[0].fixed = 'left');
    } else {
      headers.forEach((a) => {
        if (a.width < (width - 10) / headers.length) {
          delete a.width;
        }
      });
    }
  }
}
// 下载表格数据
export const exportTableData = (headers, tableData, chartTitle) => {
  let str = '';
  str += `序号,${headers.map((item) => item.title).join(',')}\n`;
  tableData.forEach((element, index) => {
    str += `${index + 1},${getColumnData(headers, element)}\n`;
  });
  let blob = new Blob([str], { type: 'text/plain;charset=utf-8' });
  blob = new Blob([String.fromCharCode(0xfeff), blob], { type: blob.type });
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `${chartTitle || i18n.t('download')}.csv`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}
const getColumnData = (headers, row) => {
  let str = '';
  headers.forEach((item) => {
    str += `${row[item.key]},`;
  });
  return str;
}