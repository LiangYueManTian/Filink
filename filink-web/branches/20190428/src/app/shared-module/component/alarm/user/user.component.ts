import { Component, OnInit, Input } from '@angular/core';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { AlarmService } from '../../../../core-module/api-service/alarm';
import { Result } from '../../../../shared-module/entity/result';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { AlarmLanguageInterface } from 'src/assets/i18n/alarm/alarm-language.interface';
import { NzI18nService } from 'ng-zorro-antd';
import { QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';
import { User } from '../alarmSelectorConfig';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  _dataSetUser = [];
  // 勾选的通知人
  checkAlarmNotifier = {
    name: '',
    ids: []
  };
  checkAlarmNotifierBackups = {
    name: '',
    ids: []
  };
  display = {
    userTable: false,
    disabled: false
  };
  queryCondition: QueryCondition = new QueryCondition();
  // 通知人弹框
  pageBeanUser: PageBean = new PageBean(10, 1, 1);
  tableConfigUser: TableConfig;
  public language: AlarmLanguageInterface;
  _alarmUserConfig: User;
  _type: 'form' | 'table'  = 'table';
  // 远程通知中 新增 编辑 通过 区域和设施类型选择
  _condition;
  constructor(
    public $alarmService: AlarmService,
    public $nzI18n: NzI18nService,
  ) {
    this.language = this.$nzI18n.getLocaleData('alarm');
  }
  @Input()
  filterValue;
  @Input()
  set alarmUserConfig(alarmUserConfig: User) {
     if ( alarmUserConfig ) {
       this._alarmUserConfig = alarmUserConfig;
       this.setData();
     }
  }

  setData() {
    if ( this._alarmUserConfig.type ) {
      this._type = this._alarmUserConfig.type;
    }
    if ( this._alarmUserConfig.initialValue && this._alarmUserConfig.initialValue.ids &&  this._alarmUserConfig.initialValue.ids.length ) {
      this.checkAlarmNotifierBackups = this.clone(this._alarmUserConfig.initialValue);
      this.checkAlarmNotifier = this.clone(this._alarmUserConfig.initialValue);
    }
    // 禁用和启用
    this.display.disabled = this._alarmUserConfig.disabled;
    // 条件
    if ( this._alarmUserConfig.condition ) {
       this._condition = this._alarmUserConfig.condition;
    }
    if (this._alarmUserConfig.clear) {
      this.checkAlarmNotifier = {
        name: '',
        ids: []
      };
      this.checkAlarmNotifierBackups = {
        name: '',
        ids: []
      };
    }
  }

  colse() {
    this.display.userTable = false;
    this.checkAlarmNotifierBackups = this.clone(this.checkAlarmNotifier);
  }

  // 克隆数据
  clone(data) {
    return JSON.parse(JSON.stringify(data));
  }

  showUser() {
    // this.setCheckData();
    this.display.userTable = false;
    this.checkAlarmNotifier = this.clone(this.checkAlarmNotifierBackups);
    if ( this._type === 'table' ) {
      this.filterValue['filterValue'] = this.checkAlarmNotifier.ids;
    }
    this._alarmUserConfig.checkUser(this.checkAlarmNotifier);
    // this.formStatus.resetControlData('alarmForwardRuleUserList', this.checkAlarmNotifier.ids);
  }

  pageUserChange(event) {
    // this.queryCondition.filterConditions = this.filterEventObject;
    // this.queryCondition.pageCondition.pageNum = event.pageIndex;
    // this.queryCondition.pageCondition.pageSize = event.pageSize;
    // this.setCheckData();
    this.refreshUserData();
  }

    // 通知人弹框
    private initTableConfigUser() {
      this.tableConfigUser = {
        isDraggable: true,
        isLoading: false,
        showSearchSwitch: true,
        showSizeChanger: true,
        noIndex: true,
        notShowPrint: true,
        scroll: { x: '800px', y: '300px' },
        columnConfig: [
          { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 50 },
          {
            type: 'serial-number', width: 50, title: this.language.serialNumber,
            fixedStyle: { fixedLeft: true, style: { left: '0px' } }
          },
          {
            // 名称
            title: this.language.name, key: 'userName', width: 160,
            searchable: true,
            searchConfig: { type: 'input' },
            fixedStyle: { fixedLeft: true, style: { left: '0px' } }
          },
          {
            // 单位
            title: this.language.responsibleDepartment, key: 'deptName', width: 200,
            searchable: true,
            searchConfig: { type: 'input' },
            fixedStyle: { fixedLeft: true, style: { left: '0px' } }
          },
          {
            title: this.language.operate, searchable: true,
            searchConfig: { type: 'operate' }, key: '',
            width: 100, fixedStyle: { fixedRight: true, style: { right: '0px' } }
          },
        ],
        showPagination: true,
        bordered: false,
        showSearch: false,
        searchReturnType: 'Array',
        topButtons: [],
        operation: [],
        leftBottomButtons: [],
        sort: (event: SortCondition) => {
          // this.queryCondition.filterConditions = this.filterEvent;
          // this.queryCondition.sortCondition.sortField = event.sortField;
          // this.queryCondition.sortCondition.sortRule = event.sortRule;
          this.refreshUserData();
        },
        handleSelect: (data, currentItem) => {
          // this.checkAlarmNotifierBackups.ids = data.map(item => item.id);
          // this.checkAlarmNotifierBackups.name = data.map(item => item.userName ).join(',');
          if ( !currentItem ) {
            // 当前页面全选 获取全部取消时
            if ( data && data.length ) {
              data.forEach(checkData => {
                if ( this.checkAlarmNotifierBackups.ids.indexOf(checkData.id) === -1 ) {
                    // 不存在时 添加进去
                    this.checkData(checkData);
                }
              });
            } else {
              // 取消当前页面的全部勾选
              this._dataSetUser.forEach(item => {
                if ( this.checkAlarmNotifierBackups.ids.indexOf(item.id) !== -1 ) {
                  // 当该条数据存在于 勾选信息中时 将其移除
                  this.cancelCheck(item);
                }
              });
            }
          } else {
            if ( currentItem.checked ) {
              // 勾选
              this.checkData(currentItem);
            } else {
              // 取消勾选
              this.cancelCheck(currentItem);
            }
          }
        },
        handleSearch: (event) => {
          this.pageBeanUser['_pageIndex'] = 1;
          this.pageBeanUser['_pageSize'] = 10;
          if (event.length) {
            const obj = {};
            event.forEach(item => {
              obj[item.filterField] = item.filterValue;
            });
            // this.queryCondition.bizCondition = obj;
          }
          this.refreshUserData(event);
        }
      };
    }

  // 勾选数据时
  checkData(currentItem) {
    this.checkAlarmNotifierBackups.ids.push(currentItem.id);
    const names = this.checkAlarmNotifierBackups.name + ',' + currentItem.userName;
    this.checkAlarmNotifierBackups.name = this.checkAlarmNotifierBackups.name === '' ? currentItem.userName : names;
  }

  // 取消勾选
  cancelCheck(currentItem) {
    this.checkAlarmNotifierBackups.ids = this.checkAlarmNotifierBackups.ids.filter(id => {
      return currentItem.id !== id && id;
    });
    const names = this.checkAlarmNotifierBackups.name.split(',');
    this.checkAlarmNotifierBackups.name = names.filter(name => currentItem.userName !== name && name ).join(',');
  }

  // 通知人请求列表数据
  refreshUserData(filterConditions?) {
    // if ( !this.display.disable ) { return; }
    const bizConditions = {};
    if ( filterConditions && filterConditions.length ) {
      const field = filterConditions[0]['filterField'];
      const value = filterConditions[0].filterValue;
      if ( field === 'deptName') {
        bizConditions['department'] = value;
      } else {
        bizConditions[field] = value;
      }
    }
    const data = {
      'filterConditions': filterConditions || [
        { 'filterField': '', 'operator': '', 'filterValue': '' }
      ],
      'pageCondition': {
        'pageNum': this.pageBeanUser['_pageIndex'], 'pageSize': this.pageBeanUser['_pageSize']
      },
      'sortCondition': {}, 'bizCondition': bizConditions
    };
    if ( this._type === 'table' ) {
      // 在列表中 显示
      this.$alarmService.queryUser(data).subscribe((res: Result) => {
        if (res['code'] === 0) {
          this.pageBeanUser['_pageIndex'] = res.data.pageNum;
          this.pageBeanUser['_pageSize'] = res.data.size;
          this.pageBeanUser['_Total'] = res.data.totalCount;
          this._dataSetUser = res.data.data.map(item => {
            // 点击如果input框中有值 就默认勾选
            this.checkAlarmNotifierBackups.ids.forEach(_item => {
              if (item.id === _item) {
                item.checked = true;
              }
            });
            let deptName;
            if (item.department && item.department.deptName ) {
              deptName = item.department.deptName;
            }
            return { ...item, deptName: deptName };
          });
        }
      });
    } else {
      // 新增 和 编辑
      this.$alarmService.queryUserInfoByDeptAndDeviceType(this._condition).subscribe((res: Result) => {
        this.pageBeanUser['_pageIndex'] = 1;
        this.pageBeanUser['_pageSize'] = res.data.length;
        this.pageBeanUser['_Total'] = res.data.length;
        this._dataSetUser = res.data.map(item => {
          // 点击如果input框中有值 就默认勾选
          this.checkAlarmNotifierBackups.ids.forEach(_item => {
            if (item.id === _item) {
              item.checked = true;
            }
          });
          let deptName;
          if (item.department && item.department.deptName ) {
            deptName = item.department.deptName;
          }
          return { ...item, deptName: deptName };
        });
      });
    }

  }

  ngOnInit() {
    this.initTableConfigUser();
    // this.refreshUserData();
  }

}
