import {Component, OnInit} from '@angular/core';
import {SwiperConfigInterface, SwiperCoverflowEffectInterface, SwiperNavigationInterface} from 'ngx-swiper-wrapper';
// 3D 切换效果参数设置
const coverflowEffectConfig: SwiperCoverflowEffectInterface = {
  rotate: 0,
  stretch: 200,
  depth: 200,
  modifier: 1,
  slideShadows: false
};
// 前进后退按钮配置
const navigationConfig: SwiperNavigationInterface = {
  nextEl: '.button-next',
  prevEl: '.button-prev',
  hideOnClick: true
  // disabledClass?: string;
  // hiddenClass?: string;
};
@Component({
  selector: 'app-facility-img-view',
  templateUrl: './facility-img-view.component.html',
  styleUrls: ['./facility-img-view.component.scss']
})
export class FacilityImgViewComponent implements OnInit {
  constructor() {
  }

  // swiper config
  config: SwiperConfigInterface;

  ngOnInit() {
    this.config = {
      direction: 'horizontal',
      // 开启鼠标的抓手状态
      grabCursor: true,
      // 被选中的滑块居中，默认居左
      centeredSlides: true,
      loop: true,
      slidesPerView: 3,
      // loopedSlides: 8,
      autoplay: true,
      speed: 1000,
      // 切换效果为 coverflow
      // effect: 'coverflow',
      // coverflow 配置
      coverflowEffect: coverflowEffectConfig,
      // 前进后退按钮设置
      navigation: navigationConfig
    };
  }
}
