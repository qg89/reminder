package com.q.reminder.reminder.util.jfree;

import cn.hutool.core.io.resource.ClassPathResource;
import com.q.reminder.reminder.ReminderApplication;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.boot.system.ApplicationHome;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.jfree.JFreeChartUtil
 * @Description :
 * @date :  2022.11.09 15:02
 */
public class JFreeChartUtil {
    public static String NO_DATA_MSG = "数据加载失败";

    /**
     * 生成主题
     *
     * @return
     */
    public static StandardChartTheme createChartTheme() throws Exception {
        StandardChartTheme theme = new StandardChartTheme("unicode") {
            @Override
            public void apply(JFreeChart chart) {
                chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                super.apply(chart);
            }
        };
        // 标题
        theme.setExtraLargeFont(getDefaultFont(Font.PLAIN, 25f));
        // xy轴
        theme.setLargeFont(getDefaultFont(Font.PLAIN, 20f));
        // X轴
        theme.setRegularFont(getDefaultFont(Font.PLAIN, 20f));
        theme.setSmallFont(getDefaultFont(Font.PLAIN, 10f));
        return theme;
    }

    /**
     * 获取默认字体
     *
     * @param style
     * @param size  字体大小
     * @return
     * @throws Exception
     */
    public static Font getDefaultFont(int style, Float size) throws Exception {
        //获取宋体文件
        ApplicationHome applicationHome = new ApplicationHome(ReminderApplication.class);
        String parent = applicationHome.getSource().getParent();
        //项目打包成jar包所在的根路径
        String configFilePath = parent + "/resources/templates/font/msyh.ttc";
        //文件路径
        File defaultFontFile = new File(configFilePath);
        Font defaultFont = Font.createFont(Font.TRUETYPE_FONT, defaultFontFile);
        defaultFont = defaultFont.deriveFont(style, size);
        return defaultFont;
    }

    /**
     * 创建饼图数据集合
     *
     * @param legendNameList 图例名称列表
     * @param dataList       数据列表
     * @return
     */
    public static DefaultPieDataset createDefaultPieDataset(List<String> legendNameList, List<Object> dataList) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        //图例名称列表或数据列表为空
        if (legendNameList == null || legendNameList.size() <= 0 || dataList == null || dataList.size() <= 0) {
            return dataset;
        }
        for (int i = 0; i < legendNameList.size() && legendNameList.size() == dataList.size(); i++) {
            String value = dataList.get(i).toString();
            dataset.setValue(legendNameList.get(i), Double.valueOf(value));
        }
        return dataset;
    }

    /**
     * 设置饼状图渲染
     */
    public static void setPieRender(Plot plot) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 5, 10));
        PiePlot piePlot = (PiePlot) plot;
        piePlot.setInsets(new RectangleInsets(0, 0, 0, 0));
        piePlot.setCircular(true);// 圆形

        // 简单标签
        piePlot.setLabelGap(0.01);
        piePlot.setInteriorGap(0.05D);
        // 图例形状
        piePlot.setLegendItemShape(new Rectangle(10, 10));
        piePlot.setIgnoreNullValues(true);
        // 去掉标签背景色
        piePlot.setLabelBackgroundPaint(null);
        //去掉图表背景颜色
        piePlot.setBackgroundPaint(null);
        // 去掉阴影
        piePlot.setLabelShadowPaint(null);
        // 去掉边框
        piePlot.setLabelOutlinePaint(null);
        piePlot.setShadowPaint(null);
        // 显示标签数据
        piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} {1} {2}"));
    }

    /**
     * 提供静态方法：获取报表图形1：饼状图
     *
     * @param title 标题
     * @param datas 数据
     * @param url   字体
     */
    public static void createPiePort(String title, Map<String, Double> datas, String url) {
        try {
            // 如果不使用Font,中文将显示不出来
            DefaultPieDataset pds = new DefaultPieDataset();

            // 获取迭代器：
            Set<Map.Entry<String, Double>> set = datas.entrySet();
            Iterator iterator = set.iterator();
            Map.Entry entry = null;
            while (iterator.hasNext()) {
                entry = (Map.Entry) iterator.next();
                pds.setValue(entry.getKey().toString(), Double.parseDouble(entry.getValue().toString()));
            }
            /**
             * 生成一个饼图的图表
             * 分别是:显示图表的标题、需要提供对应图表的DateSet对象、是否显示图例、是否生成贴士以及是否生成URL链接
             */
            JFreeChart chart = ChartFactory.createPieChart(title, pds, true, true, true);
            setPieRender(chart.getPlot());

            //将内存中的图片写到本地硬盘
            ChartUtils.saveChartAsPNG(new File(url), chart, 800, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建类别数据集合(柱形图、折线图)
     *
     * @param legendNameList 图例名称列表
     * @param xAxisNameList  x轴名称列表
     * @param dataList       数据列表
     * @return
     */
    public static DefaultCategoryDataset createDefaultCategoryDataset(List<String> legendNameList, List<String> xAxisNameList, List<List<Object>> dataList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        //图例名称列表、x轴名称列表或数据列表为空
        if (xAxisNameList == null || xAxisNameList.size() <= 0 || legendNameList == null || legendNameList.size() <= 0 || dataList == null || dataList.size() <= 0) {
            return dataset;
        }
        for (int yAxisIndex = 0; yAxisIndex < legendNameList.size() && legendNameList.size() == dataList.size(); yAxisIndex++) {
            String legendName = legendNameList.get(yAxisIndex);
            List<Object> rowList = dataList.get(yAxisIndex);
            //该组数据不存在或该组数据总数不等于x轴数据数量
            if (rowList == null || rowList.size() != xAxisNameList.size()) {
                continue;
            }
            for (int xAxisIndex = 0; xAxisIndex < rowList.size(); xAxisIndex++) {
                String value = rowList.get(xAxisIndex).toString();
                dataset.setValue(Double.parseDouble(value), legendName, xAxisNameList.get(xAxisIndex));
            }
        }
        return dataset;
    }

    /**
     * 设置柱状图渲染
     *
     * @param plot
     * @param isShowDataLabels 显示数据值标记
     */
    public static void setBarRenderer(CategoryPlot plot, boolean isShowDataLabels) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 5, 10));
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        // 设置柱子最大宽度
        renderer.setMaximumBarWidth(0.175);
        //设置图表背景颜色(透明)
        plot.setBackgroundPaint(null);
        //显示数据值标记
        if (isShowDataLabels) {
            renderer.setDefaultItemLabelsVisible(true);
        }
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        //注意：此句很关键，若无此句，那数字的显示会被覆盖，给人数字没有显示出来的问题
        renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));

        setXAixs(plot);
        setYAixs(plot);
    }

    /**
     * 设置折线图样式
     *
     * @param plot
     * @param isShowDataLabels 是否显示数据标签
     * @param isShapesVisible  是否显示数据点
     */
    public static void setLineRender(CategoryPlot plot, boolean isShowDataLabels, boolean isShapesVisible) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 0, 10), false);
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        //设置图表背景颜色(透明)
        plot.setBackgroundPaint(null);
        renderer.setDefaultStroke(new BasicStroke(1.5F));
        //显示数据标签
        if (isShowDataLabels) {
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, NumberFormat.getInstance()));
            // 位置
            renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BOTTOM_CENTER));
        }
        // 数据点绘制形状
        renderer.setDefaultShapesVisible(isShapesVisible);
        setXAixs(plot);
        setYAixs(plot);

    }

    /**
     * 设置散点图样式
     *
     * @param plot
     */
    public static void setScatterRender(XYPlot plot) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 0, 10), false);
        //设置图表背景颜色(透明)
        plot.setBackgroundPaint(null);
        setXAixs(plot);
        setYAixs(plot);
    }

    /**
     * 设置类别图表(CategoryPlot) X坐标轴线条颜色和样式
     *
     * @param plot
     */
    public static void setXAixs(CategoryPlot plot) {
        Color lineColor = new Color(31, 121, 170);
        // X坐标轴颜色
        plot.getDomainAxis().setAxisLinePaint(lineColor);
        // X坐标轴标记|竖线颜色
        plot.getDomainAxis().setTickMarkPaint(lineColor);

    }

    /**
     * 设置图表(XYPlot) X坐标轴线条颜色和样式
     *
     * @param plot
     */
    public static void setXAixs(XYPlot plot) {
        Color lineColor = new Color(31, 121, 170);
        // X坐标轴颜色
        plot.getDomainAxis().setAxisLinePaint(lineColor);
        // X坐标轴标记|竖线颜色
        plot.getDomainAxis().setTickMarkPaint(lineColor);
        // x轴网格线条
        plot.setDomainGridlinePaint(new Color(192, 192, 192));
    }

    /**
     * 设置类别图表(CategoryPlot) Y坐标轴线条颜色和样式 同时防止数据无法显示
     *
     * @param plot
     */
    public static void setYAixs(CategoryPlot plot) {
        Color lineColor = new Color(192, 208, 224);
        ValueAxis axis = plot.getRangeAxis();
        // Y坐标轴颜色
        axis.setAxisLinePaint(lineColor);
        // Y坐标轴标记|竖线颜色
        axis.setTickMarkPaint(lineColor);
        // 隐藏Y刻度
        axis.setAxisLineVisible(false);
        axis.setTickMarksVisible(false);
        // Y轴网格线条
        plot.setRangeGridlinePaint(new Color(192, 192, 192));
        plot.setRangeGridlineStroke(new BasicStroke(1));
        // 设置顶部Y坐标轴间距,防止数据无法显示
        plot.getRangeAxis().setUpperMargin(0.1);
        // 设置底部Y坐标轴间距
        plot.getRangeAxis().setLowerMargin(0.1);

    }

    /**
     * 设置图表(XYPlot) Y坐标轴线条颜色和样式 同时防止数据无法显示
     *
     * @param plot
     */
    public static void setYAixs(XYPlot plot) {
        Color lineColor = new Color(192, 208, 224);
        ValueAxis axis = plot.getRangeAxis();
        // Y坐标轴颜色
        axis.setAxisLinePaint(lineColor);
        // Y坐标轴标记|竖线颜色
        axis.setTickMarkPaint(lineColor);
        // 隐藏Y刻度
        axis.setAxisLineVisible(false);
        axis.setTickMarksVisible(false);
        // Y轴网格线条
        plot.setRangeGridlinePaint(new Color(192, 192, 192));
        // 设置顶部Y坐标轴间距,防止数据无法显示
        plot.getRangeAxis().setUpperMargin(0.1);
        // 设置底部Y坐标轴间距
        plot.getRangeAxis().setLowerMargin(0.1);
    }
}
