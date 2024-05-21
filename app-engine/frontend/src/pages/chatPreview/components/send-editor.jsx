import React, {
  useEffect,
  useState,
  useRef,
  useContext,
  useImperativeHandle,
} from "react";
import { Upload, Checkbox, Spin, Dropdown, Space, Avatar } from "antd";
import {
  DownOutlined,
  GlobalOutlined,
  HistoryOutlined,
  LinkOutlined,
  ShareAltOutlined,
  ReloadOutlined,
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  AudioOutlined,
} from "@ant-design/icons";
import $ from "jquery";
import exit from "@assets/images/ai/exit.png";
import talk from "@assets/images/ai/talk.png";
import file from "@assets/images/ai/file.png";
import image from "@assets/images/ai/image.png";
import audio from "@assets/images/ai/audio.png";
import stop from "@assets/images/ai/play.png";
import { Message } from "../../../shared/utils/message";
import { httpUrlMap } from "../../../shared/http/httpConfig";
import { uploadChatFile } from "../../../shared/http/aipp";
import { AippContext } from "../../aippIndex/context";
import "../../../shared/utils/rendos";
import robot2 from "../../../assets/images/ai/xiaohai.png";
import "../styles/send-editor.scss";
import HistoryChat from "./history-chat";

const docArr = [
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  "text/plain",
  "application/pdf",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  "application/vnd.ms-powerpoint",
];
const imgArr = [
  "image/jpeg",
  "image/bmp",
  "image/png",
  "image/gif",
  "image/webp",
  "image/svg+xml",
];
const SendEditor = (props) => {
  const {
    onSend,
    onClear,
    onStop,
    chatType,
    filterRef,
    requestLoading,
    openInspiration,
    open,
  } = props;
  const [content, setContent] = useState("");
  const [selectItem, setSelectItem] = useState({});
  const [selectDom, setSelectDom] = useState();
  const [showSelect, setShowSelect] = useState(false);
  const [positionConfig, setPositionConfig] = useState({});
  const { chatRunning } = useContext(AippContext);
  const editorRef = useRef(null);
  useEffect(() => {
    const dropBox = document.querySelector("#drop");
    dropBox?.addEventListener("dragenter", dragEnter, false);
    dropBox?.addEventListener("dragover", dragOver, false);
    dropBox?.addEventListener("drop", drop, false);
  }, []);
  // 编辑器change事件
  function messageChange() {
    setContent(editorRef.current.innerText);
  }
  // 快捷发送
  function messageKeyDown(e) {
    if (e.ctrlKey && e.keyCode === 13) {
      e.preventDefault();
      document.execCommand("insertLineBreak");
    } else if (e.keyCode === 13) {
      e.preventDefault();
      sendMessage();
    }
  }
  // 粘贴文本
  function messagePaste(e) {
    e.preventDefault();
    let items = e.clipboardData?.items || [];
    for (let i = 0; i < items?.length; i++) {
      const item = items[i];
      if (item.kind === "string" && item.type === "text/plain") {
        item.getAsString(function (str) {
          document.execCommand("insertText", true, str);
        });
      } else if (item.kind === "file" && item.type.indexOf("image") !== -1) {
        const pasteFile = item.getAsFile();
      } else if (item.kind === "file") {
        const pasteFile = item.getAsFile();
      }
    }
  }
  // 发送消息
  function sendMessage() {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    let chatContent = document.getElementById("ctrl-promet").innerText;
    onSend(chatContent);
    setContent("");
    editorRef.current.innerText = "";
  }
  // 拖拽上传功能
  function dragEnter(e) {
    e.stopPropagation();
    e.preventDefault();
  }
  function dragOver(e) {
    e.stopPropagation();
    e.preventDefault();
  }
  // 图片拖拽回调
  function drop(e) {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    e.preventDefault();
    const { files } = e.dataTransfer;
    if (files.length && imgArr.includes(files[0].type)) {
      dragUpload(files[0], "img");
    } else if (files.length && docArr.includes(files[0].type)) {
      dragUpload(files[0], "doc");
    }
  }
  function dragUpload(file, type) {
    console.log(file);
    console.log(type);
  }
  // 设置灵感大全下拉
  function setFilterHtml(prompt, promptMap) {
    const editorDom = document.getElementById("ctrl-promet");
    editorDom.innerHTML = prompt;
    bindEvents(promptMap);
  }
  // 绑定下拉事件
  function bindEvents(promptMap) {
    $("body").on("click", ".chat-focus", ($event) => {
      // clearMove();
      let filterType = $($event.target).attr("data-type");
      let selectItem = promptMap.filter((item) => item.var === filterType)[0];
      // if (!selectItem.multiple) {
      //   $('.chat-promet-list').attr('contenteditable', false);
      //   $('.chat-focus').attr('contenteditable', true);
      //   $event.target.classList.add('dom-chat');
      //   $event.target.classList.remove('clear-chat');
      // } else {
      //   $('.chat-promet-list').attr('contenteditable', true);
      //   $('.chat-focus').attr('contenteditable', false);
      // }
      setPositionConfig($event.target.getBoundingClientRect());
      setSelectItem(selectItem);
      setSelectDom($event.target);
      setShowSelect(true);
    });
    $("body").on("click", (event) => {
      let clickTarget = $(event.target);
      let chatPopup = $(".chat-focus");
      if (!clickTarget.closest(chatPopup).length) {
        // $('.chat-promet-editor').attr('contenteditable', true);
        // $('.chat-focus').attr('contenteditable', false);
        setShowSelect(false);
        // clearMove();
      }
    });
  }
  //  清除节点
  function clearMove() {
    setShowSelect(false);
  }
  // 文件自动发送
  function fileSend(fileResult, fileType) {
    onSend(fileResult, fileType);
  }
  useImperativeHandle(filterRef, () => {
    return {
      setFilterHtml: setFilterHtml,
    };
  });

  const items = [
    {
      key: "1",
      label: "delete",
    },
  ];

  const [guessQuestions, setGuessQuestions] = useState([
    "如何构建知识库",
    "我想创建一个应用",
    "推荐几个常用的应用机器人",
  ]);
  const [recording, setRecording] = useState(false);

  // 语音实时转文字
  let recorderHome = null;
  let intervalData = null;
  const onRecord = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    if (!recording) {
      window.HZRecorder.get((rec) => {
        recorderHome = rec;
        recorderHome.start();
      });
      setRecording(true);
      let conn = new WebSocket(WS_AUDIO_URL);
      conn.onopen = (evt) => {
        if (conn.readyState === 1) {
          intervalData = setInterval(() => {
            let res = recorderHome.getBlob();
            conn.send(res);
          }, 1000);
        }
      };
      conn.onmessage = (evt) => {
        if (evt.data.trim().length) {
          const editorDom = document.getElementById("ctrl-promet");
          editorDom.innerHTML = evt.data.trim();
        }
      };
      conn.onerror = (err) => {
        Message({ type: "error", content: "语音转文字失败" });
      };
      conn.onclose = (err) => {
        recorderHome.stop();
        setRecording(false);
        clearInterval(intervalData);
      };
    }
  };

  const [openHistory, setOpenHistory] = useState(false);

  return (
    <>
      {
        <div className="send-editor-container">
          <div className="editor-inner">
            <div className="editor-guess">
              <div className="editor-guess-header">
                <div className="editor-guess-title">猜你想问</div>
                <Space className="editor-guess-change">
                  <ReloadOutlined />
                  <span>换一批</span>
                </Space>
              </div>
              <div className="editor-guess-questions">
                {guessQuestions.map((question) => (
                  <div className="editor-guess-question-item">{question}</div>
                ))}
              </div>
              <div
                className="editor-open-inspiration"
                onClick={openInspiration}
              >
                <Avatar
                  size="large"
                  className="editor-open-inspiration-avatar"
                  style={{ color: open ? "#0478fc" : "#808080" }}
                  icon={open ? <MenuFoldOutlined /> : <MenuUnfoldOutlined />}
                />
              </div>
            </div>
            <div className="editor-actions">
              <Space>
                <Dropdown menu={{ items }} className="editor-action-dropdown">
                  <Space>
                    <Avatar
                      src={robot2}
                      size={48}
                      style={{ background: "#adbfdd" }}
                    />
                    小海
                    <DownOutlined />
                  </Space>
                </Dropdown>
                <LinkOutlined className="editor-action-item" />
                <span className="editor-action-item">@</span>
              </Space>
              <Space>
                <ShareAltOutlined className="editor-action-item" />
                <GlobalOutlined className="editor-action-item" />
                <Dropdown menu={{ items }} className="editor-action-dropdown">
                  <Space>
                    <span>自动</span>
                    <DownOutlined />
                  </Space>
                </Dropdown>
                <HistoryOutlined
                  className="editor-action-item"
                  onClick={() => setOpenHistory(true)}
                />
              </Space>
            </div>
            <div className="editor-input" id="drop">
              <div
                className={[
                  "quill-span",
                  "quill-item-span quill-last",
                  recording ? "recording" : null,
                ].join(" ")}
                onClick={onRecord}
              >
                <Avatar icon={<AudioOutlined />} className="editor-recording" />
              </div>
              <div
                className="chat-promet-editor"
                id="ctrl-promet"
                ref={editorRef}
                contentEditable={true}
                placeholder="Enter快捷发送，Ctrl+Enter换行"
                onInput={messageChange}
                onKeyDown={messageKeyDown}
                onPaste={messagePaste}
              ></div>
              <div className="send-icon" onClick={sendMessage}></div>
            </div>
          </div>
          {showSelect && (
            <EditorSelect
              chatSelectDom={selectDom}
              chatSelectItem={selectItem}
              positionConfig={positionConfig}
              clearMove={clearMove}
            />
          )}
          <HistoryChat open={openHistory} setOpen={setOpenHistory} />
        </div>
      }
    </>
  );
};

// 编辑器操作按钮
const EditorBtn = (props) => {
  const { onClear, onStop, chatType, fileSend, requestLoading } = props;
  const [recording, setRecording] = useState(false);
  const { chatRunning, tenantId, appId } = useContext(AippContext);
  const { WS_AUDIO_URL } = httpUrlMap[process.env.NODE_ENV];
  const beforeUpload = (file) => {
    return false;
  };
  // 文件上传
  const onChange = async ({ file }) => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    let fileType = docArr.includes(file.type) ? "file" : "img";
    let headers = {
      "attachment-filename": encodeURI(file.name || ""),
    };
    const formData = new FormData();
    formData.append("file", file);
    const result = await uploadChatFile(tenantId, appId, formData, headers);
    if (result.code === 0) {
      fileSend(result.data, fileType);
    } else {
      Message({ type: "error", content: result.msg || "上传文件失败" });
    }
  };
  // 语音实时转文字
  let recorderHome = null;
  let intervalData = null;
  const onRecord = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    if (!recording) {
      window.HZRecorder.get((rec) => {
        recorderHome = rec;
        recorderHome.start();
      });
      setRecording(true);
      let conn = new WebSocket(WS_AUDIO_URL);
      conn.onopen = (evt) => {
        if (conn.readyState === 1) {
          intervalData = setInterval(() => {
            let res = recorderHome.getBlob();
            conn.send(res);
          }, 1000);
        }
      };
      conn.onmessage = (evt) => {
        if (evt.data.trim().length) {
          const editorDom = document.getElementById("ctrl-promet");
          editorDom.innerHTML = evt.data.trim();
        }
      };
      conn.onerror = (err) => {
        Message({ type: "error", content: "语音转文字失败" });
      };
      conn.onclose = (err) => {
        recorderHome.stop();
        setRecording(false);
        clearInterval(intervalData);
      };
    }
  };
  // 退出助手
  const onExit = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    window.parent.postMessage({ eventType: "exit" }, "*");
  };

  return (
    <>
      {
        <div className="send-editor-btn">
          <div className="quill-item-inner">
            {!chatType && (
              <span className="quill-span quill-item-span" onClick={onExit}>
                <img src={exit} alt="" />
                <span>退出助手</span>
              </span>
            )}
            {!chatRunning ? (
              <Spin spinning={requestLoading} size="small">
                <span className="quill-span quill-item-span" onClick={onClear}>
                  <img src={talk} alt="" />
                  <span>全新对话</span>
                </span>
              </Spin>
            ) : (
              <Spin spinning={requestLoading} size="small">
                <span className="quill-span quill-item-span" onClick={onStop}>
                  <img src={stop} alt="" />
                  <span>终止对话</span>
                </span>
              </Spin>
            )}
          </div>
          <div className="quill-item-inner">
            <Upload
              beforeUpload={beforeUpload}
              onChange={onChange}
              accept=".jpg,.png,.bmp,.gif.svg"
            >
              <span className="quill-span quill-item-span">
                <img src={image} alt="" />
                <span>上传图片</span>
              </span>
            </Upload>
            <Upload
              beforeUpload={beforeUpload}
              onChange={onChange}
              accept=".txt,.pdf,.docx,.xlsx,.mp3,.mp4"
            >
              <span className="quill-span quill-item-span">
                <img src={file} alt="" />
                <span>上传文件</span>
              </span>
            </Upload>
            <span
              className={[
                "quill-span",
                "quill-item-span quill-last",
                recording ? "recording" : null,
              ].join(" ")}
              onClick={onRecord}
            >
              <img src={audio} alt="" />
              {recording ? (
                <span className="record-radius"></span>
              ) : (
                <span>语音消息</span>
              )}
            </span>
          </div>
        </div>
      }
    </>
  );
};

// 灵感大全下拉
const EditorSelect = (props) => {
  const { chatSelectItem, chatSelectDom, positionConfig, clearMove } = props;
  const [selectStyle, setSelectStyle] = useState({});
  const [checkedList, setCheckedList] = useState([]);
  const [checkedNameList, setCheckedNameList] = useState([]);
  useEffect(() => {
    const { left, top, width } = positionConfig;
    const styleObj = {
      left: `${left - (200 - width) / 2}px`,
      bottom: `${document.documentElement.clientHeight - top + 10}px`,
      display: "block",
    };
    setSelectStyle(styleObj);
  }, [props]);

  // 选项点击
  function selectClick(item) {
    if (!chatSelectItem.multiple) {
      $(chatSelectDom).text(item);
      clearMove();
    }
  }
  // 多选
  function onChange(e, item) {
    let arr = [];
    let nameArr = [];
    let str = "";
    if (e.target.checked) {
      arr = [...checkedList, item];
    } else {
      arr = checkedList.filter((cItem) => cItem.question !== item.question);
    }
    nameArr = arr.map((item) => {
      return item.question;
    });
    setCheckedList(arr);
    setCheckedNameList(nameArr);
    arr.forEach((item) => {
      str += `<div class='select-html'>
                <div>问题：${item.question}</div>
                <div>回答：${item.answer}</div>
              </div>`;
    });
    $("#ctrl-promet").children(".select-html").remove();
    $("#ctrl-promet").append(str);
  }
  function stopClick(e) {
    e.stopPropagation();
  }
  return (
    <>
      {
        <div
          style={selectStyle}
          className="chat-select-content"
          onClick={stopClick}
        >
          {chatSelectItem.options.map((item, index) => {
            return (
              <div
                className="select-inner-item"
                key={index}
                onClick={selectClick.bind(this, item)}
              >
                {chatSelectItem.multiple ? (
                  <Checkbox
                    checked={checkedNameList.includes(item.question)}
                    onChange={(e) => onChange(e, item)}
                  >
                    <span className="check-span" title={item.question}>
                      {item.question}
                    </span>
                  </Checkbox>
                ) : (
                  <span className="normal-span" title={item}>
                    {item}
                  </span>
                )}
              </div>
            );
          })}
        </div>
      }
    </>
  );
};

// 猜你想问
const Recommends = (props) => {
  return (
    <>
      {
        <div className="recommends-inner">
          <div className="recommends-top"></div>
          <div className="recommends-list">
            <div className="list-left"></div>
            <div className="list-right"></div>
          </div>
        </div>
      }
    </>
  );
};

export default SendEditor;
