import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import 'ztree';
import {MapService} from '../../../core-module/api-service/index/map';
import {Result} from '../../../shared-module/entity/result';
import {NzI18nService} from 'ng-zorro-antd';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {TreeSelectorConfig} from '../../../shared-module/entity/treeSelectorConfig';
import {MapConfig as BMapConfig} from '../../../shared-module/component/map/b-map.config';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
declare var $: any;

/**
 * 选择区域组件
 */
@Component({
  selector: 'app-logic-area',
  templateUrl: './logic-area.component.html',
  styleUrls: ['./logic-area.component.scss']
})
export class LogicAreaComponent implements OnInit, AfterViewInit, OnChanges {
  @ViewChild('input') inputElement: ElementRef;
  @Output() logicAreaEvent = new EventEmitter();
  @Input() treeSelectorConfig: TreeSelectorConfig = new TreeSelectorConfig();
  @Input() isShowNoData = false;
  setting;
  treeInstance: any;
  zNodes = [];
  title;
  language;
  // 国际化
  public indexLanguage: IndexLanguageInterface;
  searchKey;
  searchResult = [];
  selectInfo = {
    data: [],
    label: 'label',
    value: 'code'
  };
  noDataTip;
  public treeNodeSum = 0;
  public selectData: any[] = [];
  private firstSelectData: any[];
  constructor(
    private $mapService: MapService,
    private $nzI18n: NzI18nService,
    private $mapStoreService: MapStoreService,
    private $message: FiLinkModalService,
  ) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocale();
    // 国际化配置
    this.indexLanguage = this.$nzI18n.getLocaleData('index');
    this.title = this.language.index.selectArea;
    this.noDataTip = this.language.common.noData;
    this.initTreeSetting();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.areaDataList && changes.$mapStoreService.currentValue) {
      this.addTreeData();
    }
  }

  ngAfterViewInit() {
    this.$mapStoreService.isInitLogicAreaData ? this.getAllAreaListFromStore() : this.getAllAreaList();
  }

  /**0
   * 初始化tree配置
   */
  initTreeSetting() {
    this.setting = {
      check: {
        enable: true,
        chkboxType: {'Y': '', 'N': ''},
      },
      data: {
        simpleData: {
          enable: true
        }
      },
      view: {
        showIcon: false,
        showLine: false
      },
      callback: {
        beforeClick: (event, treeId, treeNode) => {
          return false;
        },
        onCheck: (event, treeId, treeNode) => {
          this.updateLogicAreaList();
        }
      }
    };
  }

  /**
   * 获取区域列表
   */
  getAllAreaList() {
    this.$mapService.getALLAreaListForCurrentUser().subscribe((result: Result) => {
      if (result.code === 0) {
        this.$mapStoreService.logicAreaList = result.data.map((item, index) => {
          item.checked = true;
          // 少数据量，正常配置
          if (!this.$mapStoreService.hugeData  && !this.isShowNoData) {
            item.checked = true;
          }
          // 判读是否是大数据加载
          if (this.$mapStoreService.hugeData  && !this.isShowNoData) {
            item.checked = index === 0;
          }
          // 判读是否是不渲染数据
          if (this.isShowNoData) {
            item.checked = false;
          }
          // 已经缓存的区域
          if (this.$mapStoreService.chooseAllAreaID) {
            if (this.$mapStoreService.chooseAllAreaID.indexOf(item.areaId) !== -1) {
              item.checked  = true;
            }
          }
          return item;
        });


        this.$mapStoreService.chooseAllAreaID = this.$mapStoreService.logicAreaList.filter(item => item.hasPermissions && item.checked)
          .map(item => item.areaId);
        this.addTreeData();
        this.$mapStoreService.isInitLogicAreaData = true;
        this.logicAreaEvent.emit({type: 'update', refresh: false});

      } else {
        this.$message.error(result.msg);
      }
    },  err => {
    });
  }

  /**
   * 缓存中取数据
   */
  getAllAreaListFromStore() {
    this.addTreeData();
  }

  /**
   * 更新区域
   */
  updateLogicAreaList() {
    this.$mapStoreService.logicAreaList = this.treeInstance.transformToArray(this.treeInstance.getNodes()).map(item => {
      // 去掉区域中的带小括号的区域数
      const name = item.name.replace(/\([^)]*\)/g, '');
      return {
        areaId: item.id,
        parentAreaId: item.pId,
        areaName: name,
        areaLevel: item.isParent ? 1 : 0,
        checked: item.checked,
        hasPermissions: !item.chkDisabled
        // open: false
      };
    });
    this.logicAreaEvent.emit({type: 'update', refresh: true});
  }

  /**
   * 添加数据
   */
  addTreeData() {
    let  arr: any[];
    arr = this.$mapStoreService.logicAreaList.map(item => {
        let amountData: any[];
        let newName: string;
        if (this.$mapStoreService.areaDataList) {
          amountData = this.$mapStoreService.areaDataList.filter(items => items.areaId === item.areaId);
          // 拼接区域数据
          if (amountData.length > 0) {
            newName = item.areaName + ' (' + amountData[0].deviceNum + ')';
          } else {
            newName = item.areaName;
          }
        } else {
            newName = item.areaName;
        }

        return ({
              id: item.areaId,
              pId: item.parentAreaId,
              name: newName.replace(/\s+/g, ''),  // 去掉空格
              isParent: item.areaLevel === 1,
              checked: item.hasPermissions && item.checked,
              chkDisabled: !item.hasPermissions
        });
    });
    this.zNodes = [].concat(arr);
    $.fn.zTree.init($('#ztree'), this.setting, this.zNodes);
    this.treeInstance = $.fn.zTree.getZTreeObj('ztree');

    // 选择子节点
    if (this.treeInstance && this.$mapStoreService.hugeData) {
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
    }
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
   * 关闭
   */
  close() {
    this.logicAreaEvent.emit({type: 'close'});
  }

  /**
   * 搜索组件选中某一条
   * param event id
   */
  modelChange(event) {
    const node = this.treeInstance.getNodeByParam('id', event, null);
    this.treeInstance.selectNode(node);
  }

  /**
   * 搜索框input值变化
   * param event
   */
  inputChange(event) {
    if (event) {
      const node = this.treeInstance.getNodesByParamFuzzy('name',
        event, null);
      this.selectInfo = {
        data: node,
        label: 'name',
        value: 'id'
      };
    } else {
      this.selectInfo = {
        data: [],
        label: 'name',
        value: 'id'
      };
    }

  }

  /**
   * 搜索
   */
  search() {
    const _searchKey = this.searchKey.trim();
    if (this.searchKey) {
      const node = this.treeInstance.getNodesByParamFuzzy('name', _searchKey, null);
      // this.searchResult = this.treeInstance.transformToArray(node);
      this.searchResult = node;
    }
  }

  /**
   * 选择全部区域
   */
  public ClickSelectAll(type) {
    this.$mapStoreService.logicAreaList.forEach(item => {
      item.checked = type === 1;
    });
    this.addTreeData();
    this.updateLogicAreaList();
  }

  /**
   * 定位到某一条
   * param item
   */
  selectNode(item) {
    this.treeInstance.selectNode(item);
  }
}
