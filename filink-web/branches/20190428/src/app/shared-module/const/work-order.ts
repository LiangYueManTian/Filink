export const WORK_ORDER_STATUS = {
  'assigned': 'assigned',   // 待指派
  'pending': 'pending',   // 待处理
  'processing': 'processing',   // 处理中
  'completed': 'completed',   // 已完成
  'singleBack': 'singleBack',   // 已退单
  'turnProcessing': 'turnProcessing'
};

export const WORK_ORDER_STATUS_CLASS = {
  'assigned': 'fiLink-assigned-w statistics-assigned-color',
  'pending': 'fiLink-processed statistics-pending-color',
  'processing': 'fiLink-processing statistics-processing-color',
  'turnProcessing': 'fiLink-processing statistics-processing-color',
  'completed': 'fiLink-completed statistics-completed-color',
  'singleBack': 'fiLink-chargeback statistics-singleBack-color',
};

export const WORK_ORDER_TYPE = [
  {
    'value': 'clear_failure',
    'label': 'clearBarrier'
  },
  {
    'value': 'inspection',
    'label': 'inspection'
  }
];

export const WORK_ORDER_ERROR_REASON_CODE = {
    other: '0', // 0 其他
    personDamage: '1', // 1 人为损坏
    RoadConstruction: '2', // 2 道路施工
    stealWear: '3', // 3 盗穿
    clearBarrier: '4' // 4 销障
};

export const WORK_ORDER_ERROR_REASON_NAME = {
  other: 'other',
  personDamage: 'personDamage',
  RoadConstruction: 'RoadConstruction',
  stealWear: 'stealWear',
  clearBarrier: 'clearBarrier'
};

export const WORK_ORDER_SINGLE_BACK_REASON_CODE = {
  other: '0', // 0 其他
  FalsePositives: '1' // 1人为损坏
};

export const WORK_ORDER_SINGLE_BACK_REASON_NAME = {
  other: 'other',
  FalsePositives: 'FalsePositives'
};

export const WORK_ORDER_PROCESSING_SCHEME_NAME = {
  custom: 'custom',
  repair: 'repair',
  destruction: 'destruction'
};

export const WORK_ORDER_PROCESSING_SCHEME_CODE = {
  custom: '0', // 0-自定义（对应故障原因-其他)
  repair: '1', // 1-报修（对应故障原因-人为损坏，道路施工，盗穿）
  destruction: '2' // 2 - 现场销障（对应故障原因-销障）
};
