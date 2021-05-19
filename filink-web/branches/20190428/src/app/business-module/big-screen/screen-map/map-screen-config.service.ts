import {CommonUtil} from '../../../shared-module/util/common-util';
import {styleJson} from '../screen-map/map-style-config';
import {MapAbstract} from '../../../shared-module/component/map-selector/map-abstract';
import {MapDrawingService} from '../../../shared-module/component/map-selector/map-drawing.service';

declare const BMap: any;
declare let BMapLib: any;

export class MapScreenConfigService {
  mapInstance;
  markerClusterer;

  constructor(documentId, simpleCity?) {
    this.mapInstance = this.createMap(documentId, simpleCity);
  }

  createMap(documentId, simpleCity) {
    this.mapInstance = new BMap.Map(documentId, {enableMapClick: false});  // 创建Map实例
    // this.mapInstance.centerAndZoom(new BMap.Point(116.404269, 39.916042), 13);
    this.mapInstance.setMapStyle({styleJson: styleJson});
    this.mapInstance.disableDragging();
    this.mapInstance.disableDoubleClickZoom();
    this.mapInstance.dragabled = false;
    this.creatCity(simpleCity, this.mapInstance);
    const MAX = 10;
    const markers = [];
    let pt = null;
    let i = 0;

    for (; i < MAX; i++) {
      pt = new BMap.Point(113.379358, 31.717858);
      markers.push(new BMap.Marker(pt));
    }


    const markerClusterer = new BMapLib.MarkerClusterer(this.mapInstance, {
      markers: markers, styles: []
    });
    console.log(markers);


  }

  /**
   * 构建地图
   */
  creatCity(city: any, map: any) {
    let pointArray = [];
    const config = {strokeWeight: 2, strokeColor: '#98e2ff', fillColor: '#0e3c6d', fillOpacity: 0.01};
    for (let i = 0; i < city.length; i++) {
      const cityData = city[i].boundary.split('|');
      const centerData = {
        lng: city[i].center.split(',')[0],
        lat: city[i].center.split(',')[1]
      };
      city[i]['centerData'] = centerData;
      city[i]['cityData'] = cityData;
      for (let j = 0; j < city[i].cityData.length; j++) {
        const ply = new BMap.Polygon(city[i]['cityData'][j], config);
        map.addOverlay(ply);
        pointArray = pointArray.concat(ply.getPath());
      }
      this.creatCityName(city[i].areaName, city[i]['centerData']['lng'], city[i]['centerData']['lat'], map);
    }
    map.setViewport(pointArray);
  }

  /**
   * 创建城市名称
   */
  creatCityName(name, lng, lat, map) {
    const opts = {
      position: new BMap.Point(lng, lat),    // 指定文本标注所在的地理位置
      offset: new BMap.Size(-10, -10)    // 设置文本偏移量
    };
    const label = new BMap.Label(name, opts);  // 创建文本标注对象
    label.setStyle({
      color: '#fff',
      backgroundColor: 'rgba(0, 0, 0, 0)',
      border: 'none',
      padding: '0 5px',
      fontSize: '.5em',
      height: '10px',
      lineHeight: '20px',
    });
    label.setTitle(name);
    map.addOverlay(label);
  }


}
