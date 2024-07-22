import FileContent from "./FileContent";
import {v4 as uuidv4} from "uuid";
import React from 'react';

export const fileContentComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : [
            {
                id: uuidv4(),
                name: "output",
                type: "Object",
                from: "value",
                value: [
                    {id: uuidv4(), type: "Array", from: "value", value: "fileContent"},
                ]
            }
        ]
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><FileContentComponent /></>);
    };

    /**
     * 必须.
     */
    self.reducers = (data, action) => {

    };

    return self;
};

const FileContentComponent = () => {
    return (<>
        <FileContent />
    </>)
};
