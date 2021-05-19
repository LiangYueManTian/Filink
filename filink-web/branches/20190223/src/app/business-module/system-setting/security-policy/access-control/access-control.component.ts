import {Component, OnInit, ViewChild} from '@angular/core';
import {ColumnConfigService} from '../../column-config.service';
import {NzI18nService} from 'ng-zorro-antd';
import {BasicConfig} from '../../../basic-config';

@Component({
  selector: 'app-access-control',
  templateUrl: './access-control.component.html',
  styleUrls: ['./access-control.component.scss']
})
export class AccessControlComponent extends BasicConfig implements OnInit {

  // 新增修改是否显示
  isVisible = false;

  // 当前访问控制信息
  curIpInfo = {};

  // 新增修改title
  title = '新增IP地址范围';

  @ViewChild('templateStatus') private templateStatus;
  constructor(private $columnConfigService: ColumnConfigService,
              public $nzI18n: NzI18nService) {
    super($nzI18n);
  }

  ngOnInit() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: {x: '600px', y: '325px'},
      columnConfig: this.$columnConfigService.getAccessControlColumnConfig(
        {statue: this.templateStatus}),
      showPagination: true,
      bordered: false,
      showSearch: true,
      topButtons: [
        {
          text: '+  ' + this.language.table.add,
          handle: () => {
            this.curIpInfo = {};
            this.title = '新增IP地址范围';
            this.isVisible = true;
          }
        }
        , {
          text: this.language.table.delete,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'icon-delete',
          handle: (data) => {
            if (data.length === 0) {
              // this.$message.warning('请先勾选需删除数据！');
              return;
            }
            if (data.find(item => item.templateStatus === '1')) {
              // this.$message.warning('开启状态禁止删除');
            } else {
              const ids = data.map(item => item.menuTemplateId);
              if (ids) {
                // this.delTemplate(ids);
              }
            }
          }
        }
      ],
      sort: (e) => {
        this.queryConditions.sortCondition = e;
      },
      handleSearch: (event) => {
        console.log(event);
      },
      operation: [
        {
          text: this.language.table.update,
          className: 'icon-update',
          handle: (current) => {
            this.title = '修改IP地址范围';
            this.isVisible = true;
            this.curIpInfo = current;
          }
        },
        {
          text: this.language.table.delete,
          className: 'icon-delete',
          handle: (current) => {
          }
        }],
    };
    for (let i = 0; i < 5; i++) {
      this._dataSet = [...this._dataSet, ...this._dataSet];
    }
  }


  /**
   * 监听页面切换
   * param event
   */
  pageChange(event) {
    this.pageBean.pageIndex = event.pageIndex;
    this.pageBean.pageSize = event.pageSize;
  }

  /**
   * 新增修改取消按钮
   */
  detailCancel() {
    this.isVisible = false;
  }
}
