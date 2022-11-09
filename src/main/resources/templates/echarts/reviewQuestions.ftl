{
    color: ['#d9d9d9', '#00ff00'],
    title: {
        text: '${title}'
    },
    legend:{
        data: ['${name1}', '${name2}']
    },
    xAxis: {
        type: 'category',
        data: ${categories}
    },
    yAxis: {},
    series: [
        {
            name: '${name1}',
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
            name: '${name2}',
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