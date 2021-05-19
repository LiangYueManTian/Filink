import {NzI18nService} from 'ng-zorro-antd';
import {Injectable} from '@angular/core';
import {FormOperate} from '../shared-module/component/form/form-opearte.service';
import {FormItem} from '../shared-module/component/form/form-config';
import {PageBean} from '../shared-module/entity/pageBean';
import {TableConfig} from '../shared-module/entity/tableConfig';

@Injectable()
export class BasicConfig {
  // 国际化
  language: any = {};
  // 表单通用配置
  submitLoading = false;
  formStatus: FormOperate;
  formColumn: FormItem[] = [];
  // 表格通用配置
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryConditions = {
    pageCondition: {},
    filterConditions: [],
    sortCondition: {},
    bizCondition: {}
  };

  constructor(public $nzI18n: NzI18nService) {
    this.language = $nzI18n.getLocale();
  }

  /**
   * 处理表格编号
   * param data
   * param pageBean
   */
  dealIndex(data, pageBean: PageBean) {
    let i = 1;
    return data.map(item => {
      item.indexNo = pageBean.pageSize * (pageBean.pageIndex - 1) + i;
      i++;
      return item;
    });
  }

  /**
   * 通用表单方法
   * param event
   */
  formInstance(event) {
    this.formStatus = event.instance;
  }
  /**
   * 确定
   */
  formHandleOk() {
  }

  /**
   * 取消
   */
  formHandleCancel() {
  }

  /**
   * 恢复默认
   */
  formHandleReset() {
  }
}
