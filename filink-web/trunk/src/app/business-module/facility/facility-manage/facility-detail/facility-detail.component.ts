import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {FormItem} from '../../../../shared-module/component/form/form-config';
import {FormOperate} from '../../../../shared-module/component/form/form-opearte.service';
import {Result} from '../../../../shared-module/entity/result';
import {NzI18nService, NzModalService, NzTreeNode} from 'ng-zorro-antd';
import {ActivatedRoute, Router} from '@angular/router';
import {FacilityService} from '../../../../core-module/api-service/facility/facility-manage';
import {getDeviceType} from '../../facility.config';
import {FacilityUtilService} from '../../facility-util.service';
import {TreeSelectorConfig} from '../../../../shared-module/entity/treeSelectorConfig';
import {Area} from '../../../../core-module/entity/facility/area';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {RuleUtil} from '../../../../shared-module/util/rule-util';
import {FormLanguageInterface} from '../../../../../assets/i18n/form/form.language.interface';
import {SessionUtil} from '../../../../shared-module/util/session-util';

declare const $: any;
declare const cityData: any;

/**
 * 新增（修改）设施组件
 */
@Component({
  selector: 'app-facility-detail',
  templateUrl: './facility-detail.component.html',
  styleUrls: ['./facility-detail.component.scss']
})
export class FacilityDetailComponent implements OnInit, AfterViewInit {
  // 表单配置
  formColumn: FormItem[] = [];
  // 表单状态
  formStatus: FormOperate;
  // 设施语言包
  public language: FacilityLanguageInterface;
  // 自定义模板
  @ViewChild('customTemplate') private customTemplate;
  // 位置选择模板
  @ViewChild('positionTemplate') private positionTemplate;
  // 区域选择器显示隐藏
  areaSelectVisible: boolean = false;
  // 页面类型 新增修改
  pageType = 'add';
  // 页面标题
  pageTitle: string;
  // 城市信息
  cityInfo: any = {province: '', city: '', district: '', detailInfo: ''};
  // 已选择的点
  selectPoint = {lat: 0, lng: 0};
  // 设施地理位置
  facilityAddress = '';
  // 设施id
  deviceId: string;
  // 地理位置选择器显示隐藏
  isVisible = false;
  // 区域名称
  areaName = '';
  // 责任单位选择器
  selectorData: any = {parentId: '', accountabilityUnit: ''};
  // 区域选择器配置信息
  areaSelectorConfig: any = new TreeSelectorConfig();
  // 区域信息
  areaInfo: Area = new Area();
  // 区域选择节点
  private areaNodes: any = [];
  // 区域选择器
  @ViewChild('areaSelector') private areaSelector;
  // 是否加载
  isLoading = false;
  // 页面是否加载
  pageLoading = false;
  // 区域禁用
  public areaDisabled: boolean;
  // 地理位置选择禁用
  public positionDisabled: boolean;
  // 表单语言包
  formLanguage: FormLanguageInterface;

  constructor(private $nzI18n: NzI18nService,
              private $active: ActivatedRoute,
              private $facilityUtilService: FacilityUtilService,
              private $facilityService: FacilityService,
              private $modalService: FiLinkModalService,
              private $modelService: NzModalService,
              private $ruleUtil: RuleUtil,
              private $router: Router) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.formLanguage = this.$nzI18n.getLocaleData('form');
    this.initColumn();
    this.pageType = this.$active.snapshot.params.type;
    this.pageTitle = this.getPageTitle(this.pageType);
    if (this.pageType !== 'add') {
      this.$active.queryParams.subscribe(params => {
        this.deviceId = params.id;
        this.pageLoading = true;
        this.$facilityService.deviceCanChangeDetail(this.deviceId).subscribe((result: Result) => {
          if (result.code !== 0) {
            this.modifyPermission();
          }
        });
        this.$facilityUtilService.getArea().then((data: NzTreeNode[]) => {
          this.areaNodes = data;
          this.initAreaSelectorConfig(data);
          this.queryDeviceById();
        });
      });
    } else {
      this.$facilityUtilService.getArea().then((data: NzTreeNode[]) => {
        this.areaNodes = data;
        // 递归设置区域的选择情况
        this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, null, null);
        this.initAreaSelectorConfig(data);
      });
    }
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }

  /**
   * 新增区域
   */
  addFacility() {
    this.isLoading = true;
    const data = this.formStatus.group.getRawValue();
    data['provinceName'] = this.cityInfo.province;
    data['cityName'] = this.cityInfo.city;
    data['districtName'] = this.cityInfo.district;
    const positionBase = `${this.cityInfo.detailInfo.lng},${this.cityInfo.detailInfo.lat}`;
    data['positionBase'] = positionBase;
    data['positionGps'] = '12,33';
    if (this.pageType === 'add') {
      this.$facilityService.addDevice(data).subscribe((result: Result) => {
        this.isLoading = false;
        if (result.code === 0) {
          this.goBack();
          this.$modalService.success(result.msg);
        } else {
          this.$modalService.error(result.msg);
        }
      }, () => {
        this.isLoading = false;
      });
    } else if (this.pageType === 'update') {
      data['deviceId'] = this.deviceId;
      this.$facilityService.updateDeviceById(data).subscribe((result: Result) => {
        this.isLoading = false;
        if (result.code === 0) {
          this.goBack();
          this.$modalService.success(result.msg);
        } else {
          this.$modalService.error(result.msg);
        }
      }, () => {
        this.isLoading = false;
      });
    }
  }

  goBack() {
    window.history.go(-1);
  }

  /**
   * 打开地理位置选择器
   */
  showModal(): void {
    this.isVisible = true;
  }

  /**
   * 打开区域选择器
   */
  showAreaSelectorModal() {
    if (this.areaDisabled) {
      return;
    }
    this.areaSelectorConfig.treeNodes = this.areaNodes;
    this.areaSelectVisible = true;
  }

  /**
   * 区域选中结果
   * param event
   */
  areaSelectChange(event) {
    if (event[0]) {
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, event[0].areaId);
      this.areaName = event[0].areaName;
      this.selectorData.parentId = event[0].areaId;
      this.formStatus.resetControlData('areaId', event[0].areaId);
    } else {
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, null);
      this.areaName = '';
      this.selectorData.parentId = null;
      this.formStatus.resetControlData('areaId', null);

    }
  }

  /**
   * 地图选择器结果
   * param result
   */
  selectDataChange(result: any) {
    if (result.addressComponents.province && result.addressComponents.city && result.addressComponents.district) {
      this.cityInfo = result.addressComponents;
      this.cityInfo.detailInfo = result.point;
      this.selectPoint = result.point;
      const str = `${result.addressComponents.province},${result.addressComponents.city},${result.addressComponents.district}`;
      this.facilityAddress = result.address;
      this.formStatus.resetControlData('position', str);
      this.formStatus.resetControlData('address', result.address);
    }
  }

  /**
   * 根据id查询设备详情
   */
  private queryDeviceById() {
    this.$facilityService.queryDeviceById(this.deviceId).subscribe((result: Result) => {
      this.pageLoading = false;
      if (result.code === 0) {
        this.formStatus.resetData(result.data);
        this.facilityAddress = result.data.address;
        this.formStatus.group.controls['deviceType'].disable();
        this.formStatus.group.controls['areaId'].reset(result.data.areaInfo.areaId);
        if (result.data.provinceName && result.data.cityName && result.data.districtName) {
          this.cityInfo.province = result.data.provinceName;
          this.cityInfo.city = result.data.cityName;
          this.cityInfo.district = result.data.districtName;
          const str = `${result.data.provinceName},${result.data.cityName},${result.data.districtName}`;
          this.formStatus.resetControlData('position', str);
        }
        // 地址选择器
        const position = result.data.positionBase.split(',');
        const _lng = parseFloat(position[0]);
        const __lat = parseFloat(position[1]);
        this.selectPoint.lat = __lat;
        this.selectPoint.lng = _lng;
        this.cityInfo.detailInfo = this.selectPoint;
        // 递归设置区域的选择情况
        this.areaName = result.data.areaInfo.areaName;
        this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, result.data.areaInfo.areaId);
      } else if (result.code === 130204) {
        this.$modalService.error(result.msg);
        this.goBack();
      }
    }, () => {
      this.pageLoading = false;
    });
  }

  /**
   * 初始化表单配置
   */
  private initColumn() {
    this.formColumn = [
      {
        label: this.language.deviceName,
        key: 'deviceName',
        type: 'input',
        require: true,
        rule: [
          {required: true},
          RuleUtil.getNameMaxLengthRule(),
          this.$ruleUtil.getNameRule()
        ],
        customRules: [this.$ruleUtil.getNameCustomRule()],
        asyncRules: [
          this.$ruleUtil.getNameAsyncRule((value) => {
            return this.$facilityService.queryDeviceNameIsExist({deviceId: this.deviceId, deviceName: value});
          }, (res) => {
            return res['data'];
          })
        ]
      },
      {
        label: this.language.deviceType, key: 'deviceType', type: 'select',
        selectInfo: {
          data: this.getRoleDeviceType(),
          label: 'label',
          value: 'code'
        },
        modelChange: (controls, $event, key) => {
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        label: this.language.parentId, key: 'areaId', type: 'custom',
        require: true,
        template: this.areaSelector,
        modelChange: (controls, $event, key) => {
        },
        rule: [{required: true}], asyncRules: []
      },
      {
        label: this.language.position,
        key: 'position',
        type: 'custom',
        require: true,
        rule: [{required: true}],
        template: this.positionTemplate
      },
      {
        label: this.language.region,
        key: 'managementFacilities', type: 'custom', rule: [], require: true, template: this.customTemplate
      },
      {
        label: this.language.address,
        key: 'address',
        type: 'input',
        disabled: true,
        require: true,
        rule: [{required: true}, this.$ruleUtil.getRemarkMaxLengthRule(), this.$ruleUtil.getNameRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
      {
        label: this.language.remarks, key: 'remarks', type: 'input',
        rule: [this.$ruleUtil.getRemarkMaxLengthRule(), this.$ruleUtil.getNameRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
    ];
  }

  /**
   * 获取页面标题类型
   * param type
   * returns {string}
   */
  private getPageTitle(type): string {
    let title;
    switch (type) {
      case'add':
        title = `${this.language.addArea}${this.language.device}`;
        break;
      case 'update':
        title = `${this.language.modify}${this.language.device}`;
        break;
    }
    return title;
  }

  ngAfterViewInit(): void {
    // this.initCityPicker();
  }

  /**
   * 初始化区域选择器配置
   * param nodes
   */
  private initAreaSelectorConfig(nodes) {
    this.areaSelectorConfig = {
      width: '500px',
      height: '300px',
      title: `${this.language.select}${this.language.area}`,
      treeSetting: {
        check: {
          enable: true,
          chkStyle: 'checkbox',
          chkboxType: {'Y': '', 'N': ''},
        },
        data: {
          simpleData: {
            enable: true,
            idKey: 'areaId',
          },
          key: {
            name: 'areaName'
          },
        },

        view: {
          showIcon: false,
          showLine: false
        }
      },
      treeNodes: nodes
    };
  }

  /**
   * 处理设施是否能改
   */
  private modifyPermission() {
    this.formStatus.group.disable();
    this.formStatus.group.controls['deviceName'].enable();
    this.formStatus.group.controls['remarks'].enable();
    this.areaDisabled = true;
    this.positionDisabled = true;
  }

  /**
   * 获取当前用户有权限的设施类型
   * returns {any[]}
   */
  private getRoleDeviceType(): any[] {
    // 从用户信息里面获取权限列表
    const userInfo: any = SessionUtil.getUserInfo();
    // 获取所有的设施类型
    const deviceType = getDeviceType(this.$nzI18n);
    let roleDeviceType = [];
    if (userInfo.role && userInfo.role.roleDevicetypeList) {
      //  过滤有权限的设施类型
      roleDeviceType = deviceType.filter(item => {
        return (userInfo.role.roleDevicetypeList.findIndex(_item => item.code === _item.deviceTypeId) !== -1);
      });
    }
    return roleDeviceType;
  }
}
