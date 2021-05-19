import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-template-search',
  templateUrl: './template-search.component.html',
  styleUrls: ['./template-search.component.scss']
})
export class TemplateSearchComponent implements OnInit {

  @Output() chooseTemplate = new EventEmitter();
  constructor() { }

  ngOnInit() {
  }

  /**
   * 选择么个模板
   */
  selectTemplate(item) {
    this.chooseTemplate.emit(item);
  }
}
