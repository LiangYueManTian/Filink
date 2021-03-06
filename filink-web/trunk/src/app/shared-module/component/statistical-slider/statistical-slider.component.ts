import {AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import * as Slider from 'swiper/dist/js/swiper.js';

/**
 * 列表上方统计滑块组件
 */
@Component({
  selector: 'xc-statistical-slider',
  templateUrl: './statistical-slider.component.html',
  styleUrls: ['./statistical-slider.component.scss']
})
export class StatisticalSliderComponent implements OnInit, OnChanges, AfterViewInit {
  // 是否隐藏
  isHidden: boolean = true;
  // 滑块配置
  @Input()
  sliderConfig: any[] = [];
  // 选择滑块变化
  @Output()
  selectChange = new EventEmitter();
  // 禁用
  disabled = true;
  // 滑块实例
  mySlider;
  // 页面加载
  pageLoading = false;
  // 滑块显示隐藏
  slideShow = true;
  // 滑块隐藏显示变化事件
  @Output()
  slideShowChange = new EventEmitter();
  constructor() {
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.pageLoading = true;
    if (this.mySlider) {
      this.mySlider.destroy(true, true);
      this.mySlider = null;
    }
    setTimeout(() => {
      this.mySlider = new Slider('.swiper-container', {
        direction: 'horizontal', // 垂直切换选项
        loop: false, // 循环模式选项
        slidesPerView: 5,
        // 如果需要前进后退按钮
        navigation: {
          nextEl: '.swiper-button-next',
          prevEl: '.swiper-button-prev',
          disabledClass: 'my-button-disabled',
          hideOnClick: false,
        },
        grabCursor: true,
        // 被选中的滑块居中，默认居左
        centeredSlides: false,
        autoplay: false,
        speed: 1000,
      });
      this.pageLoading = false;
    }, 1000);
  }

  ngAfterViewInit(): void {
  }

  mouseover() {
    this.isHidden = false;
  }

  mouseout() {
    this.isHidden = true;
  }

  clickSlide(slide) {
    this.selectChange.emit(slide);
  }

  toggleSlide() {
    this.slideShow = !this.slideShow;
    this.slideShowChange.emit(this.slideShow);
  }
}
