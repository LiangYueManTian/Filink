import {Component, OnInit} from '@angular/core';
import {FormItem} from '../../../../../shared-module/component/form/form-config';
import {ActivatedRoute} from '@angular/router';
import {LockService} from '../../../../../core-module/api-service/lock';
import {Result} from '../../../../../shared-module/entity/result';
import {FormOperate} from '../../../../../shared-module/component/form/form-opearte.service';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {FacilityService} from '../../../../../core-module/api-service/facility/facility-manage';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';
import {NzI18nService} from 'ng-zorro-antd';

/**
 * 配置传感器阀值组件
 */
@Component({
  selector: 'app-configuration-strategy',
  templateUrl: './configuration-strategy.component.html',
  styleUrls: ['./configuration-strategy.component.scss']
})
export class ConfigurationStrategyComponent implements OnInit {
  // 设施id
  private deviceId: any;
  // 提交按钮是否加载
  isLoading = false;
  // 表单配置数据
  formColumnData = [];
  // 序列号
  private serialNum: string;
  // 参数配置值
  public pramsConfigData: any = [];
  // 没有数据
  noDataShow = false;
  // 设施类型
  private deviceType: string;
  // 设施语言包
  public language: FacilityLanguageInterface;
  // 校验信息是否通过
  checkedInfo = true;
  constructor(private $lockService: LockService,
              private $modalService: FiLinkModalService,
              private $facilityService: FacilityService,
              private $nzI18n: NzI18nService,
              private $active: ActivatedRoute) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.$active.queryParams.subscribe(params => {

      this.deviceId = params.id;
      this.serialNum = params.serialNum;
      this.deviceType = params.deviceType;
      // 获取表单html
      this.getPramsConfig();
    });
  }

  formInstance(event, index) {
    this.formColumnData[index]['formInstance'] = event;
    this.formColumnData[index]['formInstance'].instance.group.statusChanges.subscribe (() => {
      this.checkedInfo = this.checked();
    });
  }

  /**
   * 创建表单
   * param data
   */
  createFormColumn(data) {
    const arr = [];
    data.forEach(item => {
      const formItem = new FormItem();
      formItem.key = item.id;
      formItem.type = item.type;
      formItem.label = item.name;
      formItem.col = 24;
      formItem.require = true;
      formItem.rule = item.rules || [];
      if (item.placeholder) {
        formItem.placeholder = item.placeholder;
      }
      if (item.unit) {
        formItem.suffix = item.unit;
      }
      if (formItem.type === 'select') {
        formItem.selectInfo = {
          data: item.selectParams,
          label: 'name',
          value: 'id',
        };
      }
      arr.push(formItem);
    });
    return arr;
  }

  /**
   * 设置策略
   */
  setControl() {
    const data = JSON.parse(sessionStorage.getItem('facility_config_info'));
    let body = {};
    this.formColumnData.forEach(item => {
      const formInstance = item.formInstance.instance as FormOperate;
      body = Object.assign(body, formInstance.getData());
    });
    const __body = {
      deviceIds: [],
      configParams: body
    };
    if (this.deviceId) {
      __body.deviceIds = [this.deviceId];
    } else {
      __body.deviceIds = data.map(item => item.deviceId);
    }
    this.$lockService.setControl(__body).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$modalService.success(result.msg);
        window.history.go(-1);
      } else {
        this.$modalService.error(result.msg);
      }
    });
  }

  /**
   * 获取参数配置
   */
  getPramsConfig() {
    this.$facilityService.getPramsConfig(this.deviceType).subscribe((result: Result) => {
      this.noDataShow = true;
      this.pramsConfigData = result.data || [];
      this.pramsConfigData.forEach((item: any) => {
        item.formColumn = this.createFormColumn(item.configParams);
      });
      this.formColumnData = result.data;
      if (this.deviceId) {
        this.getConfigValue();
      }
    });
  }

  /**
   * 获取参数配置的值
   */
  getConfigValue() {
    this.$lockService.getLockControlInfo(this.deviceId).subscribe((result: Result) => {
      if (result.data && result.data[0] && result.data[0].configValue) {
        const configValue = JSON.parse(result.data[0].configValue);
        this.formColumnData.forEach(item => {
          const formInstance = item.formInstance.instance as FormOperate;
          formInstance.resetData(configValue);
        });
      }

    });
  }

  /**
   * 返回
   */
  goBack() {
    window.history.go(-1);
  }

  checked() {
    let pass = true;
    // 默认通过所有都通过校验
    this.formColumnData.forEach(item => {
      // 如果有一个没有通过校验
      if (item['formInstance'].instance.getValid()) {

      } else {
        pass = false;
      }
    });
    return !pass;
  }
}