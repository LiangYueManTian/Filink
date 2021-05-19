export enum FacilityStatusCode {
  'normal' = '1',
  'alarm' = '2',
  'offline' = '5',
  'lost' = '4',
  'unknown' = '3',
}

export enum FacilityStatusName {
  'normal' = 'normal',
  'alarm' = 'alarm',
  'offline' = 'offline',
  'lost' = 'outOfContact',
  'unknown' = 'unknown'
}

export enum FacilityStatusColor {
  'normal' = '#36cfc9',
  'alarm' =  '#fb7257',
  'offline' = '#959595',
  'lost' = '#f8be32',
  'unknown' = '#36a9cf'
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
