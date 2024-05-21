
import Icon, {UploadOutlined} from "@ant-design/icons";
import React from "react";
import Edit from "./svg/edit.svg";
import Clear from "./svg/clear.svg";
import Send from "./svg/send.svg";
import SendActive from "./svg/send-active.svg";
import AddFlow from "./svg/add-flow.svg";
import LeftArrow from "./svg/left-arrow.svg";
import Start from "./svg/start.svg";
import DataRetrieval from "./svg/data-retrieval.svg";
import Llm from "./svg/llm.svg";
import End from "./svg/end.svg";
import Api from "./svg/api.svg";
import Flow from "./svg/flow.svg";
import Talk from "./svg/talk.svg";
import TalkFlow from "./svg/talk2.svg";
import ConfigFlow from "./svg/config2.svg";
import ManualCheck from "./svg/manual-check.svg";
import DownLoad from "./svg/download.svg";
import FullScreen from "./svg/full.svg";
import User from "./svg/user.svg";
import If from "./svg/if.svg";
import Fit from "./svg/fit.svg";
import Link from "./images/ai/link.svg";
import At from "./images/ai/at.svg";
import Panle from "./images/ai/panel.svg";
import PanleClose from "./images/ai/panel-active.svg";

// 编辑按钮
const EditIcon = (props) => <Icon component={() => (<Edit/>)} {...props} />;
const ClearIcon = (props) => <Icon component={() => (<Clear/>)} {...props} />;
const SendIcon = (props) => <Icon component={() => (<Send/>)} {...props} />;
const SendActiveIcon = (props) => <Icon component={() => (<SendActive />)} {...props} />;
const AddFlowIcon = (props) => <Icon component={() => (<AddFlow />)} {...props} />;
const LeftArrowIcon = (props) => <Icon component={() => (<LeftArrow />)} {...props} />;
const StartIcon = (props) => <Icon component={() => (<Start />)} {...props} />;
const EndIcon = (props) => <Icon component={() => (<End />)} {...props} />;
const ApiIcon = (props) => <Icon component={() => (<Api />)} {...props} />;
const LlmIcon = (props) => <Icon component={() => (<Llm />)} {...props} />;
const ManualCheckIcon = (props) => <Icon component={() => (<ManualCheck />)} {...props} />;
const IfIcon = (props) => <Icon component={() => (<If />)} {...props} />;
const FitIcon = (props) => <Icon component={() => (<Fit />)} {...props} />;
const DataRetrievalIcon = (props) => <Icon component={() => (<DataRetrieval />)} {...props} />;
const UploadIcon = (props) => <UploadOutlined {...props} />;
const FlowIcon = (props) => <Icon component={() => (<Flow/>)} {...props} />;
const TalkIcon = (props) => <Icon component={() => (<Talk/>)} {...props} />;
const TalkFlowIcon = (props) => <Icon component={() => (<TalkFlow/>)} {...props} />;
const ConfigFlowIcon = (props) => <Icon component={() => (<ConfigFlow/>)} {...props} />;
const DownLoadIcon = (props) => <Icon component={() => (<DownLoad/>)} {...props} />;
const FullScreenIcon = (props) => <Icon component={() => (<FullScreen/>)} {...props} />;
const UserIcon = (props) => <Icon component={() => (<User/>)} {...props} />;
const LinkIcon = (props) => <Icon component={() => (<Link/>)} {...props} />;
const AtIcon = (props) => <Icon component={() => (<At/>)} {...props} />;
const PanleIcon = (props) => <Icon component={() => (<Panle/>)} {...props} />;
const PanleCloseIcon = (props) => <Icon component={() => (<PanleClose/>)} {...props} />;
export {
  EditIcon,
  ClearIcon,
  SendIcon,
  SendActiveIcon,
  AddFlowIcon,
  LeftArrowIcon,
  StartIcon,
  DataRetrievalIcon,
  EndIcon,
  ApiIcon,
  ManualCheckIcon,
  LlmIcon,
  UploadIcon,
  FlowIcon,
  TalkIcon,
  TalkFlowIcon,
  ConfigFlowIcon,
  DownLoadIcon,
  FullScreenIcon,
  UserIcon,
  IfIcon,
  FitIcon,
  LinkIcon,
  AtIcon,
  PanleIcon,
  PanleCloseIcon
}

