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

declare const $: any;
declare const cityData: any;

@Component({
  selector: 'app-facility-detail',
  templateUrl: './facility-detail.component.html',
  styleUrls: ['./facility-detail.component.scss']
})
export class FacilityDetailComponent implements OnInit, AfterViewInit {
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  public language: FacilityLanguageInterface;
  @ViewChild('customTemplate') private customTemplate;
  @ViewChild('positionTemplate') private positionTemplate;
  areaSelectVisible: boolean = false;

  pageType = 'add';
  pageTitle: string;
  citySelector: any;
  cityInfo: any = {province: '', city: '', district: '', detailInfo: ''};
  selectPoint = {lat: 0, lng: 0};
  facilityAddress = '';
  deviceId: string;
  isVisible = false;
  areaName = '';
  selectorData: any = {parentId: '', accountabilityUnit: ''};
  areaSelectorConfig: any = new TreeSelectorConfig();
  areaInfo: Area = new Area();
  private areaNodes: any = [];
  @ViewChild('areaSelector') private areaSelector;
  isLoading = false;
  pageLoading = false;
  private areaDisabled: boolean;
  private positionDisabled: boolean;
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
          // this.$router.navigate(['/business/facility/facility-list']).then();
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
          // this.$router.navigate(['/business/facility/facility-list']).then();
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
    // this.$router.navigate(['/business/facility/facility-list']).then();
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
    console.log(result);
    if (result.addressComponents.province && result.addressComponents.city && result.addressComponents.district) {
      this.cityInfo = result.addressComponents;
      this.cityInfo.detailInfo = result.point;
      this.selectPoint = result.point;
      const str = `${result.addressComponents.province},${result.addressComponents.city},${result.addressComponents.district}`;
      this.facilityAddress = result.address;
      this.citySelector.setCityVal(str);
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
          this.citySelector.setCityVal(str);
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
          data: getDeviceType(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        modelChange: (controls, $event, key) => {
          console.log(controls);
          console.log($event);
          console.log(key);
        },
        openChange: (a, b, c) => {

        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        label: this.language.parentId, key: 'areaId', type: 'custom',
        require: true,
        template: this.areaSelector,
        modelChange: (controls, $event, key) => {
          console.log(controls);
          console.log($event);
          console.log(key);
        },
        openChange: (a, b, c) => {

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
        label: `${this.language.provinceName}${this.language.cityName}${this.language.districtName}`,
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
        label: this.language.exclusive,
        key: 'exclusive',
        type: 'input',
        require: false,
        rule: [this.$ruleUtil.getRemarkMaxLengthRule()],
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
    this.initCityPicker();
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
   * 初始化城市选择器
   */
  private initCityPicker() {
    const that = this;
    that.citySelector = $('#city-picker-selector').cityPicker({
      dataJson: cityData,
      renderMode: true,
      search: false,
      autoSelected: true,
      code: 'cityCode',
      level: 3,
      onChoiceEnd: function () {
        // that.cityInfo = this.values;
      }
    });
    that.citySelector.changeStatus('disabled');
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
    this.citySelector.changeStatus('disabled');
  }
}
