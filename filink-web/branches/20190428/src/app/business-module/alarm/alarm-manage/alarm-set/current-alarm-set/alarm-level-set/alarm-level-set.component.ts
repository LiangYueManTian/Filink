import { Component, OnInit, TemplateRef, ElementRef, ViewChild } from '@angular/core';
import { PageBean } from '../../../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../../../shared-module/entity/tableConfig';
import { Router } from '@angular/router';
import { NzI18nService, NzMessageService } from 'ng-zorro-antd';
import { FormItem } from '../../../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../../../shared-module/component/form/form-opearte.service';
import { AlarmService } from '../../../../../../core-module/api-service/alarm';
import { Result } from '../../../../../../shared-module/entity/result';
import { AlarmLanguageInterface } from '../../../../../../../assets/i18n/alarm/alarm-language.interface';
import { FiLinkModalService } from '../../../../../../shared-module/service/filink-modal/filink-modal.service';
import { FilterCondition, QueryCondition, SortCondition } from '../../../../../../shared-module/entity/queryCondition';
import { AlarmLevelSetConfig } from './config';
import { AlarmStoreService } from '../../../../../../core-module/store/alarm.store.service';
import { CurrAlarmServiceService } from '../../../current-alarm/curr-alarm-service.service';
@Component({
  selector: 'app-alarm-level-set',
  templateUrl: './alarm-level-set.component.html',
  styleUrls: ['./alarm-level-set.component.scss']
})
export class AlarmLevelSetComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryCondition: QueryCondition = new QueryCondition();
  public language: AlarmLanguageInterface;
  alarmId: string = '';
  countValue: number;
  isVisible: boolean = false;
  isPlay: boolean = false;
  player1: any;
  player2: any;
  tableColumnEdit: FormItem[];
  formStatus: FormOperate;
  selectOptions: any[] = [];
  _selectOptions: any[] = [];
  selectedColor;
  _selectedColor;
  alarmColorObj = {};
  alarmName;
  @ViewChild('alarmLevelTemp') alarmLevelTemp: TemplateRef<any>;
  @ViewChild('alarmColorTemp') alarmColorTemp: TemplateRef<any>;
  @ViewChild('musicSwitchTemp') musicSwitchTemp: TemplateRef<any>;
  @ViewChild('playCountTemp') playCountTemp: TemplateRef<any>;
  @ViewChild('selectOptionsTemp') selectOptionsTemp: TemplateRef<any>;
  @ViewChild('isPlayTemp') isPlayTemp;
  constructor(
    private $message: FiLinkModalService,
    private $router: Router,
    private $nzI18n: NzI18nService,
    private $alarmService: AlarmService,
    private $alarmStoreService: AlarmStoreService,
    private $curr: CurrAlarmServiceService,
    private el: ElementRef) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('alarm');
    this.initAlarmColor();
    this.initSelectOptions();
    this.initTableConfig();
    this.initForm();
    this.refreshData();
    // this.getAlarmSound();
    this.player1 = this.el.nativeElement.querySelector('#music');
    this.player2 = this.el.nativeElement.querySelector('#audio');
  }

  pageChange(event) {
    this.refreshData();
  }


  private refreshData() {
    this.tableConfig.isLoading = true;
    this.$alarmService.queryAlarmLevelList(this.queryCondition).subscribe((res: Result) => {
      this.pageBean.Total = res.totalCount;
      this.tableConfig.isLoading = false;
      this._dataSet = res.data;
      this.changeSelectOptions(this._selectedColor);
      if (res.code === 0 && this._dataSet && this._dataSet[0]) {
        this.$alarmStoreService.alarm = this._dataSet.map(item => {
          item.color = this.alarmColorObj[item.alarmLevelColor];
          return item;
        });
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  initAlarmColor() {
    AlarmLevelSetConfig.forEach(item => {
      this.alarmColorObj[item.value] = item;
    });
  }

  private initSelectOptions() {
    this._selectOptions = [];
    AlarmLevelSetConfig.forEach(item => {
      this._selectOptions.push({
        value: item.value,
        label: this.language[item.label],
        color: item.color,
        style: item.style
      });
    });
    this.selectOptions = this._selectOptions;
  }

  private changeSelectOptions(id) {
    const arr = this._dataSet.map(item => item.alarmLevelColor);
    this.selectOptions = this._selectOptions.filter(item => {
      return arr.indexOf(item.value) === -1 || item.value === id;
    });
  }

  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: false,
      showSizeChanger: false,
      scroll: { x: '1000px', y: '600px' },
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 62 },
        {
          title: this.language.alarmLevelCode, key: 'alarmLevelCode', width: 120,
          configurable: true,
          type: 'render',
          searchable: true,
          searchConfig: { type: 'input' },
          renderTemplate: this.alarmLevelTemp,
        },
        {
          title: this.language.alarmLevelColor, key: 'alarmLevelColor', width: 200,
          configurable: true,
          type: 'render',
          renderTemplate: this.alarmColorTemp,
        },
        {
          title: this.language.alarmLevelSound,
          key: 'alarmLevelSound',
          width: 200,
          configurable: true,
          type: 'render',
          searchable: true,
          searchConfig: { type: 'input' },
          renderTemplate: this.musicSwitchTemp
        },
        {
          title: this.language.isPlay, key: 'isPlay', width: 200,
          configurable: true,
          type: 'render',
          renderTemplate: this.isPlayTemp
        },
        {
          title: this.language.playCount, key: 'playCount', width: 200,
          configurable: true
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '', width: 100, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      showPagination: false,
      bordered: false,
      showSearch: false,
      searchReturnType: 'object',
      topButtons: [],
      operation: [
        {
          text: this.language.update,
          className: 'iconfont fiLink-edit',
          handle: (data) => {
            this.alarmId = data.id;
            this._selectedColor = data.alarmLevelColor;
            this.changeSelectOptions(data.alarmLevelColor);
            this.isVisible = true;
            this.selectedColor = this._selectedColor;
            this.$alarmService.queryAlarmLevelById(this.alarmId).subscribe((res: Result) => {
              const alarmData = res.data;
              const counts = res.data.playCount;
              console.log('aaa', counts);
              this.formStatus.resetData(alarmData);
              this.countValue = counts;
            });
          }
        }
      ]
    };
  }


  /**
   * ???????????????????????????????????????
   */
  public initForm() {
    this.tableColumnEdit = [
      {
        label: this.language.alarmLevelCode, key: 'alarmLevelCode',
        type: 'select', require: false, col: 24,
        disabled: true,
        selectInfo: {
          data: [
            { label: this.language.urgentAlarm, value: '1' },
            { label: this.language.mainAlarm, value: '2' },
            { label: this.language.secondaryAlarm, value: '3' },
            { label: this.language.promptAlarm, value: '4' }
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => {

        },
        rule: [], asyncRules: []
      },
      {
        label: this.language.alarmLevelColor, key: 'alarmLevelColor',
        type: 'custom', require: false, col: 24,
        asyncRules: [],
        template: this.selectOptionsTemp,
        rule: [],
      },
      {
        label: this.language.alarmLevelSound, key: 'alarmLevelSound',
        type: 'select', require: false, col: 24,
        selectInfo: {
          data: [
            { label: 'a.mp3', value: 'a.mp3' },
            { label: 'b.mp3', value: 'b.mp3' },
            { label: 'c.mp3', value: 'c.mp3' },
            { label: 'd.mp3', value: 'd.mp3' },
            { label: 'e.mp3', value: 'e.mp3' },
            { label: 'f.mp3', value: 'f.mp3' },
            { label: 'g.mp3', value: 'g.mp3' }
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => {
          const srcPath = 'assets/audio';
          if (b) {
            this.player2.pause();
          } else {
            if (a.alarmLevelSound.value) {
              const muiscPath = `${srcPath}/${a.alarmLevelSound.value}`;
              this.player2.src = muiscPath;
              this.player2.play();
            }
          }
        },
        rule: [], asyncRules: []
      },
      {
        label: this.language.isPlay,
        key: 'isPlay',
        type: 'radio',
        require: false,
        col: 24,
        radioInfo: {
          data: [
            { label: this.language.yes, value: 1 },
            { label: this.language.no, value: 0 },
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, $event, key, formOperate: FormOperate) => {
        },
        rule: [],
        asyncRules: []
      },
      {
        label: this.language.playCount,
        key: 'playCount',
        type: 'custom',
        col: 24,
        initialValue: 3,
        require: false,
        // rule: [],
        rule: [{ required: true }, { min: 0, max: 5, pattern: '^[0-5]\d*$', msg: '???????????????' }],
        asyncRules: [],
        template: this.playCountTemp
      }
    ];
  }


  formInstance(event) {
    this.formStatus = event.instance;
  }

  /**
   *??????
   */
  playMusic(data) {
    if (data && data.alarmLevelSound) {
      const srcPath = 'assets/audio';
      const muiscPath = `${srcPath}/${data.alarmLevelSound}`;
      this.player1.src = muiscPath;
      this.player1.play();
    }
  }

  isDisabled() {
    return !(Number.isInteger(this.countValue));
  }

  /**
   * ???
   */
  plus() {
    if (Number.isInteger(this.countValue)) {
      // ????????????????????? ????????????????????? ?????? '1'
      this.countValue = Number(this.countValue);
      if (this.countValue >= 5) {
        return;
      } else {
        this.countValue = this.countValue + 1;
      }
    } else {
      this.countValue = 1;
    }
  }

  /**
   * ???
   */
  minus() {
    if (Number.isInteger(this.countValue)) {
      this.countValue = Number(this.countValue);
      if (this.countValue <= 1) {
        this.countValue = 1;
      } else {
        this.countValue = this.countValue - 1;
      }
    } else {
      this.countValue = 1;
    }

  }

  onKey(event: any) {
    const valNumber: number = Number(event.key);
    if (valNumber <= 5 && valNumber > 0) {
      this.countValue = valNumber;
    } else {
      this.countValue = 1;
    }
  }

  /**
   * ??????????????????????????????
   */
  editHandle() {
    this.player2.pause();
    this.formStatus.resetControlData('playCount', this.countValue);
    const editData = this.formStatus.getData();
    editData.id = this.alarmId;
    editData.alarmLevelColor = this.selectedColor;
    switch (editData.id) {
      case '1':
        editData.alarmLevelName = this.language.urgent;
        break;
      case '2':
        editData.alarmLevelName = this.language.main;
        break;
      case '3':
        editData.alarmLevelName = this.language.secondary;
        break;
      case '4':
        editData.alarmLevelName = this.language.prompt;
        break;
    }
    this.$alarmService.updateAlarmLevel(editData).subscribe((res: Result) => {
      if (res.code === 0) {
        this.$message.success(res.msg);
        this.isVisible = false;
        this.$curr.sendMessage(1);
        this.refreshData();
      } else if (res.code === 170122) {
        this.$message.info(res.msg);
        this.selectedColor = this._selectedColor;
        this.refreshData();
      } else {
        this.$message.info(res.msg);
      }
    });
  }

  editHandleCancel(): void {
    this.isVisible = false;
    this.player2.pause();
  }


}
