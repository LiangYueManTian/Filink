import {Component, OnInit} from '@angular/core';
import {PictureTools} from './picture-tools';
import {RealisticConfigImg} from './realistic-config';

@Component({
  selector: 'app-realistic-picture',
  templateUrl: './realistic-picture.component.html',
  styleUrls: ['./realistic-picture.component.scss']
})
export class RealisticPictureComponent implements OnInit {
  Q = window['Q'];
  graph;
  graph2;
  LW = 300;
  LH = 600;
  col = 3;
  row = 3;
  data = {
    X: 0,
    Y: 0,
    LW: 300,
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

  ngOnInit() {
    this.graph = new this.Q.Graph('pic-canvas');
    this.graph.isSelectable = (item) => {
      return item.get('type') === 'cabinet';
    };
    this.graph.originAtCenter = false;
    this.graph.translate(20, 20);
    const box = this.createEquipment(this.graph, '', this.data.X, this.data.Y, RealisticConfigImg.boxImg, null);
    box.size = {width: this.data.LW, height: this.data.LH};
    box.set('data', this.data);
    //  创建框
    this.creatFrames(this.listK, this.data.X, this.data.Y, this.data.LW, this.data.LH, this.data.row, this.data.col, RealisticConfigImg.frameImg, box);
    // 创建槽位和在位盘
    for (let i = 0; i < this.listC.length; i++) {
      const cName = 'cn' + this.listC[i].indexN;
      const pindexN = this.listC[i].parent;
      const rackName = 'rack' + pindexN;
      const xcn = this.getx(this.racks[rackName].x, this.KW, this.PW, this.listC[i].b);
      const ycn = this.gety(this.racks[rackName].y, this.KH, this.PH, this.listC[i].a);
      this.cns[cName] = this.createEquipment(this.graph, '', xcn, ycn, RealisticConfigImg.slotImg, this.racks[rackName]);
      this.cns[cName].size = {width: this.PW, height: this.PH};
      this.cns[cName].set('data', this.listC[i]);
      if (this.listC[i].cType === 1) { // 盘在位
        const pName = 'pn' + this.listC[i].indexN;
        this.pns[pName] = this.createEquipment(this.graph, '', xcn, ycn, RealisticConfigImg.discImg, this.cns[cName]);
        this.pns[pName].size = {width: this.PW, height: this.PH};
        this.pns[pName].set('data', this.listC[i]);
      }
    }
    // // 创建端口
    for (let i = 0; i < this.listD.length; i++) {
      const dName = 'dn' + this.listD[i].indexN;
      const pindexN = this.listD[i].parent;
      const pName = 'pn' + pindexN;
      console.log(this.pns[pName]);
      if (this.pns[pName] === undefined) {
        break;
      }
      const xcn = this.getdx(this.pns[pName].x, this.PW, this.DW, this.listD[i].b);
      const ycn = this.getdy(this.pns[pName].y, this.PH, this.DH, this.listD[i].a);
      if (this.listD[i].dType === 1) { // 成端
        this.dns[dName] = this.createEquipment(this.graph, '', xcn, ycn, RealisticConfigImg.IntoPortImg, this.pns[pName]);
        this.dns[dName].size = {width: this.DW, height: this.DH};
      } else {
        this.dns[dName] = this.createEquipment(this.graph, '', xcn, ycn, RealisticConfigImg.portImg, this.pns[pName]);
        this.dns[dName].size = {width: this.DW, height: this.DH};
      }
      // 设置属性
      this.dns[dName].set('data', this.listD[i]);
    }
    this.graph.addCustomInteraction({
      onstart: e => {
        let target = e.getData();
        if (!target || target.get('data').type === 'B') {
          return;
        }
        // 点击其他的切换成原来的图片
        target = this.dgParent(target);
        console.log(target);
        // 点击的是 槽 或者是盘
        this.creatGraph();
        const rack2 = this.createEquipment(this.graph2, '', 0, 0, RealisticConfigImg.frameImg, null);
        rack2.size = {width: target.size.width * 3, height: target.size.height * 3};
        console.log(target);
        console.log(target._mda.data.cType);
        rack2.set('indexN', target._mda.data.indexN);
        console.log('259', rack2);
        this.rack2 = rack2;
        // 创建槽位和在位盘
        for (let i = 0; i < this.listC.length; i++) {
          if (rack2.get('indexN') === this.listC[i].parent) {
            const cName = 'cn' + this.listC[i].indexN;
            const pindexN = this.listC[i].parent;
            const rackName = 'rack' + pindexN;
            const PW = this.getLong(rack2.size.width, this.data.pan.col);
            const PH = this.getLong(rack2.size.height, this.data.pan.row);
            const xcn = this.getx(0, rack2.size.width, PW, this.listC[i].b);
            const ycn = this.gety(0, rack2.size.height, PH, this.listC[i].a);
            this.cnsR[cName] = this.createEquipment(this.graph2, '', xcn, ycn, RealisticConfigImg.slotImg, this.racksR[rackName]);
            this.cnsR[cName].size = {width: PW, height: PH};
            this.cnsR[cName].set('data', this.listC[i]);
            if (this.listC[i].cType === 1) { // 盘在位
              const pName = 'pn' + this.listC[i].indexN;
              this.pnsR[pName] = this.createEquipment(this.graph2, '', xcn, ycn, RealisticConfigImg.discImg, this.cnsR[cName]);
              this.pnsR[pName].size = {width: PW, height: PH};
              this.pnsR[pName].set('data', this.listC[i]);
            }
          }
        }
        // 创建端口
        for (let i = 0; i < this.listD.length; i++) {
          const dName = 'dn' + this.listD[i].indexN;
          const pindexN = this.listD[i].parent;
          const pName = 'pn' + pindexN;
          const PW = this.getLong(rack2.size.width, this.data.pan.col); // 盘宽
          const PH = this.getLong(rack2.size.height, this.data.pan.row); // 盘高
          const DW = this.getdLong2(PW, this.data.port.col);
          const DH = this.getdLong2(PH, this.data.port.row);
          // console.log('382', this.pnsR[pName])
          if (this.pnsR[pName] === undefined) {
            break;
          }
          const xcn = this.getdx(this.pnsR[pName].x, PW, DW, this.listD[i].b);
          const ycn = this.getdy(this.pnsR[pName].y, PH, DH, this.listD[i].a);
          if (this.listD[i].dType === 1) { // 成端
            this.dnsR[dName] = this.createEquipment(this.graph2, '', xcn, ycn, RealisticConfigImg.IntoPortImg, this.pnsR[pName]);
            this.dnsR[dName].size = {width: DW, height: DH};
          } else {
            this.dnsR[dName] = this.createEquipment(this.graph2, '', xcn, ycn, RealisticConfigImg.portImg, this.pnsR[pName]);
            this.dnsR[dName].size = {width: DW, height: DH};
          }
          // 设置属性
          this.dnsR[dName].set('data', this.listD[i]);
        }
      }
      // }
    });

  }

  creatGraph() {
    if (this.graph2) {
      this.graph2.clear();
    } else {
      this.graph2 = new this.Q.Graph('pic-canvas2');
      this.graph2.translate(20, 20);
      this.graph2.originAtCenter = false;
      this.graph2.isSelectable = (item) => {
        return item.get('type') === 'cabinet';
      };
      // 模拟连线
      const lineNode1 = this.createEquipment(this.graph2, '', 1000, 50, RealisticConfigImg.boxImg, null);
      lineNode1.size = {width: 100, height: 50};
      const lineNode2 = this.createEquipment(this.graph2, '', 1000, 150, RealisticConfigImg.boxImg, null);
      lineNode2.size = {width: 100, height: 50};
      this.graph2.addCustomInteraction({
        onstart: e => {
          const target2 = e.getData();
          if (!target2) {
            return;
          }
          // 点击的是端口
          if (target2 && target2.get('data') && target2.get('data').dType === 0) {
            console.log(1);
            this.graph2.interactionMode = this.Q.Consts.INTERACTION_MODE_CREATE_SIMPLE_EDGE;
            console.log(target2);
            return;
          }
          this.graph2.interactionMode = this.Q.Consts.INTERACTION_MODE_DEFAULT;
          this.Q.Consts.INTERACTION_MODE_DELETE_NODE = 'interaction.mode.delete';

          const index = target2.get('data').indexN;
          target2.get('data').cType === 0 ? target2.get('data').cType = 1 : target2.get('data').cType = 0;
          console.log(index);
          for (let i = 0; i < this.listC.length; i++) {
            if (index === this.listC[i].indexN) {
              const cName = 'cn' + this.listC[i].indexN;
              const pindexN = this.listC[i].parent;
              const rackName = 'rack' + pindexN;
              const PW = this.getLong(this.rack2.size.width, this.data.pan.col);
              const PH = this.getLong(this.rack2.size.height, this.data.pan.row);
              const xcn = this.getx(0, this.rack2.size.width, PW, this.listC[i].b);
              const ycn = this.gety(0, this.rack2.size.height, PH, this.listC[i].a);
              this.cnsR[cName] = this.createEquipment(this.graph2, '', xcn, ycn, RealisticConfigImg.slotImg, this.racksR[rackName]);
              this.cnsR[cName].size = {width: PW, height: PH};
              this.cnsR[cName].set('data', this.listC[i]);
              if (this.listC[i].cType === 1) { // 盘在位
                const pName = 'pn' + i;
                this.pnsR[pName] = this.createEquipment(this.graph2, '', xcn, ycn, RealisticConfigImg.discImg, this.cnsR[cName]);
                this.pnsR[pName].size = {width: PW, height: PH};
                this.pnsR[pName].set('data', this.listC[i]);
              }
            }
          }
          // // 创建端口
          for (let i = 0; i < this.listD.length; i++) {
            if (index === this.listD[i].parent) {
              const dName = 'dn' + this.listD[i].indexN;
              const pindexN = this.listD[i].parent;
              const pName = 'pn' + pindexN;
              const PW = this.getLong(this.rack2.size.width, this.data.pan.col); // 盘宽
              const PH = this.getLong(this.rack2.size.height, this.data.pan.row); // 盘高
              const DW = this.getdLong2(PW, this.data.port.col);
              const DH = this.getdLong2(PH, this.data.port.row);
              // console.log('382', this.pnsR[pName])
              if (this.pnsR[pName] === undefined) {
                break;
              }
              const xcn = this.getdx(this.pnsR[pName].x, PW, DW, this.listD[i].b);
              const ycn = this.getdy(this.pnsR[pName].y, PH, DH, this.listD[i].a);
              if (this.listD[i].dType === 1) { // 成端
                this.cnsR[dName] = this.createEquipment(this.graph2, '', xcn, ycn, RealisticConfigImg.IntoPortImg, this.pnsR[pName]);
                this.cnsR[dName].size = {width: DW, height: DH};
              } else {
                this.cnsR[dName] = this.createEquipment(this.graph2, '', xcn, ycn, RealisticConfigImg.portImg, this.pnsR[pName]);
                this.cnsR[dName].size = {width: DW, height: DH};
              }
            }
          }

        }
      });
    }
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
      this.racks[rackName] = this.createEquipment(this.graph, '', xk, yk, IMG, parent);
      this.racks[rackName].size = {width: W, height: H};
      this.racks[rackName].set('data', nodes[i]);
    }
  }

  dgParent(ev) {
    if (this.select_node) {
      this.select_node.image = RealisticConfigImg.frameImg;
      if (!ev) {
        this.select_node.image = RealisticConfigImg.frameImg;
        return;
      }
    }
    if (ev && ev._mda.data && ev.get('data').kType === 'k') {
      ev.image = RealisticConfigImg.selectFrameImg;
      this.select_node = ev;
      return ev;
    } else {
      const parent = ev.parent;
      return this.dgParent(parent);
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
  createEquipment(graph, name, x, y, image, host) {
    const node = graph.createNode(name, x, y);
    node.anchorPosition = this.Q.Position.LEFT_TOP;
    node.image = image;
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
    const x = X + 0.02 * W + ww * (b - 1);
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
    const y = Y + 0.02 * H + hh * (a - 1);
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
    // w = (W - 2*0.02*W)/num;
    const l = 0.96 * L / num;
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

  setToolTip(target, name) {
    target.tooltip = `
            <p >${name} </p>
        `;
  }

  /**
   * 创建连线
   * @param graph
   * @param from  起始节点
   * @param to    终止节点
   * @param type  模式
   * @param lineWidth 线的宽度
   * @param color     线的颜色
   */
  createEdge(graph, from, to, type, lineWidth, color) {
    const edge = graph.createEdge(from.name + ' --> ' + to.name, from, to);
    edge.setStyle(this.Q.Styles.EDGE_COLOR, color || '#000');
    edge.setStyle(this.Q.Styles.EDGE_WIDTH, lineWidth || 2);
    edge.edgeType = type || this.Q.Consts.EDGE_TYPE_DEFAULT;
    return edge;
  }

  /**
   *web端页面打印公用方法
   * param {string} dom 需要打印的html id名
   */
  print(dom: string) {
    window['Print'](dom, {
      onStart: function () { // 开始打印的事件
        console.log('onStart', new Date());
      },
      onEnd: function () { // 取消打印的事件
        console.log('onEnd', new Date());
      }
    });
  }

}
