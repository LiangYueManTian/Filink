import {Component, OnInit, ViewChild} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {ColumnConfigService} from '../column-config.service';
import {Router} from '@angular/router';
import {MenuManageService} from '../../../core-module/api-service/system-setting';
import {Result} from '../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {BasicConfig} from '../../basic-config';
import {PageBean} from '../../../shared-module/entity/pageBean';

@Component({
  selector: 'app-memu-management',
  templateUrl: './memu-management.component.html',
  styleUrls: ['./memu-management.component.scss']
})
export class MemuManagementComponent extends BasicConfig implements OnInit {
  @ViewChild('templateStatus') private templateStatus;
  constructor(public $nzI18n: NzI18nService,
              private $columnConfigService: ColumnConfigService,
              private $systemSettingService: MenuManageService,
              private $message: FiLinkModalService,
              private $router: Router) {
    super($nzI18n);
  }

  ngOnInit() {
    this.queryConditions.sortCondition = {
      sortField: 'createTime',
      sortRule: 'desc'
    };
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: {x: '600px', y: '325px'},
      columnConfig: this.$columnConfigService.getSystemSettingColumnConfig({templateStatus: this.templateStatus}),
      showPagination: true,
      bordered: false,
      showSearch: false,
      topButtons: [
        {
          text: '+  ' + this.language.table.add,
          handle: () => {
            this.$router.navigate(['business/system/menu-add']);
          }
        }
     , {
          text: this.language.table.delete,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
          needConfirm: true,
          canDisabled: true,
          handle: (data) => {
            if (data.find(item => item.templateStatus === '1')) {
              this.$message.warning('开启状态禁止删除');
            } else {
              const ids = data.map(item => item.menuTemplateId);
              if (ids) {
                this.delTemplate(ids);
              }
            }
          }
        }
      ],
      operation: [
        {
          text: this.language.table.update,
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            const menuTemplateId = currentIndex.menuTemplateId;
            this.$router.navigate([`/business/system/menu-update/${menuTemplateId}`]);
          }
        }],
      sort: (e) => {
        this.queryConditions.sortCondition = e;
        this.searchMenuTemplateList();
      },
      handleSearch: (event) => {
        this.queryConditions.filterConditions = event;
        this.pageBean = new PageBean(this.pageBean.pageSize, 1, 1);
        this.searchMenuTemplateList();
      }
    };
    // 初始化查询菜单列表
    this.searchMenuTemplateList();
  }

  /**
   * 查询菜单模板列表
   */
  searchMenuTemplateList() {
    this.createQueryConditions();
    this.tableConfig.isLoading = true;
    this.$systemSettingService.queryListMenuTemplateByPage(this.queryConditions).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this._dataSet = result.data;
        this.pageBean.Total = result.totalCount;
      } else {
        // TODO 查询失败！
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 启用么个模板
   * param templateInfo
   */
  useTemplate(templateInfo) {
    if (templateInfo && templateInfo.menuTemplateId) {
      // 当前开关出现加载动画
      this._dataSet = this._dataSet.map(item => {
        if (templateInfo.menuTemplateId === item.menuTemplateId) {
          item.clicked = true;
          return item;
        } else {
          return item;
        }
      });
      this.$systemSettingService.openMenuTemplate(templateInfo.menuTemplateId).subscribe((result: Result) => {
        if (result.code === 0) {
          this.searchMenuTemplateList();
        } else {
          // TODO 模板启用失败
        }
      });
    }
  }

  /**
   * 删除模板
   * param ids
   */
  delTemplate(ids) {
    this.$systemSettingService.deleteMenuTemplate(ids).subscribe((result: Result) => {
      if (result.code === 0) {
          this.$message.success(result.msg);
          this.pageBean.pageIndex = 1;
          this.searchMenuTemplateList();
      } else {
        this.$message.error(result.msg);
      }
    });
  }
  /**
   * 监听页面切换
   * param event
   */
  pageChange(event) {
    this.pageBean.pageIndex = event.pageIndex;
    this.pageBean.pageSize = event.pageSize;
    this.searchMenuTemplateList();
  }

  /**
   * 创建查询条件  主要是条件拼接
   */
  createQueryConditions() {
    this.queryConditions.pageCondition = {
      pageNum: this.pageBean.pageIndex,
      pageSize: this.pageBean.pageSize
    };

    this.queryConditions.filterConditions = this.queryConditions.filterConditions.map(item => {
      if (item.filterField === 'createTime') {
        item.filterValue = new Date(item.filterValue);
      }
      return item;
    });
  }
}
