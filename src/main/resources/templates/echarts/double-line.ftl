{
  tooltip: {
    trigger: 'axis',
    alwaysShowContent: true
  },
  title: {
    text: '${title}'
  },
  legend: {
    show: true,
    icon: 'roundRect'
  },
  xAxis: {
    type: 'category',
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
      type: 'line',
      smooth: true,
      symbolSize: 0,
      itemStyle: {
        normal: {
          label: {
            show: true,
            position: 'top',
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
      smooth: true,
      type: 'line',
      itemStyle: {
        normal: {
          label: {
            show: true,
            position: 'top',
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
