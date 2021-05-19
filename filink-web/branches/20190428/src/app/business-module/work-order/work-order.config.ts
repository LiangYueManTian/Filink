import {NzI18nService} from 'ng-zorro-antd';
import {IndexLanguageInterface} from '../../../assets/i18n/index/index.language.interface';
import {WorkOrderLanguageInterface} from '../../../assets/i18n/work-order/work-order.language.interface';
import {
  FACILITY_STATUS_COLOR,
  FACILITY_STATUS_NAME,
  FACILITY_TYPE_ICON_CLASS,
  FACILITY_TYPE_NAME
} from '../../shared-module/const/facility';
import {
  WORK_ORDER_ERROR_REASON_CODE,
  WORK_ORDER_ERROR_REASON_NAME,
  WORK_ORDER_PROCESSING_SCHEME_CODE,
  WORK_ORDER_PROCESSING_SCHEME_NAME,
  WORK_ORDER_SINGLE_BACK_REASON_CODE,
  WORK_ORDER_SINGLE_BACK_REASON_NAME,
  WORK_ORDER_STATUS,
  WORK_ORDER_STATUS_CLASS,
  WORK_ORDER_TYPE
} from '../../shared-module/const/work-order';
import {TableBasic} from '../../shared-module/component/table/table.basic';
import {FacilityLanguageInterface} from '../../../assets/i18n/facility/facility.language.interface';

export class WorkOrderConfig extends TableBasic {
  // 国际化
  indexLanguage: IndexLanguageInterface;
  facilityLanguage: FacilityLanguageInterface;
  workOrderLanguage: WorkOrderLanguageInterface;
  facilityTypeListArr: any[];
  facilityStatusListArr: any[];
  workOrderStatusListArr: any[];
  workOrderTypeListArr: any[];
  errorReasonListArr: any[];
  singleBackReasonListArr: any[];
  processingSchemeListArr: any[];
  procType;

  constructor(
    public $nzI18n: NzI18nService
  ) {
    super($nzI18n);
    this.indexLanguage = $nzI18n.getLocaleData('index');
    this.workOrderLanguage = $nzI18n.getLocaleData('workOrder');
    this.facilityLanguage = $nzI18n.getLocaleData('facility');
    this.facilityTypeListArr = this.facilityTypeList();
    this.facilityStatusListArr = this.facilityStatusList();
    this.errorReasonListArr = this.errorReasonList();
    this.singleBackReasonListArr = this.singleBackReasonList();
    this.processingSchemeListArr = this.processingSchemeList();
    this.getWorkOrderStatusListArr();
    this.getWorkOrderTypeList();
  }

  facilityStatusList() {
    return Object.keys(FACILITY_STATUS_NAME).filter(key => key !== '0').map(key => {
      return {
        value: key,
        label: this.indexLanguage[FACILITY_TYPE_NAME[key]],
      };
    });
  }

  facilityTypeList() {
    return Object.keys(FACILITY_TYPE_NAME).map(key => {
      return {
        value: key,
        label: this.indexLanguage[FACILITY_TYPE_NAME[key]],
      };
    });
  }

  errorReasonList() {
    return Object.keys(WORK_ORDER_ERROR_REASON_NAME).map(key => {
      return {
        value: WORK_ORDER_ERROR_REASON_CODE[key],
        label: this.workOrderLanguage[WORK_ORDER_ERROR_REASON_NAME[key]],
        // iconClass: FacilityTypeIconClass[key],
        // checked: true,
      };
    });
  }

  singleBackReasonList() {
    return Object.keys(WORK_ORDER_SINGLE_BACK_REASON_NAME).map(key => {
      return {
        value: WORK_ORDER_SINGLE_BACK_REASON_CODE[key],
        label: this.workOrderLanguage[WORK_ORDER_SINGLE_BACK_REASON_NAME[key]],
      };
    });
  }

  processingSchemeList() {
    return Object.keys(WORK_ORDER_PROCESSING_SCHEME_NAME).map(key => {
      return {
        value: WORK_ORDER_PROCESSING_SCHEME_CODE[key],
        label: this.workOrderLanguage[WORK_ORDER_PROCESSING_SCHEME_NAME[key]],
      };
    });
  }
  /**
   * 获取设施类型名称
   * param deviceType
   * returns {any | string}
   */
  public getFacilityTypeName(deviceType) {
    return deviceType ? this.indexLanguage[FACILITY_TYPE_NAME[deviceType]] : '';
  }

  /**
   * 获取工单类型名称
   * param status
   * returns {any | string}
   */
  getStatusName(status) {
    return this.workOrderLanguage[WORK_ORDER_STATUS[status]] || '';
  }

  /**
   * 获取工单类型样式
   * param status
   * returns {string}
   */
  getStatusClass(status) {
    return `iconfont icon-fiLink ${WORK_ORDER_STATUS_CLASS[status]}`;
  }

  /**
   * 获取工单状态列表
   */
  getWorkOrderStatusListArr() {
    const arr = [];
    for (const key in WORK_ORDER_STATUS) {
      if (WORK_ORDER_STATUS.hasOwnProperty(key)) {
        arr.push({
          value: key,
          label: this.getStatusName(key)
        });
      }
    }
    this.workOrderStatusListArr = arr;
  }

  /**
   * 获取工单类型列表
   */
  getWorkOrderTypeList() {
    this.workOrderTypeListArr = WORK_ORDER_TYPE.map(item => {
      return {
        value: item.value,
        label: this.workOrderLanguage[item.label]
      };
    });
  }
}
