import {
  AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, QueryList, SimpleChanges, ViewChild,
  ViewChildren
} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {FormItem} from './form-config';
import {FormOperate} from './form-opearte.service';
import {NzI18nService, NzSelectComponent} from 'ng-zorro-antd';

const FORM = 'form';

@Component({
  selector: 'xc-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements OnInit, OnChanges, AfterViewInit {


  @Input()
  column;
  formGroup = new FormGroup({});
  @Input()
  isDisabled: boolean;
  @Output()
  formInstance = new EventEmitter();
  formOperate: FormOperate;
  language: any;
  @ViewChildren(NzSelectComponent) nzSelectComps: QueryList<NzSelectComponent>;
  @ViewChild('nz-select') nzSelectComp: NzSelectComponent;

  constructor(private $i18n: NzI18nService) {
    this.language = this.$i18n.getLocaleData(FORM);

  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.column) {
      this.initForm();
    }
  }

  ngAfterViewInit(): void {
    // 由于佐罗组件select的bug这里拿到组件进行修改其内部逻辑
    this.nzSelectComps.forEach((nzSelectComp: NzSelectComponent) => {
      nzSelectComp.onKeyDown = function (event) {
        if (nzSelectComp.nzDisabled) {
        } else {
          nzSelectComp.nzSelectService['onKeyDown'](event);
        }
      };
    });
  }

  modelChange(controls, $event, col) {
    if (col.inputType === 'password') {
      const node = document.getElementById(col.key);
      node.setAttribute('type', 'password');
    }
    if (col.modelChange) {
      col.modelChange(controls, $event, col.key, this.formOperate);
    }

  }

  openChange(controls, $event, col) {
    if (col.openChange) {
      col.openChange(controls, $event, col.key, this.column);
    }

  }

  private initForm() {
    this.formGroup = new FormGroup({});
    this.formOperate = new FormOperate(this.formGroup, this.column, this.language);
    this.column.forEach((item: FormItem) => {
      const value = item.initialValue || null;
      const formControl = new FormControl({value: value, disabled: this.isDisabled || item.disabled},
        this.formOperate.addRule(item.rule, item.customRules),
        this.formOperate.addAsyncRule(item.asyncRules));
      this.formGroup.addControl(item.key, formControl);
    });
    this.formInstance.emit({instance: this.formOperate});
  }
}
