import {
  Pipe,
  PipeTransform
} from '@angular/core';
import {CommonUtil} from '../util/common-util';

@Pipe({name: 'dynamic'})
export class DynamicPipe implements PipeTransform {

  transform(value: string, modifier: string, param: any) {
    if (!modifier) {
      return value;
    }
    return this[modifier](value, param);
  }


  date(value: any, param: any): any {
    const fmt = param || 'yyyy-MM-dd hh:mm:ss';
    if (value) {
      return CommonUtil.dateFmt(fmt, new Date(parseInt(value, 0)));
    } else {
      return null;
    }
  }

  remark(value: any, param: any): any {
    if (value && value.includes('\n')) {
      return value.replace(/\n/g, '');
    } else {
      return value;
    }
  }
}
