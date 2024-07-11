// 标准时间转年月
export const formatYYYYMM = (timeStr) => {
  if (!timeStr) return '';
  let date = new Date(timeStr);
  let year = date.getFullYear();
  let month = ('0' + (date.getMonth() + 1)).slice(-2);
  let ym = year + month;
  return ym;
};
