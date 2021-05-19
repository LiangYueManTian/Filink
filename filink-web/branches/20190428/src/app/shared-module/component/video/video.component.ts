import {Component, OnInit} from '@angular/core';
import Video from 'video.js';
import zh_CN from '../../service/i18N/language/video/zh_CN';
import en from '../../service/i18N/language/video/en';

@Component({
  selector: 'xc-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.scss']
})
export class VideoComponent implements OnInit {
  private video;

  constructor() {
  }

  ngOnInit() {
    console.log(Video);
    Video.addLanguage(zh_CN.local, zh_CN);
    Video.addLanguage(en.local, en);
    const language = localStorage.getItem('localId') || 'zh_CN';
    this.video = new Video('video', {
      controls: true,
      loop: false,
      width: '500px',
      height: '500px',
      language: language,
      controlBar: {
        captionsButton: true,
        chaptersButton: false,
        playbackRateMenuButton: true,
        LiveDisplay: true,
        subtitlesButton: true,
        remainingTimeDisplay: true,
        progressControl: true,
        volumeMenuButton: {
          inline: false,
          vertical: true
        },
        fullscreenToggle: true
      }
    });
    this.video.src('./../../../assets/video/test.mp4');
  }

}
