import React from 'react';
import Icon from '@ant-design/icons';
import { Medical, Finance, Education, Link } from './base.tsx';

const MedicalIcon = (props) => <Icon component={() => (<Medical />)} {...props} />;
const FinanceIcon = (props) => <Icon component={() => (<Finance />)} {...props} />;
const EducationIcon = (props) => <Icon component={() => (<Education />)} {...props} />;
const LinkIcon = (props) => <Icon component={() => (<Link />)} {...props} />;
export {
    MedicalIcon,
    FinanceIcon,
    EducationIcon,
    LinkIcon
}