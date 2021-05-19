import { Component, OnInit } from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {ActivatedRoute, UrlSegment} from '@angular/router';
import {ColumnConfigService} from '../../column-config.service';
import {BasicConfig} from '../../../basic-config';

@Component({
  selector: 'app-id-security-policy',
  templateUrl: './id-security-policy.component.html',
  styleUrls: ['./id-security-policy.component.scss']
})
export class IdSecurityPolicyComponent extends BasicConfig implements OnInit {

  pageTitle = '账号安全策略';

  accessPolicyType = '';
  constructor(public $nzI18n: NzI18nService,
              private $activatedRoute: ActivatedRoute,
              private $columnConfigService: ColumnConfigService,
              ) {
    super($nzI18n);
  }

  ngOnInit() {
    // 判断页面  用户日志和系统日志 没有操作类型
    this.$activatedRoute.url.subscribe((urlSegmentList: Array<UrlSegment>) => {
      if (urlSegmentList.find(urlSegment => urlSegment.path === 'account')) {
        this.accessPolicyType = 'account';
        this.pageTitle = '账户安全策略';
        this.formColumn = this.$columnConfigService.getIDAccessControlFormConfig({});
      } else {
        this.pageTitle = '密码安全策略';
        this.accessPolicyType = 'password';
        this.formColumn = this.$columnConfigService.getPasswordAccessControlFormConfig({});
      }
    });
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
