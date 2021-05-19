import {Component, OnInit} from '@angular/core';
import {Result} from '../../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {AreaService} from '../../../../core-module/api-service/facility';
import {NzI18nService} from 'ng-zorro-antd';
import {MapSelectorConfig} from '../../../../shared-module/entity/mapSelectorConfig';
import {ActivatedRoute, Router} from '@angular/router';
import {MapService} from '../../../../core-module/api-service/index/map';
import {InspectionLanguageInterface} from '../../../../../assets/i18n/inspection-task/inspection.language.interface';
import {InspectionService} from '../../../../core-module/api-service/work-order/inspection';

@Component({
  selector: 'app-set-area-device',
  templateUrl: './set-area-device.component.html',
  styleUrls: ['./set-area-device.component.scss']
})
export class SetAreaDeviceComponent implements OnInit {
  inspectionTaskId;  // 巡检任务ID
  mapVisible = true;
  deviceSet = [];
  fristData = [];
  selectData = [];
  isSelectAll = true;  // 是否巡检全集
  public language: InspectionLanguageInterface;
  mapSelectorConfig: MapSelectorConfig;
  public areaId: string = null;
  public noIndex: boolean = false;

  constructor(private $message: FiLinkModalService,
              private $nzI18n: NzI18nService,
              private $router: Router,
              private $active: ActivatedRoute,
              private $mapService: MapService,
              private $inspection: InspectionService,
              private $areaService: AreaService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('inspection');
    this.initMapSelectorConfig();
    this.inspectionWorkSelectDevice();
  }

  /**
   * 关联每条巡检任务所选设施
   * param id
   */
  inspectionWorkSelectDevice() {
    this.$active.queryParams.subscribe(param => {
      this.inspectionTaskId = param.inspectionTaskId;
      const id = new Object();
      id['inspectionTaskId'] = this.inspectionTaskId;
      this.$inspection.inspectionFacility(id).subscribe((result: Result) => {
        if (result.code === 0) {
          if (result.data.length > 0) {
            this.areaId = result.data[0].deviceAreaId;
            this.deviceSet = result.data.map(item => item.deviceId);
            console.log(this.deviceSet);
          }
        }
      });
    });
  }


  /**
   * 添加到列表
   * param item
   */
  public pushToTable(item) {
    const index = this.selectData.findIndex(_item => item.deviceId === _item.deviceId);
    if (index === -1) {
      item.checked = true;
      if (item.areaId && item.areaId !== this.areaId) {
        // item.remarks = `当前选择属于${item.areaName}区`;
        item.rowActive = true;
      }
      this.selectData = this.selectData.concat([item]);
    } else {
    }
  }

  /**
   * 关联区域所选结果
   * param event
   */
  mapSelectDataChange(event) {
    const list = event.map(item => item.deviceId);
    const obj = {};
    obj[this.areaId] = list;
    console.log(obj);
    this.setAreaDevice(obj);
  }

  /**
   * 弹框的显示隐藏
   * param event
   */
  xcVisibleChange(event) {
    if (event) {

    } else {
      this.$router.navigate(['/business/work-order/inspection/task']).then();
    }
  }

  /**
   * 关联设施
   * param body
   */
  private setAreaDevice(body) {
    this.$areaService.setAreaDevice(body).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  private initMapSelectorConfig() {
    this.mapSelectorConfig = {
      title: this.language.inspectionFacility,
      width: '1100px',
      height: '465px',
      mapData: [],
      selectedColumn: [
        {
          title: this.language.deviceName, key: 'deviceName', width: 80
        },
        {
          title: this.language.deviceCode, key: 'deviceCode', width: 80,
        },
        {
          title: this.language.deviceType, key: '_deviceType', width: 60,
        },
        {
          title: this.language.parentId, key: 'areaName', width: 80,
        }
      ]
    };
  }
}
