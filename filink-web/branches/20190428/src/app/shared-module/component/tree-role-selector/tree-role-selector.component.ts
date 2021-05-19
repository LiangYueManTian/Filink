import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { TreeSelectorConfig } from '../../entity/treeSelectorConfig';
import { CommonLanguageInterface } from '../../../../assets/i18n/common/common.language.interface';
import { NzI18nService } from 'ng-zorro-antd';

declare var $: any;

@Component({
  selector: 'xc-tree-role-selector',
  templateUrl: './tree-role-selector.component.html',
  styleUrls: ['./tree-role-selector.component.scss']
})
export class TreeRoleSelectorComponent implements OnInit, OnChanges {
  @Input()
  treeSelectorConfig: TreeSelectorConfig = new TreeSelectorConfig();
  @Output() xcVisibleChange = new EventEmitter<boolean>();
  @Output() selectDataChange = new EventEmitter<any>();
  @Input() isHiddenButton = false;
  public commonLanguage: CommonLanguageInterface;
  private selectDataLeft;
  private selectDataRight;
  private treeInstanceLeft: any;
  private treeInstanceRight: any;
  searchValueLeft;
  searchValueRight;
  selectInfoLeft = {
    data: [],
    label: 'label',
    value: 'code'
  };
  selectInfoRight = {
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
    this.commonLanguage = this.$i18n.getLocaleData('common');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes && changes.xcVisible && changes.xcVisible.currentValue) {
      // 初始化左边的树
      $.fn.zTree.init($('#roleLeft'),
        Object.assign(this.treeSelectorConfig.treeLeftSetting, {}),
        this.treeSelectorConfig.leftNodes);
      this.treeInstanceLeft = $.fn.zTree.getZTreeObj('roleLeft');
      // 展开到已选数据
      this.selectDataLeft = this.treeInstanceLeft.getCheckedNodes(true);
      if (this.selectDataLeft.length > 0) {
        this.selectDataLeft.forEach(item => {
          this.treeInstanceLeft.selectNode(item);
        });
      }
      // 初始化右边的树
      $.fn.zTree.init($('#roleRight'),
        Object.assign(this.treeSelectorConfig.treeRightSetting, {}),
        this.treeSelectorConfig.rightNodes);
      this.treeInstanceRight = $.fn.zTree.getZTreeObj('roleRight');
      // 展开到已选数据
      this.selectDataRight = this.treeInstanceRight.getCheckedNodes(true);
      if (this.selectDataRight.length > 0) {
        this.selectDataRight.forEach(item => {
          this.treeInstanceRight.selectNode(item);
        });
      }
    }
  }

  handleOk() {
    const selectData = {
      left: this.treeInstanceLeft.getCheckedNodes(true),
      right: this.treeInstanceRight.getCheckedNodes(true),
    };
    this.selectDataChange.emit(selectData);
    this.handleCancel();
  }

  handleCancel() {
    this.xcVisible = false;
  }

  restSelectData() {
    // 重置左边数据
    this.treeInstanceLeft.checkAllNodes(false);
    if (this.selectDataLeft.length > 0) {
      this.selectDataLeft.forEach(item => {
        this.treeInstanceLeft.checkNode(item, true, false);
      });
    } else {
    }
    // 重置右边数据
    this.treeInstanceRight.checkAllNodes(false);
    if (this.selectDataRight.length > 0) {
      this.selectDataRight.forEach(item => {
        this.treeInstanceRight.checkNode(item, true, false);
      });
    } else {
    }
  }

  /**
   * 左侧搜索框input值变化
   * param event
   */
  inputChangeLeft(event) {
    this.searchValueLeft = event;
    if (event) {
      const node = this.treeInstanceLeft.getNodesByParamFuzzy(this.treeSelectorConfig.treeLeftSetting.data.key.name || 'name',
        event, null);
      this.selectInfoLeft = {
        data: node,
        label: this.treeSelectorConfig.treeLeftSetting.data.key.name || 'name',
        value: this.treeSelectorConfig.treeLeftSetting.data.simpleData.idKey || 'id'
      };
    } else {
      this.selectInfoLeft = {
        data: [],
        label: this.treeSelectorConfig.treeLeftSetting.data.key.name || 'name',
        value: this.treeSelectorConfig.treeLeftSetting.data.simpleData.idKey || 'id'
      };
    }

  }
   /**
   * 左侧搜索组件选中某一条
   * param event id
   */
  modelChangeLeft(event) {
    const node = this.treeInstanceLeft.getNodeByParam(this.treeSelectorConfig.treeLeftSetting.data.simpleData.idKey, event, null);
    this.treeInstanceLeft.selectNode(node);
  }

/////////////////////////////////////////////////

  /**
   * 右侧搜索框input值变化
   * param event
   */
  inputChangeRight(event) {
    this.searchValueRight = event;
    if (event) {
      const node = this.treeInstanceRight.getNodesByParamFuzzy(this.treeSelectorConfig.treeRightSetting.data.key.name || 'name',
        event, null);
      this.selectInfoRight = {
        data: node,
        label: this.treeSelectorConfig.treeRightSetting.data.key.name || 'name',
        value: this.treeSelectorConfig.treeRightSetting.data.simpleData.idKey || 'deviceTypeId'
      };
    } else {
      this.selectInfoRight = {
        data: [],
        label: this.treeSelectorConfig.treeRightSetting.data.key.name || 'name',
        value: this.treeSelectorConfig.treeRightSetting.data.simpleData.idKey || 'deviceTypeId'
      };
    }

  }
   /**
   * 右侧搜索组件选中某一条
   * param event id
   */
  modelChangeRight(event) {
    const node = this.treeInstanceRight.getNodeByParam(this.treeSelectorConfig.treeRightSetting.data.simpleData.idKey, event, null);
    this.treeInstanceRight.selectNode(node);
  }
}
