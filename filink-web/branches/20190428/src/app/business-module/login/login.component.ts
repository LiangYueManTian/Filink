import {Component, OnInit, ViewChild} from '@angular/core';
import {SystemParameterService} from '../../core-module/api-service/system-setting/stystem-parameter/system-parameter.service';
import {Result} from '../../shared-module/entity/result';
import {NzI18nService} from 'ng-zorro-antd';
import {FiLinkModalService} from '../../shared-module/service/filink-modal/filink-modal.service';
import {LoginService} from '../../core-module/api-service/login';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  // 是否是用户登入  切换登入方式
  autoLoginType = true;
  loginTitle = '手机登入在这';
  // 新增弹出框显示隐藏
  isVisible = false;
  language: any = {};
  title = '上传证书';
  // 文件名称
  fileName: '';
  // 上传的文件
  file: any;
  @ViewChild('tplFooter') public tplFooter;
  // xml文件校验
  limit = {
    nameLength: 32,
    size: 1048576,
    nameI18n: '文件名称长度不能大于32位',
    sizeI18n: '文件大小小于1M'
  };
  // 隐藏按钮
  showBtn = false;
  submitLoading = false;

  constructor(
    private $systemParameterService: SystemParameterService,
    public $nzI18n: NzI18nService,
    private $message: FiLinkModalService,
    private $loginService: LoginService) {
    this.language = $nzI18n.getLocale();
  }

  ngOnInit(): void {
    this.$systemParameterService.selectDisplaySettingsParamForPageCollection().subscribe((result: Result) => {
      if (result.code === 0) {
        // 显示设置
        localStorage.setItem('displaySettings', JSON.stringify(result.data.displaySettings));
        // 消息提示设置
        localStorage.setItem('messageNotification', JSON.stringify(result.data.messageNotification));
      }
    });
    this.validateLicense();
  }

  /**
   * licenes验证
   */
  validateLicense() {
    this.$loginService.validateLicense().subscribe((result: Result) => {
      if (result.code === 0) {
        if (result.data.licenseStatus === true) {
          this.showBtn = false;
        } else {
          this.showBtn = true;
          this.$message.warning('licenes文件已过期');
        }
      } else {
        this.$message.error(result.msg);
        this.showBtn = true;
      }
    });
  }

  /**
   * 提交License
   */
  submit() {
    const formData = new FormData();
    this.submitLoading = true;
    if (this.file) {
      formData.append('file', this.file);
      this.$loginService.uploadLicense(formData).subscribe((result: Result) => {
        if (result.code === 0) {
          this.validateLicense();
          this.submitLoading = false;
          this.modalCancel();
          this.showBtn = false;
        } else {
          this.$message.error(result.msg);
          this.submitLoading = false;
        }
      });
    } else {
      this.$message.warning('文件不能为空');
      this.submitLoading = false;
    }

  }

  changeAutoLoginType() {
    this.autoLoginType = !this.autoLoginType;
    if (this.autoLoginType) {
      this.loginTitle = '手机登入在这';
    } else {
      this.loginTitle = '用户登入在这';
    }
  }

  showModal() {
    this.isVisible = true;
  }

  /**
   * 隐藏弹出框
   */
  modalCancel() {
    this.isVisible = false;
    this.fileName = '';
    this.file = null;
    this.submitLoading = false;
  }

  /**
   * 文件上传
   */
  upload() {
    const fileNode = document.getElementById('file');
    fileNode.click();
  }

  /**
   * 文件变化
   */
  fileChange($event) {
    // 文件名效验
    const fileNode = document.getElementById('file');
    const fileName = fileNode['files'][0].name;
    const reg = /.xml$/;
    if (reg.test(fileName)) {
      this.file = fileNode['files'][0];
      if (this.file.name.length <= this.limit.nameLength) {
        if (this.file.size <= this.limit.size) {
          this.fileName = this.file.name;
          this.$message.info(this.language.agreement.uploadSuccess);
        } else {
          this.errorFile(this.limit.sizeI18n, $event);
        }
      } else {
        this.errorFile(this.limit.nameI18n, $event);
      }
    } else {
      this.$message.warning(this.language.agreement.currentlyOnlyXMLFormatFilesAreSupported + '!');
    }
  }

  /**
   * 文件错误提示
   */
  errorFile(msg, event) {
    this.fileName = '';
    this.file = null;
    event.target.value = '';
    this.$message.warning(msg + '!');
  }
}
