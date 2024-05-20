import ManageCubeCreateReport from "./ManageCubeCreateReport.jsx";
import {v4 as uuidv4} from "uuid";
import React from 'react';

export const manageCubeCreateReportComponent = (jadeConfig) => {
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
                    {id: uuidv4(), type: "Array", from: "value", value: "reportResult"},
                ]
            }
        ]
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><ManageCubeCreateReportComponent/></>);
    };

    /**
     * 必须.
     */
    self.reducers = (data, action) => {

    };

    return self;
};

const ManageCubeCreateReportComponent = () => {
    return (<>
        <ManageCubeCreateReport/>
    </>)
};
