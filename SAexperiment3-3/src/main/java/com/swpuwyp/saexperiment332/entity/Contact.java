package com.swpuwyp.saexperiment332.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("address_book") // 数据库表名
public class Contact {

    // 通讯录主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id; // 自增的 id 字段

    @TableField("name")
    private String name; // 联系人姓名

    @TableField("phone_num")
    private String phoneNum; // 联系人电话
}
