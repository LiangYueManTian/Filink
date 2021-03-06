import {Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {NzI18nService, NzModalService, NzTableComponent} from 'ng-zorro-antd';
import {TableService} from '../table.service';
import {TableComponent} from '../table.component';
import {FiLinkModalService} from '../../../service/filink-modal/filink-modal.service';
import {TableStylePixel} from '../../../enum/tableStylePixel';
import {AreaLevelEnum} from '../../../../business-module/facility/facility.config';
import {SystemParameterService} from '../../../../core-module/api-service/system-setting/stystem-parameter/system-parameter.service';
import {TableStyleConfig} from '../../../enum/tableStyleConfig';

declare var $: any;

/**
 * 虚拟表格组件
 */
@Component({
  selector: 'xc-table-virtual',
  templateUrl: './table-virtual.component.html',
  styleUrls: ['../table.component.scss'],
  providers: [TableService]
})
export class TableVirtualComponent extends TableComponent implements OnInit, OnChanges {
  // 放入虚拟滚动视图的数据
  __dataSet = [];
  // 选择显示级别的值
  selectedValue = 0;
  // 显示级别的待选项
  @Input()
  selectedOption = [];
  // 虚拟滚动最大Buffer高度
  nzVirtualMaxBufferPx;
  // 没数据值
  nzNoResult = '';
  // 列表最大高度
  tableMaxHeight;
  @ViewChild('nzTable') nzTable: NzTableComponent;

  constructor(public modalService: NzModalService,
              public i18n: NzI18nService,
              public $message: FiLinkModalService,
              public $systemParameterService: SystemParameterService,
              public tableService: TableService) {
    super(modalService, $message, i18n, $systemParameterService, tableService);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.dataSet) {
      this.selectedValue = null;
      this.checkStatus();
      // 计算表格高度
      const outHeight = this.tableConfig.outHeight || 0;
      this.tableMaxHeight = $(window).height() - 310 - outHeight;
      if (Math.floor((this.tableMaxHeight / TableStyleConfig.TABLE_ROW_HEIGHT)) > this.dataSet.length) {
        // 计算数据量应占的高度
        this.nzVirtualMaxBufferPx = (this.dataSet.length) * TableStyleConfig.TABLE_ROW_HEIGHT + TableStyleConfig.SCROLL_HEIGHT;
      } else {
        this.nzVirtualMaxBufferPx = this.tableMaxHeight;
      }
      this.tableConfig.scroll.y = this.nzVirtualMaxBufferPx + TableStylePixel.PIXEL;
      // 如果是树表 创建父子结构
      if (this.tableConfig.columnConfig[0].type === 'expend') {
        const arr = [];
        this.dataSet.forEach((item, index) => {
          arr.push(item);
          item.serialNumber = index + 1;
          const areaLevelName = this.i18n.translate(`facility.config.${AreaLevelEnum[item.level]}`);
          item.areaNameTitle = `${areaLevelName}:${item.areaName}\n`;
          if (item[this.tableConfig.columnConfig[0].expendDataKey] && item[this.tableConfig.columnConfig[0].expendDataKey].length > 0) {
            const that = this;
            (function setFather(data) {
              data[that.tableConfig.columnConfig[0].expendDataKey].forEach((_item, _index) => {
                _item.father = data;
                if (data['expand']) {
                  arr.push(_item);
                }
                _item.serialNumber = `${data.serialNumber}-${_index + 1}`;
                const _areaLevelName = that.i18n.translate(`facility.config.${AreaLevelEnum[_item.level]}`);
                _item.areaNameTitle = `${data.areaNameTitle}${_areaLevelName}:${_item.areaName}\n`;
                if (_item[that.tableConfig.columnConfig[0].expendDataKey] &&
                  _item[that.tableConfig.columnConfig[0].expendDataKey].length > 0) {
                  setFather(_item);
                }
              });
            })(item);
          }
        });
        this.__dataSet = [];
        this.nzNoResult = ' ';
        setTimeout(() => {
          this.__dataSet = arr;
          this.nzNoResult = this.language.table.noData;
        });
      }
    }
    if (changes.tableConfig) {
      this.configurableColumn = this.tableConfig.columnConfig.filter(item => item.configurable);
      this.queryTerm = this.tableService.initFilterParams(this.tableConfig);
      // 获取表格列设置
      if (this.tableConfig.primaryKey) {
        this.getColumnSettings();
      }
      this.calcTableWidth();
    }
  }

  /**
   * 展开事件处理函数
   * param data
   * param event
   */
  tableCollapse(_data, event, index) {
    // 先统一处理展开回调 后期可能会为每行展开加事件
    if (this.tableConfig.expandHandle) {
      this.tableConfig.expandHandle();
    }
    this.selectedValue = null;
    this.collapse(_data, event);
    // 如果展开开启滚动补偿机制
    if (event) {
      this.scrollCompensate(index);
    }
  }

  /**
   * 展开指定子集
   * param zIndex
   */
  openChildren(zIndex) {
    const arr = [];
    const openRecursive = (data) => {
      data.forEach(item => {
        item.expand = item.level <= zIndex;
        if (item.father) {
          if (item.father.expand) {
            arr.push(item);
          }
        } else {
            arr.push(item);
        }
        if (item.children) {
          openRecursive(item.children);
        }
      });
    };
    openRecursive(this.dataSet);
    this.__dataSet = arr;
    this.__dataSet = [];
    this.nzNoResult = ' ';
    setTimeout(() => {
      this.__dataSet = arr;
      this.nzNoResult = this.language.table.noData;
      this.resetTableHeight();
    });
  }

  /**
   * 关闭所有子数据
   * param data
   * param event
   */
  collapse(data, event) {
    if (event === false) {
      data.forEach(item => {
        item.expand = false;
        if (item[this.tableConfig.columnConfig[0].expendDataKey] && item[this.tableConfig.columnConfig[0].expendDataKey].length > 0) {
          this.collapse(item[this.tableConfig.columnConfig[0].expendDataKey], event);
        }
      });
    } else {

    }
    const arr = [];
    this.dataSet.forEach(item => {
      arr.push(item);
      if (item[this.tableConfig.columnConfig[0].expendDataKey] && item[this.tableConfig.columnConfig[0].expendDataKey].length > 0) {
        const that = this;
        (function setFather(_data) {
          _data[that.tableConfig.columnConfig[0].expendDataKey].forEach(_item => {
            if (_data['expand']) {
              arr.push(_item);
            }
            if (_item[that.tableConfig.columnConfig[0].expendDataKey] &&
              _item[that.tableConfig.columnConfig[0].expendDataKey].length > 0) {
              setFather(_item);
            }
          });
        })(item);
      }
    });
    this.__dataSet = arr;
    this.resetTableHeight();
  }

  /**
   * 重新计算表格高度(树表小数据量使用)
   */
  resetTableHeight() {
    // 当树表第一层的数据比较少的时候重新计算树表格的高度
    if (this.dataSet.length < 5) {
      // 计算当前数据应有高度
      const tableHeight = this.__dataSet.length * TableStyleConfig.TABLE_ROW_HEIGHT + TableStyleConfig.SCROLL_HEIGHT;
      if (tableHeight < this.tableMaxHeight) {
        this.nzVirtualMaxBufferPx = tableHeight;
      } else {
        this.nzVirtualMaxBufferPx = this.tableMaxHeight;
      }
      this.tableConfig.scroll.y = this.nzVirtualMaxBufferPx + TableStylePixel.PIXEL;
    }
  }

  /**
   * 滚动补偿机制
   * param index
   */
  scrollCompensate(index) {
    // 当前index的高度
    const height = index * TableStyleConfig.TABLE_ROW_HEIGHT;
    // 页面卷曲的高度
    const scrollTop = this.nzTable.cdkVirtualScrollNativeElement.scrollTop;
    // 当前展开条数在页面地步最后一条滚动到下一条
    if ((Number.parseInt(this.tableConfig.scroll.y, 10) - (height - scrollTop)) < (TableStyleConfig.TABLE_ROW_HEIGHT * 2)) {
      const scrollHeight = TableStyleConfig.TABLE_ROW_HEIGHT * (index + 1);
      this.nzTable.cdkVirtualScrollNativeElement.scrollTo({top: scrollHeight});
    }
  }
}
