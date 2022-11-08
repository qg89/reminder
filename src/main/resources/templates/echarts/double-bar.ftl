{
  tooltip: {
    trigger: 'axis'
  },
  title: {
    text: '${title}'
  },
  legend: {
    show: true,
    bottom: 0,
    icon: 'roundRect'
  },
  xAxis: {
    type: 'category',
    axisLabel: {
      rotate: 45
    },
    data: ${categories}
  },
  yAxis: {
    type: 'value',
    minInterval: 1
  },
  series: [
    {
      name: '${name1}',
      data: ${data1},
      type: 'bar',
      symbolSize: 0,
      itemStyle: {
        normal: {
          label: {
            show: true,
            position: 'inside',
            textStyle: {
              color: 'black',
              fontSize: 12
            }
          }
        }
      }
    },
    {
      name: '${name2}',
      symbolSize: 0,
      data: ${data2},
      type: 'bar',
      itemStyle: {
        normal: {
          label: {
            show: true,
            position: 'inside',
            textStyle: {
              color: 'black',
              fontSize: 12
            }
          }
        }
      }
    }
  ]
}
