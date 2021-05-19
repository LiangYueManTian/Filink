import { Component, OnInit } from '@angular/core';
import {DrawInfo, TemplateItem} from './template-item.enum';

@Component({
  selector: 'app-business-template',
  templateUrl: './business-template.component.html',
  styleUrls: ['./business-template.component.scss']
})
export class BusinessTemplateComponent implements OnInit {
  // 获取queen实例
  Q = window['Q'];
  graph;
  // 当前模板名称
  templateInfo = {
    name: '模板001',
  };
  // 默认箱配置
  boxConfig: DrawInfo = {
    startX: 40,
    startY: 40,
    row: 2,
    col: 2,
    width: TemplateItem.FRAME_WIDTH,
    height: TemplateItem.FRAME_HEIGHT,
    position: 'leftBottom',
    direction: 'row',
  };
  // 默认框配置
  frameConfig: DrawInfo = {
    startX: 40,
    startY: 40,
    row: 2,
    col: 2,
    width: TemplateItem.FRAME_WIDTH,
    height: TemplateItem.FRAME_HEIGHT,
    position: 'leftBottom',
    direction: 'row',
  };
  // 默认盘配置
  boardConfig: DrawInfo = {
    startX: 0,
    startY: 0,
    row: 7,
    col: 8,
    width: TemplateItem.BOARD_WIDTH,
    height: TemplateItem.BOARD_HEIGHT,
    position: 'leftBottom',
    direction: 'row',
  };
  zIndex = 4;
  // 默认初始化坐标原点
  coordinate = {
    x: 0,
    y: 100
  };
  constructor() { }

  ngOnInit() {
    this.graph = new this.Q.Graph('template-info-canvas');
    // 线条禁止拖动
    this.graph.isSelectable = (item) => {
      return item.get('type') === 'cabinet';
    };
    this.draw();
  }

  /**
   * 模板选择
   * param item
   */
  selectTemplate(item) {
    this.templateInfo.name = `模板${parseInt((Math.random() * 100).toString(), 0)}`;
    this.boardConfig.col = parseInt((Math.random() * 5).toString(), 0) + 1;
    this.draw();
  }

  /**
   * 模板绘制
   */
  draw() {
    // 绘制之前先清空画布
    this.zIndex = 4;
    this.graph.clear();
    // this.graph.restore();
    // 根据盘大小来计算框的大小
    this.initWH(this.frameConfig, this.boardConfig);
    // 根据框的宽高计算箱的大小
    this.initWH(this.boxConfig, this.frameConfig);
    // 设置坐标原点
    this.graph.originAtCenter = false;
    if (this.coordinate.x !== 0) {
      // 恢复左边原点  主要用于再次计算左边远点
      this.graph.translate(0 - this.coordinate.x, 0 - this.coordinate.y);
    }
    this.graph.translate(this.getX(), this.coordinate.y);
    // 第一步绘制盘
    this.drawTemplate(this.boardConfig);
    // 第二步绘制框
    this.drawTemplate(this.frameConfig);
    // 第三步绘制箱
    this.drawTemplate(this.boxConfig);
  }
  /**
   * 绘制 箱 框 盘
   * 通过绘制行和绘制列  交叉成为一个方格
   * param row 行数
   * param col 列数
   */
  drawTemplate(drawInfo: DrawInfo) {
    // 参数解构
    const {startX, startY, row, col, width, height, position, direction} = drawInfo;
    // 绘制行
    for (let i = 0; i <= row; i++) {
      const rows = this.graph.createShapeNode();
      rows.moveTo(startX, startY + i * height);
      rows.lineTo( startX + col * width, startY + i * height);
      rows.setStyle(this.Q.Styles.SHAPE_STROKE_STYLE, '#696969');
      rows.setStyle(this.Q.Styles.SHAPE_STROKE, 0.5);
      // rows.setStyle(this.Q.Styles.SHAPE_LINE_SOLID, [5, 2]);
    }
    // 绘制列
    for (let i = 0; i <= col; i++) {
      const cols = this.graph.createShapeNode();
      cols.moveTo(startX + i * width, startY);
      cols.lineTo(startX + i * width, startY + row * height);
      cols.setStyle(this.Q.Styles.SHAPE_STROKE_STYLE, '#696969');
      cols.setStyle(this.Q.Styles.SHAPE_STROKE, 0.5);
      // cols.setStyle(this.Q.Styles.SHAPE_LINE_SOLID, [5, 2]);
    }
    // 方格填充数据
    for (let m = 0; m < row; m++) {
      for (let q = 0; q < col; q++) {
        let num;
        // 判断走向
        if (position === 'leftTop' && direction === 'row') { // 左上 行优
          num = m * row + q + 1;
        } else if (position === 'leftTop' && direction === 'col') {  // 左上 列优
          num = m + 1 + q * row;
        } else if (position === 'leftBottom' && direction === 'row') { // 左下 行优
          num = (row - m - 1) * col + q + 1;
        } else if (position === 'leftBottom' && direction === 'col') { // 左下 列优
          num = (row - m) + q * row;
        } else if (position === 'rightTop' && direction === 'row') {  // 右上 行优
          num = (col - q) + m * col;
        } else if (position === 'rightTop' && direction === 'col') {   // 右上 列优
          num = (col - q - 1) * col + m + 1;
        } else if (position === 'rightBottom' && direction === 'row') {  // 右下 行优
          num = (row - m) * col - q;
        } else if (position === 'rightBottom' && direction === 'col') {    // 右下 列优
          num = row * col - m - row * q;
        } else {
          num = 0;
        }
        const x = startX + q * width + width / 2;
        const y = startY + m * height + height / 2;
        const grid = this.graph.createNode(`${num}`, x, y);
        grid.zIndex = this.zIndex --;
        grid.image = null;
        grid.size = {width: width, height: height};
        grid.set('name', name);
        grid.setStyle(this.Q.Styles.LABEL_FONT_SIZE, 12);
      }
    }
  }

  /**
   * 获取X租表原点
   */
  getX(): number {
    const node = document.getElementById('template-info-canvas');
    const offsetWidth = node.offsetWidth;
    if (this.boxConfig.width * this.boxConfig.col > offsetWidth) {
      // 3个边距
      this.coordinate.x = 20 * 3;
    } else {
      const differ = offsetWidth - this.boxConfig.width * this.boxConfig.col;
      // 2个边距加上多余位置的一般
      this.coordinate.x = 20 * 2 + differ / 2;
    }
    return this.coordinate.x;
  }
  /**
   * 根据下级计算上级 宽高
   * param a 上级
   * param b 下级
   */
  initWH (a: DrawInfo, b: DrawInfo) {
    // 20*2 用来处理上下间距
    a.width = b.width * b.col + 20 * 2;
    a.height = b.height * b.row + 20 * 2;
    a.startX = b.startX - 20;
    a.startY = b.startX - 20;
}
}
