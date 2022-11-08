{
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
            data: ${open},
            stack: 'one',
            type: 'bar',
            color: '#00ff00',
            itemStyle: {
                normal: {
                    label: {
                        show: true,
                        position: 'top',
                        textStyle: {
                            color: 'black',
                            fontSize: 18
                        }
                    }
                }
            }
        },
        {
            data: ${close},
            stack: 'one',
            type: 'bar',
            color: '#d9d9d9',
            itemStyle: {
                normal: {
                    label: {
                        show: true,
                        position: 'top',
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