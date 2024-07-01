import { isJsonString, getUiD } from "@shared/utils/common";
import { queryInspirationSelect } from "@shared/http/aipp";
import { Message } from "@shared/utils/message";

// 历史会话消息处理
export const historyChatProcess = (res) => {
  let chatArr = [];
  res.data.forEach((item) => {
    let questionObj = { type: "send", sendType: "text" };
    let { msg } = JSON.parse(item.question.logData);
    questionObj.logId = item.question.logId;
    questionObj.content = msg;
    item.question.logType !== 'HIDDEN_QUESTION' && chatArr.push(questionObj);
    if (item.instanceLogBodies.length) {
      item.instanceLogBodies.forEach((aItem) => {
        const regex = /```markdown(.*?)```/g;
        const replacedArr = aItem.logData.match(regex);
        let markdowned = aItem.logData.indexOf("```");
        if (replacedArr && replacedArr.length) {
          replacedArr.forEach((item) => {
            let str = item.substring(11, item.length - 3);
            aItem.logData = aItem.logData.replace(item, str);
          });
        }
        let { msg } = JSON.parse(aItem.logData);
        let answerObj = {
          content: msg,
          loading: false,
          openLoading: false,
          checked: false,
          logId: aItem.logId,
          markdownSyntax: markdowned !== -1,
          type: "recieve",
          instanceId: item.instanceId,
          finished: true,
          feedbackStatus: -1,
          appName: item.appName,
          appIcon: item.appIcon
        };
        if (item.appName) {
          answerObj.isAt = true;
        }
        if (aItem.logType === 'FORM') {
          let data  = JSON.parse(aItem.logData);
          let formAppearance = JSON.parse(data.formAppearance);
          let formData = JSON.parse(data.formData);
          answerObj.recieveType = 'form';
          answerObj.formConfig =  {
            instanceId: item.instanceId,
            version: '',
            aippId: '',
            formName: formAppearance[0]?.name || 'normal',
            type: 'history',
            formAppearance,
            formData,
          }
        }
        if (aItem.logType === 'FILE') {
          answerObj.type = 'send';
          let { file_type } = JSON.parse(msg);
          answerObj.sendType = fileTypeSet(file_type);
        }
        if (isJsonString(msg)) {
          let msgObj = JSON.parse(msg);
          if (msgObj.chartData && msgObj.chartType) {
            answerObj.chartConfig = msgObj;
          }
        }
        chatArr.push(answerObj);
      });
    } else {
      let answerObj = { type: "recieve", content: "未获取回答" };
      chatArr.push(answerObj);
    }
  });
  return chatArr;
};
// 灵感大全设置下拉列表
export const inspirationProcess = (tenantId, data, prompItem) => {
let { prompt, promptVarData } = prompItem;
let promptArr = [];
data.forEach(async (item) => {
  let replaceStr = `{{${item}}}`;
  let selectItem = promptVarData.filter((sItem) => sItem.var === item)[0];
  let options = [];
  let selectStr = "";
  if (selectItem.sourceType === "fitable") {
    let params = { appId, appType: "PREVIEW" };
    const res = await queryInspirationSelect( tenantId, "GetQAFromLog", params);
    if (res.code === 0) {
      options = res.data || [];
    }
    selectStr = `<div class="chat-focus" contenteditable="false" data-type="${item}" style="min-width: 40px;">${
      selectItem.var || ""
    }</div>`;
  } else {
    options = selectItem ? selectItem.sourceInfo.split(";") : [];
    options = options.filter((item) => item.length > 0);
    selectStr = `<div class="chat-focus" contenteditable="false" data-type="${item}" style="min-width: 40px;">${
      options[0] || ""
    }</div>`;
  }
  selectItem.options = options;
  promptArr.push(selectItem);
  prompt = prompt.replaceAll(replaceStr, selectStr);
});
return { prompt, promptArr }
};
//  发送消息前验证与数据拼装
export const beforeSend = (chatRunning, value, type) => {
  const chatInitObj = JSON.parse(JSON.stringify({
    content: '',
    type: 'send',
    checked: false
  }));
  if (!type && !value.trim().length) {
    return;
  }
  if (chatRunning) {
    Message({ type: "warning", content: "对话进行中, 请稍后再试" });
    return;
  }
  chatInitObj.type = "send";
  chatInitObj.logId = getUiD();
  if (type) {
    value.file_name = decodeURI(value.file_name);
    chatInitObj.sendType = type;
    chatInitObj.content = JSON.stringify(value);
  } else {
    chatInitObj.sendType = "text";
    chatInitObj.content = value;
  }
  return chatInitObj;
};
// 用户自勾选数据处理
export const reportProcess = (list, listRef) => {
  let memoriesList = [];
  let reportArr = list.map(item => {
    let reportItem = JSON.parse(item.query);
    return reportItem;
  })
  let questionArr = reportArr.filter(item => item.type === 'send');
  let answerArr = reportArr.filter(item => item.type === 'recieve');
  questionArr.forEach(item => {
    let obj = {
      question: { query: item.content }
    };
    let index = listRef.current.findIndex(lItem => lItem.logId === item.logId);
    let answerLogId = listRef.current[index + 1].logId;
    let answerItem = answerArr.filter(aItem => aItem.logId === answerLogId)[0];
    if (answerItem) {
      let content = isJsonString(answerItem.content) ? JSON.parse(answerItem.content) : answerItem.content;
      if (content.chartData) {
        let arr = []
        content.chartData?.forEach(cItem => {
          arr.push(JSON.parse(cItem));
        });
        content.chartData = arr;
      } else {
        content = { answer: content }
      }
      obj.answer = content
    } else {
      obj.answer = '';
    }
    memoriesList.push(obj);
  });
  answerArr.forEach(item => {
    let obj = {
      answer: item.content
    };
    let index = listRef.current.findIndex(lItem => lItem.logId === item.logId);
    let questionLogId = listRef.current[index - 1].logId;
    let questionItem = questionArr.filter(qItem => qItem.logId === questionLogId)[0];
    if (!questionItem) {
      obj.question = '';
      memoriesList.push(obj);
    }
  });
  return memoriesList;
};
// 流式接收消息数据处理
export const messageProcess = (aipp_id, instanceId, version, messageData, atAppInfo) => {
  let obj = {
    loading: false,
    openLoading: false,
    content: '',
    recieveType: 'form',
    finished: true,
    checked: false,
    formConfig: {
      instanceId,
      version,
      aippId: aipp_id,
      parentInstanceId: messageData.parentInstanceId,
      formName: messageData.formAppearance[0].name || 'normal',
      type: 'edit',
      formAppearance: messageData.formAppearance,
      formData: messageData.formData,
    }
  }
  if (atAppInfo) {
    obj.appName = atAppInfo.name;
    obj.appIcon = atAppInfo.attributes.icon;
    obj.isAt = true;
  }
  return obj;
};
// 流式接收消息数据处理
export const messageProcessNormal = (log, instanceId, atAppInfo) => {
  let { msg } = JSON.parse(log.logData);
  const regex = /```markdown(.*?)```/g;
  const replacedArr = log.logData.match(regex);
  let markdowned = log.logData.indexOf('```');
  if (replacedArr && replacedArr.length) {
    replacedArr.forEach(item => {
      let str = item.substring(11, item.length - 3);
      log.logData = log.logData.replace(item, str);
    });
  }
  let recieveChatItem = {
    content: msg,
    loading: false,
    openLoading: false,
    checked: false,
    logId: log.msgId || -1,
    markdownSyntax: markdowned !== -1,
    type: 'recieve',
    instanceId,
    feedbackStatus: -1,
  };
  if (atAppInfo) {
    recieveChatItem.appName = atAppInfo.name;
    recieveChatItem.appIcon = atAppInfo.attributes.icon;
    recieveChatItem.isAt = true;
  }
  return { 
    msg, 
    recieveChatItem
  };
};
// 深拷贝
export const deepClone = (obj) => {
  return JSON.parse(JSON.stringify(obj));
}
// 文件类型设置
export const fileTypeSet = (type) => {
  const audioType = ['mp3', 'wav', 'wmv'];
  const videoType = ['mp4', 'm2v', 'mkv', 'rmvb', 'wmv', 'avi', 'flv', 'mov', 'm4v'];
  const imgType = ['png', 'jpg', 'jpeg', 'bmp', 'gif'];
  let fileType = '';
  if (audioType.includes(type)) {
    fileType = 'audio';
  } else if (videoType.includes(type)) {
    fileType = 'video';
  } else if (imgType.includes(type)) {
    fileType = 'image';
  } else {
    fileType = 'file';
  }
  return fileType
}
// 滚动底部
export const scrollBottom = () => {
  const messageBox = document.getElementById('chat-list-dom');
  messageBox?.scrollTo({
    top: messageBox.scrollHeight,
    behavior: 'smooth',
  });
}