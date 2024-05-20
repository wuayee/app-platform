import { message } from 'antd';

let isNesting = window.self === window.top;
export const Message = ({ type, content }) => {
  const style = {
    fontSize: '12px',
    marginLeft: isNesting ? null : '-360px'
  }
  switch(type) {
    case 'info':
      message.warning({
        content,
        style
      });
      break
    case 'success':
      message.success({
        content,
        style
      });
      break
    case 'error':
      message.error({
        content,
        style
      });
      break
    case 'warning':
      message.warning({
        content,
        style
      });
      break
    case 'loading':
      message.loading({
        content,
        style
      });
      break
    default:
  }
}