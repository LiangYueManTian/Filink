import {en_US, zh_CN} from 'ng-zorro-antd';
import _zh_CN from '../../../assets/i18n/zh_CN';
import _en_US from '../../../assets/i18n/en_US';
import {facilityStatusName, facilityTypeName} from './facility';

/**
 * Created by xiaoconghu on 2018/11/20.
 */
export class CommonUtil {
  /**
   * 获取uuid
   * param {number} len
   * returns {string}
   */
  public static getUUid(len: number = 36) {
    const uuid = [];
    const str = '0123456789abcdef';
    for (let i = 0; i < len; i++) {
      uuid[i] = str.substr(Math.floor(Math.random() * 0x10), 1);
    }
    if (len === 36) {
      uuid[14] = '4';
      uuid[19] = str.substr((uuid[19] & 0x3 | 0x8), 1);
      uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
    }
    return uuid.join('').replace(/-/g, '');
  }

  /**
   * 切换语言包
   * param localId
   * returns {any}
   */
  public static toggleNZi18n(localId?) {
    localId = localId || window.localStorage.getItem('localId') || 'zh_CN';
    let language;
    switch (localId) {
      case 'zh_CN':
        language = Object.assign(zh_CN, _zh_CN);
        break;
      case 'en_US':
        language = Object.assign(en_US, _en_US);
        break;
      default:
        language = Object.assign(zh_CN, _zh_CN);
        break;
    }
    return language;
  }

  /**
   * 时间格式化
   * param fmt
   * param date
   * returns {any}
   */
  public static dateFmt(fmt, date) {
    const o = {
      'M+': date.getMonth() + 1,                 // 月份
      'd+': date.getDate(),                    // 日
      'h+': date.getHours(),                   // 小时
      'm+': date.getMinutes(),                 // 分
      's+': date.getSeconds(),                 // 秒
      'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
      'S': date.getMilliseconds()             // 毫秒
    };
    if (/(y+)/.test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (const k in o) {
      if (new RegExp('(' + k + ')').test(fmt)) {

        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)));
      }
    }
    return fmt;

  }

  /**
   * 深拷贝
   * param obj   拷贝对象
   * returns {any[] | {}}   返回拷贝对象
   */
  public static deepClone(obj): any {
    const objClone = Array.isArray(obj) ? [] : {};
    if (obj && typeof obj === 'object') {
      for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
          // 判断ojb子元素是否为对象，如果是，递归复制
          if (obj[key] && typeof obj[key] === 'object') {
            objClone[key] = this.deepClone(obj[key]);
          } else {
            // 如果不是，简单复制
            objClone[key] = obj[key];
          }
        }
      }
    }
    return objClone;
  }

  /**
   * 获取设施图标地址
   * param {string} size   设施图标大小  如：16-20
   * param {string} type   设施类型  如：16-20
   * param {string} status 设施状态  如：1   不传为选中状态的图标
   * returns {string}
   */
  public static getFacilityIconUrl(size: string, type: string, status: string = '0') {
    const iconName = `icon_${facilityTypeName[type]}_${facilityStatusName[status]}.png`;
    return `assets/facility-icon/${size}/${iconName}`;
  }

  /**
   * 获取设施图标样式
   * param {string} type
   * returns {string}
   */
  public static getFacilityIconClassName(type: string) {
    // const iconClass = `icon-${facilityTypeName[type]}`;
    const facilityType = facilityTypeName[type] || 'all-facility';
    const iconClass = `iconfont facility-icon fiLink-${facilityType} ${facilityType}-color`;
    return iconClass;
  }

  /**
   * 返回几天前日期
   * param day
   */
  public static funDate (day: number) {
    const date1 = new Date();
    const time1 = date1.getFullYear() + '/' + (date1.getMonth() + 1) + '/' + date1.getDate();
    const date2 = new Date(time1);
    date2.setDate(date1.getDate() + day);
    return date2.getFullYear() + '/' + (date2.getMonth() + 1) + '/' + date2.getDate();
  }

/**
 *
 * param time  传递过来的时间
 * param language 对象 里面有 月 天 时
 */
  // 转化告警持续时间 (当前告警 历史告警使用)
  public static setAlarmContinousTime(time: number, language ) {
    let name: string;
    if ( time >= 8760 ) {
      // 年
      name = Math.floor(time / 8760) + language.year;
      const month = Math.ceil(time % 8760 / 720) ? Math.ceil(time % 8760 / 720) + language.month : '';
      name += month;
    } else if ( time >= 720 ) {
      // 月
      name =  Math.floor(time / 720) + language.month;
      const day = Math.ceil(time % 720) ? Math.ceil(time % 720) + language.day : '';
      name += day;
    } else if ( 24 <= time ) {
      // 天
      name =  Math.floor(time / 24) + language.day;
      const hour = Math.ceil(time % 24) ? Math.ceil(time % 24) + language.hour : '';
      name += hour;
    } else {
      // 时
      name = Math.ceil(time) + language.hour;
    }
    return name;
  }
}
