{
  title: {
    text: '${title}'
  },
  legend: {
    icon: 'circle',
    bottom: 0
  },
  series: [
    {
      radius: '50%',
      data: ${data},
      type: 'pie',
      avoidLabelOverlap: true,
      emphasis: {
        label: {
          fontSize: '12',
          fontWeight: 'bold'
        }
      },
      itemStyle: {
        normal: {
          label: {
            formatter: '{b}\n{time|{c}  {d}%}',
            lineHeight: 15,
            rich: {
              time: {
                color: '#999'
              }
            }
          }
        }
      },
    }
  ]
};
