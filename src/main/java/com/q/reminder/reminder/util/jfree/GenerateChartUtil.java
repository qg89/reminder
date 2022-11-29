package com.q.reminder.reminder.util.jfree;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.log4j.Log4j2;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.jfree.GenerateChartUtil
 * @Description :
 * @date :  2022.11.09 15:01
 */
@Log4j2
public class GenerateChartUtil {

    /**
     * 生成柱状图(返回outputStream)
     *
     * @param chartTitle      图表标题
     * @param legendNameList  图例名称列表
     * @param xAxisNameList   x轴名称列表
     * @param dataList        数据列表
     * @param yAxisTitle      y轴标题
     * @param xAxisTitle      x轴标题
     * @param out             输出流
     * @param width           宽度
     * @param height          高度
     * @param yAxisMinValue   y轴最小值（可以为空）
     * @param yAxisMaxValue   y轴最大值（可以为空）
     * @param legendColorList 图例背景颜色（可以为空）
     * @param barLabelVisible 是否显示柱体标签（可以为空）
     * @param barLabelFormat  柱体标签格式（可以为空）
     * @return
     */
    public static void createBarChart(FileOutputStream out, String chartTitle, List<String> legendNameList, List<String> xAxisNameList
            , List<List<Object>> dataList, String yAxisTitle, String xAxisTitle, int width, int height
            , Double yAxisMinValue, Double yAxisMaxValue, List<Color> legendColorList, Boolean barLabelVisible, String barLabelFormat) throws Exception {
        JFreeChart chart = createBarChart(chartTitle, legendNameList, xAxisNameList, dataList, yAxisTitle, xAxisTitle, yAxisMinValue, yAxisMaxValue, legendColorList, barLabelVisible, barLabelFormat);
        try {
            ChartUtils.writeChartAsJPEG(out, 1.0f, chart, width, height);
        }finally {
            out.flush();
            out.close();
        }
    }

    /**
     * 生成堆叠柱状图(返回outputStream)
     *
     * @param chartTitle     图表标题
     * @param legendNameList 图例名称列表
     * @param xAxisNameList  x轴名称列表
     * @param dataList       数据列表
     * @param yAxisTitle     y轴标题
     * @param xAxisTitle     x轴标题
     * @param out            输出流
     * @param width          宽度
     * @param height         高度
     * @return
     */
    public static void createStackedBarChart(FileOutputStream out, String chartTitle, List<String> legendNameList, List<String> xAxisNameList
            , List<List<Object>> dataList, String yAxisTitle, String xAxisTitle, int width, int height) throws Exception {
        JFreeChart chart = createStackedBarChart(chartTitle, legendNameList, xAxisNameList, dataList, yAxisTitle, xAxisTitle);
        try {
            ChartUtils.writeChartAsJPEG(out, 1.0f, chart, width, height);
        } finally {
            out.flush();
            out.close();
        }
    }

    /**
     * 生成折线图(返回outputStream)
     *
     * @param chartTitle     图表标题
     * @param legendNameList 图例名称列表
     * @param xAxisNameList  x轴名称列表
     * @param dataList       数据列表
     * @param yAxisTitle     y轴标题
     * @param xAxisTitle     x轴标题
     * @param out            输出流
     * @param width          宽度
     * @param height         高度
     * @return
     */
    public static void createLineChart(FileOutputStream out, String chartTitle, List<String> legendNameList, List<String> xAxisNameList
            , List<List<Object>> dataList, String yAxisTitle, String xAxisTitle, int width, int height) throws Exception {
        JFreeChart chart = createLineChart(chartTitle, legendNameList, xAxisNameList, dataList, yAxisTitle, xAxisTitle);
        try {
            ChartUtils.writeChartAsJPEG(out, 1.0f, chart, width, height);
        } finally {
            out.flush();
            out.close();
        }
    }

    /**
     * 生成散点图(返回outputStream)
     *
     * @param chartTitle 图表标题
     * @param dataset    数据集
     * @param yAxisTitle y轴标题
     * @param xAxisTitle x轴标题
     * @param out        输出流
     * @param width      宽度
     * @param height     高度
     * @return
     */
    public static void createScatterPlot(FileOutputStream out, String chartTitle, XYDataset dataset, String yAxisTitle, String xAxisTitle, int width, int height
    ) throws Exception {
        JFreeChart chart = createScatterPlot(chartTitle, dataset, yAxisTitle, xAxisTitle);
        try {
            ChartUtils.writeChartAsJPEG(out, 1.0f, chart, width, height);
        } finally {
            out.flush();
            out.close();
        }
    }

    /**
     * 生成散点图(返回JFreeChart)
     *
     * @param chartTitle 图表标题
     * @param dataset    数据集
     * @param yAxisTitle y轴标题
     * @param xAxisTitle x轴标题
     * @return
     */
    private static JFreeChart createScatterPlot(String chartTitle
            , XYDataset dataset, String yAxisTitle, String xAxisTitle) throws Exception {
        //设置主题，防止中文乱码
        StandardChartTheme theme = JFreeChartUtil.createChartTheme();
        ChartFactory.setChartTheme(theme);
        //创建散点图
        JFreeChart chart = ChartFactory.createScatterPlot(chartTitle, xAxisTitle, yAxisTitle
                , dataset);
        // 设置抗锯齿，防止字体显示不清楚
        chart.setTextAntiAlias(false);
        //散点图渲染
        JFreeChartUtil.setScatterRender(chart.getXYPlot());
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        // 标注位于上侧
        chart.getLegend().setPosition(RectangleEdge.TOP);
        return chart;
    }

    /**
     * 生成折线图(返回JFreeChart)
     *
     * @param chartTitle     图表标题
     * @param legendNameList 图例名称列表
     * @param xAxisNameList  x轴名称列表
     * @param dataList       数据列表
     * @param yAxisTitle     y轴标题
     * @param xAxisTitle     x轴标题
     * @return
     */
    private static JFreeChart createLineChart(String chartTitle, List<String> legendNameList, List<String> xAxisNameList
            , List<List<Object>> dataList, String yAxisTitle, String xAxisTitle) throws Exception {
        //设置主题，防止中文乱码
        StandardChartTheme theme = JFreeChartUtil.createChartTheme();
        ChartFactory.setChartTheme(theme);
        //创建折线图
        JFreeChart chart = ChartFactory.createLineChart(chartTitle, xAxisTitle, yAxisTitle
                , JFreeChartUtil.createDefaultCategoryDataset(legendNameList, xAxisNameList, dataList));
        // 设置抗锯齿，防止字体显示不清楚
        chart.setTextAntiAlias(true);
        TextTitle title = chart.getTitle();
        title.setMargin(15, 0, 0, 0);
        title.setFont(JFreeChartUtil.getDefaultFont(Font.BOLD, 20f));
        chart.setTitle(title);
        // 对折现进行渲染
        CategoryPlot plot = chart.getCategoryPlot();
        JFreeChartUtil.setLineRender(plot, true, true);
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        // 标注位于上侧
        chart.getLegend().setPosition(RectangleEdge.TOP);
        CategoryAxis axis = new IntervalCategoryAxis(1);
        // 设置X轴轴线不显示
        axis.setAxisLineVisible(false);
        // 设置X轴刻度是否显示
        axis.setTickMarksVisible(true);
        axis.setUpperMargin(0);
        axis.setLowerMargin(0);
        axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        axis.setTickLabelFont(JFreeChartUtil.getDefaultFont(Font.BOLD, 15f));
        plot.setDomainAxes(new CategoryAxis[]{axis});
        return chart;
    }

    /**
     * 生成柱堆叠状图(返回JFreeChart)
     *
     * @param chartTitle     图表标题
     * @param legendNameList 图例名称列表
     * @param xAxisNameList  x轴名称列表
     * @param dataList       数据列表
     * @param yAxisTitle     y轴标题
     * @param xAxisTitle     x轴标题
     * @return
     */
    public static JFreeChart createStackedBarChart(String chartTitle, List<String> legendNameList, List<String> xAxisNameList
            , List<List<Object>> dataList, String yAxisTitle, String xAxisTitle) throws Exception {
        //设置主题，防止中文乱码
        StandardChartTheme theme = JFreeChartUtil.createChartTheme();
        ChartFactory.setChartTheme(theme);
        //创建堆叠柱状图
        JFreeChart chart = ChartFactory.createStackedBarChart(chartTitle, xAxisTitle, yAxisTitle, JFreeChartUtil.createDefaultCategoryDataset(legendNameList, xAxisNameList, dataList));
        // 设置抗锯齿，防止字体显示不清楚
        chart.setTextAntiAlias(false);
        CategoryPlot plot = chart.getCategoryPlot();
        // 对柱子进行渲染
        JFreeChartUtil.setBarRenderer(plot, true);
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        // 标注位于上侧
        chart.getLegend().setPosition(RectangleEdge.TOP);
        TextTitle title = chart.getTitle();
        title.setFont(JFreeChartUtil.getDefaultFont(Font.BOLD, 20f));
        title.setMargin(15, 0, 0, 0);
        chart.setTitle(title);
        CategoryAxis axis = new IntervalCategoryAxis(1);
        axis.setAxisLineVisible(false);
        axis.setTickMarksVisible(false);
        axis.setUpperMargin(0);
        axis.setLowerMargin(0);
        axis.setTickLabelFont(JFreeChartUtil.getDefaultFont(Font.BOLD, 15f));
        axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        plot.setDomainAxes(new CategoryAxis[]{axis});
        return chart;
    }

    /**
     * 生成柱状图(返回JFreeChart)
     *
     * @param chartTitle      图表标题
     * @param legendNameList  图例名称列表
     * @param xAxisNameList   x轴名称列表
     * @param dataList        数据列表
     * @param yAxisTitle      y轴标题
     * @param xAxisTitle      x轴标题
     * @param yAxisMinValue   y轴最小值（可以为空）
     * @param yAxisMaxValue   y轴最大值（可以为空）
     * @param legendColorList 图例背景颜色（可以为空）
     * @param barLabelVisible 是否显示柱体标签（可以为空）
     * @param barLabelFormat  柱体标签格式（可以为空）
     * @return
     */
    public static JFreeChart createBarChart(String chartTitle, List<String> legendNameList, List<String> xAxisNameList
            , List<List<Object>> dataList, String yAxisTitle, String xAxisTitle, Double yAxisMinValue
            , Double yAxisMaxValue, List<Color> legendColorList, Boolean barLabelVisible, String barLabelFormat) throws Exception {
        //设置主题，防止中文乱码
        StandardChartTheme theme = JFreeChartUtil.createChartTheme();
        ChartFactory.setChartTheme(theme);
        //创建柱状图
        JFreeChart chart = ChartFactory.createBarChart(chartTitle, xAxisTitle, yAxisTitle
                , JFreeChartUtil.createDefaultCategoryDataset(legendNameList, xAxisNameList, dataList));
        // 设置抗锯齿，防止字体显示不清楚
        chart.setTextAntiAlias(false);
        // 对柱子进行渲染
        JFreeChartUtil.setBarRenderer(chart.getCategoryPlot(), true);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis categoryAxis = plot.getDomainAxis();
        // 最大换行数
        categoryAxis.setMaximumCategoryLabelLines(10);
        //y轴
        ValueAxis valueAxis = chart.getCategoryPlot().getRangeAxis();
        if (yAxisMinValue != null) {
            valueAxis.setLowerBound(yAxisMinValue);
        }
        if (yAxisMaxValue != null) {
            valueAxis.setUpperBound(yAxisMaxValue);
        }
        CategoryItemRenderer customBarRenderer = plot.getRenderer();
        //显示每个柱的数值
        if (barLabelVisible != null) {
            customBarRenderer.setDefaultItemLabelsVisible(barLabelVisible);
            //柱体数值格式
            if (StrUtil.isNotEmpty(barLabelFormat)) {
                customBarRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(barLabelFormat, NumberFormat.getInstance()));
            }
        }
        //设置系列柱体背景颜色
        if (CollectionUtil.isNotEmpty(legendColorList)) {
            for (int i = 0; i < legendNameList.size() && i < legendColorList.size(); i++) {
                Color color = legendColorList.get(i);
                if (color == null) {
                    continue;
                }
                customBarRenderer.setSeriesPaint(i, color);
            }
        }
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        // 标注位于上侧
        chart.getLegend().setPosition(RectangleEdge.TOP);
        return chart;
    }
}
