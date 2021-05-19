import { Component, OnInit } from '@angular/core';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {QueryCondition, SortCondition} from '../../../../shared-module/entity/queryCondition';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {FacilityService} from '../../../../core-module/api-service/facility/facility-manage';
import {Router} from '@angular/router';
import {NzI18nService} from 'ng-zorro-antd';
import {getDeviceType} from '../../../facility/facility.config';
import {Result} from '../../../../shared-module/entity/result';

@Component({
  selector: 'app-unfinished-sales-work-order',
  templateUrl: './unfinished-sales-work-order.component.html',
  styleUrls: ['./unfinished-sales-work-order.component.scss']
})
export class UnfinishedSalesWorkOrderComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  public language: FacilityLanguageInterface;
  queryCondition: QueryCondition = new QueryCondition();
  constructor(private $nzI18n: NzI18nService,
              private $facilityService: FacilityService,
              private $router: Router) { }

  ngOnInit() {
    this.drawCircle('canvas1', '#2dbef7', 1.8 * Math.PI, 1.2 * Math.PI);
    this.drawCircle('canvas2', '#38dbac', 0.3 * Math.PI, 0.8 * Math.PI);
    this.drawCircle('canvas3', '#91e15c', 0.35 * Math.PI, 0.7 * Math.PI);
    this.drawCircle('canvas4', '#fdc94f', 0.3 * Math.PI, 0.7 * Math.PI);
    this.drawCircle('canvas5', '#fb5d1e', 0.4 * Math.PI, 0.65 * Math.PI);
    this.language = this.$nzI18n.getLocaleData('facility');
    this.initTableConfig();
    this.refreshData();
  }

  drawCircle(id, color, startingAngle = 0, endingAngle = 2 * Math.PI) {
    const canvas = document.getElementById(id);
    const context = canvas['getContext']('2d');
    const centerX = 75;
    const centerY = 75;
    context.beginPath();
    context.lineWidth = 10;
    context.strokeStyle = '#eff0f4';

   // 创建变量,保存圆弧的各方面信息


    // 画完整的环
    context.arc(centerX, centerY, 65, 0, 2 * Math.PI);
    context.stroke();

    context.beginPath();
    // 画部分的环
    context.lineWidth = 3;
    context.strokeStyle = color;
    // 使用确定的信息绘制圆弧
    context.arc(centerX, centerY, 68, startingAngle, endingAngle);
    context.stroke();
  }

  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showRowSelection: true,
      showSizeChanger: true,
      scroll: {x: '1800px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {
          title: this.language.deviceType, key: 'deviceType', width: 200,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'select', selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.deviceName, key: 'deviceName', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.deviceCode, key: 'deviceCode', width: 200,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.parentId, key: 'areaId', width: 200,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'input'},
        },
        {
          title: this.language.provinceName, key: 'provinceName', width: 100,
          isCustomFilter: true,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.cityName, key: 'cityName', width: 100, configurable: true,
        },
        {
          title: this.language.districtName, key: 'districtName', width: 100, configurable: true,
        },
        {
          title: this.language.address, key: 'address', width: 100, configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.accountabilityUnit, key: 'accountabilityUnit', width: 100, configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.deployStatus, key: 'deployStatus', configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.remarks, key: 'remarks', configurable: true,
          searchable: true,
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
            this.addWorkOrder();
          }
        },
        {
          text: '删除',
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'icon-delete',
          handle: (data) => {
            const ids = data.map(item => item.areaId);
            console.log(ids);
            // this.$areaService.deleteAreaByIds(ids).subscribe((result: Result) => {
            //   if (result.code === 0) {
            //     this.refreshData();
            //   } else {
            //   }
            //   this.$message.info(result.msg);
            // });
            console.log(data);
          }
        }
      ],
      operation: [
        {
          text: '查看', className: 'icon_view_detail', handle: (currentIndex) => {
            // this.mapVisible = true;
            this.navigateToDetail('business/facility/facility-detail-view', {queryParams: {id: currentIndex.deviceId}});
          }
        },
        {
          text: '定位', className: 'icon-location', handle: () => {
            // this.mapVisible = true;
          }
        },
        {
          text: this.language.update,
          className: 'icon-update',
          handle: (currentIndex) => {
            this.navigateToDetail('business/facility/facility-detail/update', {queryParams: {id: currentIndex.deviceId}});
          }
        },

        {
          text: '操作', className: 'icon-operation', handle: () => {
            // this.mapVisible = true;
          }
        }, {
          text: '统计', className: 'icon-statistical', handle: () => {
            // this.mapVisible = true;
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
      sort: (event: SortCondition) => {
        console.log(event);
        this.queryCondition.sortCondition.sortField = event.sortField;
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        console.log(event);
        this.queryCondition.filterConditions = event;
        this.refreshData();
      }
    };
  }

  private refreshData() {
    this.tableConfig.isLoading = true;
    this.$facilityService.deviceListByPage(this.queryCondition).subscribe((result: Result) => {
      this.pageBean.Total = result.totalCount;
      this.tableConfig.isLoading = false;
      this._dataSet = result.data;
    });
  }

  private addWorkOrder() {
    this.navigateToDetail(`business/work-order/sales/add`);
  }

  /**
   * 跳转到详情
   * param url
   */
  private navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }
}
