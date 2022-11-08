{
    color: ['#d9d9d9', '#00ff00'],
    title: {
        text: '${title}',
        x:'middle',
        textAlign:'center'
    },
    xAxis: {
        type: 'category',
        data: ${categories}
    },
    yAxis: {},
    series: [
        {
            data: ${close},
            stack: 'one',
            type: 'bar',
            itemStyle: {
                normal: {
                    label: {
                        show: true,
                        position: 'inside',
                        textStyle: {
                            color: 'black',
                            fontSize: 18
                        }
                    }
                }
            }
        },
        {
            data: ${open},
            stack: 'one',
            type: 'bar',
            itemStyle: {
                normal: {
                    label: {
                        show: true,
                        position: 'inside',
                        textStyle: {
                            color: 'black',
                            fontSize: 18
                        }
                    }
                }
            }
        }
    ]
}