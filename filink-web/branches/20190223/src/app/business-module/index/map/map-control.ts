import {NzI18nService} from 'ng-zorro-antd';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';
import {FacilityName} from './facility-name';

export class MapControl extends FacilityName {
  // 设施过滤组件
  isExpandLeftComponents = false; // 是否展开左侧组件
  isExpandLogicArea = false;      // 是否展开逻辑区域筛选组件
  isExpandFacilityList = false;   // 是否展开设施列表筛选组件
  isExpandMyCollection = false;   // 是否展开我的关注组件
  isExpandFacilityType = false;   // 是否展开设施类型筛选组件
  isExpandFacilityStatus = false; // 是否展开设施状态筛选组件

  // 点击后的模态框
  isShowFacilityPanel = false;         // 是否展开设施详情面板

  // 模拟鼠标移上去时的提示框
  isShowClustererFacilityTable = false;     // 是否显示聚合点设施详情

  isShowLeftComponents = true;      // 是否显示左侧筛选组件

  constructor(public $nzI18n: NzI18nService) {
    super($nzI18n);
  }

  /**
   * 展开逻辑区域组件
   */
  expandLogicArea() {
    this.isExpandLogicArea = true;
    this.checkLeftComponents();
  }

  /**
   * 收起逻辑区域组件
   */
  foldLogicArea() {
    this.isExpandLogicArea = false;
    this.checkLeftComponents();
  }

  /**
   * 展开设施列表组件
   */
  expandFacilityList() {
    this.isExpandFacilityList = true;
    this.checkLeftComponents();
  }

  /**
   * 收起设施列表组件
   */
  foldFacilityList() {
    this.isExpandFacilityList = false;
    this.checkLeftComponents();
  }

  /**
   * 展开我的关注组件
   */
  expandMyCollection() {
    this.isExpandMyCollection = true;
    this.checkLeftComponents();
  }

  /**
   * 收起我的关注组件
   */
  foldMyCollection() {
    this.isExpandMyCollection = false;
    this.checkLeftComponents();
  }

  /**
   * 展开设施类型组件
   */
  expandFacilityType() {
    this.isExpandFacilityType = true;
  }

  /**
   * 收起设施类型组件
   */
  foldFacilityType() {
    this.isExpandFacilityType = false;
  }

  /**
   * 展开设施状态组件
   */
  expandFacilityStatus() {
    this.isExpandFacilityStatus = true;
  }

  /**
   * 收起设施状态组件
   */
  foldFacilityStatus() {
    this.isExpandFacilityStatus = false;
  }

  /**
   * 展开左侧组件
   */
  expandLeftComponents() {
    this.isExpandLeftComponents = true;
    this.expandFacilityList();
    this.expandLogicArea();
    this.expandMyCollection();
  }

  /**
   * 收起左侧组件
   */
  foldLeftComponents() {
    this.isExpandLeftComponents = false;
    this.foldFacilityList();
    this.foldLogicArea();
    this.foldMyCollection();
  }

  /**
   * 显示左侧组件
   */
  showLeftComponents() {
    this.isShowLeftComponents = true;
  }

  /**
   * 隐藏左侧组件
   */
  hideLeftComponents() {
    this.isShowLeftComponents = false;
  }

  /**
   * 收起左侧组件
   */
  checkLeftComponents() {
    if (this.isExpandLogicArea && this.isExpandFacilityList) {
      this.expandLeftComponents();
    } else if (!this.isExpandLogicArea && !this.isExpandFacilityList) {
      this.foldLeftComponents();
    } else {
    }
  }

  /**
   * 显示设施详情面板
   */
  showFacilityPanel() {
    this.isShowFacilityPanel = true;
  }

  /**
   * 隐藏设施详情面板
   */
  hideFacilityPanel() {
    this.isShowFacilityPanel = false;
  }

  /**
   * 点击展开显示聚合点设施详情
   */
  showClustererFacilityTable() {
    this.isShowClustererFacilityTable = true;
  }

  /**
   * 隐藏聚合点设施详情
   */
  hideClustererFacilityTable() {
    this.isShowClustererFacilityTable = false;
  }

  /**
   * 展开收起左侧
   */
  expandAndFoldLeftComponents() {
    this.hideFacilityPanel();
    this.isExpandLeftComponents = !this.isExpandLeftComponents;
    if (this.isExpandLeftComponents) {
      this.expandLogicArea();
      this.expandFacilityList();
    } else {
      this.foldLogicArea();
      this.foldFacilityList();
    }
  }
}
