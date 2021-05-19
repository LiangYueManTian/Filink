import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, TemplateRef, ViewChild} from '@angular/core';
import {PageBean} from '../../entity/pageBean';
import {ColumnConfig, Operation, TableConfig} from '../../entity/tableConfig';
import {NzTableSortConfig, TableSortConfig, TableStyleConfig} from '../../enum/tableStyleConfig';
import {TableStylePixel} from '../../enum/tableStylePixel';
import {TableComponentInterface} from './table.component.interface';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {TreeNode} from '../tree/tree-node';
import {FilterCondition, SortCondition} from '../../entity/queryCondition';
import {TableService} from './table.service';
import {CommonUtil} from '../../util/common-util';
import {FiLinkModalService} from '../../service/filink-modal/filink-modal.service';

declare var $: any;

@Component({
  selector: 'xc-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
  providers: [TableService]
})
export class TableComponent implements OnInit, OnChanges, TableComponentInterface {
  language: any = {table: {}, form: {}, common: {}};
  @Input()
  dataSet = [];
  @Input()
  pageBean: PageBean;
  @Input()
  tableConfig: TableConfig = new TableConfig();
  @Output()
  pageChange = new EventEmitter();
  allChecked = false;
  indeterminate = false;
  allUnChecked = true;
  configurableColumn = [];
  listOfSelection = [];
  public dragging: boolean;
  public resizeProxyShow: boolean;
  public dragState: { startMouseLeft: any; startLeft: number; startColumnLeft: number; tableLeft: number };
  public draggingColumn: ColumnConfig;
  queryTerm: Map<string, FilterCondition> = new Map<string, FilterCondition>();
  public searchDate = {};
  public rangDateValue = {};
  public exportRadioValue;
  @ViewChild('exportTemp')
  exportTemp: TemplateRef<any>;
  resizeProxyId = '';
  tableId = '';
  printVisible;

  constructor(public modalService: NzModalService,
              public $message: FiLinkModalService,
              public i18n: NzI18nService,
              public tableService: TableService) {
  }

  ngOnInit(): void {
    this.language = this.i18n.getLocale();
    this.listOfSelection = [
      {
        text: this.language.table.SelectAllRow,
        onSelect: () => {
          this.checkAll(true);
        }
      },
      {
        text: this.language.table.SelectOddRow,
        onSelect: () => {
          this.dataSet.forEach((data, index) => data.checked = index % 2 !== 0);
          this.refreshCheckStatus();
        }
      },
      {
        text: this.language.table.SelectEvenRow,
        onSelect: () => {
          this.dataSet.forEach((data, index) => data.checked = index % 2 === 0);
          this.refreshCheckStatus();
        }
      }
    ];
    this.initIndexNo();
    // this.queryTerm = this.tableService.initFilterParams(this.tableConfig);
    this.resizeProxyId = CommonUtil.getUUid();
    this.tableId = CommonUtil.getUUid();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.dataSet) {
      this.checkStatus();
      // 如果是树表 创建父子结构
      if (this.tableConfig.columnConfig[0].type === 'expend') {
        this.dataSet.forEach(item => {
          if (item[this.tableConfig.columnConfig[0].expendDataKey] && item[this.tableConfig.columnConfig[0].expendDataKey].length > 0) {
            const that = this;
            (function setFather(data) {
              data[that.tableConfig.columnConfig[0].expendDataKey].forEach(_item => {
                _item.father = data;
                _item.areaName = `${_item['father'].areaName}-${_item.areaName}`;
                if (_item[that.tableConfig.columnConfig[0].expendDataKey] &&
                  _item[that.tableConfig.columnConfig[0].expendDataKey].length > 0) {
                  setFather(_item);
                }
              });
            })(item);
          }
        });
      }
    }
    if (changes.tableConfig) {
      this.configurableColumn = this.tableConfig.columnConfig.filter(item => item.configurable);
      this.queryTerm = this.tableService.initFilterParams(this.tableConfig);
      // 表格高度自动适配 340 为基础表格的外高度 如果表格上有其他高的控制占高度需要传入outHeight
      const outHeight = this.tableConfig.outHeight || 0;
      if (this.tableConfig.scroll && (!this.tableConfig.noAutoHeight)) {
        this.tableConfig.scroll.y = $(document).height() - 340 - outHeight + TableStylePixel.PIXEL;
      }
      // 计算表格宽度
      this.calcTableWidth();
    }
  }

  refreshCheckStatus(status?: boolean, data?): void {
    // 当为树表的情况
    if (this.tableConfig.columnConfig[0].type === 'expend') {

      if (data[this.tableConfig.columnConfig[0].expendDataKey]) {
        const setChild = (childData) => {
          childData.forEach(item => {
            item.checked = status;
            if (item[this.tableConfig.columnConfig[0].expendDataKey] && item[this.tableConfig.columnConfig[0].expendDataKey].length > 0) {
              setChild(item[this.tableConfig.columnConfig[0].expendDataKey]);
            }
          });
        };
        setChild(data[this.tableConfig.columnConfig[0].expendDataKey]);
      }
      if (!status) {
        if (data && data.father) {
          (
            function setFather(treeNode) {
              treeNode.checked = status;
              if (treeNode.father) {
                setFather(treeNode.father);
              }
            }
          )(data.father);
        }
      }
      const allChecked = this.dataSet.every(value => value.checked === true);
      // const allUnChecked = this.dataSet.every(value => !value.checked);
      this.allChecked = allChecked;
      // this.indeterminate = (!allChecked) && (!allUnChecked);
      this.allUnChecked = this.tableService.checkStatus(this.dataSet, this.tableConfig.columnConfig[0].expendDataKey);
    } else {
      this.checkStatus();
    }
    if (this.tableConfig.handleSelect) {
      this.tableConfig.handleSelect(this.getDataChecked(), data);
    }
  }

  /**
   *  检查全选、有选状态
   */
  checkStatus() {
    if (this.dataSet.length > 0) {
      const allChecked = this.dataSet.every(value => value.checked === true);
      const allUnChecked = this.dataSet.every(value => !value.checked);
      this.allChecked = allChecked;
      this.allUnChecked = allUnChecked;
      this.indeterminate = (!allChecked) && (!allUnChecked);
    } else {
      this.allChecked = false;
      this.indeterminate = false;
      this.allUnChecked = true;
    }
  }

  getDataChecked(): any[] {
    const newArr: any[] = [];
    const that = this;
    (function _getTreeDataChecked(data) {
      data.forEach((item: TreeNode) => {
        if (item.checked) {
          newArr.push(item);
        }
        if (item[that.tableConfig.columnConfig[0].expendDataKey] && item[that.tableConfig.columnConfig[0].expendDataKey].length > 0) {
          _getTreeDataChecked(item[that.tableConfig.columnConfig[0].expendDataKey]);
        }
      });
    })(this.dataSet);
    return newArr;
  }

  checkAll(value: boolean): void {
    // 当为树表的情况
    if (this.tableConfig.columnConfig[0].type === 'expend') {
      this.dataSet.forEach(data => {
        data.checked = value;
        if (data[this.tableConfig.columnConfig[0].expendDataKey] && data[this.tableConfig.columnConfig[0].expendDataKey].length > 0) {
          const setChild = (childData) => {
            childData.forEach(item => {
              item.checked = value;
              if (item[this.tableConfig.columnConfig[0].expendDataKey] && item[this.tableConfig.columnConfig[0].expendDataKey].length > 0) {
                setChild(item[this.tableConfig.columnConfig[0].expendDataKey]);
              }
            });
          };
          setChild(data[this.tableConfig.columnConfig[0].expendDataKey]);
        }
      });
    } else {
      this.dataSet.forEach(data => data.checked = value);
    }
    this.checkStatus();

    if (this.tableConfig.handleSelect) {
      this.tableConfig.handleSelect(this.getDataChecked());
    }

  }

  refreshStatus(e) {
    this.pageChange.emit(this.pageBean);
  }

  handle(operation: Operation, index, data, key) {
    if (data[key] === 'disabled') {
      return;
    }
    if (operation.needConfirm) {
      this.modalService.confirm(this.tablePrompt(() => {
        this.tableConfig.operation[index].handle(data);
      }, () => {
      }, operation.confirmTitle, operation.confirmContent));
    } else {
      this.tableConfig.operation[index].handle(data);
    }

  }

  topHandle(operation: Operation) {
    if (operation.needConfirm) {
      this.modalService.confirm(this.tablePrompt(() => {
        const data = this.getDataChecked();
        operation.handle(data);
      }, () => {
      }, operation.confirmTitle, operation.confirmContent));
    } else {
      const data = this.getDataChecked();
      operation.handle(data);
    }
  }

  sort(event, key): void {
    if (event) {
      const sortCondition = new SortCondition();
      sortCondition.sortField = key;
      if (event === NzTableSortConfig.DESCEND) {
        sortCondition.sortRule = TableSortConfig.DESC;
      } else if (event === NzTableSortConfig.ASCEND) {
        sortCondition.sortRule = TableSortConfig.ASC;
      } else {
        sortCondition.sortRule = null;
      }
      this.tableConfig.sort(sortCondition);
    } else {
      this.tableConfig.sort({});
    }

  }

  /**
   * 过滤处理函数
   * param config
   * param $event
   */
  handleFilter(config, $event) {
    if (config.handleFilter) {
      config.handleFilter($event);
    }
  }

  handleMouseMove(event, column: ColumnConfig) {
    let target = event.target;
    while (target && target.tagName !== TableStylePixel.TH_TAG_NAME) {
      target = target.parentNode;
    }
    const rect = target.getBoundingClientRect();
    const bodyStyle = document.body.style;
    // (虚拟滚动会有小问题，先禁用固定列拖拽列宽)
    if (!this.dragging && this.tableConfig.isDraggable &&
      (!column.fixedStyle || this.tableConfig.columnConfig[0].type !== 'expend') && rect.width > 12 &&
      rect.right - event.pageX < 8 && column.width) {
      bodyStyle.cursor = 'col-resize';
      target.style.cursor = 'col-resize';
      this.draggingColumn = column;
      if (column.hasOwnProperty('isShowSort')) {
        column.isShowSort = false;
      }
    } else if (!this.dragging) {
      bodyStyle.cursor = '';
      target.style.cursor = '';
      this.draggingColumn = null;
      if (column.hasOwnProperty('isShowSort')) {
        column.isShowSort = true;
      }
    }

  }

  handleMouseDown(event, column) {
    let target = event.target;
    while (target && target.tagName !== 'TH') {
      target = target.parentNode;
    }
    const table = document.getElementById(this.tableId);
    const tableLeft = table.getBoundingClientRect().left;
    const columnRect = target.getBoundingClientRect();
    const resizeProxy = document.getElementById(this.resizeProxyId);
    if (this.tableConfig.isDraggable && this.draggingColumn) {
      const minLeft = columnRect.left - tableLeft + (this.draggingColumn.minWidth || TableStyleConfig.MIN_WIDTH);
      this.dragging = true;
      this.resizeProxyShow = true;
      this.dragState = {
        startMouseLeft: event.clientX,
        startLeft: columnRect.right - tableLeft,
        startColumnLeft: columnRect.left - tableLeft,
        tableLeft
      };
      resizeProxy.style.left = this.dragState.startLeft + TableStylePixel.PIXEL;
      const handleMouseMove = (_event) => {
        const deltaLeft = _event.clientX - this.dragState.startMouseLeft;
        const proxyLeft = this.dragState.startLeft + deltaLeft;
        resizeProxy.style.left = Math.max(minLeft, proxyLeft) + TableStylePixel.PIXEL;

      };
      const handleMouseUp = () => {
        if (this.dragging) {
          const {startColumnLeft, startLeft} = this.dragState;
          const finalLeft = parseInt(document.getElementById(this.resizeProxyId).style.left, 10);
          const columnWidth = finalLeft - startColumnLeft;
          // 设置table的宽度
          this.tableConfig.scroll.x = parseInt(this.tableConfig.scroll.x, 10) + (finalLeft - startLeft) + TableStylePixel.PIXEL;
          this.draggingColumn.width = columnWidth;
          // const temp = this.tableConfig.columnConfig.filter(item => (!item.hidden) && item.key);
          // const tempWidth = parseInt(temp[temp.length - 1].width, 10) - (finalLeft - startLeft);
          // const tableContainer = $(`#${this.tableId}`).width();
          // const aa = tableContainer > parseInt(this.tableConfig.scroll.x, 10);
          // if (this.draggingColumn.key !== temp[temp.length - 1].key) {
          //   temp[temp.length - 1].width = tempWidth;
          // }
          // 如果拖动列为固定列重新计算所有固定列列宽 (虚拟滚动会有小问题)todo
          if (this.draggingColumn.fixedStyle && this.draggingColumn.fixedStyle.fixedLeft) {
            const changeColumn = this.tableConfig.columnConfig.filter(item => (item.fixedStyle && item.fixedStyle.fixedLeft));
            changeColumn.forEach((item, index) => {
              let left = 0;
              for (let i = index; i--; i > 0) {
                left += changeColumn[i].width;
              }
              item.fixedStyle.style.left = left + TableStylePixel.PIXEL;
            });
          }
          this.draggingColumn = null;
          this.dragging = false;
          this.resizeProxyShow = false;
        }
        document.removeEventListener('mousemove', handleMouseMove);
        document.removeEventListener('mouseup', handleMouseUp);
      };
      document.addEventListener('mousemove', handleMouseMove);
      document.addEventListener('mouseup', handleMouseUp);
    }
  }

  handleMouseOut(event, column) {
    document.body.style.cursor = '';
    if (column.hasOwnProperty('isShowSort')) {
      column.isShowSort = true;
    }
  }

  handleSearch() {
    let result;
    if (this.tableConfig.searchReturnType && this.tableConfig.searchReturnType === 'object') {
      result = this.tableService.createFilterConditionMap(this.queryTerm);
    } else {
      result = this.tableService.createFilterConditions(this.queryTerm);
    }
    this.tableConfig.handleSearch(result);
  }

  /**
   * 手动设置某一列过滤条件
   * param key
   * param value
   */
  handleSetControlData(key, value) {
    this.queryTerm.set(key, {
      filterValue: value,
      filterField: this.queryTerm.get(key).filterField,
      operator: this.queryTerm.get(key).operator
    });
  }

  onChange(event, key) {
    if (event) {
      this.queryTerm.get(key).filterValue = event.getTime();
    } else {
      this.queryTerm.get(key).filterValue = null;
    }

  }

  selectChange(event, selectInfo) {
  }

  rangValueChange(event: any[], key) {
    const startTime = event[0] ? event[0].getTime() : null;
    const endTime = event[1] ? event[1].getTime() : null;
    if (startTime && endTime) {
      this.queryTerm.get(key).filterValue = [startTime, endTime];
    } else {
      this.queryTerm.get(key).filterValue = null;
    }
  }

  handleRest() {
    this.searchDate = {};
    this.rangDateValue = {};
    this.tableService.resetFilterConditions(this.queryTerm);
    this.handleSearch();
  }

  /**
   * 点击导出
   */
  clickExport() {
    const modal = this.modalService.create({
      nzTitle: this.language.table.exportTemp,
      nzContent: this.exportTemp,
      nzOkType: 'danger',
      nzClassName: 'custom-create-modal',
      nzFooter: [
        {
          label: this.language.table.okText,
          onClick: () => {
            if (!this.exportRadioValue && this.exportRadioValue !== 0) {
              this.$message.error(this.language.table.exportTemp);
              return;
            }
            if (this.exportRadioValue === 'HTML') {
              setTimeout(() => {
                CommonUtil.exportHtml('FiLinkWeb.html');
              }, 500);
            } else {
              // 获取查询条件
              let queryTerm;
              if (this.tableConfig.searchReturnType && this.tableConfig.searchReturnType === 'object') {
                queryTerm = this.tableService.createFilterConditionMap(this.queryTerm);
              } else {
                queryTerm = this.tableService.createFilterConditions(this.queryTerm);
              }
              // 获取当显示的列(排除序号操作展开等列)
              const column = this.tableConfig.columnConfig.filter(item => !item['hidden'] && (item.key && item.key !== 'serialNumber'));
              const __column = [];
              // 格式化参数
              column.forEach(item => {
                __column.push({
                  columnName: item.title,
                  propertyName: item.key
                });
              });
              const exportParams = {
                queryTerm: queryTerm,
                selectItem: this.getDataChecked(),
                columnInfoList: __column,
                excelType: this.exportRadioValue,
              };
              this.tableConfig.handleExport(exportParams);
            }
            modal.destroy();
          }
        },
        {
          label: this.language.table.cancelText,
          type: 'danger',
          onClick: () => {
            modal.destroy();
          }
        },
      ]
    });
  }

  /**
   * 点击打印
   */
  printList() {
    // window['Print']('#fiLink', {
    //   onStart: function () { // 开始打印的事件
    //   },
    //   onEnd: function () { // 取消打印的事件
    //   }
    // });
    // 使用原生打印
    this.printVisible = false;
    setTimeout(() => {
      window.print();
    }, 200);
  }

  /**
   * 展开事件处理函数
   * param data
   * param event
   */
  tableCollapse(data, event) {
    // 先统一处理展开回调 后期可能会为每行展开加事件
    if (this.tableConfig.expandHandle) {
      this.tableConfig.expandHandle();
    }
    this.collapse(data, event);
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
      return;
    }
  }

  /**
   * 设置列下拉菜单显示状态改变
   * param event boolean
   */
  dropDownChange(event: boolean) {
    // 点击显示下拉菜单 不做处理
    if (event) {
      return;
    }
    // 为了三期做表格列的保存
  }

  /**
   * 自定义列 显示隐藏重新计算表格宽地
   * param event
   */
  configurableColumnChange(event, _item) {
    _item.hidden = !event;
    this.calcTableWidth();
  }

  /**
   * 计算表格宽度
   */
  calcTableWidth() {
    let tableWidth = 0;
    this.tableConfig.columnConfig.forEach((item: ColumnConfig) => {
      // 如果有一列没设置宽 为了防止这一列在操作之后不显示
      if (!item.width) {
        item.width = 100;
      }
      if (item.width && !item['hidden']) {
        tableWidth += item.width;
      }
    });
    if (this.tableConfig.scroll) {
      this.tableConfig.scroll.x = tableWidth + TableStylePixel.PIXEL;
    }
  }

  /**
   * 初始化序号
   */
  initIndexNo() {
    if (!this.tableConfig.noIndex) {
      const columnConfig = {
        select: true,
        expand: true
      };
      let index = 0;
      this.tableConfig.columnConfig.forEach(item => {
        if (item.type in columnConfig) {
          index++;
        }
      });
      this.tableConfig.columnConfig.splice(index, 0, {
        type: 'serial-number', width: 62, title: this.language.facility.serialNumber,
        fixedStyle: {fixedLeft: true, style: {left: '62px'}}
      });
    }
  }

  /**
   * 表格统一提示配置
   * param handleOK
   * param handleCancel
   * returns any
   */
  tablePrompt(handleOK, handleCancel, title, content) {
    // 采用确定和取消互换
    const obj = {
      nzTitle: title || this.language.table.prompt,
      nzContent: content || `<span>${this.language.table.promptContent}</span>`,
      nzOkText: this.language.table.cancelText,
      nzOkType: 'danger',
      nzMaskClosable: false,
      nzOnOk: handleCancel,
      nzCancelText: this.language.table.okText,
      nzOnCancel: handleOK,
    };
    return obj;
  }

  onOpenChange(event, key) {
    // 为了解决UI框架的bug而采用的无奈代码，重新赋值
    if (!event) {
      // 这里深拷贝一个对象
      let temp;
      if (this.rangDateValue[key] && this.rangDateValue[key].length === 2) {
        temp = [new Date(this.rangDateValue[key][0].getTime()), new Date(this.rangDateValue[key][1].getTime())];
        if (this.rangDateValue[key][0].getTime() > this.rangDateValue[key][1].getTime()) {
          // 当选时间的时候ui组件判断错误，赋值为开始的那个
          this.rangDateValue[key] = [];
          this.queryTerm.get(key).filterValue = null;
          this.$message.warning(this.language.common.timeMsg);
        } else {
          this.rangDateValue[key] = [];
          this.rangDateValue[key] = temp;
        }
      } else {
        this.rangDateValue[key] = [];
        this.queryTerm.get(key).filterValue = null;
      }
    }

  }
}
