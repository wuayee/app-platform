import {useDataContext, useDispatch} from "@/components/DefaultRoot.jsx";
import {JadeReferenceTreeSelect} from "@/components/common/JadeReferenceTreeSelect.jsx";

export const listener2Component = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : [{
            id: "123456", name: "zzzzz", type: "Reference", value: [], referenceNode: "", referenceId: "", referenceKey: ""
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
            case 'updateValue': {
                return config.map(c => {
                    if (c.id === action.id) {
                        return {...c, referenceKey: action.referenceKey};
                    } else {
                        return c;
                    }
                });
            }
            case 'update': {
                return config.map(c => {
                    if (c.id === action.id) {
                        return {...c, referenceNode: action.referenceNode, referenceId: action.referenceId, value: action.value};
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
    const dispatch = useDispatch();
    const data = useDataContext();

    return (<>
        <JadeReferenceTreeSelect reference={data[0]} onReferencedValueChange={(v) => {
            dispatch({type: "updateValue", id: data[0].id, referenceKey: v});
        }} onReferencedKeyChange={(e) => {
            dispatch({type: "update", id: data[0].id, referenceNode: e.referenceNode, referenceId: e.referenceId, value: e.value});
        }}/>
    </>);
};