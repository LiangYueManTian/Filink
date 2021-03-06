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
  // 表格通用分页配置
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  // 表格通用查询条件
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

  /**
   * 通用查询
   */
  searchList() {
  }

  /**
   * 通用查询表单数据
   */
  searchFromData() {}
  /**
   * 监听页面切换
   * param event
   */
  pageChange(event) {
    this.pageBean.pageIndex = event.pageIndex;
    this.pageBean.pageSize = event.pageSize;
    this.searchList();
  }

  /**
   * 手動查詢
   * param event
   */
  handleSearch(event) {
    this.queryConditions.filterConditions = event;
    this.pageBean = new PageBean(this.pageBean.pageSize, 1, this.pageBean.Total);
    this.searchList();
  }
}
