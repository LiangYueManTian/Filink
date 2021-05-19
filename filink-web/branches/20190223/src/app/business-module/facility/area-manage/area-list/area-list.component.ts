import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {Router} from '@angular/router';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {AreaService} from '../../../../core-module/api-service/facility';
import {MapSelectorConfig} from '../../../../shared-module/entity/mapSelectorConfig';
import {Result} from '../../../../shared-module/entity/result';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {AreaLevel, getAreaLevel} from '../../facility.config';
import {TreeSelectorConfig} from '../../../../shared-module/entity/treeSelectorConfig';
import {FacilityUtilService} from '../../facility-util.service';
import {UserService} from '../../../../core-module/api-service/user/user-manage';

@Component({
  selector: 'app-area-list',
  templateUrl: './area-list.component.html',
  styleUrls: ['./area-list.component.scss']
})
export class AreaListComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryConditions = {bizCondition: {}};
  public language: FacilityLanguageInterface;
  @ViewChild('areaLevelTemp') areaLevelTemp: TemplateRef<any>;
  @ViewChild('topCustomButton') topCustomButton: TemplateRef<any>;
  @ViewChild('UnitNameSearch') UnitNameSearch: TemplateRef<any>;
  treeSelectorConfig: TreeSelectorConfig;
  treeNodes = [];
  treeSetting = {};
  mapVisible = false;
  mapSelectorConfig: MapSelectorConfig;
  selectedValue = 0;
  private deviceId: string;
  public AreaLevel;
  public selectedOption;
  isVisible: boolean = false;
  public selectUnitName: string = '';
  private filterValue: any;

  constructor(private $message: FiLinkModalService, private $router: Router,
              private $modalService: NzModalService,
              private $facilityUtilService: FacilityUtilService,
              private $userService: UserService,
              private $nzI18n: NzI18nService, private $areaService: AreaService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.initTableConfig();
    this.initTreeSelectorConfig();
    this.queryDeptList();
    this.refreshData();
    this.initMapSelectorConfig();
    this.AreaLevel = AreaLevel;
    this.selectedOption = [
      {label: `${this.language.open}${this.language.config.AREA_LEVEL_ONE}${this.language.area}`},
      {label: `${this.language.open}${this.language.config.AREA_LEVEL_TWO}${this.language.area}`},
      {label: `${this.language.open}${this.language.config.AREA_LEVEL_THREE}${this.language.area}`},
      {label: `${this.language.open}${this.language.config.AREA_LEVEL_FOUR}${this.language.area}`},
      {label: `${this.language.open}${this.language.config.AREA_LEVEL_FIVE}${this.language.area}`},
    ];
  }

  /**
   * 翻页回调
   * param event
   */
  pageChange(event) {
    this.pageBean.pageIndex = event.pageIndex;
    this.pageBean.pageSize = event.pageSize;
  }

  mapSelectDataChange(event) {
    const list = event.map(item => item.deviceId);
    const obj = {};
    obj[this.deviceId] = list;
    this.setAreaDevice(obj);
  }

  /**
   * 展开指定子集
   * param zIndex
   */
  openChildren(zIndex) {
    const openRecursive = (data) => {
      data.forEach(item => {
        item.expand = item.level <= zIndex;
        if (item.children) {
          openRecursive(item.children);
        }
      });
    };
    openRecursive(this._dataSet);
  }

  /**
   * 责任单位选择结果
   * param event
   */
  selectDataChange(event) {
    let selectArr = [];
    this.selectUnitName = '';
    if (event.length > 0) {
      selectArr = event.map(item => {
        this.selectUnitName += `${item.deptName},`;
        return item.id;
      });
    } else {
    }
    this.selectUnitName = this.selectUnitName.substring(0, this.selectUnitName.length - 1);
    if (selectArr.length === 0) {
      this.filterValue['filterValue'] = null;
    } else {
      this.filterValue['filterValue'] = selectArr;
    }
    this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, selectArr);
  }

  /**
   * 打开责任单位选择器
   */
  showModal(filterValue) {
    this.filterValue = filterValue;
    if (!this.filterValue['filterValue']) {
      this.filterValue['filterValue'] = [];
    }
    this.treeSelectorConfig.treeNodes = this.treeNodes;
    this.isVisible = true;
  }

  /**
   * 初始化单位选择器配置
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
          title: this.language.deptName, key: 'deptName', width: 100,
        },
        {
          title: this.language.deptLevel, key: 'deptLevel', width: 100,
        },
        {
          title: this.language.parentDept, key: 'parmentDeparmentName', width: 100,
        }
      ]
    };
  }

  /**
   * 初始地图选择器代码已废弃
   */
  private initMapSelectorConfig() {
    this.mapSelectorConfig = {
      title: '管理设施',
      width: '1000px',
      height: '300px',
      mapData: [],
      selectedColumn: [
        {
          title: this.language.deviceName, key: 'deviceName', width: 100,
        },
        {
          title: this.language.deviceCode, key: 'deviceCode', width: 100,
        },
        {
          title: this.language.deviceType, key: 'deviceType', width: 100,
        },
        {
          title: this.language.parentId, key: 'areaName', width: 100,
        }
      ]
    };
  }

  /**
   * 刷新区域数据
   */
  private refreshData() {
    this.selectedValue = 0;
    this.tableConfig.isLoading = true;
    this.$areaService.areaListByPage(this.queryConditions).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      this._dataSet = result.data || [];
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 初始化表格配置
   */
  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: true,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: {x: '1600px', y: '400px'},
      noIndex: true,
      columnConfig: [
        {type: 'expend', width: 30, expendDataKey: 'children', fixedStyle: {fixedLeft: true, style: {left: '0px'}}},
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '30px'}}, width: 62},
        {type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: {fixedLeft: true, style: {left: '92px'}}},
        {
          title: this.language.areaName, key: 'areaName', width: 200,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'input'},
          fixedStyle: {fixedLeft: true, style: {left: '154px'}}
        },
        {
          title: this.language.parentId, key: 'parentName', width: 200,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.level, key: 'level', width: 100,
          configurable: true,
          type: 'render',
          searchable: true,
          searchConfig: {
            type: 'select', selectInfo: getAreaLevel(this.$nzI18n),
            notAllowClear: true,
            initialValue: 1, label: 'label', value: 'code'
          },
          renderTemplate: this.areaLevelTemp,
        },
        {
          title: this.language.provinceName, key: 'provinceName', width: 100,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.cityName, key: 'cityName', width: 100, configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.districtName, key: 'districtName', width: 100, configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.address, key: 'address', width: 100, configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.accountabilityUnit, key: 'accountabilityUnitName', width: 100, configurable: true,
          searchKey: 'accountabilityUnit',
          searchable: true,
          searchConfig: {type: 'render', renderTemplate: this.UnitNameSearch}
        },
        {
          title: this.language.remarks, key: 'remarks', configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 150, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: false,
      bordered: false,
      showSearch: false,
      searchReturnType: 'object',
      topButtons: [
        {
          text: '+  ' + this.language.addArea,
          handle: (currentIndex) => {
            this.addArea();
          }
        },
        {
          text: this.language.deleteHandle,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
          canDisabled: true,
          needConfirm: true,
          handle: (data) => {
            const ids = data.map(item => item.areaId);
            console.log(ids);
            this.$areaService.deleteAreaByIds(ids).subscribe((result: Result) => {
              if (result.code === 0) {
                this.refreshData();
              } else {
              }
              this.$message.info(result.msg);
            });
            console.log(data);
          }
        },
      ],
      operation: [
        {
          text: this.language.setDevice, className: 'fiLink-facility-m', handle: (event) => {
            this.navigateToDetail('business/facility/set-area-device', {queryParams: {id: event.areaId}});
          }
        },
        {
          text: this.language.update,
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            this.navigateToDetail('business/facility/area-detail/update', {queryParams: {id: currentIndex.areaId}});
          }
        },
        {
          text: this.language.deleteHandle,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          handle: (currentIndex) => {
            this.$areaService.deleteAreaByIds([currentIndex.areaId]).subscribe((result: Result) => {
              if (result.code === 0) {
                this.refreshData();
                this.$message.success(result.msg);
              } else {
                this.$message.error(result.msg);
              }
            });
          }
        },
      ],
      topCustomButton: this.topCustomButton,
      sort: (e) => {
        console.log(e);
      },
      handleSearch: (event) => {
        this.queryConditions.bizCondition = event;
        // 没有值的时候重置已选数据
        if (!event.accountabilityUnit) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        this.refreshData();
      },
      expandHandle: () => {
        this.selectedValue = null;
      }
    };
  }

  /**
   * 点击新增区域
   */
  private addArea() {
    this.$router.navigate(['business/facility/area-detail/add']).then();
  }

  /**
   * 跳转到详情
   * param url
   */
  private navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }

  /**
   * 关联设施
   * param body
   */
  private setAreaDevice(body) {
    this.$areaService.setAreaDevice(body).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  /**
   * 查询所有的区域
   */
  private queryDeptList() {
    this.$userService.queryAllDepartment().subscribe((result: Result) => {
      this.treeNodes = result.data || [];
    });
  }
}
