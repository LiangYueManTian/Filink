import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {Result} from '../../../../shared-module/entity/result';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {FacilityService} from '../../../../core-module/api-service/facility/facility-manage';
import {FilterCondition, QueryCondition, SortCondition} from '../../../../shared-module/entity/queryCondition';
import {DeployStatus, DeviceTypeCode, getDeployStatus, getDeviceStatus, getDeviceType} from '../../facility.config';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {LockService} from '../../../../core-module/api-service/lock';
import {IndexLanguageInterface} from '../../../../../assets/i18n/index/index.language.interface';
import {CommonUtil} from '../../../../shared-module/util/common-util';
import {FACILITY_TYPE_ICON_CLASS, FACILITY_TYPE_NAME} from '../../../../shared-module/const/facility';

@Component({
  selector: 'app-facility-list',
  templateUrl: './facility-list.component.html',
  styleUrls: ['./facility-list.component.scss']
})
export class FacilityListComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  _pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  lockInfoConfig: TableConfig;
  lockInfo = [];
  public language: FacilityLanguageInterface;
  public indexLanguage: IndexLanguageInterface;
  queryCondition: QueryCondition = new QueryCondition();
  @ViewChild('deviceStatusTemp') deviceStatusTemp: TemplateRef<any>;
  @ViewChild('openLockTemp') openLockTemp: TemplateRef<any>;
  @ViewChild('deviceTypeTemp') deviceTypeTemp: TemplateRef<any>;

  constructor(private $nzI18n: NzI18nService,
              private $message: FiLinkModalService,
              private $modal: NzModalService,
              private $lockService: LockService,
              private $facilityService: FacilityService,
              private $router: Router) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.indexLanguage = this.$nzI18n.getLocaleData('index');
    this.initTableConfig();
    this.refreshData();
  }

  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  /**
   * 获取电子锁信息
   */
  getLockInfo(deviceId) {
    this.lockInfoConfig.isLoading = true;
    this.$lockService.getLockInfo(deviceId).subscribe((result: Result) => {
      this.lockInfo = result.data || [];
      this.lockInfoConfig.isLoading = false;
    });
  }

  /**
   * 开锁
   */
  openLock(body) {
    this.$lockService.openLock(body).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
      } else {
        this.$message.error(result.msg);
      }
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
      scroll: {x: '1804px', y: '340px'},
      noIndex: true,
      showSearchExport: true,
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: {fixedLeft: true, style: {left: '62px'}}
        },
        {
          title: this.language.deviceName, key: 'deviceName', width: 124,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'},
          fixedStyle: {fixedLeft: true, style: {left: '124px'}}
        },
        {
          title: this.language.deviceType, key: 'deviceType', width: 120,
          configurable: true,
          type: 'render',
          renderTemplate: this.deviceTypeTemp,
          minWidth: 90,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.deviceStatus, key: 'deviceStatus', width: 120,
          type: 'render',
          renderTemplate: this.deviceStatusTemp,
          configurable: true,
          isShowSort: true,
          searchable: true,
          minWidth: 90,
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: getDeviceStatus(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.deviceCode, key: 'deviceCode', width: 150,
          configurable: true,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.parentId, key: 'areaName', width: 100,
          configurable: true,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'input'},
        },
        {
          title: this.language.provinceName, key: 'provinceName', width: 100,
          configurable: true,
          isShowSort: true,
          searchable: true,
          hidden: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.cityName, key: 'cityName', width: 100, configurable: true,
          isShowSort: true,
          searchable: true,
          hidden: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.districtName, key: 'districtName', width: 100, configurable: true,
          isShowSort: true,
          searchable: true,
          hidden: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.address, key: 'address', width: 150, configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.deployStatus, key: 'deployStatus', configurable: true, width: 150,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: getDeployStatus(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.remarks, key: 'remarks', configurable: true,
          searchable: true,
          isShowSort: true,
          hidden: true,
          width: 150,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 180, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      topButtons: [
        {
          text: '+  ' + this.language.addArea,
          permissionCode: '03-1-2',
          handle: (currentIndex) => {
            this.addFacility();
          }
        },
        {
          text: this.language.deleteHandle,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
          permissionCode: '03-1-4',
          needConfirm: true,
          canDisabled: true,
          confirmContent: this.language.deleteFacilityMsg,
          handle: (data) => {
            const ids = data.map(item => item.deviceId);
            this.$facilityService.deleteDeviceDyIds(ids).subscribe((result: Result) => {
              if (result.code === 0) {
                this.$message.success(result.msg);
                // 删除跳第一页
                this.queryCondition.pageCondition.pageNum = 1;
                this.refreshData();
              } else {
                this.$message.error(result.msg);
              }
            });
          }
        }
      ],
      operation: [
        {
          text: this.language.viewDetail, className: 'fiLink-view-detail', handle: (currentIndex) => {
            this.navigateToDetail('business/facility/facility-detail-view',
              {queryParams: {id: currentIndex.deviceId, deviceType: currentIndex._deviceType, serialNum: currentIndex.serialNum}});
          },
          permissionCode: '03-1-5',
        },
        {
          text: this.language.location, className: 'fiLink-location',
          permissionCode: '03-1-13',
          handle: (currentIndex) => {
            this.navigateToDetail('business/index', {queryParams: {id: currentIndex.deviceId}});
          }
        },
        {
          text: this.language.update,
          permissionCode: '03-1-3',
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            this.navigateToDetail('business/facility/facility-detail/update', {queryParams: {id: currentIndex.deviceId}});
          }
        },
        {
          text: this.language.control, className: 'fiLink-control',
          permissionCode: '03-1-11',
          handle: (currentIndex) => {
            if (currentIndex._deviceType === DeviceTypeCode.Well ||
              currentIndex._deviceType === DeviceTypeCode.Optical_Box
            ) {
              this.lockInfo = [];
              const modal = this.$modal.create({
                nzTitle: this.language.remoteUnlock,
                nzContent: this.openLockTemp,
                nzOkText: this.language.handleCancel,
                nzCancelText: this.language.handleOk,
                nzOkType: 'danger',
                nzClassName: 'custom-create-modal',
                nzMaskClosable: false,
                nzFooter: [
                  {
                    label: this.language.handleOk,
                    onClick: () => {
                      const slotNum = [];
                      this.lockInfo.forEach(item => {
                        if (item.checked) {
                          slotNum.push(item.doorNum);
                        }
                      });
                      if (slotNum.length === 0) {
                        this.$message.error(this.language.chooseDoorLock);
                        return;
                      }
                      const body = {deviceId: currentIndex.deviceId, doorNumList: slotNum};
                      this.openLock(body);
                      modal.destroy();
                    }
                  },
                  {
                    label: this.language.handleCancel,
                    type: 'danger',
                    onClick: () => {
                      modal.destroy();
                    }
                  },
                ]
              });
              this.getLockInfo(currentIndex.deviceId);
            } else {
              this.$message.error(this.language.noSupport);
            }

          }
        }, {
          text: this.language.statistical, className: 'fiLink-statistical',
          permissionCode: '03-1-12',
          handle: (currentIndex) => {
          }
        },
        {
          text: this.language.deleteHandle,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          permissionCode: '03-1-4',
          confirmContent: this.language.deleteFacilityMsg,
          handle: (currentIndex) => {
            this.$facilityService.deleteDeviceDyIds([currentIndex.deviceId]).subscribe((result: Result) => {
              if (result.code === 0) {
                this.$message.success(result.msg);
                // 删除跳第一页
                this.queryCondition.pageCondition.pageNum = 1;
                this.refreshData();
              } else {
                this.$message.error(result.msg);
              }
            });
          }
        },
      ],
      leftBottomButtons: [
        // 二期使用 布防 撤防  停用 维护 拆除
        {
          text: this.language.deployed, handle: (event) => {
            this.updateDeviceStatus(DeployStatus.DEPLOYED, event);
          },
          needConfirm: true,
          permissionCode: '03-1-14',
          canDisabled: true,
          confirmContent: `${this.language.editDeployStatusMsg}${this.language.deployed}${this.language.questionMark}`,
          className: 'small-button'
        },
        {
          canDisabled: true,
          className: 'small-button',
          needConfirm: true,
          confirmContent: `${this.language.editDeployStatusMsg}${this.language.noDefence}${this.language.questionMark}`,
          permissionCode: '03-1-15',
          text: this.language.noDefence, handle: (event) => {
            this.updateDeviceStatus(DeployStatus.NODEFENCE, event);
          }
        },
        {
          canDisabled: true,
          needConfirm: true,
          confirmContent: `${this.language.editDeployStatusMsg}${this.language.config.NOTUSED}${this.language.questionMark}`,
          className: 'small-button',
          permissionCode: '03-1-16',
          text: this.language.config.NOTUSED, handle: (event) => {
            this.updateDeviceStatus(DeployStatus.NOTUSED, event);

          }
        },
        {
          canDisabled: true,
          needConfirm: true,
          confirmContent: `${this.language.editDeployStatusMsg}${this.language.config.MAIINTENANCE}${this.language.questionMark}`,
          className: 'small-button',
          permissionCode: '03-1-17',
          text: this.language.config.MAIINTENANCE, handle: (event) => {
            this.updateDeviceStatus(DeployStatus.MAIINTENANCE, event);

          }
        },
        {
          canDisabled: true,
          needConfirm: true,
          className: 'small-button',
          // tslint:disable-next-line
          confirmContent: `${this.language.dismantleMsg}${this.language.editDeployStatusMsg}${this.language.config.DISMANTLE}${this.language.questionMark}`,
          permissionCode: '03-1-18',
          text: this.language.config.DISMANTLE, handle: (event) => {
            const ids = event.map(item => item.deviceId);
            this.$facilityService.deleteDeviceDyIds(ids).subscribe((result: Result) => {
              if (result.code === 0) {
                this.$message.success(result.msg);
                // 删除跳第一页
                this.queryCondition.pageCondition.pageNum = 1;
                this.refreshData();
              } else {
                this.$message.error(result.msg);
              }
            });
            // 拆除目前先当删除处理
            // this.updateDeviceStatus(DeployStatus.DISMANTLE, event);
          }
        },
        {
          text: this.language.configuration,
          canDisabled: true,
          permissionCode: '03-1-6',
          className: 'small-button',
          handle: (selectData) => {
            if (selectData.length > 0) {
              let deviceType, isSame = true, allHasControl = true;
              // 循环判断是否有重复的
              selectData.forEach(item => {
                if (!deviceType) {
                  deviceType = item._deviceType;
                } else {
                  if (deviceType !== item._deviceType) {
                    isSame = false;
                  }
                }
                if (item.lockList && item.lockList.length > 0) {

                } else {
                  allHasControl = false;
                }
              });
              // 所选设施类型是不相同
              if (!isSame) {
                this.$message.error(this.language.errMsg);
                return;
              }
              // 所选设施有一个没有主控
              if (!allHasControl) {
                this.$message.error(this.language.noControlMsg);
                return;
              }
              // 跳转到 配置设施策略页面
              sessionStorage.setItem('facility_config_info', JSON.stringify(selectData));
              this.navigateToDetail('business/facility/facility-config', {queryParams: {deviceType: deviceType}});
            } else {
              this.$message.error(this.language.errorMsg);
            }
          }
        }

      ],
      rightTopButtons: [],
      sort: (event: SortCondition) => {
        this.queryCondition.sortCondition.sortField = event.sortField;
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        this.queryCondition.pageCondition.pageNum = 1;
        this.queryCondition.filterConditions = event;
        this.refreshData();
      },
      handleExport: (event) => {
        // 处理参数
        const body = {
          queryCondition: new QueryCondition(),
          columnInfoList: event.columnInfoList,
          excelType: event.excelType
        };
        body.columnInfoList.forEach(item => {
          if (item.propertyName === 'deviceType'
            || item.propertyName === 'deviceStatus' || item.propertyName === 'deployStatus') {
            item.isTranslation = 1;
          }
        });
        // 处理选择的项目
        if (event.selectItem.length > 0) {
          const ids = event.selectItem.map(item => item.deviceId);
          const filter = new FilterCondition('deviceId');
          filter.filterValue = ids;
          filter.operator = 'in';
          body.queryCondition.filterConditions.push(filter);
        } else {
          // 处理查询条件
          body.queryCondition.filterConditions = event.queryTerm;
        }
        this.$facilityService.exportDeviceList(body).subscribe((res: Result) => {
          if (res.code === 0) {
            this.$message.success(res.msg);
          } else {
            this.$message.error(res.msg);
          }
        });
      }
    };
    this.lockInfoConfig = {
      noIndex: true,
      columnConfig: [
        {type: 'select', width: 62},
        {type: 'serial-number', width: 62, title: this.language.serialNumber},
        {title: this.language.doorNum, key: 'doorNum', width: 100},
        {title: this.language.doorName, key: 'doorName', width: 100},
        // {title: this.language.lockNum, key: 'lockNum', width: 100},
      ]
    };
  }

  /**
   * 跳转到新增设施页面
   */
  private addFacility() {
    this.navigateToDetail(`business/facility/facility-detail/add`);
  }

  /**
   * 跳转到详情
   * param url
   */
  private navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }

  /**
   * 刷新表格数据
   */
  private refreshData() {
    this.tableConfig.isLoading = true;
    this.$facilityService.deviceListByPage(this.queryCondition).subscribe((result: Result) => {
      if (result.code === 0) {
        this.pageBean.Total = result.totalCount;
        this.pageBean.pageIndex = result.pageNum;
        this.pageBean.pageSize = result.size;
        this.tableConfig.isLoading = false;
        this._dataSet = result.data || [];
        this._dataSet.forEach(item => {
          item.areaName = item.areaInfo ? item.areaInfo.areaName : '';
          item['_deviceType'] = item.deviceType;
          item.deviceType = getDeviceType(this.$nzI18n, item.deviceType);
          item['_deviceStatus'] = item.deviceStatus;
          item.deviceStatus = getDeviceStatus(this.$nzI18n, item.deviceStatus);
          item.deployStatus = getDeployStatus(this.$nzI18n, item.deployStatus);
          item['iconClass'] = CommonUtil.getFacilityIconClassName(item._deviceType);
        });
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  facilityTypeList() {
    return Object.keys(FACILITY_TYPE_NAME).map(key => {
      return {
        value: key,
        label: this.indexLanguage[FACILITY_TYPE_NAME[key]],
        iconClass: FACILITY_TYPE_ICON_CLASS[key],
        checked: true,
      };
    });
  }

  /**
   * 更新设施状态
   * param {string} deviceStatus
   * param {any[]} devices
   */
  private updateDeviceStatus(deviceStatus: string, devices: any[]) {
    const arr = devices.map(item => item.deviceId);
    const body = {deviceIds: arr, deployStatus: deviceStatus};
    this.$lockService.updateDeviceStatus(body).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        this.refreshData();
      } else {
        this.$message.error(result.msg);
      }
    });
  }
}
