import {AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import 'ztree';
import {TreeSelectorConfig} from '../../entity/treeSelectorConfig';
import {PageBean} from '../../entity/pageBean';
import {TableComponent} from '../table/table.component';
import {NzI18nService} from 'ng-zorro-antd';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';
import {CommonUtil} from '../../util/common-util';

declare var $: any;

@Component({
  selector: 'xc-tree-selector',
  templateUrl: './tree-selector.component.html',
  styleUrls: ['./tree-selector.component.scss']
})
export class TreeSelectorComponent implements OnInit, OnChanges, AfterViewInit {
  @Input()
  treeSelectorConfig: TreeSelectorConfig = new TreeSelectorConfig();
  @Output() xcVisibleChange = new EventEmitter<boolean>();
  @Output() selectDataChange = new EventEmitter<any[]>();
  @Input() isHiddenButton = false;
  @ViewChild(TableComponent) childCmp: TableComponent;
  treeInstance: any;
  searchValue = '';
  searchResult: any[] = [];
  selectData: any[] = [];
  selectPageData = [];
  selectPageBean: PageBean = new PageBean(6, 1, 0);
  data: any[] = [];
  settings = {
    callback: {
      onCheck: (event, treeId, treeNode) => {
        this.selectData = this.getTreeCheckedNodes();
        this.refreshSelectPageData();
      },
      // beforeCheck: (treeId, treeNode) => {
      //   this.treeInstance.checkNode(treeNode, null, false, false);
      //   this.selectData = this.getTreeCheckedNodes();
      //   this.refreshSelectPageData();
      //   return false;
      // }
    },
  };
  selectorConfig;
  treeNodeSum = 0;
  private firstSelectData: any[];
  public language: any;
  public commonLanguage: CommonLanguageInterface;
  treeId;
  selectInfo = {
    data: [],
    label: 'label',
    value: 'code'
  };

  constructor(private $i18n: NzI18nService) {
  }

  _xcVisible = false;

  get xcVisible() {
    return this._xcVisible;
  }

  @Input()
  set xcVisible(params) {
    this._xcVisible = params;
    this.xcVisibleChange.emit(this._xcVisible);
  }

  ngOnInit() {
    this.language = this.$i18n.getLocaleData('form');
    this.commonLanguage = this.$i18n.getLocaleData('common');
    this.treeId = CommonUtil.getUUid();
    this.selectorConfig = {
      isDraggable: false,
      isLoading: false,
      showSearchSwitch: false,
      searchTemplate: null,
      scroll: {x: '440px', y: '310px'},
      noAutoHeight: true,
      columnConfig: [
        {type: 'select'}
      ].concat(this.treeSelectorConfig.selectedColumn),
      showPagination: false,
      bordered: false,
      showSearch: false,
      showSizeChanger: false,
      handleSelect: (data, currentItem) => {
        // 加入被选容器
        console.log(data);
        console.log(currentItem);
        if (currentItem) {
          // 找到要删除的项目
          const index = this.selectData.findIndex(item => item.id === currentItem.id);
          this.selectData.splice(index, 1);
          // 删除完刷新被选数据
          this.childCmp.checkStatus();
          this.refreshSelectPageData();
          this.data.forEach(item => {
            if (item.id === currentItem.id) {
              item.checked = false;
            }
          });
          this.treeInstance.checkNode(currentItem, false, true);
        }
        if (data && data.length === 0) {
          this.selectData = [];
          this.refreshSelectPageData();
          this.treeInstance.checkAllNodes(false);
        }
      },
      // operation: [
      //   {
      //     text: '删除',
      //     needConfirm: false,
      //     handle: (currentIndex) => {
      //       // 找到要删除的项目
      //       const index = this.selectData.findIndex(item => item.id === currentIndex.id);
      //       this.selectData.splice(index, 1);
      //       // 删除完刷新被选数据
      //       this.refreshSelectPageData();
      //       this.data.forEach(item => {
      //         if (item.id === currentIndex.id) {
      //           item.checked = false;
      //         }
      //       });
      //       // 树刷新全选和有选状态 强行删除可实现 但会很突兀
      //       this.treeInstance.checkNode(currentIndex, false, true);
      //       this.selectData = this.getTreeCheckedNodes();
      //       this.refreshSelectPageData();
      //     }
      //   },
      // ],
    };
  }

  handleCancel() {
    this.xcVisible = false;
  }

  handleOk() {
    this.selectDataChange.emit(this.selectData);
    this.handleCancel();
  }

  search() {
    if (this.searchValue) {
      const node = this.treeInstance.getNodesByParamFuzzy(this.treeSelectorConfig.treeSetting.data.key.name || 'name',
        this.searchValue, null);
      // this.searchResult = this.treeInstance.transformToArray(node);
      this.searchResult = node;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes && changes.xcVisible.currentValue) {
      this.searchValue = null;
      $.fn.zTree.init($(`#${this.treeId}`),
        Object.assign(this.treeSelectorConfig.treeSetting, this.settings),
        this.treeSelectorConfig.treeNodes);
      this.treeInstance = $.fn.zTree.getZTreeObj(this.treeId);
      if (this.treeInstance) {
        let nodes = [];
        if (this.treeSelectorConfig.onlyLeaves) {
          nodes = this.treeInstance.getNodesByFilter((node) => {
            return (!node.isParent);
          });
        } else {
          nodes = this.treeInstance.getNodes();
        }
        this.treeNodeSum = this.treeInstance.transformToArray(nodes).length;
        this.firstSelectData = this.getTreeCheckedNodes();
        if (this.firstSelectData.length > 0) {
          this.firstSelectData.forEach(item => {
            this.treeInstance.selectNode(item);
          });
        }
        this.selectData = this.firstSelectData;
        this.refreshSelectPageData();
      }
    }
  }

  ngAfterViewInit(): void {
  }

  /**
   * 为了阻止事件冒泡
   * param event
   */
  click(event) {
    event.stopPropagation();
  }

  /**
   * 定位到某一条
   * param item
   */
  selectNode(item) {
    this.treeInstance.selectNode(item);
  }

  /**
   * 刷新数据
   */
  refreshSelectPageData() {
    this.selectPageBean.pageSize = this.selectData.length;
    this.selectPageBean.Total = this.selectData.length;
    // 不需要分页
    // this.selectPageData = this.selectData.slice(this.selectPageBean.pageSize * (this.selectPageBean.pageIndex - 1),
    //   this.selectPageBean.pageIndex * this.selectPageBean.pageSize);
  }

  /**
   * 左边表格数据变化
   * param event
   */
  selectPageChange(event) {
    this.selectPageBean.pageIndex = event.pageIndex;
    this.selectPageBean.pageSize = event.pageSize;
    // 不需要分页
    // this.selectPageData = this.selectData.slice(this.selectPageBean.pageSize * (this.selectPageBean.pageIndex - 1),
    //   this.selectPageBean.pageIndex * this.selectPageBean.pageSize);
  }

  /**
   * 清空数据
   */
  restSelectData() {
    this.searchValue = null;
    this.selectData = this.firstSelectData;
    this.refreshSelectPageData();
    this.treeInstance.checkAllNodes(false);
    this.firstSelectData.forEach(item => {
      this.treeInstance.checkNode(item, true, false);
    });
  }

  /**
   * 获取选中的节点
   */
  getTreeCheckedNodes() {
    let checkedNodes = [];
    // 只选中叶子
    if (this.treeSelectorConfig.onlyLeaves) {
      checkedNodes = this.treeInstance.getNodesByFilter((node) => {
        return (!node.isParent && node.checked);
      });
    } else {
      checkedNodes = this.treeInstance.getCheckedNodes(true);
    }
    return checkedNodes;
  }

  /**
   * 键盘弹起事件
   * param event
   */
  onInputKeyUp(event) {
    if (event.keyCode === 13) {
      this.search();
      const a = document.getElementById('searchDropDown')as any;
      const obj = document.createEvent('MouseEvents');
      obj.initMouseEvent('click', true,
        true, window, 1, 12,
        345, 7, 220,
        false, false, true, false, 0, null);
      a.dispatchEvent(obj);
    }
  }

  /**
   *用于父组件调用设置被选择的节点
   * param node
   * param {any} checked
   * param {any} checkTypeFlag
   * param {any} callbackFlag
   */
  checkNode(data, checked, checkTypeFlag, callbackFlag) {
    data.forEach(item => {
      const node = this.treeInstance.getNodesByParam(this.treeSelectorConfig.treeSetting.data.simpleData.idKey, item, null);
      this.treeInstance.checkNode(node, checked, checkTypeFlag, callbackFlag);
    });
  }

  // modelChange(event) {
  //   this.search();
  //   const a = document.getElementById('searchDropDown')as any;
  //   const obj = document.createEvent('MouseEvents');
  //   obj.initMouseEvent('click', true,
  //     true, window, 1, 12,
  //     345, 7, 220,
  //     false, false, true, false, 0, null);
  //   a.dispatchEvent(obj);
  // }

  modelChange(event) {
    const node = this.treeInstance.getNodeByParam(this.treeSelectorConfig.treeSetting.data.simpleData.idKey, event, null);
    this.treeInstance.selectNode(node);
  }

  inputChange(event) {
    this.searchValue = event;
    if (event) {
      const node = this.treeInstance.getNodesByParamFuzzy(this.treeSelectorConfig.treeSetting.data.key.name || 'name',
        event, null);
      this.selectInfo = {
        data: node,
        label: this.treeSelectorConfig.treeSetting.data.key.name || 'name',
        value: this.treeSelectorConfig.treeSetting.data.simpleData.idKey || 'id'
      };
    } else {
      this.selectInfo = {
        data: [],
        label: this.treeSelectorConfig.treeSetting.data.key.name || 'name',
        value: this.treeSelectorConfig.treeSetting.data.simpleData.idKey || 'id'
      };
    }

  }
}
