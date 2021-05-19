import {Component, Input, OnInit} from '@angular/core';
import {Result} from '../../../../../shared-module/entity/result';
import {FacilityService} from '../../../../../core-module/api-service/facility/facility-manage';
import {FacilityLanguageInterface} from '../../../../../../assets/i18n/facility/facility.language.interface';
import {NzI18nService} from 'ng-zorro-antd';
import {ImageViewService} from '../../../../../shared-module/service/picture-view/image-view.service';

@Component({
  selector: 'app-facility-img-view',
  templateUrl: './facility-img-view.component.html',
  styleUrls: ['./facility-img-view.component.scss']
})
export class FacilityImgViewComponent implements OnInit {

  @Input()
  deviceId;
  public devicePicInfo: any[];
  public language: FacilityLanguageInterface;

  constructor(private $facilityService: FacilityService,
              private $imageViewService: ImageViewService,
              private $i18n: NzI18nService) {
  }


  ngOnInit() {
    this.language = this.$i18n.getLocaleData('facility');
    this.getDevicePic();
  }

  private getDevicePic() {
    const body = {
      resource: '3',
      deviceId: this.deviceId,
      picNum: '3'
    };
    this.$facilityService.picRelationInfo(body).subscribe((result: Result) => {
      if (result.code === 0 && result.data && result.data.length > 0) {
        this.devicePicInfo = result.data;
        this.devicePicInfo.forEach((item: any) => {
          item.picSize = item.picSize ? (item.picSize / 1000).toFixed(2) + 'kb' : '';
        });
      }
    });
  }

  clickImage(currentImage) {
    this.$imageViewService.showPictureView(this.devicePicInfo, currentImage);
  }
}
