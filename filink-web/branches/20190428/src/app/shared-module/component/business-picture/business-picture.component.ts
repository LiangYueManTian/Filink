import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {BusinessImageUrl} from '../../enum/business-image-url.enum';

@Component({
  selector: 'app-business-picture',
  templateUrl: './business-picture.component.html',
  styleUrls: ['./business-picture.component.scss']
})
export class BusinessPictureComponent implements OnInit, AfterViewInit {
  Q = window['Q'];
  graph;
  @Input() smartData = {
    frameList: [],
    boardList: [],
    portList: []
  };
  // 箱默认宽高
  BW = 300;
  BH = 600;
  // 默认框行列
  frameConfig = {
    row: 2,
    col: 3
  };
  // 默认盘配置
  boardConfig = {
    row: 10,
    col: 1
  };

  // 默认端口绘制
  portConfig = {
    row: 3,
    col: 10
  };
  constructor() { }

  ngOnInit() {
    this.graph = new this.Q.Graph('business-picture-canvas');
    this.graph.isSelectable = (item) => {
      return item.get('type') === 'cabinet';
    };
    this.graph.originAtCenter = false;
    this.graph.translate(20, 20);
  }

  ngAfterViewInit(): void {
    // 绘制方式大致为逐级绘制 先绘制父级 然后逐级绘制子级
    this.draw();
  }

  /**
   * 绘制实景图
   */
  draw() {
    const box = this.drawNode(0, 0, this.BW, this.BH, BusinessImageUrl.BOX_IMG, null);
    // 绘制框 先更具箱的宽高计算框的宽高
    const frameW = this.getWH(this.BW, this.frameConfig.col);
    const frameH = this.getWH(this.BH, this.frameConfig.row);
    for (let i = 0; i < this.frameConfig.col; i++) {
      for (let j = 0; j < this.frameConfig.row; j++) {
        // 绘制框
        const startFrameX = this.getStartX(frameW, i, box);
        const startFrameY = this.getStartY(frameH, j, box);
        const frame = this.drawNode(startFrameX, startFrameY, frameW, frameH, BusinessImageUrl.FRAME_IMG, box);
        // 绘制当前框下的盘 先计算盘的宽和高
        const boardW = this.getWH(frame.$size.width, this.boardConfig.col);
        const boardH = this.getWH(frame.$size.height, this.boardConfig.row);
        for (let m = 0; m < this.boardConfig.col; m++) {
          for (let n = 0; n < this.boardConfig.row; n++) {
            // 绘制框
            const startBoardX = this.getStartX(boardW, m, frame);
            const startBoardY = this.getStartY(boardH, n,  frame);
            const board = this.drawNode(startBoardX, startBoardY, boardW, boardH, BusinessImageUrl.BOARD_IMG, frame);
            // 绘制当前盘下的端口 先计算端口的宽和高
            const portW = this.getPortWH(board.$size.width, this.portConfig.col);
            const portH = this.getPortWH(board.$size.height, this.portConfig.row);
            for (let a = 0; a < this.portConfig.col; a++) {
              for (let b = 0; b < this.portConfig.row; b++) {
                // 绘制框
                const startPortX = this.getPortStartX(portW, a, board);
                const startPortY = this.getPortStartY(portH, b, board);
                const newH = this.getPortWH(board.$size.height, this.portConfig.row);
                const newW = this.getPortWH(board.$size.width, this.portConfig.col);
                this.drawNode(startPortX, startPortY, newW, newH, BusinessImageUrl.INTO_PORT_IMG, board);
              }
            }
          }
        }
      }
    }
  }

  /**
   * 绘制节点
   */
  drawNode(startX, startY, width, height, img, parent) {
    const node = this.graph.createNode('', startX, startY);
    node.anchorPosition = this.Q.Position.LEFT_TOP;
    node.image = img;
    node.size = {width, height};
    if (parent) {
      node.host = parent;
      node.parent = parent;
    }
    return node;
  }

  /**
   * 计算frame，board的宽高
   */
  private getWH(WH: number, col: number): number {
    return WH * 0.96 / col;
  }

  /**
   * 计算端口宽高
   */
  private getPortWH(WH: number, col: number): number {
    return WH / (2 * col + 1);
  }

  /**
   * 计算起始坐标X
   * param width
   * param col
   * param parent
   */
  private getStartX(width, col, parent): number {
    return parent.$size.width * 0.02 + col * width + parent.x;
  }

  /**
   * 计算起始坐标Y
   * param height
   * param row
   * param parent
   */
  private getStartY(height, row, parent): number {
    return parent.$size.height * 0.02 + row * height + parent.y;
  }

  /**
   * 计算端口起始坐标X
   * param width
   * param col
   * param parent
   */
  private getPortStartX(width, col, parent) {
    return parent.$size.width * 0.05 + col * width * 2 + parent.x;
  }

  /**
   * 计算端口起始坐标Y
   * param width
   * param col
   * param parent
   */
  private getPortStartY(height, row, parent) {
    return parent.$size.height * 0.1 + row * height * 2 + parent.y;
  }
}
