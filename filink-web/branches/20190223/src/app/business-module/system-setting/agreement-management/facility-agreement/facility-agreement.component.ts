import {Component, OnInit, ViewChild} from '@angular/core';
import {ColumnConfigService} from '../../column-config.service';
import {NzI18nService} from 'ng-zorro-antd';
import {AgreementManageService} from '../../../../core-module/api-service/system-setting/agreement-manage/agreement-manage.service';
import {Result} from '../../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {BasicConfig} from '../../../basic-config';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AsyncRuleUtil} from '../../../../shared-module/util/async-rule-util';
import {Download} from '../../../../shared-module/util/download';

@Component({
  selector: 'app-facility-agreement',
  templateUrl: './facility-agreement.component.html',
  styleUrls: ['./facility-agreement.component.scss']
})
export class FacilityAgreementComponent extends BasicConfig implements OnInit {
  title = '新增设施协议';
  // 新增弹出框显示隐藏
  isVisible = false;
  // 协议名称
  agreementName: '';
  // 文件名称
  fileName: '';
  // 上传的文件
  file: any;
  // 协议id
  protocolId = '';
  // xml文件校验
  limit = {
    nameLength: 32,
    size: 1048576,
    nameI18n: this.language.agreement.fileNameLengthLessThan32bits,
    sizeI18n: this.language.agreement.fileSizeLessThan1M
  };

  // 新增表单验证
  validateForm: FormGroup;
  @ViewChild('tplFooter') public tplFooter;
  @ViewChild('fileNameRef') public fileNameRef;

  constructor(private $columnConfigService: ColumnConfigService,
              private $agreementManageService: AgreementManageService,
              private $message: FiLinkModalService,
              private $download: Download,
              private fb: FormBuilder,
              public $nzI18n: NzI18nService) {
    super($nzI18n);
  }

  ngOnInit() {
    this.validateForm = this.fb.group({
      agreementName: [this.agreementName, [Validators.required, Validators.maxLength(32)], [AsyncRuleUtil.nameReg().asyncRule]],
    });
    this.language = this.$nzI18n.getLocale();
    this.title = this.language.agreement.addFacilityProtocol;
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSizeChanger: true,
      scroll: {x: '1000px', y: '325px'},
      columnConfig: this.$columnConfigService.getFacilityColumnConfig({
        fileName: this.fileNameRef
      }),
      bordered: false,
      topButtons: [
        {
          text: '+  ' + this.language.table.add,
          handle: () => {
            this.isVisible = true;
            this.agreementName = '';
            this.validateForm = this.fb.group({
              agreementName: [this.agreementName, [Validators.required, Validators.maxLength(32)], [AsyncRuleUtil.nameReg().asyncRule]],
            });
          }
        }, {
          text: this.language.table.delete,
          btnType: 'danger',
          needConfirm: true,
          canDisabled: true,
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
          handle: (data) => {
            if (data.length === 0) {
              this.$message.warning('请先勾选需删除数据！');
              return;
            }
            const ids = data.map(item => item.protocolId);
            this.deleteFacility(ids);
          }
        }
      ],
      operation: [
        {
          text: this.language.table.update,
          className: 'fiLink-edit',
          handle: (current) => {
            this.title = this.language.agreement.updateFacilityProtocol;
            this.isVisible = true;
            this.fileName = current.fileName;
            this.agreementName = current.protocolName;
            this.protocolId = current.protocolId;
            this.validateForm = this.fb.group({
              agreementName: [this.agreementName, [Validators.required, Validators.maxLength(32)], [AsyncRuleUtil.nameReg().asyncRule]],
            });
          }
        },
        {
          text: this.language.table.delete,
          className: 'icon-delete',
          needConfirm: true,
          handle: (current) => {
            this.deleteFacility([current.protocolId]);
          }
        }],
    };
    this.searchFacilityList();
    this.queryLimit();
  }

  /**
   * 查询设施协议文件规格
   */
  queryLimit() {
    this.$agreementManageService.queryFileLimit().subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this.limit = result.data;
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 查询设施协议列表
   */
  searchFacilityList() {
    this.tableConfig.isLoading = true;
    this.$agreementManageService.queryDeviceProtocolList().subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this._dataSet = result.data;
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 删除设施协议
   * param protocolIds
   */
  deleteFacility(protocolIds) {
    this.$agreementManageService.deleteDeviceProtocol(protocolIds).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        this.searchFacilityList();
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  /**
   * 监听页面切换
   * param event
   */
  pageChange(event) {
    this.pageBean.pageIndex = event.pageIndex;
    this.pageBean.pageSize = event.pageSize;
    this.searchFacilityList();
  }

  /**
   * 隐藏弹出框
   */
  modalCancel() {
    // 初始化信息
    this.isVisible = false;
    this.agreementName = '';
    this.fileName = '';
    this.file = null;
    this.protocolId = '';
    this.title = this.language.agreement.addFacilityProtocol;
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
          this.fileName = '';
          this.file = null;
          $event.target.value = '';
          this.$message.warning(this.limit.sizeI18n);
        }
      } else {
        this.fileName = '';
        this.file = null;
        $event.target.value = '';
        this.$message.warning(this.limit.nameI18n);
      }
    } else {
      this.$message.warning('目前只支持传.xml格式文件！');
    }
  }

  /**
   * 下载文件
   * param url
   */
  download(item) {
    this.$download.downloadFile(item.fileDownloadUrl, item.fileName);
  }

  /**
   * 点击确认按钮
   */
  submit() {
    if (!this.protocolId) {
      // 新增
      const formData = new FormData();
      formData.append('file', this.file);
      formData.append('protocolName', this.agreementName);
      this.submitLoading = true;
      this.$agreementManageService.addDeviceProtocol(formData).subscribe((result: Result) => {
        this.submitLoading = false;
        if (result.code === 0) {
          this.$message.success(result.msg);
          this.modalCancel();
          this.searchFacilityList();
        } else {
          this.$message.error(result.msg);
        }
      }, () => {
        this.submitLoading = false;
      });
    } else {
      // 修改
      let sendData = null;
      let funcName = '';
      if (this.file) {
        funcName = 'updateDeviceProtocol';
        sendData = new FormData();
        sendData.append('file', this.file);
        sendData.append('protocolId', this.protocolId);
        sendData.append('protocolName', this.agreementName);
      } else {
        funcName = 'updateProtocolName';
        sendData = {
          protocolId: this.protocolId,
          protocolName: this.agreementName
        };
      }
      this.submitLoading = true;
      this.$agreementManageService[funcName](sendData).subscribe((result: Result) => {
        this.submitLoading = false;
        if (result.code === 0) {
          this.$message.success(result.msg);
          this.modalCancel();
          this.searchFacilityList();
        } else {
          this.$message.error(result.msg);
        }
      }, () => {
        this.submitLoading = false;
      });
    }
  }
}
