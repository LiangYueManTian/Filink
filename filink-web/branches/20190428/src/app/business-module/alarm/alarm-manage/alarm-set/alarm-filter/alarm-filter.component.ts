import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {PageBean} from '../../../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../../../shared-module/entity/tableConfig';
import {ActivatedRoute, Router} from '@angular/router';
import {NzI18nService} from 'ng-zorro-antd';
import {AlarmService} from '../../../../../core-module/api-service/alarm/alarm-manage';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';

@Component({
  selector: 'alarm-filter',
  templateUrl: './alarm-filter.component.html',
  styleUrls: ['./alarm-filter.component.scss']
})
export class AlarmFilterComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  language;
  @ViewChild('openStatusTemp') openStatusTemp: TemplateRef<any>;
  @ViewChild('isInventory') isInventory: TemplateRef<any>;
  @ViewChild('creatTimeTemp') creatTimeTemp: TemplateRef<any>;
  constructor(public $router: Router,
              public $nzI18n: NzI18nService,
              public $alarmService: AlarmService,
              public $message: FiLinkModalService,
              public $active: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('alarm');
    this.initTableConfig();
  }
  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showRowSelection: true,
      showSizeChanger: true,
      pageSizeOptions: [10, 20, 100, 400],
      scroll: { x: '1200px', y: '600px' },
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0' } }, width: 62 },
        {
          title: this.language.name, key: 'name', width: 120, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.filterConditions, key: 'filterConditions', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.opertionUser, key: 'opertionUser', width: 120, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.openStatus, key: 'openStatus', width: 120, isShowSort: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.openStatusTemp,
          searchConfig: {
            type: 'select', selectInfo: [
              { label: this.language.disable, value: '0' },
              { label: this.language.enable, value: '1' }
            ]
          },
          handleFilter: ($event) => {
            console.log($event);
          },
        },
        {
          title: this.language.creatTime, key: 'creatTime', width: 150, type: 'render', isShowSort: true,
          renderTemplate: this.creatTimeTemp
        },
        {
          title: this.language.isInventory, key: 'isInventory', width: 120, isShowSort: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.isInventory,
          searchConfig: {
            type: 'select', selectInfo: [
              { label: '单用户', value: '1' },
              { label: '多用户', value: '2' }
            ]
          },
          handleFilter: ($event) => {
            console.log($event);
          },
        },
        {
          title: this.language.remark, key: 'remark', width: 100
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '', width: 120, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      searchReturnType: 'Object',
      topButtons: [
        {
          text: '+  ' + this.language.add,
          handle: (currentIndex) => {
            this.openAdd();
          }
        },
        {
          text: this.language.deleteHandle,
          canDisabled: true,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'icon-delete',
          handle: (data) => {
            console.log(data);
          }
        }
      ],
      operation: [
        // build2功能
        // {
        //   text: this.language.location,
        //   className: 'icon-location',
        //   handle: (e) => {
        //   }
        // },
        {
          text: this.language.update,
          className: 'icon-update',
          handle: (currentIndex) => {
            this.openModify(currentIndex.id);
          }
        },
        // {
        //   text: this.language.view,
        //   className: 'icon_view',
        //   handle: (e) => {
        //   }
        // },
        // {
        //   text: this.language.buildOrder,
        //   className: 'icon_build_order',
        //   handle: (e) => {
        //   }
        // }
      ],
      leftBottomButtons: [
        {
          text: this.language.enable, handle: (data) => {
          }
        },
        {
          text: this.language.disable, handle: (data) => {
          }
        },
        // build2功能
        // {
        //   text: this.language.remark, handle: (e) => {
        //   }
        // }
      ],
      // sort: (event: SortCondition) => {
      //   // console.log(event);
      //   this.queryCondition.sortCondition.sortField = event.sortField;
      //   this.queryCondition.sortCondition.sortRule = event.sortRule;
      //   this.refreshData();
      // },
      // handleSearch: (event) => {
      //   console.log(event);
      //   this.queryCondition.filterConditions = event;
      //   this.pageBean = new PageBean(10, 1, 1);
      //   this.refreshData({
      //     'filterConditions': event,
      //     'pageCondition': { pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex }
      //   });
      // }
    };
  }

  /**
   * 新增告警过滤
   */
  public openAdd() {
    this.$router.navigate(['business/alarm/add-alarm-filter/add']).then();
  }

  /**
   * 编辑告警过滤
   */
  public openModify(event) {
    this.$router.navigate(['business/alarm/modify-alarm-filter/update'], {
      queryParams: { id: 1 }
    }).then();
  }
}
