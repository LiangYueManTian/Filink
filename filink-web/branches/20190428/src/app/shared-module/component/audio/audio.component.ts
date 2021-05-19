import { Component, OnInit, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-audio',
  templateUrl: './audio.component.html',
  styleUrls: ['./audio.component.scss']
})
export class AudioComponent implements OnInit {

  start;
  times = 1;
  audioElem;
  constructor() { }
  @Output() stopAudio = new EventEmitter();
  /**
   * 告警播放音乐
   */
  alarmMusic(data?) {
    // if (data.channelKey === 'alarm') {
    this.audioElem = document.querySelector('#audio');
    this.start = 0;
    if (data.msg.playCount) {
      this.times = data.msg.playCount;
    }
    if (data.msg.prompt) {
      this.audioElem.src = `assets/audio/${data.msg.prompt}`;
      // this.elem.addEventListener('ended', this.playFunction());
      this.audioElem.play();
      // setTimeout(() => {
      //   this.audioElem.src = ``;
      // }, 0);
    }

    // }

  }

  /**
   * 告警播放音乐 结束时
   */
  audioEnded() {
    this.start++;
    if (this.start < this.times) {
      this.audioElem.play();
    } else {
      this.stopAudio.emit();
    }
  }
  ngOnInit() { }

}
