import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FacilityService} from '../../../../core-module/api-service/facility/facility-manage';
import {Result} from '../../../../shared-module/entity/result';

@Component({
  selector: 'app-facility-view-detail',
  templateUrl: './facility-view-detail.component.html',
  styleUrls: ['./facility-view-detail.component.scss']
})
export class FacilityViewDetailComponent implements OnInit {
  public deviceId: string;
  public serialNum: string;
  public detailCode: any = [];
  public deviceType: string;

  constructor(private $active: ActivatedRoute,
              private $facilityService: FacilityService) {
  }

  ngOnInit() {
    this.$active.queryParams.subscribe(params => {

      this.deviceId = params.id;
      this.deviceType = params.deviceType;
      this.serialNum = params.serialNum;
      // 获取 详情页面模块id
      this.$facilityService.getDetailCode({deviceId: this.deviceId, deviceType: this.deviceType})
        .subscribe((result: Result) => {
          const data = result.data || [];
          this.detailCode = data.map(item => item.id);
        });

    });
  }

}
