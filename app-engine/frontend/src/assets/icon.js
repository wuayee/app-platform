
import Icon, {UploadOutlined} from '@ant-design/icons';
import React from 'react';
import Edit from './svg/edit.svg';
import Clear from './svg/clear.svg';
import ClearChat from './svg/clear2.svg';
import Send from './svg/send2.svg';
import SendActive from './svg/send-active.svg';
import AddFlow from './svg/add-flow.svg';
import LeftArrow from './svg/left-arrow.svg';
import Start from './svg/start.svg';
import DataRetrieval from './svg/data-retrieval.svg';
import Llm from './svg/llm.svg';
import End from './svg/end.svg';
import Api from './svg/api.svg';
import Flow from './svg/flow.svg';
import Talk from './svg/talk.svg';
import TalkFlow from './svg/talk2.svg';
import ConfigFlow from './svg/config2.svg';
import ManualCheck from './svg/manual-check.svg';
import DownLoad from './svg/download.svg';
import FullScreen from './svg/full.svg';
import User from './svg/user.svg';
import If from './svg/if.svg';
import Fit from './svg/fit.svg';
import Link from './images/ai/link.svg';
import At from './images/ai/at.svg';
import Panle from './images/ai/panel.svg';
import PanleClose from './images/ai/panel-active.svg';
import AppBox from './svg/app-box.svg';
import CreateApp from './svg/create-app.svg';
import TabLeft from './images/ai/icon1.svg';
import TabRight from './images/ai/icon2.svg';
import Audio from './images/ai/audio.svg';
import Avatar from './images/avatar-default.svg';
import AppDefault from './svg/app-default.svg';
import History from './svg/history.svg';
import ArrowDown from './svg/arrow_down_normal.svg';
import Languages from './svg/setting.svg';
import Rebot from './svg/rebot.svg';
import Like from './svg/like.svg';
import Unlike from './svg/unlike.svg';
import Share from './svg/share.svg';
import Copy from './svg/copy.svg';
import Delete from './svg/delete.svg';
import DeleteContent from './svg/deleteContent.svg';
import More from './svg/more.svg';
import ClearFile from './svg/clear-file.svg';

// 编辑按钮
const EditIcon = (props) => <Icon component={() => (<Edit/>)} {...props} />;
const ClearIcon = (props) => <Icon component={() => (<Clear/>)} {...props} />;
const ClearChatIcon = (props) => <Icon component={() => (<ClearChat/>)} {...props} />;
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
const AppBoxIcon = (props) => <Icon component={() => (<AppBox/>)} {...props} />;
const CreateAppIcon = (props) => <Icon component={() => (<CreateApp/>)} {...props} />;
const TabLeftIcon = (props) => <Icon component={() => (<TabLeft/>)} {...props} />;
const TabRightIcon = (props) => <Icon component={() => (<TabRight/>)} {...props} />;
const AudioIcon = (props) => <Icon component={() => (<Audio/>)} {...props} />;
const AvatarIcon = (props) => <Icon component={() => (<Avatar />)} {...props} />;
const AppDefaultIcon = (props) => <Icon component={() => (<AppDefault />)} {...props} />;
const HistoryIcon = (props) => <Icon component={() => (<History />)} {...props} />;
const ArrowDownIcon = (props) => <Icon component={() => (<ArrowDown />)} {...props} />;
const LanguagesIcon = (props) => <Icon component={() => (<Languages />)} {...props} />;
const RebotIcon = (props) => <Icon component={() => (<Rebot />)} {...props} />;
const LikeIcon = (props) => <Icon component={() => (<Like />)} {...props} />;
const UnlikeIcon = (props) => <Icon component={() => (<Unlike />)} {...props} />;
const ShareIcon = (props) => <Icon component={() => (<Share />)} {...props} />;
const CopyIcon = (props) => <Icon component={() => (<Copy />)} {...props} />;
const DeleteIcon = (props) => <Icon component={() => (<Delete />)} {...props} />;
const DeleteContentIcon = (props) => <Icon component={() => (<DeleteContent />)} {...props} />;
const MoreIcon = (props) => <Icon component={() => (<More />)} {...props} />;
const ClearFileIcon = (props) => <Icon component={() => (<ClearFile />)} {...props} />;

export {
  EditIcon,
  ClearIcon,
  ClearChatIcon,
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
  PanleCloseIcon,
  AppBoxIcon,
  CreateAppIcon,
  TabLeftIcon,
  TabRightIcon,
  AudioIcon,
  AvatarIcon,
  AppDefaultIcon,
  HistoryIcon,
  ArrowDownIcon,
  LanguagesIcon,
  RebotIcon,
  LikeIcon,
  UnlikeIcon,
  ShareIcon,
  CopyIcon,
  DeleteIcon,
  DeleteContentIcon,
  MoreIcon,
  ClearFileIcon
}

