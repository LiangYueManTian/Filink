import {Component, OnInit, ElementRef, ViewChild, Input, Output, AfterViewInit, EventEmitter} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {Result} from '../../../shared-module/entity/result';
import {EventManager} from '@angular/platform-browser';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {fromEvent} from 'rxjs';
import {MapSelectorService} from '../../../shared-module/component/map-selector/map-selector.service';
import {GMapSelectorService} from '../../../shared-module/component/map-selector/g-map-selector.service';
import {styleJson} from '../screen-map/map-style-config';
import {MapScreenService} from '../../../core-module/api-service/screen-map/map-screen';
import {MapScreenConfigService} from '../screen-map/map-screen-config.service';
import {Flip} from 'number-flip';


declare const $: any;
declare let BMap: any;   // 一定要声明BMap，要不然报错找不到BMap
@Component({
  selector: 'app-show-screen',
  templateUrl: './show-screen.component.html',
  styleUrls: ['./show-screen.component.scss']
})
export class ShowScreenComponent implements OnInit, AfterViewInit {
  @Input() isVisible = false;
  @Output() close = new EventEmitter();
  viewScreen = true;
  time = '';
  interval: any;
  nowAlarm = {};
  numAlarm = {};
  typeAlarm = {};
  incrementAlarm = {};
  private mapType: string;
  mapConfigService: MapScreenConfigService;
  areaName = '湖北省';
  showWeek = true; // 显示一周数据
  // 获取告警增量实例
  alarmIncremental = '';
  demoDataweek = [
    {'map': 3237},
    {'lines': 2164},
    {'bar': 7561},
    {'line': 7778},
    {'pie': 7355},
    {'scatter': 2405},
    {'candlestick': 1842},
    {'radar': 2090},
    {'heatmap': 1762},
    {'treemap': 1593}
  ];
  demoData1week = {};
  demoDatamouth = [
    {'map': 377},
    {'lines': 164},
    {'bar': 561},
    {'line': 478},
    {'pie': 355},
    {'scatter': 405},
    {'candlestick': 42},
    {'radar': 90},
    {'heatmap': 762},
    {'treemap': 593},
    {'kkk': 123}
  ];
  demoData1mouth = {};


  constructor(private $I18n: NzI18nService,
              private $message: FiLinkModalService,
              private el: ElementRef,
              private eventManager: EventManager,
              private $MapScreenService: MapScreenService
  ) {
  }

  ngOnInit() {

    this.mapType = localStorage.getItem('mapType');
    this.updateTimeNow();
    this.eventManager.addGlobalEventListener('window', 'keyup.esc', () => {
      this.close.emit();
      clearInterval(this.interval);
    });
    this.arrTransformObj(this.demoDataweek, this.demoData1week);
    this.initChart(this.demoData1week);
  }

  ngAfterViewInit() {
    this.getScreenMap();
    this.movedome();
    fromEvent(window, 'resize')
      .subscribe((event) => {
        // 这里处理页面变化时的操作
        const isFull = !!(document['webkitIsFullScreen'] || document['mozFullScreen'] ||
          document['mozFullScreen'] || document['fullscreenElement']
        );
        if (!isFull) {
          this.close.emit();
        }
      });

    const count = 772274;
    console.log('显示大屏');
    console.log(document.getElementById('facilities-num-ta'));
    const slot = new Flip({
      node: document.getElementById('facilities-num-ta'),
      from: count,
      delay: 1,

    });
    setInterval(() => {
      slot.flipTo({
        to: parseInt((Math.random() * 100000).toString(), 0),
        direct: true
      });
    }, 4500);

    const slotNum = new Flip({
      node: document.getElementById('facilities-num-tas'),
      from: count,
      delay: 1,
    });
    setInterval(() => {
      slotNum.flipTo({
        to: parseInt((Math.random() * 100000).toString(), 0),
        direct: false
      });
    }, 5500);


  }

  /**
   * 将数组转化为对象
   */
  arrTransformObj(arr: any, obj: object) {
    return arr.map(item => {
      Object.assign(obj, item);
    });
  }

  onChartInit(ec) {
    this.alarmIncremental = ec;
  }

  /**
   * 显示大屏
   */
  shoeViewScreen() {
    this.viewScreen = true;
    const fullscreenDiv = window.document.body;
    let fullscreenFunc = fullscreenDiv.requestFullscreen;
    if (!fullscreenFunc) {
      // 判断浏览器内核
      ['mozRequestFullScreen', 'msRequestFullscreen', 'webkitRequestFullScreen'].forEach(function (req) {
        fullscreenFunc = fullscreenFunc || fullscreenDiv[req];
      });
    }
    // 把全屏展示的内容 通过call 改变this指向
    fullscreenFunc.call(fullscreenDiv);
    this.movedome();
    this.updateTimeNow();

  }

  updateTimeNow() {
    this.interval = setInterval(() => {
      const time = new Date();
      const m = time.getMonth() + 1;
      const t = time.getFullYear() + '-' + m + '-'
        + time.getDate() + ' ' + time.getHours() + ':'
        + time.getMinutes() + ':' + time.getSeconds();
      this.time = t;
    }, 1000);
  }

  /**
   * 获取地图配置
   */
  getScreenMap() {
    this.$MapScreenService.queryGetArea(this.areaName).subscribe((result: Result) => {
      if (result.code === 0) {
        console.log(result, '123');
        this.initMap(result.data);
        // this.mapConfigService.addMarkerClusterer();
      }
    });
  }

  /**
   * 告警滚动
   */
  movedome() {
    $('.screen-charts').slide({
      mainCell: 'ul',
      effect: 'top',
      autoPlay: true,
      vis: 6,
      easing: 'easeOutCirc',
      delayTime: 700
    });

  }

  /**
   * 显示周数据
   */
  showWeekData() {
    this.showWeek = true;
    this.initChart(this.demoData1week);
  }

  /**
   * 显示月数据
   */
  showmouthData() {
    this.showWeek = false;
    this.arrTransformObj(this.demoDatamouth, this.demoData1mouth);
    // this.demoDatamouth.map(item => {
    //   Object.assign(this.demoData1mouth, item);
    // });
    this.initChart(this.demoData1mouth);
  }

  /**
   * 初始化echart
   */
  private initChart(data) {


    this.nowAlarm = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        x: 'left',
        data: ['告警1', '告警2', '告警3'],
        top: '20%',
        textStyle: {
          color: '#00fcf9'
        }
      },
      color: ['#fbd517', '#009edf', '#ff6608'],
      series: [
        {
          name: '访问来源',
          type: 'pie',
          radius: [0, '87%'],
          label: {
            normal: {
              position: 'inside',
              formatter: '{d}%  ',
              fontSize: '11',
              color: 'black',
            },
            align: 'center'
          },
          labelLine: {
            normal: {
              show: true
            }
          },
          data: [
            {value: 535, name: '告警1', selected: true},
            {value: 679, name: '告警2', selected: true},
            {value: 948, name: '告警3'}
          ]
        }
      ]
    };
    this.numAlarm = {
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(255,0,255,0)',
        position: 'top',
        formatter: function (params) {
          return `<div style="background: url(
'../../../../assets/img/screen/tips.png')
 no-repeat; background-size: 100% 100%; width: 120px; height: 50px;text-align: center;line-height: 40px;
  padding: 6px; font-size: 14px; color: #aae6f7">${
            params.name}:${params.value}</div>`;
        }
      },
      color: ['#009edf'],
      grid: {
        left: '2%',
        right: '3%',
        bottom: '11%',
        top: '9%',
        containLabel: true
      },
      xAxis:
        {
          type: 'category',
          data: ['设施一', '设施二', '设施三', '设施四',
            '设施五', '设施六', '设施七', '设施八', '设施九', '设施十'],
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#034e8a'],
              width: 1,
              type: 'solid',
            },
          },
          axisPointer: {
            type: 'shadow'
          },
          axisLabel: {
            interval: 0,
            formatter: function (params) {
              let newParamsName = ''; // 最终拼接成的字符串
              const paramsNameNumber = params.length; // 实际标签的个数
              const provideNumber = 2; // 每行能显示的字的个数
              const rowNumber = Math.ceil(paramsNameNumber / provideNumber); // 换行的话，需要显示几行，向上取整
              /**
               * 判断标签的个数是否大于规定的个数， 如果大于，则进行换行处理 如果不大于，即等于或小于，就返回原标签
               */
              // 条件等同于rowNumber>1
              if (paramsNameNumber > provideNumber) {
                /** 循环每一行,p表示行 */
                for (let p = 0; p < rowNumber; p++) {
                  let tempStr = ''; // 表示每一次截取的字符串
                  const start = p * provideNumber; // 开始截取的位置
                  const end = start + provideNumber; // 结束截取的位置
                  // 此处特殊处理最后一行的索引值
                  if (p === rowNumber - 1) {
                    // 最后一次不换行
                    tempStr = params.substring(start, paramsNameNumber);
                  } else {
                    // 每一次拼接字符串并换行
                    tempStr = params.substring(start, end) + '\n';
                  }
                  newParamsName += tempStr; // 最终拼成的字符串
                }

              } else {
                // 将旧标签的值赋给新标签
                newParamsName = params;
              }
              // 将最终的字符串返回
              return newParamsName;
            },
            textStyle: {
              color: '#00fcf9',
              fontSize: 10,
            }
          }
        },
      yAxis: {
        type: 'value',
        axisLine: {
          show: false,

        },
        splitLine: {
          show: true,
          lineStyle: {
            show: false,
            color: ['#034e8a'],
            width: 1,
            type: 'solid'
          },
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          textStyle: {
            color: '#00fcf9',
            fontSize: 10
          }
        }
      },
      series: [
        {
          name: '直接访问',
          type: 'bar',
          itemStyle: {
            normal: {
              color: function (params) {
                // 首先定义一个数组
                const colorList = ['#009edf', '#e61017', '#ff6600', '#f1c620', '#58c1f0', '#ffea01', '#0068b7', '#0bda0b'];
                return colorList[params.dataIndex];
              },
              label: {
                show: false
              }
            }
          },
          barWidth: '30%',
          label: {
            normal: {
              show: true,
              position: 'top',
              color: '#00fcf9'
            }
          },
          data: [10, 52, 200, 334, 390, 330, 220, 110, 17, 112]
        },
        {

          type: 'line',
          lineStyle: {
            color: '#224d95',
            width: '1'
          },
          barWidth: '60%',
          data: [10, 52, 200, 334, 390, 330, 220, 110, 17, 112]
        }

      ]
    };
    this.typeAlarm = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        x: 'left',
        data: ['告警1', '告警2', '告警3'],
        top: '20%',
        left: '5%',
        textStyle: {
          color: '#00fcf9'
        }
      },
      color: ['#fbd517', '#009edf', '#ff6608'],
      series: [
        {
          name: '访问来源',
          type: 'pie',
          radius: ['22%', '78%'],
          selectedOffset: 23,
          label: {
            normal: {
              position: 'inside',
              formatter: '{d}%  ',
              fontSize: '11',
              color: 'black',
              padding: [3, 4, 5, 10]
            },
            align: 'center'
          },
          avoidLabelOverlap: false,
          data: [
            {value: 535, name: '告警1'},
            {value: 679, name: '告警2'},
            {value: 948, name: '告警3'}
          ]
        }
      ]
    };
    this.incrementAlarm = {
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(255,0,255,0)',
        position: 'top',
        formatter: function (params) {
          return `<div style="background: url(
'../../../../assets/img/screen/tips.png')
 no-repeat; background-size: 100% 100%; width: 120px; height: 50px;text-align: center;line-height: 40px;
  padding: 6px; font-size: 14px; color: #aae6f7">${
            params.seriesName}:${params.value}</div>`;
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '11%',
        top: '6%',
        containLabel: true
      },
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          data: Object.keys(data),
          splitLine: {
            show: false,
          },
          axisLabel: {
            textStyle: {
              color: '#00fcf9'
            }
          }
        }
      ],
      yAxis: [
        {
          type: 'value',
          splitLine: {
            show: false,
          },
          axisLabel: {
            textStyle: {
              color: '#00fcf9'
            }
          }
        }
      ],
      series: [
        {
          name: '本周最高值',
          type: 'line',
          smooth: true,
          stack: '总量',
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0, color: '#0471a3' // 0% 处的颜色
              }, {
                offset: 1, color: '#151729' // 100% 处的颜色
              }],
            }

          },
          itemStyle: {
            normal: {
              color: '#5cd4e8'
            }
          },
          data: Object.keys(data).map(key => {
            return data[key];
          })
        }
      ]
    };
  }

  private initMap(city) {
// 实例化地图服务类
    this.mapConfigService = new MapScreenConfigService('map', city);

  }

}
