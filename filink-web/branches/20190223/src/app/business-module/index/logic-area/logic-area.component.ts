import {AfterViewInit, Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import 'ztree';
import {MapService} from '../../../core-module/api-service/index/map';
import {Result} from '../../../shared-module/entity/result';
import {NzI18nService} from 'ng-zorro-antd';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
declare var $: any;

@Component({
  selector: 'app-logic-area',
  templateUrl: './logic-area.component.html',
  styleUrls: ['./logic-area.component.scss']
})
export class LogicAreaComponent implements OnInit, AfterViewInit {
  @ViewChild('input') inputElement: ElementRef;
  @Output() logicAreaEvent = new EventEmitter();
  setting;
  treeInstance: any;
  zNodes = [];
  title;
  language;
  searchKey;
  searchResult = [];
  noDataTip;
  constructor(
    private $mapService: MapService,
    private $nzI18n: NzI18nService,
    private $mapStoreService: MapStoreService,
    private $message: FiLinkModalService,
  ) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocale();
    this.title = this.language.index.selectArea;
    this.noDataTip = this.language.common.noData;
    this.initTreeSetting();
    this.$mapStoreService.isInitLogicAreaData ? this.getAllAreaListFromStore() : this.getAllAreaList();
  }

  initTreeSetting() {
    this.setting = {
      check: {
        enable: true,
        chkboxType: {'Y': '', 'N': ''}
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
        onCheck: (event, treeId, treeNode) => {
          this.updateLogicAreaList();
        }
      }
    };
  }

  getAllAreaList() {
    this.$mapService.getALLAreaList().subscribe((result: Result) => {
      console.log(result);
      this.$mapStoreService.logicAreaList = result.data.map(item => {
        item.checked = true;
        return item;
      });
      this.addTreeData();
      this.$mapStoreService.isInitLogicAreaData = true;
      this.logicAreaEvent.emit({type: 'update', refresh: false});
    },  err => {
      this.$message.error(err.msg);
    });
  }

  getAllAreaListFromStore() {
    this.addTreeData();
  }

  updateLogicAreaList() {
    this.$mapStoreService.logicAreaList = this.treeInstance.transformToArray(this.treeInstance.getNodes()).map(item => {
      return {
        areaId: item.id,
        parentAreaId: item.pId,
        areaName: item.name,
        areaLevel: item.isParent ? 1 : 0,
        checked: item.checked
        // open: false
      };
    });
    this.logicAreaEvent.emit({type: 'update', refresh: true});
  }

  addTreeData() {
    const arr = [];
    this.$mapStoreService.logicAreaList.forEach(item => {
      arr.push({
        id: item.areaId,
        pId: item.parentAreaId,
        name: item.areaName,
        isParent: item.areaLevel === 1,
        checked: item.checked
          // open: false
      });
    });
    this.zNodes = [].concat(arr);
    $.fn.zTree.init($('#ztree'), this.setting, this.zNodes);
    this.treeInstance = $.fn.zTree.getZTreeObj('ztree');
    // if (this.treeInstance) {
    // }
  }

  close() {
    this.logicAreaEvent.emit({type: 'close'});
  }


  search() {
    if (this.searchKey) {
      const node = this.treeInstance.getNodesByParamFuzzy('name', this.searchKey, null);
      this.searchResult = this.treeInstance.transformToArray(node);
    }
  }

  /**
   * 定位到某一条
   * param item
   */
  selectNode(item) {
    this.treeInstance.selectNode(item);
  }

  ngAfterViewInit() {
    console.log(this.inputElement);
    this.inputElement.nativeElement.focus();
  }
}
