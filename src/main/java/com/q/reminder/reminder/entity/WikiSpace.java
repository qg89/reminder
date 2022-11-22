package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.WikiSpace
 * @Description :
 * @date :  2022.11.18 15:20
 */
@TableName("f_wiki_space")
@Data
public class WikiSpace implements Serializable {

    @Serial
    private static final long serialVersionUID = -24731796539589981L;

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("p_id")
    private Integer pId;

    @TableField("week_num")
    private Integer weekNum;

    /**
     * 知识空间id;[获取方式](https://open.feishu.cn/document/ukTMukTMukTM/uUDN04SN0QjL1QDN/wiki-overview)
     * <p> 示例值：6946843325487912356
     */
    @TableField("space_id")
    private String spaceId;
    /**
     * 节点token
     * <p> 示例值：wikcnKQ1k3p******8Vabcef
     */
    @TableField("node_token")
    private String nodeToken;
    /**
     * 对应文档类型的token，可根据 obj_type 判断属于哪种文档类型。
     * <p> 示例值：doccnzAaOD******Wabcdef
     */
    @TableField("obj_token")
    private String objToken;
    /**
     * 文档类型，对于快捷方式，该字段是对应的实体的obj_type。
     * <p> 示例值：doc
     */
    @TableField("obj_type")
    private String objType;
    /**
     * 父节点 token。若当前节点为一级节点，父节点 token 为空。
     * <p> 示例值：wikcnKQ1k3p******8Vabcef
     */
    @TableField("parent_node_token")
    private String parentNodeToken;
    /**
     * 节点类型
     * <p> 示例值：origin
     */
    @TableField("node_type")
    private String nodeType;
    /**
     * 快捷方式对应的实体node_token，当节点为快捷方式时，该值不为空。
     * <p> 示例值：wikcnKQ1k3p******8Vabcef
     */
    @TableField("origin_node_token")
    private String originNodeToken;
    /**
     * 快捷方式对应的实体所在的space id
     * <p> 示例值：6946843325487912356
     */
    @TableField("origin_space_id")
    private String originSpaceId;
    /**
     * 是否有子节点
     * <p> 示例值：false
     */
    @TableField("has_child")
    private Boolean hasChild;
    /**
     * 文档标题
     * <p> 示例值：标题
     */
    @TableField("title")
    private String title;
    /**
     * 文档创建时间
     * <p> 示例值：1642402428
     */
    @TableField("obj_create_time")
    private String objCreateTime;
    /**
     * 文档最近编辑时间
     * <p> 示例值：1642402428
     */
    @TableField("obj_edit_time")
    private String objEditTime;
    /**
     * 节点创建时间
     * <p> 示例值：1642402428
     */
    @TableField("node_create_time")
    private String nodeCreateTime;
    /**
     * 节点创建者
     * <p> 示例值：ou_xxxxx
     */
    @TableField("creator")
    private String creator;
    /**
     * 节点所有者
     * <p> 示例值：ou_xxxxx
     */
    @TableField("owner")
    private String owner;
}
