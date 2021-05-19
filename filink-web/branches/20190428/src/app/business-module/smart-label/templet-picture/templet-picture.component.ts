import {Component, OnInit} from '@angular/core';
import {SmartTools} from './smart-tools';
import {RealisticConfigImg} from '../realistic-picture/realistic-config';

@Component({
  selector: 'app-templet-picture',
  templateUrl: './templet-picture.component.html',
  styleUrls: ['./templet-picture.component.scss']
})
export class TempletPictureComponent implements OnInit {

  constructor() {
  }

  // 编号规则;
  Q = window['Q'];
  graph;
  graphBak;
  _row = 2;
  _col = 2;
  _position = 'rightTop';
  _direction = 'row';
  data = {
    X: 0,
    Y: 0,
    LW: 600,
    LH: 600,
    col: 2,
    row: 2,
    type: 'B',
    pan: {
      col: 1,
      row: 10
    },
    port: {
      col: 10,
      row: 3
    }
  };
  //  选中的槽位节点
  select_node;

  listK = [
    {indexN: 0, a: 1, b: 1, kType: 'k', parent: 1},
    {indexN: 1, a: 1, b: 2, kType: 'k', parent: 1},
    {indexN: 2, a: 2, b: 1, kType: 'k', parent: 1},
    {indexN: 3, a: 2, b: 2, kType: 'k', parent: 1},
  ];
  listC = [
    {indexN: 0, a: 1, b: 1, cType: 1, parent: 0},
    {indexN: 1, a: 2, b: 1, cType: 0, parent: 0},
    {indexN: 2, a: 3, b: 1, cType: 1, parent: 0},
    {indexN: 3, a: 4, b: 1, cType: 1, parent: 0},
    {indexN: 4, a: 5, b: 1, cType: 1, parent: 0},
    {indexN: 5, a: 6, b: 1, cType: 0, parent: 0},
    {indexN: 6, a: 7, b: 1, cType: 1, parent: 0},
    {indexN: 7, a: 8, b: 1, cType: 0, parent: 0},
    {indexN: 8, a: 9, b: 1, cType: 1, parent: 0},
    {indexN: 9, a: 10, b: 1, cType: 1, parent: 0},

    {indexN: 10, a: 1, b: 1, cType: 1, parent: 1},
    {indexN: 11, a: 2, b: 1, cType: 0, parent: 1},
    {indexN: 12, a: 3, b: 1, cType: 1, parent: 1},
    {indexN: 13, a: 4, b: 1, cType: 0, parent: 1},
    {indexN: 14, a: 5, b: 1, cType: 1, parent: 1},
    {indexN: 15, a: 6, b: 1, cType: 0, parent: 1},
    {indexN: 16, a: 7, b: 1, cType: 1, parent: 1},
    {indexN: 17, a: 8, b: 1, cType: 0, parent: 1},
    {indexN: 18, a: 9, b: 1, cType: 0, parent: 1},
    {indexN: 19, a: 10, b: 1, cType: 1, parent: 1},

    {indexN: 20, a: 1, b: 1, cType: 1, parent: 2},
    {indexN: 21, a: 2, b: 1, cType: 1, parent: 2},
    {indexN: 22, a: 3, b: 1, cType: 0, parent: 2},
    {indexN: 23, a: 4, b: 1, cType: 1, parent: 2},
    {indexN: 24, a: 5, b: 1, cType: 0, parent: 2},
    {indexN: 25, a: 6, b: 1, cType: 1, parent: 2},
    {indexN: 26, a: 7, b: 1, cType: 0, parent: 2},
    {indexN: 27, a: 8, b: 1, cType: 0, parent: 2},
    {indexN: 28, a: 9, b: 1, cType: 0, parent: 2},
    {indexN: 29, a: 10, b: 1, cType: 1, parent: 2},

    {indexN: 30, a: 1, b: 1, cType: 1, parent: 3},
    {indexN: 31, a: 2, b: 1, cType: 1, parent: 3},
    {indexN: 32, a: 3, b: 1, cType: 1, parent: 3},
    {indexN: 33, a: 4, b: 1, cType: 1, parent: 3},
    {indexN: 34, a: 5, b: 1, cType: 1, parent: 3},
    {indexN: 35, a: 6, b: 1, cType: 1, parent: 3},
    {indexN: 36, a: 7, b: 1, cType: 1, parent: 3},
    {indexN: 37, a: 8, b: 1, cType: 1, parent: 3},
    {indexN: 38, a: 9, b: 1, cType: 1, parent: 3},
    {indexN: 39, a: 10, b: 1, cType: 0, parent: 3},
  ];

  listD = [
    {indexN: 0, a: 1, b: 1, dType: 0, parent: 0},
    {indexN: 1, a: 1, b: 2, dType: 0, parent: 0},
    {indexN: 2, a: 1, b: 3, dType: 1, parent: 0},
    {indexN: 3, a: 1, b: 4, dType: 1, parent: 0},
    {indexN: 4, a: 1, b: 5, dType: 1, parent: 0},
    {indexN: 5, a: 1, b: 6, dType: 0, parent: 0},
    {indexN: 6, a: 1, b: 7, dType: 0, parent: 0},
    {indexN: 7, a: 1, b: 8, dType: 1, parent: 0},
    {indexN: 8, a: 1, b: 9, dType: 1, parent: 0},
    {indexN: 9, a: 1, b: 10, dType: 1, parent: 0},

    {indexN: 10, a: 2, b: 1, dType: 0, parent: 0},
    {indexN: 11, a: 2, b: 2, dType: 0, parent: 0},
    {indexN: 12, a: 2, b: 3, dType: 0, parent: 0},
    {indexN: 13, a: 2, b: 4, dType: 1, parent: 0},
    {indexN: 14, a: 2, b: 5, dType: 0, parent: 0},
    {indexN: 15, a: 2, b: 6, dType: 0, parent: 0},
    {indexN: 16, a: 2, b: 7, dType: 0, parent: 0},
    {indexN: 17, a: 2, b: 8, dType: 1, parent: 0},
    {indexN: 18, a: 2, b: 9, dType: 1, parent: 0},
    {indexN: 19, a: 2, b: 10, dType: 1, parent: 0},

    {indexN: 20, a: 3, b: 1, dType: 0, parent: 0},
    {indexN: 21, a: 3, b: 2, dType: 0, parent: 0},
    {indexN: 22, a: 3, b: 3, dType: 1, parent: 0},
    {indexN: 23, a: 3, b: 4, dType: 1, parent: 0},
    {indexN: 24, a: 3, b: 5, dType: 1, parent: 0},
    {indexN: 25, a: 3, b: 6, dType: 0, parent: 0},
    {indexN: 26, a: 3, b: 7, dType: 0, parent: 0},
    {indexN: 27, a: 3, b: 8, dType: 1, parent: 0},
    {indexN: 28, a: 3, b: 9, dType: 1, parent: 0},
    {indexN: 29, a: 3, b: 10, dType: 1, parent: 0},

    {indexN: 30, a: 1, b: 1, dType: 1, parent: 2},
    {indexN: 31, a: 1, b: 2, dType: 0, parent: 2},
    {indexN: 32, a: 1, b: 3, dType: 1, parent: 2},
    {indexN: 33, a: 1, b: 4, dType: 1, parent: 2},
    {indexN: 34, a: 1, b: 5, dType: 1, parent: 2},
    {indexN: 35, a: 1, b: 6, dType: 1, parent: 2},
    {indexN: 36, a: 1, b: 7, dType: 0, parent: 2},
    {indexN: 37, a: 1, b: 8, dType: 1, parent: 2},
    {indexN: 38, a: 1, b: 9, dType: 1, parent: 2},
    {indexN: 39, a: 1, b: 10, dType: 1, parent: 2},
    //
    {indexN: 40, a: 2, b: 1, dType: 1, parent: 2},
    {indexN: 41, a: 2, b: 2, dType: 0, parent: 2},
    {indexN: 42, a: 2, b: 3, dType: 1, parent: 2},
    {indexN: 43, a: 2, b: 4, dType: 0, parent: 2},
    {indexN: 44, a: 2, b: 5, dType: 1, parent: 2},
    {indexN: 45, a: 2, b: 6, dType: 0, parent: 2},
    {indexN: 46, a: 2, b: 7, dType: 0, parent: 2},
    {indexN: 47, a: 2, b: 8, dType: 0, parent: 2},
    {indexN: 48, a: 2, b: 9, dType: 0, parent: 2},
    {indexN: 49, a: 2, b: 10, dType: 1, parent: 2},

    // {indexN: 0, a: 1, b: 1, dType: 1, parent: 1},
    // {indexN: 1, a: 1, b: 2, dType: 0, parent: 1},
    // {indexN: 2, a: 1, b: 3, dType: 1, parent: 1},
    // {indexN: 3, a: 1, b: 4, dType: 0, parent: 1},
    // {indexN: 4, a: 1, b: 5, dType: 1, parent: 1},
    // {indexN: 5, a: 1, b: 6, dType: 0, parent: 1},
    // {indexN: 6, a: 1, b: 7, dType: 1, parent: 1},
    // {indexN: 7, a: 1, b: 8, dType: 0, parent: 1},
    // {indexN: 8, a: 1, b: 9, dType: 0, parent: 1},
    // {indexN: 9, a: 1, b: 10, dType: 1, parent: 1},
    //
    // {indexN: 0, a: 1, b: 1, cType: 1, parent: 2},
    // {indexN: 1, a: 2, b: 1, cType: 1, parent: 2},
    // {indexN: 2, a: 3, b: 1, cType: 0, parent: 2},
    // {indexN: 3, a: 4, b: 1, cType: 1, parent: 2},
    // {indexN: 4, a: 5, b: 1, cType: 0, parent: 2},
    // {indexN: 5, a: 6, b: 1, cType: 1, parent: 2},
    // {indexN: 6, a: 7, b: 1, cType: 0, parent: 2},
    // {indexN: 7, a: 8, b: 1, cType: 0, parent: 2},
    // {indexN: 8, a: 9, b: 1, cType: 0, parent: 2},
    // {indexN: 9, a: 10, b: 1, cType: 1, parent: 2},
    //
    // {indexN: 0, a: 1, b: 1, cType: 1, parent: 3},
    // {indexN: 1, a: 2, b: 1, cType: 1, parent: 3},
    // {indexN: 2, a: 3, b: 1, cType: 1, parent: 3},
    // {indexN: 3, a: 4, b: 1, cType: 1, parent: 3},
    // {indexN: 4, a: 5, b: 1, cType: 1, parent: 3},
    // {indexN: 5, a: 6, b: 1, cType: 1, parent: 3},
    // {indexN: 6, a: 7, b: 1, cType: 1, parent: 3},
    // {indexN: 7, a: 8, b: 1, cType: 1, parent: 3},
    // {indexN: 8, a: 9, b: 1, cType: 1, parent: 3},
    // {indexN: 9, a: 10, b: 1, cType: 0, parent: 3},
  ];
  // 框
  KH = this.getLong(this.data.LH, this.data.row); // 框的高度
  KW = this.getLong(this.data.LW, this.data.col); // 框的宽度
  // 盘
  PH = this.getLong(this.KH, this.data.pan.row); // 盘的高度
  PW = this.getLong(this.KW, this.data.pan.col); // 盘的宽度
  // 端口
  DH = this.getdLong2(this.PH, this.data.port.row); // 端口高度
  DW = this.getdLong2(this.PW, this.data.port.col); // 端口宽度
  // 左侧实景的对象
  racks = {}; // 框
  cns = {};   // 槽位
  pns = {};   // 盘在位
  dns = {};

  pnsR = {};
  cnsR = {};
  racksR = {};
  dnsR = {};
  rack2;

  ngOnInit(): void {
    this.graph = new this.Q.Graph('smart-canvas');
    this.graph.isSelectable = (item) => {
      return item.get('type') === 'cabinet';
    };
    this.graph.originAtCenter = false;
    this.graph.translate(20, 20);
    console.log(this.graph);
    const startX = 0, startY = 0;
    const jj = 40;
    // 公式根据这个算法来做 暂时没写
    // this.hLine(1, startX, startY, 600, 600, 1, this._position, this._direction, null);
    this.hLine(2, 0, 0, 600, 600, 2, this._position, this._direction, null);
    this.hLine(2, 40, 40, (600 - 40 * 2) / 2, (600 - 40 * 2) / 2, 2, this._position, this._direction, null);
    this.hLine(2, 80, 80, (((600 - 40 * 2) / 2) - 40 * 2) / 2, (((600 - 40 * 2) / 2) - 40 * 2) / 2, 2, this._position, this._direction, null);
    // const box = this.createEquipment(this.graph, '', this.data.X, this.data.Y, null);
    // box.size = {width: this.data.LW, height: this.data.LH};
    // box.set('data', this.data);
  }

  /**
   * 创建框
   * @param startX
   * @param startY
   * @param WW
   * @param HH
   * @param row
   * @param col
   * @param nodes
   * @param IMG
   * @param parent
   */
  creatFrames(nodes, startX, startY, WW, HH, row, col, IMG, parent) {
    const H = this.getLong(HH, row); // 框的高度
    const W = this.getLong(WW, col); // 框的宽度
    for (let i = 0; i < nodes.length; i++) {
      const rackName = 'rack' + i;
      const xk = this.getx(startX, WW, W, nodes[i].b);
      const yk = this.gety(startY, HH, H, nodes[i].a);
      this.racks[rackName] = this.createEquipment(this.graph, `${i + 1}`, xk, yk, parent);
      this.racks[rackName].size = {width: W, height: H};
      this.racks[rackName].set('data', nodes[i]);
    }
  }

  /**
   *
   * @param name
   * @param x 坐标值
   * @param y 坐标值
   * @param image 图片
   * @param host 父节点对象
   * @returns {any}
   */
  createEquipment(graph, name, x, y, host) {
    const node = graph.createNode(name, x, y);
    node.anchorPosition = this.Q.Position.LEFT_TOP;
    node.image = RealisticConfigImg.slotImg;
    node.setStyle(this.Q.Styles.BORDER_COLOR, '#2898E0');
    node.setStyle(this.Q.Styles.BORDER, 1);
    node.setStyle(this.Q.Styles.BORDER_RADIUS, 0);
    if (host) {
      node.host = host;
      node.parent = host;
    }
    return node;
  }

  /**
   *
   * @param X 初始的x坐标 如果没有父元素默认为 0
   * @param W 父元素的宽度
   * @param ww  自身的宽度
   * @param b
   * @returns {any}
   */
  getx(X, W, ww, b) {
    const x = X + 0.1 * W + ww * (b - 1);
    return x;
  }

  /**
   *
   * @param Y 初始父元素的y坐标 如果没有父元素默认为 0
   * @param H  父元素的高度
   * @param hh 自身的高度
   * @param a
   * @returns {any}
   */
  gety(Y, H, hh, a) {
    const y = Y + 0.1 * H + hh * (a - 1);
    return y;
  }

  /**
   *
   * @param X 初始的x坐标 如果没有父元素默认为 0
   * @param W 父元素的宽度
   * @param ww  自身的宽度
   * @param b
   * @returns {any}
   */
  getdx(X, W, ww, b) {
    const x = X + ww + ww * (b - 1) * 2;
    return x;
  }

  /**
   *
   * @param Y 初始父元素的y坐标 如果没有父元素默认为 0
   * @param H 父元素的宽度
   * @param hh 自身的高度
   * @param a
   * @returns {any}
   */
  getdy(Y, H, hh, a) {
    const y = Y + hh + hh * (a - 1) * 2;
    return y;
  }

  /**
   * 框和盘宽高计算通用公式
   * @param L 为父元素的宽度或者是高度
   * @param num  行数row 或者是 列数col
   * 0.96 为中间可用部分的缩放比 （600 - 2*10）/ 600
   * @returns {number}
   */
  getLong(L, num) {
    // w = (W - 2*0.1*W)/num;
    const l = 0.8 * L / num;
    return l;
  }

  /**
   * 端口的计算公式
   * @param L 父元素的长或高
   * @param num 传入的行row或者列col
   * @returns {number}
   */
  getdLong2(L, num) {
    // w = (W - 2*0.02*W)/num;
    const l = L / (2 * num + 1);
    return l;
  }

  /**
   * 绘制线框
   * @param row
   * @param startX
   * @param startY
   * @param spaceWidth
   * @param spaceHeight
   * @param col
   * @param position
   * @param direction
   * @param image
   */
  hLine(row, startX, startY, spaceWidth, spaceHeight, col, position, direction, image) {
    for (let i = 0; i <= row; i++) {
      const rows = this.graph.createShapeNode();
      rows.moveTo(startX, startY + i * spaceWidth);
      rows.lineTo(startX + col * spaceWidth, startY + i * spaceWidth);
      rows.setStyle(this.Q.Styles.SHAPE_STROKE_STYLE, '#000');
      rows.setStyle(this.Q.Styles.SHAPE_STROKE, 0.5);
      rows.setStyle(this.Q.Styles.SHAPE_LINE_SOLID, [5, 2]);
      // console.log(rows);
      rows.isSelected = function () {
        return false;
      };
      rows.isMovable = function () {
        return false;
      };
    }
    for (let j = 0; j <= col; j++) {
      const line = this.graph.createShapeNode();
      line.moveTo(startX + j * spaceWidth, startY);
      line.lineTo(startX + j * spaceWidth, row * spaceWidth + startY);
      line.setStyle(this.Q.Styles.SHAPE_STROKE_STYLE, '#000');
      line.setStyle(this.Q.Styles.SHAPE_STROKE, 0.5);
      line.setStyle(this.Q.Styles.SHAPE_LINE_SOLID, [5, 2]);
    }
    // 绘制格子
    for (let m = 0; m < row; m++) {
      for (let q = 0; q < col; q++) {
        let num;
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
        const x = startX + q * spaceWidth + spaceWidth / 2;
        const y = startY + m * spaceHeight + spaceHeight / 2;
        const grid = this.graph.createNode(`${num}`, x, y);
        grid.image = image || 'assets/img/login/login-bg.png';
        grid.image = null;
        grid.zIndex = 3;
        grid.size = {width: spaceWidth, height: spaceHeight};
        grid.set('name', name);
        grid.setStyle(this.Q.Styles.LABEL_FONT_SIZE, 12);
      }
    }
  }
}

