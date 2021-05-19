import { Component, OnInit } from '@angular/core';
import {BasicConfig} from '../../basic-config';
import {NzI18nService} from 'ng-zorro-antd';
import {ColumnConfigService} from '../column-config.service';
import {AgreementManageService} from '../../../core-module/api-service/system-setting/agreement-manage/agreement-manage.service';
import {Result} from '../../../shared-module/entity/result';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss']
})
export class AboutComponent extends BasicConfig implements OnInit {

  // 关于信息
  aboutInfo = {
    androidAddress: '',
    iosAddress: '',
    licenseAuthorize: '',
    license: ''
  };
  constructor(public $nzI18n: NzI18nService,
              private $agreementManageService: AgreementManageService,
              private $columnConfigService: ColumnConfigService,
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.formColumn = this.$columnConfigService.getAboutFormConfig({});
    this.searchFromData();
  }

  searchFromData() {
    this.$agreementManageService.about().subscribe((result: Result) => {
      if (result.code === 0) {
        this.aboutInfo = result.data;
        if (this.aboutInfo.licenseAuthorize === '1') {
          this.aboutInfo.license = '已授权';
        } else {
          this.aboutInfo.license = '未授权';
        }
        this.formStatus.resetData(result.data);
      }
    });
  }
}
