import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {FormItem} from './form-config';
import {FormOperate} from './form-opearte.service';
import {NzI18nService} from 'ng-zorro-antd';

const FORM = 'form';

@Component({
  selector: 'xc-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements OnInit, OnChanges {


  @Input()
  column;
  formGroup = new FormGroup({});
  @Input()
  isDisabled: boolean;
  @Output()
  formInstance = new EventEmitter();
  formOperate: FormOperate;
  language: any;

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

  modelChange(controls, $event, col) {
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
    this.formOperate = new FormOperate(this.formGroup, this.column, this.language);
    this.column.forEach((item: FormItem) => {
      const value = item.initialValue || null;
      const formControl = new FormControl({value: value, disabled: this.isDisabled || item.disabled}, this.formOperate.addRule(item.rule),
        this.formOperate.addAsyncRule(item.asyncRules));
      this.formGroup.addControl(item.key, formControl);
    });
    this.formInstance.emit({instance: this.formOperate});
  }
}
