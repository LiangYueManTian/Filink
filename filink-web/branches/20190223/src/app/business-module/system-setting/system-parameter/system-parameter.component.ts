import {Component, OnInit, ViewChild} from '@angular/core';
import {BasicConfig} from '../../basic-config';
import {NzI18nService} from 'ng-zorro-antd';
import {ActivatedRoute, Router} from '@angular/router';
import {ColumnConfigService} from '../column-config.service';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';

@Component({
  selector: 'app-system-parameter',
  templateUrl: './system-parameter.component.html',
  styleUrls: ['./system-parameter.component.scss']
})
export class SystemParameterComponent extends BasicConfig implements OnInit {

  pageTitle = '显示设置';
  // 参数设置类型
  settingType = '';

  // 新增弹出框显示隐藏
  isVisible = true;

  // 文件名称
  fileName: '';
  // 上传的文件
  file: any;
  @ViewChild('logo') private logoTemplate;
  constructor(public $nzI18n: NzI18nService,
              private $columnConfigService: ColumnConfigService,
              private $message: FiLinkModalService,
              private $activatedRoute: ActivatedRoute) {
    super($nzI18n);
  }

  ngOnInit() {
    this.$activatedRoute.params.subscribe(params => {
      this.dealTitle(params['setting-type']);
    });
  }

  /**
   * 处理不同参数设置的title以及表单配置
   * param type
   */
  dealTitle(type) {
    switch (type) {
      case 'show':
        this.settingType = 'show';
        this.pageTitle = '显示设置';
        // 初始化表单
        const initData = {
          logo: this.logoTemplate
        };
        this.formColumn = this.$columnConfigService.getShowSystemParameterFormConfig(initData);
        break;
      case 'msg':
        this.settingType = 'msg';
        this.pageTitle = '消息通知设置';
        this.formColumn = this.$columnConfigService.getMsgSystemParameterFormConfig({});
        break;
      case 'email':
        this.settingType = 'email';
        this.pageTitle = '邮件服务器设置';
        this.formColumn = this.$columnConfigService.getEmailSystemParameterFormConfig({});
        break;
      case 'note':
        this.settingType = 'note';
        this.pageTitle = '短信服务设置';
        this.formColumn = this.$columnConfigService.getNoteSystemParameterFormConfig({});
        break;
      case 'push':
        this.settingType = 'push';
        this.pageTitle = '推送服务设置';
        this.formColumn = this.$columnConfigService.getPushSystemParameterFormConfig({});
        break;
      case 'ftp':
        this.settingType = 'ftp';
        this.pageTitle = 'FTP服务设置';
        this.formColumn = this.$columnConfigService.getFTPSystemParameterFormConfig({});
        break;
    }
  }

  /**
   * 文件变化
   */
  fileChange() {
    // 文件名效验
    const fileNode = document.getElementById('file');
    const fileName = fileNode['files'][0].name;
    const reg = /(.jpg|.png|.jpeg|.gig)$/i;
    if (reg.test(fileName)) {
      this.file = fileNode['files'][0];
      this.fileName = this.file.name;
      this.$message.info(this.language.agreement.uploadSuccess);
    } else {
      this.$message.warning('目前只支持传图片格式文件！');
    }
  }

  /**
   * 文件上传
   */
  upload() {
    const fileNode = document.getElementById('file');
    fileNode.click();
  }

  /**
   * 确定
   */
  formHandleOk() {
    super.formHandleOk();
  }

  /**
   * 取消
   */
 formHandleCancel() {
    super.formHandleCancel();
  }

  /**
   * 恢复默认
   */
  formHandleReset() {
    super.formHandleReset();
  }
}
