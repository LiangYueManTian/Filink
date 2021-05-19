import {AfterViewInit, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {FormItem} from '../../../../shared-module/component/form/form-config';
import {FormOperate} from '../../../../shared-module/component/form/form-opearte.service';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {NzI18nService} from 'ng-zorro-antd';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {TreeSelectorConfig} from '../../../../shared-module/entity/treeSelectorConfig';
import {MapSelectorConfig} from '../../../../shared-module/entity/mapSelectorConfig';
import {AreaService} from '../../../../core-module/api-service/facility';
import {ActivatedRoute, Router} from '@angular/router';
import {Result} from '../../../../shared-module/entity/result';
import {UserService} from '../../../../core-module/api-service/user/user-manage';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {TreeSelectorComponent} from '../../../../shared-module/component/tree-selector/tree-selector.component';
import {Area} from '../../../../core-module/entity/facility/area';
import {FacilityUtilService} from '../../facility-util.service';

declare const $: any;
declare const cityData: any;

@Component({
  selector: 'app-area-detail',
  templateUrl: './area-detail.component.html',
  styleUrls: ['./area-detail.component.scss']
})
export class AreaDetailComponent implements OnInit, AfterViewInit {
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  public language: FacilityLanguageInterface;
  public areaInfo: any = new Area();
  @ViewChild('accountabilityUnit') private accountabilityUnitTep;
  @ViewChild('unitTreeSelector') private unitTreeSelector: TreeSelectorComponent;
  areaSelectVisible: boolean = false;
  isVisible = false;
  mapVisible = false;
  treeSetting = {};
  treeNodes = [];
  treeSelectorConfig: TreeSelectorConfig;
  mapSelectorConfig: MapSelectorConfig;
  areaId = '';
  pageType = 'add';
  pageTitle: string;
  cityInfo: any[] = [{name: ''}, {name: ''}, {name: ''}];
  citySelector: any;
  areaSelectorConfig: any = new TreeSelectorConfig();
  selectorData: any = {parentId: '', accountabilityUnit: ''};
  areaName = '';
  @ViewChild('areaSelector') private areaSelector;
  private areaNodes: any = [];
  @ViewChild('customTemplate') private customTemplate: TemplateRef<any>;
  unitDisabled = false;
  areaDisabled = false;
  isLoading = false;
  public selectUnitName: string = '';

  constructor(private $nzI18n: NzI18nService,
              private $areaService: AreaService,
              private $active: ActivatedRoute,
              private $modalService: FiLinkModalService,
              private $facilityUtilService: FacilityUtilService,
              private $userService: UserService,
              private $router: Router) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.initTreeSelectorConfig();
    this.initColumn();
    // this.queryDeptList();
    this.pageType = this.$active.snapshot.params.type;
    this.pageTitle = this.getPageTitle(this.pageType);
    if (this.pageType !== 'add') {
      this.$active.queryParams.subscribe(params => {
        this.areaId = params.id;
        this.$areaService.queryNameCanChange(this.areaId).subscribe((result: Result) => {
          if (result.code !== 0) {
            this.modifyPermission();
          }
        });
        this.queryDeptList().then(() => {
          this.getArea().then((data) => {
            this.areaNodes = data;
            this.initAreaSelectorConfig(data);
            this.queryAreaById();
          });
        });
      });
    } else {
      this.queryDeptList().then(() => {
        this.getArea().then((data) => {
          this.areaNodes = data;
          this.initAreaSelectorConfig(data);
        });
      });
    }

  }

  formInstance(event) {
    this.formStatus = event.instance;
  }

  /**
   * 打开责任单位选择器
   */
  showModal() {
    this.treeSelectorConfig.treeNodes = this.treeNodes;
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
   * 责任单位选择结果
   * param event
   */
  selectDataChange(event) {
    this.selectUnitName = '';
    const selectArr = event.map(item => {
      this.selectUnitName += `${item.deptName},`;
      return item.id;
    });
    this.selectUnitName = this.selectUnitName.substring(0, this.selectUnitName.length - 1);
    this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, selectArr);
    console.log(this.treeNodes);
    this.formStatus.resetControlData('accountabilityUnit', selectArr);
  }

  /**
   * 区域选中结果
   * param event
   */
  areaSelectChange(event) {
    if (event[0]) {
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, event[0].areaId, this.areaInfo.areaId);
      this.areaName = event[0].areaName;
      this.selectorData.parentId = event[0].areaId;
    } else {
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, null, this.areaInfo.areaId);
      this.areaName = '';
      this.selectorData.parentId = null;
    }
  }

  /**
   * 点击新增区域
   */
  addArea() {
    this.isLoading = true;
    const data = this.formStatus.group.getRawValue();
    if (this.cityInfo.length >= 3) {
      data['provinceName'] = this.cityInfo[0].name;
      data['cityName'] = this.cityInfo[1].name;
      data['districtName'] = this.cityInfo[2].name;
    }
    data.parentId = this.selectorData.parentId;
    if (this.pageType === 'add') {

      this.$areaService.addArea(data).subscribe((result: Result) => {
        this.isLoading = false;
        if (result.code === 0) {
          this.$router.navigate(['/business/facility/area-list']).then();
          this.$modalService.success(result.msg);
        } else {
          this.$modalService.error(result.msg);
        }
      }, () => {
        this.isLoading = false;
      });
    } else if (this.pageType === 'update') {
      data['areaId'] = this.areaId;
      this.$areaService.updateAreaById(data).subscribe((result: Result) => {
        this.isLoading = false;
        if (result.code === 0) {
          this.$router.navigate(['/business/facility/area-list']).then();
          this.$modalService.success(result.msg);
        } else {
          this.$modalService.error(result.msg);
        }
      }, () => {
        this.isLoading = false;
      });
    }
  }

  /**
   * 根据id获取区域详情
   */
  private queryAreaById() {
    this.$areaService.queryAreaById(this.areaId).subscribe((result: Result) => {
      if (result.code === 0) {
        const areaInfo = result.data || new Area();
        this.areaInfo = areaInfo;
        this.areaName = this.areaInfo.parentName;
        this.selectorData.parentId = this.areaInfo.parentId;
        this.selectUnitName = this.areaInfo.accountabilityUnitName;
        this.formStatus.resetData(areaInfo);
        if (areaInfo.provinceName && areaInfo.cityName && areaInfo.districtName) {
          this.cityInfo[0].name = result.data.provinceName;
          this.cityInfo[1].name = result.data.cityName;
          this.cityInfo[2].name = result.data.districtName;
          const str = `${areaInfo.provinceName},${areaInfo.cityName},${areaInfo.districtName}`;
          this.citySelector.setCityVal(str);
        }
        // 递归设置区域的选择情况
        this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, areaInfo.parentId, areaInfo.areaId);
        // 递归设置树的节点状态
        this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, areaInfo.accountabilityUnit);
      } else if (result.code === 130109) {
        this.$modalService.error(result.msg);
        this.goBack();
      }

    });
  }

  /**
   * 获取区域列表信息
   * returns {Promise<any>}
   */
  private getArea() {
    return new Promise((resolve, reject) => {
      this.$areaService.areaListByPage({
        bizCondition: {}
      }).subscribe((result: Result) => {
        const data = result.data;
        resolve(data);
      });
    });

  }

  /**
   * 获取所有单位
   */
  private queryDeptList() {
    return new Promise((resolve, reject) => {
      this.$userService.queryAllDepartment().subscribe((result: Result) => {
        this.treeNodes = result.data || [];
        resolve();
      });
    });
  }

  /**
   * 取消返回
   */
  goBack() {
    this.$router.navigate(['/business/facility/area-list']).then();
  }

  /**
   * 初始化树选择器配置
   */
  private initTreeSelectorConfig() {
    this.treeSetting = {
      check: {
        enable: true,
        chkStyle: 'checkbox',
        chkboxType: {'Y': '', 'N': ''},
      },
      data: {
        simpleData: {
          enable: false,
          idKey: 'id',
        },
        key: {
          name: 'deptName',
          children: 'childDepartmentList'
        },
      },
      view: {
        showIcon: false,
        showLine: false
      }
    };
    this.treeSelectorConfig = {
      title: `${this.language.selectUnit}`,
      width: '1000px',
      height: '300px',
      treeNodes: this.treeNodes,
      treeSetting: this.treeSetting,
      onlyLeaves: false,
      selectedColumn: [
        {
          title: `${this.language.deptName}`, key: 'deptName', width: 100,
        },
        {
          title: `${this.language.deptLevel}`, key: 'deptLevel', width: 100,
        },
        {
          title: `${this.language.parentDept}`, key: 'parmentDeparmentName', width: 100,
        }
      ]
    };
  }

  /**
   * 初始化表单配置
   */
  private initColumn() {
    this.formColumn = [
      {
        label: this.language.areaName,
        key: 'areaName',
        type: 'input',
        require: true,
        rule: [{required: true}, {maxLength: 32}, {pattern: '^(?!_)[a-zA-Z0-9_\u4e00-\u9fa5]+$'}],
        asyncRules: [
          {
            asyncRule: (control: FormControl) => {
              return Observable.create(observer => {
                this.$areaService.queryAreaNameIsExist({areaId: this.areaId, areaName: control.value}).subscribe((result: Result) => {
                  if (result.code === 0) {
                    observer.next(null);
                    observer.complete();
                  } else {
                    observer.next({error: true, duplicated: true});
                    observer.complete();
                  }
                });
              });
            },
            asyncCode: 'duplicated', msg: `${this.language.nameExists}`
          }
        ],
      },
      {
        label: this.language.parentId, key: 'parentId', type: 'custom',
        template: this.areaSelector,
        rule: [], asyncRules: []
      },
      {
        label: `${this.language.provinceName}${this.language.cityName}${this.language.districtName}`,
        key: 'managementFacilities',
        type: 'custom',
        rule: [],
        template: this.customTemplate
      },
      {
        label: this.language.address,
        key: 'address',
        type: 'input',
        require: false,
        rule: [{maxLength: 64}],
        modelChange: (controls, event, key, formOperate) => {
          console.log(event);
        }
      },
      {
        label: this.language.accountabilityUnit,
        key: 'accountabilityUnit',
        type: 'custom',
        require: false,
        rule: [],
        asyncRules: [],
        template: this.accountabilityUnitTep
      },
      {
        label: this.language.remarks, key: 'remarks', type: 'input',
        rule: [{maxLength: 200}], modelChange: (controls, event, key, formOperate) => {
          console.log(event);
        }
      },
    ];
  }

  /**
   * 初始化选择区域配置
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
   * 获取页面类型(add/update)
   * param type
   * returns {string}
   */
  private getPageTitle(type): string {
    let title;
    switch (type) {
      case'add':
        title = `${this.language.addArea}${this.language.area}`;
        break;
      case 'update':
        title = `${this.language.modify}${this.language.area}`;
        break;
    }
    return title;
  }

  ngAfterViewInit(): void {
    this.initCityPicker();
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
        that.cityInfo = this.values;
      }
    });

  }

  /**
   * 处理区域能否修改
   */
  private modifyPermission() {
    this.formStatus.group.disable();
    this.formStatus.group.controls['areaName'].enable();
    this.unitDisabled = true;
    this.areaDisabled = true;
    this.citySelector.changeStatus('disabled');
  }
}
