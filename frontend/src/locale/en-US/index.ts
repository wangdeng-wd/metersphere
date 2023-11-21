import common from './common';
import localeSettings from './settings';
import sys from './sys';
import dayjsLocale from 'dayjs/locale/en';

const _Cmodules: any = import.meta.glob('../../components/**/locale/en-US.ts', { eager: true });
const _Vmodules: any = import.meta.glob('../../views/**/locale/en-US.ts', { eager: true });
let result = {};
Object.keys(_Cmodules).forEach((key) => {
  const defaultModule = _Cmodules[key as any].default;
  if (!defaultModule) return;
  result = { ...result, ...defaultModule };
});
Object.keys(_Vmodules).forEach((key) => {
  const defaultModule = _Vmodules[key as any].default;
  if (!defaultModule) return;
  result = { ...result, ...defaultModule };
});

export default {
  message: {
    'menu.workbench': 'Workbench',
    'menu.testPlan': 'Test Plan',
    'menu.bugManagement': 'Bug',
    'menu.featureTest': 'Feature Test',
    'menu.apiTest': 'API Test',
    'menu.uiTest': 'UI Test',
    'menu.performanceTest': 'Performance Test',
    'menu.projectManagement': 'Project',
    'menu.projectManagement.fileManagement': 'File Management',
    'menu.projectManagement.messageManagement': 'Message Management',
    'menu.projectManagement.messageManagementEdit': 'Update Template',
    'menu.featureTest.featureCase': 'Feature Case',
    'meun.workstation': 'Workstation',
    'menu.loadTest': 'Performance Test',
    'menu.caseManagement': 'Feature Test',
    'menu.projectManagement.projectPermission': 'Project Permission',
    'menu.projectManagement.log': 'Log',
    'menu.settings': 'Settings',
    'menu.settings.system': 'System',
    'menu.settings.system.usergroup': 'User Group',
    'menu.settings.system.authorizedManagement': 'Authorized Management',
    'menu.settings.system.pluginManager': 'Plugin Manger',
    'menu.settings.system.user': 'User',
    'menu.settings.system.organizationAndProject': 'Org & Project',
    'menu.settings.system.resourcePool': 'Resource Pool',
    'menu.settings.system.resourcePoolDetail': 'Add resource pool',
    'menu.settings.system.resourcePoolEdit': 'Edit resource pool',
    'menu.settings.system.parameter': 'System Parameter',
    'menu.settings.system.log': 'Log',
    'menu.settings.organization': 'Organization',
    'menu.settings.organization.member': 'Member',
    'menu.settings.organization.userGroup': 'User Group',
    'menu.settings.organization.project': 'Project',
    'menu.settings.organization.template': 'Template',
    'menu.settings.organization.bugTemplate': 'BUG Template',
    'menu.settings.organization.templateFieldSetting': 'fieldSetting',
    'menu.settings.organization.templateManagementList': 'Template list',
    'menu.settings.organization.templateManagementEdit': 'Update Template',
    'menu.settings.organization.templateManagementDetail': 'Create Template',
    'menu.settings.organization.templateManagementWorkFlow': 'WorkFlow Setting',
    'menu.settings.organization.serviceIntegration': 'Service Integration',
    'menu.settings.organization.log': 'Log',
    'navbar.action.locale': 'Switch to English',
    ...sys,
    ...localeSettings,
    ...result,
    ...common,
  },
  dayjsLocale,
  dayjsLocaleName: 'en-US',
};