import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {ColumnConfigService} from '../../../column-config.service';
import {BasicConfig} from '../../../../basic-config';

@Component({
  selector: 'app-access-control-detial',
  templateUrl: './access-control-detial.component.html',
  styleUrls: ['./access-control-detial.component.scss']
})
export class AccessControlDetialComponent extends BasicConfig implements OnInit {

  @Input() title = '新增IP地址范围';
  @Input() isVisible = false;
  @Input() ipInfo = {};
  // 推送取消事件
  @Output() cancel = new EventEmitter();

  // 确认按钮动画配置
  isConfirmLoading = false;

  constructor(public $nzI18n: NzI18nService,
              private $columnConfigService: ColumnConfigService) {
    super($nzI18n);
  }

  ngOnInit(): void {
    this.formColumn = this.$columnConfigService.getAccessControlFormConfig(this.ipInfo);
  }


  /**
   * 确认按钮
   */
  handleOk(): void {
    this.isConfirmLoading = true;
  }

  /**
   * 取消按钮
   */
  handleCancel(): void {
    this.cancel.emit();
  }

}
