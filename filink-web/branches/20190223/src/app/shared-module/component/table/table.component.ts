import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {PageBean} from '../../entity/pageBean';
import {ColumnConfig, Operation, TableConfig} from '../../entity/tableConfig';
import {NzTableSortConfig, TableSortConfig, TableStyleConfig} from '../../enum/tableStyleConfig';
import {TableStylePixel} from '../../enum/tableStylePixel';
import {TableComponentInterface} from './table.component.interface';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {TreeNode} from '../tree/tree-node';
import {FilterCondition, SortCondition} from '../../entity/queryCondition';
import {TableService} from './table.service';


@Component({
  selector: 'xc-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
  providers: [TableService]
})
export class TableComponent implements OnInit, OnChanges, TableComponentInterface {
  language: any = {table: {}, form: {}};
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
  private dragState: { startMouseLeft: any; startLeft: number; startColumnLeft: number; tableLeft: number };
  private draggingColumn: any;
  expandable = true;
  queryTerm: Map<string, FilterCondition> = new Map<string, FilterCondition>();
  public searchDate = {};

  constructor(private modalService: NzModalService,
              private i18n: NzI18nService,
              private tableService: TableService) {
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

  handle(operation: Operation, index, data) {
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
    console.log(event);
    console.log(key);
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
    if (!this.dragging && this.tableConfig.isDraggable &&
      !column.fixedStyle && rect.width > 12 &&
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
    const table = document.getElementById('xTable');
    const tableLeft = table.getBoundingClientRect().left;
    const columnRect = target.getBoundingClientRect();
    const resizeProxy = document.getElementById('resizeProxy');
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
          const finalLeft = parseInt(document.getElementById('resizeProxy').style.left, 10);
          const columnWidth = finalLeft - startColumnLeft;

          this.tableConfig.scroll.x = parseInt(this.tableConfig.scroll.x, 10) + (finalLeft - startLeft) + TableStylePixel.PIXEL;
          this.draggingColumn.width = columnWidth;
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

  onChange(event, key) {
    if (event) {
      this.queryTerm.get(key).filterValue = event.getTime();
    } else {
      this.queryTerm.get(key).filterValue = null;
    }

  }

  handleRest() {
    this.searchDate = {};
    this.tableService.resetFilterConditions(this.queryTerm);
    this.handleSearch();
  }

  leftBottomButtonsHandle() {

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
   * 表格统一提示配置
   * param handleOK
   * param handleCancel
   * returns any
   */
  private tablePrompt(handleOK, handleCancel, title, content) {
    const obj = {
      nzTitle: title || this.language.table.prompt,
      nzContent: content || `<span>${this.language.table.promptContent}</span>`,
      nzOkText: this.language.table.cancelText,
      nzOkType: 'danger',
      nzMaskClosable: false,
      nzOnOk: handleCancel,
      nzCancelText: this.language.table.okText,
      nzOnCancel: handleOK,
      // nzFooter: [
      //   {
      //     label: this.language.table.okText,
      //     onClick: ()=>{
      //       console.log(11);
      //     },
      //   },
      //   {
      //     label: this.language.table.cancelText,
      //     onclick: handleCancel,
      //   }
      // ]
    };
    return obj;
  }

  /**
   * 初始化序号
   */
  initIndexNo() {
    if (!this.tableConfig.noIndex) {
      const columnConfig = {
        select: true,
        expend: true
      };
      let index = 0;
      this.tableConfig.columnConfig.forEach(item => {
        if (item.type in columnConfig) {
          index++;
        }
      });
      this.tableConfig.columnConfig.splice(index, 0, {
        type: 'serial-number',
        width: 62,
        title: this.language.facility.serialNumber,
        fixedStyle: {fixedLeft: true, style: {left: '62px'}}
      });
    }
  }
}
