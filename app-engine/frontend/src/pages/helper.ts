import { getCurUser } from '../shared/http/aipp';

export const getUser = async () => {
  const res = await getCurUser();
  localStorage.setItem('currentUserId', res.data.account?.substr(1));
  localStorage.setItem('currentUserIdComplete', res.data.account);
  localStorage.setItem('currentUser', res.data.chineseName);
};
