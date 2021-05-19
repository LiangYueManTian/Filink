import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormItem} from '../../../../shared-module/component/form/form-config';
import {FormOperate} from '../../../../shared-module/component/form/form-opearte.service';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {NzI18nService, NzModalService, NzTreeNode} from 'ng-zorro-antd';
import {getDeviceType} from '../../../facility/facility.config';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {Result} from '../../../../shared-module/entity/result';

@Component({
  selector: 'app-sales-work-order-detail',
  templateUrl: './sales-work-order-detail.component.html',
  styleUrls: ['./sales-work-order-detail.component.scss']
})
export class SalesWorkOrderDetailComponent implements OnInit {
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  public language: FacilityLanguageInterface;
  pageTitle;
  pageType = 'add';
  constructor(
    private $activatedRoute: ActivatedRoute,
    private $nzI18n: NzI18nService,
    private $modelService: NzModalService,
    private $router: Router
  ) { }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.initColumn();
    console.log(this.$activatedRoute.snapshot);
    this.pageType = this.$activatedRoute.snapshot.routeConfig.path;
    this.pageTitle = this.getPageTitle(this.pageType);
    if (this.pageType !== 'insert') {

    } else {

    }
  }
  private initColumn() {
    this.formColumn = [
      {
        label: this.language.deviceName,
        key: 'deviceName',
        type: 'input',
        require: true,
        rule: [{required: true}, {minLength: 2}, {maxLength: 12}],
        asyncRules: [
          {
            asyncRule: (control: FormControl) => {
              return Observable.create(observer => {
                observer.next(null);
                observer.complete();
                // observer.next({error: true, duplicated: true});
                // observer.complete();
              });
            },
            asyncCode: 'duplicated', msg: '此名称已经存在！'
          }
        ],
      },
      {
        label: this.language.deviceType, key: 'deviceType', type: 'select',
        selectInfo: {
          data: getDeviceType(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        modelChange: (controls, $event, key) => {
          console.log(controls);
          console.log($event);
          console.log(key);
        },
        openChange: (a, b, c) => {

        },
        require: true,
        rule: [], asyncRules: []
      },
      {
        label: this.language.address,
        key: 'address',
        type: 'input',
        require: false,
        rule: [],
      },
      {
        label: this.language.exclusive,
        key: 'exclusive',
        type: 'input',
        require: false,
        rule: [],
      },
      {label: this.language.remarks, key: 'remarks', type: 'input', rule: []},
    ];
  }

  private getPageTitle(type): string {
    let title;
    switch (type) {
      case'add':
        title = '新增销障工单';
        break;
      case 'update':
        title = '修改销障工单';
        break;
      case 'view':
        title = '查看销障工单';
        break;
    }
    return title;
  }

  goBack() {
    this.$router.navigate(['/business/work-order/sales/unfinished']).then();
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }
}
