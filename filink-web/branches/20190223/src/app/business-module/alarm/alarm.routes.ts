import { Routes } from '@angular/router';
import { AlarmComponent } from './alarm.component';
import { CurrentAlarmComponent } from './alarm-manage/current-alarm/current-alarm.component';
import { HistoryAlarmComponent } from './alarm-manage/history-alarm/history-alarm.component';
// import { AlarmSetComponent } from './alarm-manage/alarm-set/alarm-set.component';
import { CurrentAlarmSetComponent } from './alarm-manage/alarm-set/current-alarm-set/current-alarm-set.component';
import { HistoryAlarmSetComponent } from './alarm-manage/alarm-set/history-alarm-set/history-alarm-set.component';
import { AlarmLevelSetComponent } from './alarm-manage/alarm-set/current-alarm-set/alarm-level-set/alarm-level-set.component';

export const ROUTER_CONFIG: Routes = [
    {
        path: '',
        component: AlarmComponent,
        data: {
            breadcrumb: '告警管理'
        },
        children: [
            {
                path: 'current-alarm',
                component: CurrentAlarmComponent,
                data: {
                    breadcrumb: [{ label: 'alarm', url: 'current-alarm' }, { label: 'currentalarm' }]
                }
            },
            {
                path: 'history-alarm',
                component: HistoryAlarmComponent,
                data: {
                    breadcrumb: [{ label: 'alarm', url: 'history-alarm' }, { label: 'historyalarm' }]

                }
            },
            // {
            //     path: 'alarm-set',
            //     component: AlarmSetComponent,
            //     data: {
            //         breadcrumb: [{ label: 'alarm', url: 'alarm-set' }, { label: 'alarmset' }]

            //     }
            // },
            {
                path: 'current-alarm-set',
                component: CurrentAlarmSetComponent,
                data: {
                    breadcrumb: [
                        { label: 'alarm' },
                        { label: 'alarmset' },
                        { label: 'currentAlarmSet' }
                    ]

                }
            },
            {
                path: 'history-alarm-set',
                component: HistoryAlarmSetComponent,
                data: {
                    breadcrumb: [{ label: 'alarm' },
                    { label: 'alarmset' },
                    { label: 'historyAlarmSet' }
                    ]

                }
            },
            {
                path: 'alarm-level-set',
                component: AlarmLevelSetComponent,
                data: {
                    breadcrumb: [{ label: 'alarm' }, { label: 'alarmset' },
                    { label: 'currentAlarmSet', url: 'current-alarm-set' }, { label: 'alarmLevelSet' }
                    ]

                }
            }

        ]
    }
];
