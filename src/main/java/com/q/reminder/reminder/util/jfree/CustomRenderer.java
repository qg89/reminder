package com.q.reminder.reminder.util.jfree;

import lombok.Data;
import org.jfree.chart.renderer.category.BarRenderer;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.jfree.CustomRenderer
 * @Description :
 * @date :  2022.11.10 10:13
 */
@Data
public class CustomRenderer extends BarRenderer implements Serializable {

    @Serial
    private static final long serialVersionUID = 7400737864572851102L;
    private Paint[] colors;
    /**
     * 初始化柱子颜色
     */
    private String[] colorValues = {"#AFD8F8", "#F6BD0F", "#D64646", "#8BBA00", "#FF8E46", "#008E8E"};

    public CustomRenderer() {
        colors = new Paint[colorValues.length];
        for (int i = 0; i < colorValues.length; i++) {
            colors[i] = Color.decode(colorValues[i]);
        }
    }

    /**
     * 每根柱子以初始化的颜色不断轮循
     */
    @Override
    public Paint getItemPaint(int i, int j) {
        return colors[j % colors.length];
    }

}
