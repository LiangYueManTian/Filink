import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NgZorroAntdModule, NZ_DATE_LOCALE, NZ_I18N, NZ_MODAL_CONFIG,} from 'ng-zorro-antd';
import {FormsModule,} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HashLocationStrategy, LocationStrategy,} from '@angular/common';
import {CoreModule} from './core-module/core-module.module';
import {SharedModule} from './shared-module/shared-module.module';
import {CommonUtil} from './shared-module/util/common-util';
import {NgxEchartsModule} from 'ngx-echarts';
import {SWIPER_CONFIG, SwiperConfigInterface, SwiperModule} from 'ngx-swiper-wrapper';
import * as enDateLocale from 'date-fns/locale/en';
import * as cnDateLocale from 'date-fns/locale/zh_cn';
import {NotfoundComponent} from './business-module/notfound/notfound.component';

const DEFAULT_SWIPER_CONFIG: SwiperConfigInterface = {
  direction: 'horizontal',
  slidesPerView: 'auto'
};

@NgModule({
  declarations: [
    AppComponent,
    NotfoundComponent
  ],
  imports: [
    BrowserModule,
    NgZorroAntdModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    CoreModule,
    SharedModule,
    NgxEchartsModule,
    SwiperModule,
    AppRoutingModule
  ],
  providers: [
    {provide: NZ_I18N, useValue: CommonUtil.toggleNZi18n()},
    {provide: NZ_DATE_LOCALE, useValue: cnDateLocale},
    {provide: LocationStrategy, useClass: HashLocationStrategy},
    {
      provide: SWIPER_CONFIG,
      useValue: DEFAULT_SWIPER_CONFIG
    },
    {provide: NZ_MODAL_CONFIG, useValue: {nzMask: true, nzMaskClosable: false}}

  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
