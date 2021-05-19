import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {Result} from '../../../../../shared-module/entity/result';
import {FacilityService} from '../../../../../core-module/api-service/facility/facility-manage';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {DeviceTypeCode} from '../../../facility.config';
import {LockService} from '../../../../../core-module/api-service/lock';
import {TableConfig} from '../../../../../shared-module/entity/tableConfig';
import {PageBean} from '../../../../../shared-module/entity/pageBean';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';

@Component({
  selector: 'app-basic-operation',
  templateUrl: './basic-operation.component.html',
  styleUrls: ['./basic-operation.component.scss']
})
export class BasicOperationComponent implements OnInit {
  @Input()
  deviceId: string;
  @Input()
  serialNum: string;
  @Input()
  deviceType: string;
  @ViewChild('openLockTemp') openLockTemp: TemplateRef<any>;
  public lockInfo = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  public language: FacilityLanguageInterface;
  public lockInfoConfig: TableConfig;

  constructor(private $router: Router,
              private $message: FiLinkModalService,
              private $facilityService: FacilityService,
              private $modal: NzModalService,
              private $lockService: LockService,
              private $nzI18n: NzI18nService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.lockInfoConfig = {
      noIndex: true,
      columnConfig: [
        {type: 'select', width: 62},
        {type: 'serial-number', width: 62, title: this.language.serialNumber},
        {title: this.language.doorNum, key: 'lockNum', width: 100},
        {title: this.language.doorName, key: 'doorName', width: 100},
        {title: this.language.lockNum, key: 'lockNum', width: 100},
      ]
    };
  }

  clickButton() {
    this.$router.navigate(['business/facility/facility-Config'],
      {queryParams: {id: this.deviceId, serialNum: this.serialNum, deviceType: this.deviceType}}).then();
  }

  deleteFacility() {
    // 组件中的确定取消按钮是反的所以反着用
    this.$modal.confirm({
      nzTitle: this.language.prompt,
      nzContent: this.language.deleteFacilityMsg,
      nzMaskClosable: false,
      nzOkText: this.language.handleCancel,
      nzCancelText: this.language.handleOk,
      nzOkType: 'danger',
      nzOnOk: () => {
      },
      nzOnCancel: () => {
        this.$facilityService.deleteDeviceDyIds([this.deviceId]).subscribe((result: Result) => {
          if (result.code === 0) {
            this.$message.success(result.msg);
            this.$router.navigate(['business/facility/facility-list']).then();
          } else {
            this.$message.error(result.msg);
          }
        });
      }
    });

  }

  updateFacility() {
    this.$router.navigate(['business/facility/facility-detail/update'],
      {queryParams: {id: this.deviceId}}).then();
  }

  authorization() {
    if (this.deviceType === DeviceTypeCode.Well ||
      this.deviceType === DeviceTypeCode.Optical_Box
    ) {
      // todo 此次应该使用[nzFooter]传按钮的模式
      this.$modal.create({
        nzTitle: this.language.authorization,
        nzContent: this.openLockTemp,
        nzOkText: this.language.handleCancel,
        nzCancelText: this.language.handleOk,
        nzOkType: 'danger',
        nzClassName: 'custom-create-modal',
        nzOnOk: () => {
          //   const slotNum = [];
          //   this.lockInfo.forEach(item => {
          //     if (item.checked) {
          //       slotNum.push(item.lockNum);
          //     }
          //   });
          //   const body = {serialNum: currentIndex.serialNum, slotNumList: slotNum};
          //   this.openLock(body);
        }
      });
      this.getLockInfo(this.deviceId);
    } else {
      this.$message.error(this.language.noSupport);
    }
  }

  /**
   * 获取电子锁信息
   */
  getLockInfo(deviceId) {
    this.$lockService.getLockInfo(deviceId).subscribe((result: Result) => {
      this.lockInfo = result.data || [];
    });
  }
}

