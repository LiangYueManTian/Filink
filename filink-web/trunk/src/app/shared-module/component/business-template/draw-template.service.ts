import {Injectable} from '@angular/core';
import {DrawInfo} from './template-item.enum';
import {TemplateCodeRule, TemplateTrend} from '../../../business-module/facility/facility.config';
import {TemplateColor} from '../../entity/template';

@Injectable()
export class DrawTemplateService {
  // 获取queen实例
  Q = window['Q'];
  // 模板数字
  grid;

  constructor() {
  }

  /**
   * 绘制 箱 框 盘
   * 通过绘制行和绘制列  交叉成为一个方格
   * param row 行数
   * param col 列数
   */
  // drawTemplate(drawInfo: DrawInfo, graph) {
  drawTemplate(drawInfo: DrawInfo, graph, template?) {
    // 参数解构
    const {startX, startY, row, col, width, height, position, direction} = drawInfo;
    // 绘制行
    for (let i = 0; i <= row; i++) {
      const rows = graph.createShapeNode();
      rows.moveTo(startX, startY + i * height);
      rows.lineTo(startX + col * width, startY + i * height);
      rows.setStyle(this.Q.Styles.SHAPE_STROKE_STYLE, '#696969');
      rows.setStyle(this.Q.Styles.SHAPE_STROKE, 0.5);
      // rows.setStyle(this.Q.Styles.SHAPE_LINE_SOLID, [5, 2]);
    }
    // 绘制列
    for (let i = 0; i <= col; i++) {
      const cols = graph.createShapeNode();
      cols.moveTo(startX + i * width, startY);
      cols.lineTo(startX + i * width, startY + row * height);
      cols.setStyle(this.Q.Styles.SHAPE_STROKE_STYLE, '#696969');
      cols.setStyle(this.Q.Styles.SHAPE_STROKE, 0.5);
    }
    // 方格填充数据
    for (let m = 0; m < row; m++) {
      for (let q = 0; q < col; q++) {
        let num;
        // 判断走向
        if (position === TemplateCodeRule.leftUp && direction === TemplateTrend.TEMPLATE_TREND_ROW) { // 左上 行优
          num = m * col + q + 1;
        } else if (position === TemplateCodeRule.leftUp && direction === TemplateTrend.TEMPLATE_TREND_COL) {  // 左上 列优
          num = m + 1 + q * row;
        } else if (position === TemplateCodeRule.leftDown && direction === TemplateTrend.TEMPLATE_TREND_ROW) { // 左下 行优
          num = (row - m - 1) * col + q + 1;
        } else if (position === TemplateCodeRule.leftDown && direction === TemplateTrend.TEMPLATE_TREND_COL) { // 左下 列优
          num = (row - m) + q * row;
        } else if (position === TemplateCodeRule.rightUp && direction === TemplateTrend.TEMPLATE_TREND_ROW) {  // 右上 行优
          num = (col - q) + m * col;
        } else if (position === TemplateCodeRule.rightUp && direction === TemplateTrend.TEMPLATE_TREND_COL) {   // 右上 列优
          num = (col - q - 1) * row + m + 1;
        } else if (position === TemplateCodeRule.rightDown && direction === TemplateTrend.TEMPLATE_TREND_ROW) {  // 右下 行优
          num = (row - m) * col - q;
        } else if (position === TemplateCodeRule.rightDown && direction === TemplateTrend.TEMPLATE_TREND_COL) {    // 右下 列优
          num = row * col - m - row * q;
        } else {
          num = 0;
        }
        const x = startX + q * width + width / 2;
        const y = startY + m * height + height / 2;
        this.grid = graph.createNode(`${num}`, x, y);
        // 节点禁止拖动
        this.grid.movable = false;
        if (template === TemplateColor.theTemplate) {
          this.grid.setStyle(this.Q.Styles.LABEL_COLOR, '#ff0000');
          this.grid.setStyle(this.Q.Styles.LABEL_FONT_SIZE, 24);
        } else {
          this.grid.setStyle(this.Q.Styles.LABEL_FONT_SIZE, 2);
        }
        this.grid.image = null;
        this.grid.size = {width: width, height: height};
        this.grid.set('name', name);
      }
    }
  }

}
