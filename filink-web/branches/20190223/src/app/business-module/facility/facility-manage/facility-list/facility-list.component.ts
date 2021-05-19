import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {Result} from '../../../../shared-module/entity/result';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {FacilityService} from '../../../../core-module/api-service/facility/facility-manage';
import {QueryCondition, SortCondition} from '../../../../shared-module/entity/queryCondition';
import {DeviceTypeCode, getDeployStatus, getDeviceStatus, getDeviceType} from '../../facility.config';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {LockService} from '../../../../core-module/api-service/lock';
import {FacilityTypeCode, FacilityTypeIconClass, FacilityTypeName} from '../../../index/map/facility';
import {IndexLanguageInterface} from '../../../../../assets/i18n/index/index.language.interface';
import {CommonUtil} from '../../../../shared-module/util/common-util';

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
    // this.getTestData();
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
    this.$lockService.getLockInfo(deviceId).subscribe((result: Result) => {
      this.lockInfo = result.data || [];
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
      scroll: {x: '1800px', y: '340px'},
      noIndex: true,
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: {fixedLeft: true, style: {left: '62px'}}
        },
        {
          title: this.language.deviceName, key: 'deviceName', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'},
          fixedStyle: {fixedLeft: true, style: {left: '124px'}}
        },
        {
          title: this.language.deviceType, key: 'deviceType', width: 200,
          configurable: true,
          type: 'render',
          renderTemplate: this.deviceTypeTemp,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'select', selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.deviceStatus, key: 'deviceStatus', width: 100,
          type: 'render',
          renderTemplate: this.deviceStatusTemp,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'select', selectInfo: getDeviceStatus(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.deviceCode, key: 'deviceCode', width: 200,
          configurable: true,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.parentId, key: 'areaName', width: 200,
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
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.cityName, key: 'cityName', width: 100, configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.districtName, key: 'districtName', width: 100, configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.address, key: 'address', width: 100, configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.deployStatus, key: 'deployStatus', configurable: true, width: 100,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'select', selectInfo: getDeployStatus(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.remarks, key: 'remarks', configurable: true,
          width: 100,
          searchable: true,
          isShowSort: true,
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
          handle: (currentIndex) => {
            this.addFacility();
          }
        },
        {
          text: this.language.deleteHandle,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
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
          }
        },
        {
          text: this.language.location, className: 'fiLink-location', handle: (currentIndex) => {
            this.navigateToDetail('business/index', {queryParams: {id: currentIndex.deviceId}});
          }
        },
        {
          text: this.language.update,
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            this.navigateToDetail('business/facility/facility-detail/update', {queryParams: {id: currentIndex.deviceId}});
          }
        },

        {
          text: this.language.control, className: 'fiLink-control', handle: (currentIndex) => {
            if (currentIndex._deviceType === DeviceTypeCode.Well ||
              currentIndex._deviceType === DeviceTypeCode.Optical_Box
            ) {
              const modal = this.$modal.create({
                nzTitle: this.language.remoteUnlock,
                nzContent: this.openLockTemp,
                nzOkText: this.language.handleCancel,
                nzCancelText: this.language.handleOk,
                nzOkType: 'danger',
                nzClassName: 'custom-create-modal',
                nzFooter: [
                  {
                    label: this.language.handleOk,
                    onClick: () => {
                      const slotNum = [];
                      this.lockInfo.forEach(item => {
                        if (item.checked) {
                          slotNum.push(item.lockNum);
                        }
                      });
                      if (slotNum.length === 0) {
                        this.$message.error(this.language.chooseDoorLock);
                        return;
                      }
                      const body = {deviceId: currentIndex.deviceId, slotNumList: slotNum};
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
          text: this.language.statistical, className: 'fiLink-statistical', handle: (currentIndex) => {
          }
        },
        // {
        //   text: this.language.deleteHandle,
        //   needConfirm: true,
        //   className: 'icon-delete',
        //   handle: (currentIndex) => {
        //   }
        // },
      ],
      leftBottomButtons: [
        // 二期使用
        // {
        //   text: '布防', handle: (e) => {
        //     console.log(e);
        //   }
        // },
        // {
        //   text: '撤防', handle: (e) => {
        //     console.log(e);
        //   }
        // },
        // {
        //   text: '停用', handle: (e) => {
        //     console.log(e);
        //   }
        // },
        // {
        //   text: '维护', handle: (e) => {
        //     console.log(e);
        //   }
        // }, {
        //   text: '拆除', handle: (e) => {
        //     console.log(e);
        //   }
        // },

      ],
      rightTopButtons: [
        {
          text: this.language.configuration,
          iconClassName: 'fiLink-setting',
          handle: (selectData) => {
            if (selectData.length > 0) {
              let deviceType, isSame = true;
              // 循环判断是否有重复的
              selectData.forEach(item => {
                if (!deviceType) {
                  deviceType = item._deviceType;
                } else {
                  if (deviceType !== item._deviceType) {
                    isSame = false;
                  }
                }
              });
              // 如果设施类型是相同的跳转到 配置设施策略页面
              if (isSame) {
                sessionStorage.setItem('facility_config_info', JSON.stringify(selectData));
                this.navigateToDetail('business/facility/facility-Config', {queryParams: {deviceType: deviceType}});
              } else {
                this.$message.error(this.language.errMsg);
              }
            } else {
              this.$message.error(this.language.errorMsg);
            }
          }
        }
      ],
      sort: (event: SortCondition) => {
        console.log(event);
        this.queryCondition.sortCondition.sortField = event.sortField;
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        console.log(event);
        this.queryCondition.pageCondition.pageNum = 1;
        this.queryCondition.filterConditions = event;
        this.refreshData();
      }
    };
    this.lockInfoConfig = {
      noIndex: true,
      columnConfig: [
        {type: 'select', width: 62},
        {type: 'serial-number', width: 62, title: this.language.serialNumber},
        {title: this.language.doorNum, key: 'lockNum', width: 100},
        {title: this.language.doorName, key: 'doorName', width: 100},
        {title: this.language.lockNum, key: 'lockNum', width: 100},
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
      this.pageBean.Total = result.totalCount;
      this.pageBean.pageIndex = result.pageNum;
      this.pageBean.pageSize = result.size;
      this.tableConfig.isLoading = false;
      this._dataSet = result.data;
      this._dataSet.forEach(item => {
        item.areaName = item.areaInfo ? item.areaInfo.areaName : '';
        item['_deviceType'] = item.deviceType;
        item.deviceType = getDeviceType(this.$nzI18n, item.deviceType);
        item['_deviceStatus'] = item.deviceStatus;
        item.deviceStatus = getDeviceStatus(this.$nzI18n, item.deviceStatus);
        item.deployStatus = getDeployStatus(this.$nzI18n, item.deployStatus);
        item['iconClass'] = CommonUtil.getFacilityIconClassName(item._deviceType);
      });
    }, () => {
      this.tableConfig.isLoading = false;
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
