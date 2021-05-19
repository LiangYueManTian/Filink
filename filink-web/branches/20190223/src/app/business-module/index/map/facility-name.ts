import {NzI18nService} from 'ng-zorro-antd';
import {
  FacilityStatusCode, FacilityStatusColor, FacilityStatusName, FacilityTypeCode, FacilityTypeIconClass,
  FacilityTypeName
} from './facility';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';

export class FacilityName {
  // 国际化
  indexLanguage: IndexLanguageInterface;
  commonLanguage: CommonLanguageInterface;
  facilityTypeListArr: any[];
  facilityStatusListArr: any[];
  facilityStatusNameObj = {};
  facilityTypeNameObj = {};
  facilityTypeIconClassObj = {};

  constructor(public $nzI18n: NzI18nService) {
    this.indexLanguage = $nzI18n.getLocaleData('index');
    this.commonLanguage = $nzI18n.getLocaleData('common');
    this.facilityTypeListArr = this.facilityTypeList();
    this.facilityStatusListArr = this.facilityStatusList();
    Object.keys(FacilityStatusCode).forEach(key => {
      this.facilityStatusNameObj[FacilityStatusCode[key]] = FacilityStatusName[key];
    });
    Object.keys(FacilityTypeCode).forEach(key => {
      this.facilityTypeNameObj[FacilityTypeCode[key]] = FacilityTypeName[key];
      this.facilityTypeIconClassObj[FacilityTypeCode[key]] = FacilityTypeIconClass[key];
    });
    // console.log(this.facilityTypeListArr);
    // console.log(this.facilityStatusListArr);
  }

  facilityStatusList() {
    return Object.keys(FacilityStatusName).map(key => {
      return {
        value: FacilityStatusCode[key],
        label: this.indexLanguage[FacilityStatusName[key]],
        checked: true,
        bgColor: FacilityStatusColor[key]
      };
    });
  }

  facilityTypeList() {
    return Object.keys(FacilityTypeName).map(key => {
      return {
        value: FacilityTypeCode[key],
        label: this.indexLanguage[FacilityTypeName[key]],
        iconClass: FacilityTypeIconClass[key],
        checked: true,
      };
    });
  }
}
