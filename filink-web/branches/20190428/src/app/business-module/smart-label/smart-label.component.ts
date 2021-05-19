import {Component, OnInit} from '@angular/core';
import {SmartTools} from './templet-picture/smart-tools';

@Component({
  selector: 'app-smart-label',
  templateUrl: './smart-label.component.html',
  styleUrls: ['./smart-label.component.scss']
})
export class SmartLabelComponent implements OnInit {
  编号规则;
  Q = window['Q'];
  graph;
  graphBak;
  _row = 2;
  _col = 2;
  _position = 'rightTop';
  _direction = 'row';

  ngOnInit(): void {
    // this.graph = new this.Q.Graph('smart-canvas');
    // this.graph.isSelectable = (item) => {
    //   return item.get('type') === 'cabinet';
    // };
    // console.log(this.graph);
    // const startX = -200, startY = -200;
    // SmartTools.drawSmartLabel(this.Q, this.graph, startX, startY, 200, this._row, this._col, this._position, this._direction);
    // SmartTools.drawSmartLabel(this.Q, this.graph, -190, -190, 45, 4, 4, this._position, this._direction);
    // SmartTools.drawSmartLabel(this.Q, this.graph, -180, -180, 5, 5, 5, this._position, this._direction, false);
    //
    // this.graphBak = new this.Q.Graph('smart-canvas-bak');
    // SmartTools.drawSmartLabel(this.Q, this.graphBak, startX, startY, 200, this._row, this._col, this._position, this._direction);
    // SmartTools.drawSmartLabel(this.Q, this.graphBak, -190, -190, 45, 4, 4, this._position, this._direction);
    // SmartTools.drawSmartLabel(this.Q, this.graphBak, -180, -180, 5, 5, 5, this._position, this._direction, false);
  }
}


