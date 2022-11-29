package com.q.reminder.reminder.util.jfree;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.log4j.Log4j2;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.Rotation;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.jfree.GeneratePieChartUtil
 * @Description :
 * @date :  2022.11.09 15:37
 */
@Log4j2
public class GeneratePieChartUtil {
    /**
     * 生成饼图(返回JFreeChart)
     *
     * @param chartTitle         图表标题
     * @param legendNameList     图例名称列表
     * @param dataList           数据列表
     * @param legendColorList    图例背景颜色列表（为空，使用默认背景颜色）
     * @return
     */
    public static JFreeChart createPieChart(String chartTitle, List<String> legendNameList, List<Object> dataList, List<Color> legendColorList) throws Exception {
        //设置主题，防止中文乱码
        ChartFactory.setChartTheme(JFreeChartUtil.createChartTheme());
        //创建饼图
        JFreeChart chart = ChartFactory.createPieChart(chartTitle, JFreeChartUtil.createDefaultPieDataset(legendNameList, dataList));
        TextTitle title = chart.getTitle();
        title.setMargin(15,0,0,0);
        title.setFont(JFreeChartUtil.getDefaultFont(Font.BOLD, 20f));
        chart.setTitle(title);
        // 设置抗锯齿，防止字体显示不清楚
        chart.setTextAntiAlias(false);
        PiePlot piePlot = (PiePlot) chart.getPlot();
        //边框线为白色
        piePlot.setOutlinePaint(Color.WHITE);
        //连接线类型为直线
        piePlot.setLabelLinkStyle(PieLabelLinkStyle.QUAD_CURVE);
        // 对饼图进行渲染
        JFreeChartUtil.setPieRender(chart.getPlot());
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        // 标注位于右侧
        chart.getLegend().setPosition(RectangleEdge.BOTTOM);

        //设置图例背景颜色（饼图）
        if (CollectionUtil.isNotEmpty(legendColorList)) {
            for (int i = 0; i < legendNameList.size() && i < legendColorList.size(); i++) {
                Color color = legendColorList.get(i);
                if (color == null) {
                    continue;
                }
                piePlot.setSectionPaint(legendNameList.get(i), color);
            }
        }
        //设置偏离百分比
//        if (CollectionUtil.isNotEmpty(explodePercentList)) {
//            for (int i = 0; i < legendNameList.size() && i < explodePercentList.size(); i++) {
//                piePlot.setExplodePercent(legendNameList.get(i), explodePercentList.get(i));
//            }
//        }
        return chart;
    }

    /**
     * 生成饼图(返回outputStream)
     *
     * @param outputStream       输出流
     * @param chartTitle         图表标题
     * @param legendNameList     图例名称列表
     * @param dataList           数据列表
     * @param width              宽度
     * @param height             高度
     * @param legendColorList    图例背景颜色列表（为空，使用默认背景颜色）
     * @return
     */
    public static void createPieChart(OutputStream outputStream, String chartTitle, List<String> legendNameList, List<Object> dataList
            , int width, int height, List<Color> legendColorList) throws Exception {
        JFreeChart chart = createPieChart(chartTitle, legendNameList, dataList, legendColorList);
        try {
            ChartUtils.writeChartAsJPEG(outputStream, 1.0f, chart, width, height, null);
        } catch (IOException e) {
            log.error(e);
        }
    }
}
