import {Input} from "antd";
import {useDataContext, useDispatch} from "@/components/DefaultRoot.jsx";
import {JadeObservableInput} from "@/components/common/JadeObservableInput.jsx";

export const listener1Component = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : [{
            id: "listener1-name",
            name: "name",
            type: "String",
            value: "请输入一个名字"
        }, {
            id: "listener1-firstName",
            name: "firstName",
            type: "String",
            value: "请输入第一名字"
        }];
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><Component/></>);
    };

    /**
     * 必须.
     */
    self.reducers = (config, action) => {
        switch (action.type) {
            case 'updateName': {
                return config.map(c => {
                    if (c.id === action.id) {
                        return {...c, name: action.name}
                    } else {
                        return c;
                    }
                });
            }
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
};

const Component = () => {
    const data = useDataContext();
    const dispatch = useDispatch();

    const onNameChange = (id, e) => {
        dispatch({type: "updateName", id,  name: e.target.value});
    };

    return (<>
        <div>
            <JadeObservableInput id={data[0].id}
                                 value={data[0].name}
                                 onChange={(e) => onNameChange(data[0].id, e)}/>
            <JadeObservableInput id={data[1].id}
                                 parent={data[0].id}
                                 value={data[1].name}
                                 onChange={(e) => onNameChange(data[1].id, e)}/>
        </div>
    </>);
};