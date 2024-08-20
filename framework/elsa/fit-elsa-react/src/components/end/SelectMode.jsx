import {JadeStopPropagationSelect} from "@/components/common/JadeStopPropagationSelect.jsx";
import {useDispatch} from "@/components/DefaultRoot.jsx";
import {v4 as uuidv4} from "uuid";
import { useTranslation, Trans } from "react-i18next";

/**
 * 选择模式的组件
 *
 * @param mode 模式
 * @param onSelectChange 更改模式的回调
 * @returns {JSX.Element}
 * @constructor
 */
export default function SelectMode({mode, disabled}) {
    const dispatch = useDispatch();
    const { t } = useTranslation();

    /**
     * 模式切换后的回调
     *
     * @param value 模式的值
     */
    const onChange = (value) => {
        if (value === mode) {
            return;
        }
        let config;
        if (value === "manualCheck") {
            config = [{
                id: uuidv4(),
                name: "endFormId",
                type: "String",
                from: "Input",
                value: ''
            }, {
                id: uuidv4(),
                name: "endFormName",
                type: "String",
                from: "Input",
                value: ''
            }]
        } else {
            config = [{
                id: uuidv4(),
                name: "finalOutput",
                type: "String",
                from: "Reference",
                referenceNode: "",
                referenceId: "",
                referenceKey: "",
                value: []
            }]
        }
        dispatch({type: "changeMode", value: config});
    };

    return (<>
        <div style={{display: 'flex', alignItems: 'center'}}>
            <div className="mode-select-title jade-panel-header-font">{t('modeSelect')}</div>
            <JadeStopPropagationSelect
                    disabled={disabled}
                    showSearch
                    optionFilterProp="children"
                    onChange={onChange}
                    defaultValue='mode-variables'
                    value={mode}
                    options={[
                        {value: 'variables', label: t('directlyOutputTheResult')},
                        {value: 'manualCheck', label: "智能表单展示结果"},
                    ]}
                    style={{width: '70%', marginLeft: 'auto'}}
            >
            </JadeStopPropagationSelect>
        </div>
    </>);
}