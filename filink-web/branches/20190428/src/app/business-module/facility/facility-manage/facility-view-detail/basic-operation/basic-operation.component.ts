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
import {FacilityMissionService} from '../../../../../core-module/mission/facility.mission.service';

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
  @Input()
  hasGuard: boolean = false;
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
              private $refresh: FacilityMissionService,
              private $nzI18n: NzI18nService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.lockInfoConfig = {
      noIndex: true,
      columnConfig: [
        {type: 'select', width: 62},
        {type: 'serial-number', width: 62, title: this.language.serialNumber},
        {title: this.language.doorNum, key: 'doorNum', width: 100},
        {title: this.language.doorName, key: 'doorName', width: 100},
        // {title: this.language.lockNum, key: 'lockNum', width: 100},
      ]
    };
  }

  clickButton() {
    this.getLockInfo(this.deviceId).then((res: any[]) => {
      if (res.length > 0) {
        this.$router.navigate(['business/facility/facility-config'],
          {queryParams: {id: this.deviceId, serialNum: this.serialNum, deviceType: this.deviceType}}).then();
      } else {
        this.$message.error(this.language.noControlMsg);
      }
    });
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
      // 设施授权弹框
      const modal = this.$modal.create({
        nzTitle: this.language.authorization,
        nzContent: this.openLockTemp,
        nzOkText: this.language.handleCancel,
        nzCancelText: this.language.handleOk,
        nzOkType: 'danger',
        nzClassName: 'custom-create-modal',
        nzMaskClosable: false,
        nzFooter: [
          {
            label: this.language.handleOk,
            onClick: () => {
              const slotNum = [];
              this.lockInfo.forEach(item => {
                if (item.checked) {
                  slotNum.push(item.doorNum);
                }
              });
              if (slotNum.length === 0) {
                this.$message.error(this.language.chooseDoorLock);
                return;
              }
              // 跳转到设施授权传入设施id和门号
              this.$router.navigate(['/business/user/unified-details/add'],
                {queryParams: {id: this.deviceId, slotNum: slotNum.join(',')}}).then(() => {
                modal.destroy();
              });
            }
          },
          {
            label: this.language.handleCancel,
            type: 'danger',
            onClick: () => {
              modal.destroy();
            }
          },
        ]
      });
      this.getLockInfo(this.deviceId).then();
    } else {
      this.$message.error(this.language.noSupport);
    }
  }

  /**
   * 获取电子锁信息
   */
  getLockInfo(deviceId) {
    return new Promise((resolve, reject) => {
      this.$lockService.getLockInfo(deviceId).subscribe((result: Result) => {
        this.lockInfo = result.data || [];
        resolve(this.lockInfo);
      }, (err) => {
        reject(err);
      });
    });

  }

  /**
   * 更新设施状态
   * param {string} deviceStatus
   * param {any[]} devices
   */
  public updateDeviceStatus(deviceStatus: string, msg) {
    this.handlePrompt(msg, () => {
      const body = {deviceIds: [this.deviceId], deployStatus: deviceStatus};
      this.$lockService.updateDeviceStatus(body).subscribe((result: Result) => {
        if (result.code === 0) {
          this.$message.success(result.msg);
          // 请求成功要求面板刷新数据
          this.$refresh.refreshChange(true);
        } else {
          this.$message.error(result.msg);
        }
      });
    });

  }

  /**
   *  处理提示消息
   *  param nzContent
   *  param handle
   */
  handlePrompt(nzContent, handle) {
    this.$modal.confirm({
      nzTitle: this.language.prompt,
      nzContent: `${this.language.editDeployStatusMsg}${nzContent}${this.language.questionMark}`,
      nzMaskClosable: false,
      nzOkText: this.language.handleCancel,
      nzCancelText: this.language.handleOk,
      nzOkType: 'danger',
      nzOnOk: () => {
      },
      nzOnCancel: () => {
        handle();
      }
    });
  }
}

