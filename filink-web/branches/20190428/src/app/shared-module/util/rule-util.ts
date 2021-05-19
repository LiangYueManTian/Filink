import {NzI18nService} from 'ng-zorro-antd';
import {Injectable} from '@angular/core';
import {AbstractControl, FormControl} from '@angular/forms';
import {CommonLanguageInterface} from '../../../assets/i18n/common/common.language.interface';
import {FiLinkModalService} from '../service/filink-modal/filink-modal.service';
import {debounceTime, distinctUntilChanged, first, mergeMap} from 'rxjs/operators';
import {Observable, of} from 'rxjs';

@Injectable()
export class RuleUtil {
  language: CommonLanguageInterface;

  constructor(private $i18n: NzI18nService,
              private $message: FiLinkModalService) {
    this.language = this.$i18n.getLocaleData('common');
  }

  static getNameMinLengthRule() {
    return {minLength: 1};
  }

  static getNameMaxLengthRule() {
    return {maxLength: 32};
  }

  static getNamePatternRule(msg = null) {
    if (msg) {
      return {pattern: '^(?!_)[a-zA-Z0-9_\u4e00-\u9fa5]+$', msg};
    } else {
      return {pattern: '^(?!_)[a-zA-Z0-9_\u4e00-\u9fa5]+$'};
    }
  }


  /**
   * 获取名称检验规则
   * returns {{pattern: string; msg: any}}
   */
  getNameRule() {
    return {
      pattern: '^[\\s\\da-zA-Z\u4e00-\u9fa5`\\-=\\[\\]\\\\;\',./~!@#$%^&*\\(\\)_+{}|:"<>?·【】、；\'、‘’，。、！￥……（）——+｛｝：“”《》？]+$',
      msg: this.language.nameRuleMsg
    };
  }

  /**
   * 获取名称自定义校验规则
   * returns {{code: string; msg: any; validator: (control: AbstractControl) => {[p: string]: boolean}}}
   */
  getNameCustomRule() {
    return {
      code: 'notEmpty', msg: this.language.notEmpty, validator: (control: AbstractControl): { [key: string]: boolean } => {
        if (/^\s+$/.test(control.value)) {
          return {notEmpty: true};
        } else {
          return null;
        }
      }
    };
  }

  /**
   * 获取名称异步校验规则
   * param {() => Observable<Object>} httpMethod 请求函数
   * param {(res) => boolean} nameNotExist 验证名称是否存在 不存在返回true
   * returns {{asyncRule: (control: FormControl) => Observable<any>; asyncCode: string; msg: string}}
   */
  getNameAsyncRule(httpMethod: (value) => Observable<Object>, nameNotExist: (res) => boolean) {
    return {
      asyncRule: (control: FormControl) => {
        return control.valueChanges.pipe(
          distinctUntilChanged(),
          debounceTime(1000),
          mergeMap(() => httpMethod(control.value)),
          mergeMap(res => {
            if (nameNotExist(res)) {
              return of(null);
            } else {
              return of({error: true, duplicated: true});
            }
          }),
          first()
        );
      },
      asyncCode: 'duplicated', msg: this.language.nameExists
    };
  }

  /**
   * 备注类最大长度
   * returns {{maxLength: number}}
   */
  getRemarkMaxLengthRule() {
    return {maxLength: 255};
  }


  getEndTimeRule() {
    return {
      code: 'isTime', msg: this.language.expectCompleteTimeMoreThanNowTime
      , validator: (control: AbstractControl): { [key: string]: boolean } => {
        if (control.value && new Date(control.value).getTime() < new Date().getTime()) {
          return {isTime: true};
        } else {
          return null;
        }
      }
    };
  }


  /**
   * 获取数字检验规则
   */
  getNumberRule(param) {
    if (param === '1') {
      return {
        code: 'num',
        msg: '最少包含1个数字！',
        validator: (control: AbstractControl): { [key: string]: boolean } => {
          if (!/^.*(?=.*\d).*$/.test(control.value)) {
            return {num: true};
          } else {
            return null;
          }
        }
      };
    } else {
      return {
        code: 'num',
        msg: '',
        validator: (control: AbstractControl): { [key: string]: boolean } => {
          return null;
        }
      };
    }
  }

  /**
   * 获取小写字母检验规则
   */
  getLowerCaseRule(param) {
    if (param === '1') {
      return {
        code: 'lowercase',
        msg: '最少包含1个小写字母！',
        validator: (control: AbstractControl): { [key: string]: boolean } => {
          if (!/^.*(?=.*[a-z]).*$/.test(control.value)) {
            return {lowercase: true};
          } else {
            return null;
          }
        }
      };
    } else {
      return {
        code: 'lowercase',
        msg: '',
        validator: (control: AbstractControl): { [key: string]: boolean } => {
          return null;
        }
      };
    }
  }


  /**
   * 获取大写字母检验规则
   */
  getUpperCaseRule(param) {
    if (param === '1') {
      return {
        code: 'uppercase',
        msg: '最少包含1个大写字母！',
        validator: (control: AbstractControl): { [key: string]: boolean } => {
          if (!/^.*(?=.*[A-Z]).*$/.test(control.value)) {
            return {uppercase: true};
          } else {
            return null;
          }
        }
      };
    } else {
      return {
        code: 'uppercase',
        msg: '',
        validator: (control: AbstractControl): { [key: string]: boolean } => {
          return null;
        }
      };
    }
  }

  /**
   * 获取特殊字符检验规则
   */
  getSpecialCharacterRule(param) {
    if (param === '1') {
      return {
        code: 'Character',
        msg: '最少包含1个特殊字符！',
        validator: (control: AbstractControl): { [key: string]: boolean } => {
          if (!/^.*(?=.*[!@#$%.\-_&*<>?\(\)\[\]{}\\|;:]).*$/.test(control.value)) {
            return {Character: true};
          } else {
            return null;
          }
        }
      };
    } else {
      return {
        code: 'Character',
        msg: '',
        validator: (control: AbstractControl): { [key: string]: boolean } => {
          return null;
        }
      };
    }
  }

   /**
   * 获取邮箱检验规则
   */
  getMailRule() {
    return {
      pattern: '^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\\.[a-zA-Z0-9_-]+)+$',
      msg: '邮箱格式不正确！'
    };
  }


}
