import {AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {TreeSelectorConfig} from '../../entity/treeSelectorConfig';
import {FiLinkModalService} from '../../service/filink-modal/filink-modal.service';
import {NzI18nService} from 'ng-zorro-antd';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';

declare var $: any;

@Component({
  selector: 'xc-tree-area-selector',
  templateUrl: './tree-area-selector.component.html',
  styleUrls: ['./tree-area-selector.component.scss']
})
export class TreeAreaSelectorComponent implements OnInit, OnChanges, AfterViewInit {
  @Output() xcVisibleChange = new EventEmitter<any>();
  @Output() selectDataChange = new EventEmitter<any>();

  @Input() pageType;
  @Input() level;
  @Input() treeSelectorConfig: any = new TreeSelectorConfig();
  @Input() hasChild: boolean;
  @Input() canClear: boolean = true;
  searchValue;
  searchResult = [];
  checkedNode = [];
  private treeInstance: any;

  _xcVisible = false;
  public language: any;
  public commonLanguage: CommonLanguageInterface;

  get xcVisible() {
    return this._xcVisible;
  }

  @Input()
  set xcVisible(params) {
    this._xcVisible = params;
    this.xcVisibleChange.emit(this._xcVisible);
  }

  constructor(private $message: FiLinkModalService,
              private $i18n: NzI18nService) {
  }

  ngOnInit() {
    this.language = this.$i18n.getLocaleData('form');
    this.commonLanguage = this.$i18n.getLocaleData('common');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes && changes.xcVisible && changes.xcVisible.currentValue) {
      this.searchValue = '';
      this.checkedNode = [];
      this.treeInstance = $.fn.zTree.init($('#treeAreaSelector'), Object.assign(this.treeSelectorConfig.treeSetting, {
        callback: {
          beforeCheck: (treeId, treeNode) => {
            // 如果为修改 只能选同一级别的
            if (this.pageType === 'update' && this.level && this.hasChild) {
              // ztree 的级别从0开始 要先加1
              if (treeNode.level + 1 + 1 !== this.level) {
                this.$message.error(this.commonLanguage.levelEdit);

                return false;
              }
            }
            // 最多只能有5级
            if (treeNode.level + 1 === 5) {
              this.$message.error(this.commonLanguage.levelLimit);
              return false;
            }
            const checkedSelf = treeNode.checked;
            const checkedItems = this.treeInstance.getCheckedNodes(true);
            // 所有去选
            this.treeInstance.checkAllNodes(false);
            const id = this.treeSelectorConfig.treeSetting.data.simpleData.idKey;
            if (checkedItems.length > 0 && checkedItems[0][id] === treeNode[id] && this.canClear) {
              if (this.hasChild) {
                // 还原自己的选择状态 下次取反
                treeNode.checked = checkedSelf;
                this.$message.error(this.commonLanguage.levelEdit);
                return false;
              } else {
                treeNode.checked = checkedSelf;
              }

            } else {

            }
          }
        }
      }), this.treeSelectorConfig.treeNodes);
      // 获取刚进来的时候的选中状态
      this.checkedNode = this.treeInstance.getCheckedNodes(true);
      if (this.checkedNode.length > 0) {
        this.treeInstance.selectNode(this.checkedNode[0]);
      }
    }
  }

  ngAfterViewInit(): void {
  }

  handleCancel() {
    this.xcVisible = false;
  }

  handleOk() {
    this.selectDataChange.emit(this.treeInstance.getCheckedNodes(true));
    this.handleCancel();
  }

  /**
   * 阻止触发事件冒泡
   * param event
   */
  inputClick(event) {
    event.stopPropagation();
  }

  search() {
    if (this.searchValue) {
      const node = this.treeInstance.getNodesByParamFuzzy(this.treeSelectorConfig.treeSetting.data.key.name || 'name',
        this.searchValue, null);
      // this.searchResult = this.treeInstance.transformToArray(node);
      this.searchResult = node;
    }
  }

  /**
   * 定位到某一条
   * param item
   */
  selectNode(item) {
    this.treeInstance.selectNode(item);
  }

  onInputKeyUp(event) {
    if (event.keyCode === 13) {
      this.search();
      const a = document.getElementById('aaa')as any;
      const obj = document.createEvent('MouseEvents');
      obj.initMouseEvent('click', true,
        true, window, 1, 12,
        345, 7, 220,
        false, false, true, false, 0, null);
      a.dispatchEvent(obj);
    }
  }

  restSelectData() {
    this.searchValue = '';
    // 如果原来有值到情况下重置到原来到状态
    this.treeInstance.checkAllNodes(false);
    if (this.checkedNode.length > 0) {
      this.checkedNode.forEach(item => {
        this.treeInstance.checkNode(item, true, false);
      });
    } else {
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

  modelChange(event) {
    this.search();
    const a = document.getElementById('aaa')as any;
    const obj = document.createEvent('MouseEvents');
    obj.initMouseEvent('click', true,
      true, window, 1, 12,
      345, 7, 220,
      false, false, true, false, 0, null);
    a.dispatchEvent(obj);
  }
}
