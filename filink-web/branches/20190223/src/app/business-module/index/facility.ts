export enum FacilityStatusCode {
  'normal' = '1',
  'alarm' = '2',
  'offline' = '5',
  'outOfContact' = '4',
  'unknown' = '3',
}

export enum FacilityStatusName {
  'normal' = 'normal',
  'alarm' = 'alarm',
  'offline' = 'offline',
  'outOfContact' = 'outOfContact',
  'unknown' = 'unknown'
}

export enum FacilityStatusColor {
  'normal' = '#36D1C9',
  'alarm' =  '#FB7356',
  'offline' = '#959595',
  'outOfContact' = '#F8C032',
  'unknown' = '#35AACE'
}

export function facilityStatusList() {
  return Object.keys(FacilityStatusName).map(key => {
    return {
      value: FacilityStatusCode[key],
      label: FacilityStatusName[key],
      checked: true,
      bgColor: FacilityStatusColor[key]
    };
  });
}

export enum FacilityTypeCode {
  'patchPanel' = '060', // 配线架
  'opticalBox' = '001', // 光交箱
  'manWell' = '030',   // 人井
  'jointClosure' = '090',  // 接头盒
  'fiberBox' = '150',   // 分纤箱
}

export enum FacilityTypeName {
  'patchPanel' = 'patchPanel',
  'opticalBox' = 'opticalBox',
  'manWell' = 'manWell',
  'jointClosure' = 'jointClosure',
  'fiberBox' = 'fiberBox',
}

export enum FacilityTypeIconClass {
  'patchPanel' = 'icon-patchPanel',
  'opticalBox' = 'icon-opticalBox',
  'manWell' = 'icon-manWell',
  'jointClosure' = 'icon-jointClosure',
  'fiberBox' = 'icon-fiberBox',
}


export function facilityTypeList() {
  return Object.keys(FacilityTypeName).map(key => {
    return {
      value: FacilityTypeCode[key],
      label: FacilityTypeName[key],
      iconClass: FacilityTypeIconClass[key],
      checked: true,
    };
  });
}
