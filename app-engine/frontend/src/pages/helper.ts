import { getCurUser } from '../shared/http/aipp';

export const getUser = async () => {
  const res = await getCurUser();
  
  localStorage.setItem('currentUserId', res.data.account?.substr(1));
  localStorage.setItem('currentUserIdComplete', res.data.account);
  localStorage.setItem('currentUser', res.data.chineseName);
};
// 超过一千，转为K
export const FormatQaNumber = (val) => {
  if (!val) {
    return '--';
  }
  if (val < 1000) {
    return val + 'QA对';
  }
  return Math.floor(val / 1000) + 'k QA对';
};
