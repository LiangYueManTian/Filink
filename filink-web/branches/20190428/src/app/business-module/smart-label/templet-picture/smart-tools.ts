export class SmartTools {
  private static graph;

  public static init(graph) {
    SmartTools.graph = graph;
  }

  /**
   * 绘制格子
   * param Q
   * param graph
   * param row 行 行长由列决定
   * param col 列 列长由行决定
   * param position  走向决定起始位置点  leftTop  leftBottom rightTop rightBottom
   * param direction 编码规则决定线的方向 row col
   */
  public static drawSmartLabel(Q, graph, startX, startY, spaceWidth, row, col, position, direction, bol = false) {
    //  startX 起始点 -200
    //  startY 起始点y -200
    //  spaceWidth 间距 200
    // const startX = -200, startY = -200, spaceWidth = 200;
    // 绘制横线
    for (let i = 0; i <= row; i++) {
      const rows = graph.createShapeNode();
      rows.moveTo(startX, startY + i * spaceWidth);
      rows.lineTo(startX + col * spaceWidth, startY + i * spaceWidth);
      rows.setStyle(Q.Styles.SHAPE_STROKE_STYLE, '#000');
      rows.setStyle(Q.Styles.SHAPE_STROKE, 0.5);
      rows.setStyle(Q.Styles.SHAPE_LINE_SOLID, [5, 2]);
      // console.log(rows);
      rows.isSelected = function () {
        return false;
      };
      rows.isMovable = function () {
        return false;
      };
    }
    // 绘制竖线
    for (let j = 0; j <= col; j++) {
      const line = graph.createShapeNode();
      line.moveTo(startX + j * spaceWidth, startY);
      line.lineTo(startX + j * spaceWidth, row * spaceWidth + startY);
      line.setStyle(Q.Styles.SHAPE_STROKE_STYLE, '#000');
      line.setStyle(Q.Styles.SHAPE_STROKE, 0.5);
      line.setStyle(Q.Styles.SHAPE_LINE_SOLID, [5, 2]);
    }
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
        console.log(num);

          const x = startX + q * spaceWidth + spaceWidth / 2;
          const y = startY + m * spaceWidth + spaceWidth / 2;
          console.log(x, y);
          const width = spaceWidth;
          SmartTools.drawGrid(Q, graph, `${num}`, '', x, y, width, width, '', 1);


      }
    }
  }

  public static drawGridNumber(Q, graph, row, col, position, direction, startX, startY, spaceWidth, spaceHeight) {
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
        console.log(num);
          const x = startX + q * spaceWidth + spaceWidth / 2;
          const y = startY + m * spaceHeight + spaceHeight / 2;
          console.log(x, y);
          const width = spaceWidth;
          SmartTools.drawGrid(Q, graph, `${num}`, '', x, y, width, width, '', 1);

      }
    }
  }


  /**
   * 绘制编号
   * param Q
   * param graph
   * param x  编号的x坐标
   * param y  编号的y坐标
   * param w  宽度
   * param h  高度
   * param name 名字
   * param image 背景图
   * return any
   */
  public static drawGrid(Q, graph, name, coed, x, y, w, h, image, cabinetId): any {
    const _name = name.replace(/[\u4e00-\u9fa5]/g, 'aa').length > 8 ? name.substring(0, 4) + '...' : name;
    const grid = graph.createNode(_name, x, y);
    // grid.image = image || 'assets/img/login/login-bg.png';
    grid.image = null;
    grid.zIndex = 3;
    grid.size = {width: w, height: h};
    // SmartTools.setToolTip(grid, name, coed);
    grid.set('type', 'cabinet22');
    grid.set('cabinetId', cabinetId);
    grid.set('code', coed);
    grid.set('name', name);
    grid.setStyle(Q.Styles.SELECTION_COLOR, '#2146e9');
    grid.setStyle(Q.Styles.SELECTION_BORDER, 1);
    grid.setStyle(Q.Styles.SELECTION_SHADOW_BLUR, 3);
    grid.setStyle(Q.Styles.SELECTION_SHADOW_OFFSET_X, 2);
    grid.setStyle(Q.Styles.SELECTION_SHADOW_OFFSET_Y, 2);
    grid.setStyle(Q.Styles.LABEL_FONT_SIZE, 12);
    // grid.setStyle(Q.Styles.LABEL_OFFSET_X, -w / 2 +5);
    grid.setStyle(Q.Styles.IMAGE_BORDER, 1);
    grid.setStyle(Q.Styles.IMAGE_BORDER_RADIUS, 0);
    grid.isSelected = function () {
      return false;
    };
    grid.isMovable = function () {
      return false;
    };
    grid.isSelectable = (item) => {
      return item.get('type') === 'cabinet';
    };
    return grid.id;
  }


  public static setToolTip(target, name, code = '无数据') {
    target.tooltip = `
            <p >名称：${name} </p>
        `;
  }
}
