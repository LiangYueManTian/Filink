import {
  AfterViewInit,
  Component, ElementRef, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, TemplateRef, ViewChild,
} from '@angular/core';
import {FormLanguageInterface} from '../../../../assets/i18n/form/form.language.interface';
import {NzAutocompleteComponent, NzI18nService} from 'ng-zorro-antd';
import {fromEvent} from 'rxjs';
import {debounceTime, distinctUntilChanged, map} from 'rxjs/internal/operators';

@Component({
  selector: 'xc-search-input',
  templateUrl: './search-input.component.html',
  styleUrls: ['./search-input.component.scss']
})
export class SearchInputComponent implements OnInit, OnChanges, AfterViewInit {

  public language: FormLanguageInterface;
  @Input()
  searchValue;
  @Input()
  inputStyle;
  @Input()
  itemStyle;
  @Output()
  searchValueChange = new EventEmitter<any>();
  @Input()
  selectInfo = {
    data: [],
    label: 'name',
    value: 'id'
  };
  @Output()
  modelChange = new EventEmitter<any>();
  @Output()
  inputChange = new EventEmitter<any>();
  @ViewChild('searchInput') searchInput: ElementRef;
  @ViewChild('resultTemp') resultTemp: TemplateRef<any>;
  @ViewChild('auto') auto: NzAutocompleteComponent;
  currentIndex;

  constructor(private $i18n: NzI18nService) {
  }

  ngOnInit() {
    this.language = this.$i18n.getLocaleData('form');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.searchValue === null) {
      this.selectInfo.data = [];
    }
  }


  ngAfterViewInit(): void {
    const typeAhead = fromEvent(this.searchInput.nativeElement, 'input').pipe(
      map((e: KeyboardEvent) => e.target['value']),
      debounceTime(500),
      distinctUntilChanged()
    );

    typeAhead.subscribe(data => {
      this.inputChange.emit(data);
    });
  }

  optionChange(event, value) {
    this.modelChange.emit(value);
  }

  handleKeyDown(event) {
    if (event.code === 'Enter') {
      const index = this.auto.getOptionIndex(this.auto.activeItem);
      if (index !== undefined) {
        this.modelChange.emit(this.searchValue);
      }
    }

  }
}
