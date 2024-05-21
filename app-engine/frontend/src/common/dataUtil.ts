import moment from "moment";

export function formatLocalDate(date: number | string | Date): string {
  const isNil = date === undefined || date === null || date === "";
  if (isNil || date === "--") {
    return "--";
  }
  let curDate = date;
  if (typeof date === "number" && date.toString().length === 10) {
    curDate *= 1000;
  }
  const res = moment(curDate).format("YYYY-MM-DD HH:mm:ss");
  return res;
}
// 返回格式为xx小时xx分钟xx秒
export function getExecTime(startTime, endTime): string {
  const stime = new Date(startTime).getTime();
  const etime = new Date(endTime).getTime();
  // 两个时间戳相差的毫秒数
  const usedTime = etime - stime;

  if (usedTime < 0) {
    return `0小时0分钟0秒`;
  }

  // 计算出小时数
  const hours = Math.floor(usedTime / (3600 * 1000));
  // 计算小时数后剩余的毫秒数
  const leave1 = usedTime % (3600 * 1000);
  // 计算相差分钟数
  const minutes = Math.floor(leave1 / (60 * 1000));
  // 计算分钟数后剩余的毫秒数
  const leave2 = leave1 % (60 * 1000);
  // 计算相差秒数
  const seconds = Math.floor(leave2 / 1000);

  const time = `${hours}小时${minutes}分钟${seconds}秒`;
  return time;
}
