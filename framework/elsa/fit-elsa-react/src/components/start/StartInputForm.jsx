import Name from './Name.jsx';
import Type from './Type.jsx';
import Description from './Description.jsx';
import PropTypes from 'prop-types'; // 导入 PropTypes
import {useDispatch} from "@/components/DefaultRoot.jsx";
import Required from "@/components/start/Required.jsx";
import React from "react";

_StartInputForm.propTypes = {
    item: PropTypes.shape({
        id: PropTypes.string.isRequired,
        name: PropTypes.string.isRequired,
        type: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        from: PropTypes.string.isRequired,
        value: PropTypes.string.isRequired,
        disableModifiable: PropTypes.bool,
        isRequired: PropTypes.bool
    }).isRequired
};

/**
 * 开始节点入参表单
 *
 * @param item 表单对应的对象
 * @returns {JSX.Element} 开始节点入参表单的DOM
 */
function _StartInputForm({item}) {
    const dispatch = useDispatch();
    const formId = item.id;

    /**
     * 传递至子组件，用于使用dispatch更新
     *
     * @param type 子组件对应的type
     * @param value 更新后的value
     */
    const handleFormValueChange = (type, value) => {
        dispatch({actionType: "changeInputParam", id: formId, type: type, value: value});
    };

    return (<>
        <Name itemId={item.id}
              propValue={item.name}
              type={item.type}
              disableModifiable={item.disableModifiable}
              onChange={handleFormValueChange}/>
        <Type itemId={item.id} propValue={item.type} disableModifiable={item.disableModifiable} onChange={handleFormValueChange}/>
        <Description itemId={item.id} propValue={item.description} disableModifiable={item.disableModifiable} onChange={handleFormValueChange}/>
        <Required itemId={item.id} propValue={item.isRequired} disableModifiable={item.disableModifiable} onChange={handleFormValueChange}/>
    </>);
}

const areEqual = (prevProps, nextProps) => {
    return prevProps.disabled === nextProps.disabled
            && prevProps.item.id === nextProps.item.id
            && prevProps.item.name === nextProps.item.name
            && prevProps.item.type === nextProps.item.type
            && prevProps.item.description === nextProps.item.description
            && prevProps.item.from === nextProps.item.from
            && prevProps.item.value === nextProps.item.value
            && prevProps.item.disableModifiable === nextProps.item.disableModifiable
            && prevProps.item.isRequired === nextProps.item.isRequired;
};

export const StartInputForm =  React.memo(_StartInputForm, areEqual);