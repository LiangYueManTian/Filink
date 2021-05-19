import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'xc-table-search',
  templateUrl: './table-search.component.html',
  styleUrls: ['./table-search.component.css']
})
export class TableSearchComponent implements OnInit {

  @Input()
  tableConfig;

  constructor() {
  }

  ngOnInit() {
  }

}
