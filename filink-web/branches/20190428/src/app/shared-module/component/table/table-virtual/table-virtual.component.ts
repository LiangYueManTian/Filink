import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {TableService} from '../table.service';
import {TableComponent} from '../table.component';
import {FiLinkModalService} from '../../../service/filink-modal/filink-modal.service';
import {TableStylePixel} from '../../../enum/tableStylePixel';
import {AreaLevelEnum} from '../../../../business-module/facility/facility.config';

declare var $: any;

@Component({
  selector: 'xc-table-virtual',
  templateUrl: './table-virtual.component.html',
  styleUrls: ['../table.component.scss'],
  providers: [TableService]
})
export class TableVirtualComponent extends TableComponent implements OnInit, OnChanges {
  __dataSet = [];
  selectedValue = 0;
  @Input()
  selectedOption = [];
  nzVirtualMaxBufferPx;
  nzNoResult = '';

  constructor(public modalService: NzModalService,
              public i18n: NzI18nService,
              public $message: FiLinkModalService,
              public tableService: TableService) {
    super(modalService, $message, i18n, tableService);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.dataSet) {
      this.selectedValue = 0;
      this.checkStatus();
      // 计算表格高度
      const outHeight = this.tableConfig.outHeight || 0;
      const tableHeight = $(document).height() - 310 - outHeight;
      if (Math.floor((tableHeight / 30)) > this.dataSet.length) {
        this.nzVirtualMaxBufferPx = (this.dataSet.length) * 30;
      } else {
        this.nzVirtualMaxBufferPx = tableHeight;
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
      this.calcTableWidth();
    }
  }

  /**
   * 展开事件处理函数
   * param data
   * param event
   */
  tableCollapse(_data, event) {
    // 先统一处理展开回调 后期可能会为每行展开加事件
    if (this.tableConfig.expandHandle) {
      this.tableConfig.expandHandle();
    }
    this.selectedValue = null;
    this.collapse(_data, event);
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
        if (item.level === 1 || item.expand || (item.father && item.father.expand)) {
          arr.push(item);
        }
        if (item.children) {
          openRecursive(item.children);
        }
      });
    };
    openRecursive(this.dataSet);
    this.__dataSet = arr;
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
  }
}
