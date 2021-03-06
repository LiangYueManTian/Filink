import {MapAbstract} from './map-abstract';
import {MapConfig} from '../../../business-module/index/config';
import {DEFAUT_ZOOM, iconSize} from './map.config';
import {GMapDrawingService} from './g-map-drawing.service';
import {CommonUtil} from '../../util/common-util';

/**
 * Created by wh1709040 on 2019/2/14.
 */
declare let google: any;
declare let MarkerClusterer: any;

export class GMapSelectorService extends MapAbstract {
  mapInstance;
  mapDrawUtil;
  markersMap = new Map();

  constructor(documentId, simpleConfig?) {
    super();
    if (!simpleConfig) {
      this.createMap(documentId);
    } else {
      this.mapInstance = new google.maps.Map(document.getElementById(documentId), {
        zoom: MapConfig.defalutZoom,
        center: {lat: 30, lng: 120},
        mapTypeControl: false,
        draggable: false,
        scrollwheel: false,
        mapTypeControlOptions: {
          style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
          position: google.maps.ControlPosition.TOP_RIGHT
        },
        zoomControl: false,
        zoomControlOptions: {
          position: google.maps.ControlPosition.LEFT_CENTER
        },
        scaleControl: false,
        streetViewControl: false,
        streetViewControlOptions: {
          position: google.maps.ControlPosition.LEFT_TOP
        },
        fullscreenControl: false,
        clickableIcons: false
      });
    }
  }

  addMarker(point, id, fn?) {
  }

  createMarker(point, fn?) {
    console.log(point);
    const status = point.checked ? '0' : '1';
    const url = CommonUtil.getFacilityIconUrl(iconSize, point.deviceType, status);
    const icon = this.toggleIcon(url);
    const position = point.positionBase.split(',');
    const _lat = parseFloat(position[1]);
    const _lng = parseFloat(position[0]);
    super.updateCenterPoint(_lng, _lat);
    const marker = new google.maps.Marker({position: {lat: _lat, lng: _lng}});
    marker.setIcon(icon);
    marker.customData = {id: point.deviceId};
    if (fn) {
      if (fn.length > 0) {
        fn.forEach(item => {
          google.maps.event.addListener(marker, item.eventName, (event) => {
            item.eventHandler({target: marker}, event);
          });
        });
      }
    }
    this.markersMap.set(point.deviceId, {marker: marker, data: point});
    return marker;
  }

  clearOverlay(overlay) {
    overlay.setMap(null);
  }

  createMap(documentId) {
    this.mapInstance = new google.maps.Map(document.getElementById(documentId), {
      zoom: MapConfig.defalutZoom,
      center: {lat: 30, lng: 120},
      mapTypeControl: false,
      mapTypeControlOptions: {
        style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
        position: google.maps.ControlPosition.TOP_RIGHT
      },
      zoomControl: true,
      zoomControlOptions: {
        position: google.maps.ControlPosition.LEFT_CENTER
      },
      scaleControl: true,
      streetViewControl: false,
      streetViewControlOptions: {
        position: google.maps.ControlPosition.LEFT_TOP
      },
      fullscreenControl: false,
      clickableIcons: false
    });
    this.mapDrawUtil = new GMapDrawingService(this.mapInstance);
  }

  getMarkerById(id) {
    return this.markersMap.get(id).marker;
  }

  getMarkerDataById(id) {
    return this.markersMap.get(id).data;
  }

  isInsidePolygon(pt, poly) {
  }

  toggleIcon(url) {
    const icon = new google.maps.MarkerImage(url, new google.maps.Size(18, 30));
    return icon;
  }

  getMarkerMap(): Map<string, any> {
    return this.markersMap;
  }

  /**
   * ?????????
   * param markers
   */
  addMarkerClusterer(markers, fn?) {
    const imgPath = 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m';
    const markerClusterer = new MarkerClusterer(this.mapInstance, markers, {averageCenter: true, imagePath: imgPath});
    if (fn.length && fn.length > 0) {
      fn.forEach(item => {
        google.maps.event.addListener(markerClusterer, item.eventName.slice(2), (__event) => {
          // ???????????????????????????????????????????????????????????????????????? (mouseout??????)
          if ((this.mapInstance.getZoom() >= MapConfig.gMaxZoom) || item.eventName.slice(2) === 'mouseout') {
            item.eventHandler(event, __event.getMarkers());
          }
        });
      });
    }
  }

  setCenterPoint(lat?, lng?, zoom?) {
    this.mapInstance.setCenter(new google.maps.LatLng(lat || (this.maxLat + this.minLat) / 2, lng || (this.maxLng + this.minLng) / 2));
    this.mapInstance.setZoom(zoom || DEFAUT_ZOOM);
  }

  locateToUserCity() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(position => {
        this.setCenterPoint(position.coords.latitude, position.coords.longitude, DEFAUT_ZOOM);
      }, () => {
        console.log(this.mapInstance.getCenter());
      });
    } else {
      // Browser doesn't support Geolocation
      console.log(this.mapInstance.getCenter());
    }
  }

  mockData() {
  }

  getLocation(overlays, fn) {
    const lat = overlays.position.lat();
    const lng = overlays.position.lng();
    const latlng = {lat: lat, lng: lng};
    const geocoder = new google.maps.Geocoder;
    geocoder.geocode({'location': latlng}, (result) => {
      console.log(result);
      const _result = {
        address: '',
        point: {lat: lat, lng: lng},
        addressComponents: {
          province: '',
          city: '',
          district: ''
        }
      };
      if (result.length > 0) {
        _result.address = result[0].formatted_address;
        if (result.length >= 3) {
          _result.addressComponents.province = result[result.length - 1].address_components[0].long_name;
          _result.addressComponents.city = result[result.length - 2].address_components[0].long_name;
          _result.addressComponents.district = result[result.length - 3].address_components[0].long_name;
        }
      }
      fn(_result);
    });
  }

  addOverlay(marker) {
    const _marker = new google.maps.Marker({
      position: marker,
      map: this.mapInstance,
    });
    const url = `https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png`;
    _marker.setIcon(this.toggleIcon(url));
    return _marker;
  }

  createPoint(lng, lat) {
    return {lat: lat, lng: lng};
  }

  addMarkerMap(marker) {
    marker.setMap(this.mapInstance);
    // this.mapInstance.addOverlay(marker);
  }

  searchLocation(key, fn) {
    const geocoder = new google.maps.Geocoder;
    geocoder.geocode({'address': key}, fn);
  }

  addZoomEnd(fn) {
    this.mapInstance.addListener('zoom_changed', fn);
  }
}
