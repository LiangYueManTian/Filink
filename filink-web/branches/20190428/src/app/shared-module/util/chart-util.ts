export class ChartUtil {
  /**
   * 环形图配置
   * param data
   * param name
   */
  public static setRingChartOption(data, name) {
    const option =  {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        x: 'left',
        data: name
      },
      series: [
        {
          name: '',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          label: {
            normal: {
              show: false,
              position: 'center'
            },
            emphasis: {
              show: true,
              textStyle: {
                fontSize: '30',
                fontWeight: 'bold'
              }
            }
          },
          labelLine: {
            normal: {
              show: false
            }
          },
          data: data
        }
      ]
    };
    return option;
  }

  /**
   * 扇形图配置
   * param data
   * param name
   */
  public static setPieChartOption(data, name) {
    const option = {
      tooltip : {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      series : [
        {
          name: '',
          type: 'pie',
          radius : '65%',
          center: ['50%', '50%'],
          data: data,
          itemStyle: {
            emphasis: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };
    return option;
  }

  /**
   * 柱状图配置
   * param data
   * param name
   */
  public static setBarChartOption(data, name) {
    const option = {
      color: ['#009edf'],
      xAxis: {
        type: 'category',
        data: name
      },
      grid: {
        left: '13px',
        right: '4%',
        bottom: '11px',
        top: '30px',
        containLabel: true
      },
      tooltip : {
        trigger: 'axis',
      },
      yAxis: {
        type: 'value',
        splitLine: {
          lineStyle: {
            // 使用深浅的间隔色
            color: ['#aaa'],
            type: 'dotted',
            width: 0.5
          }
        }
      },
      series: [{
        data: data,
        type: 'bar',
        barWidth: 20
      }]
    };
    return option;
  }
}
