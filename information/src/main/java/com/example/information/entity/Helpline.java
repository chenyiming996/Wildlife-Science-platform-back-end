package com.example.information.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@TableName("helpline")
public class Helpline {
    private Long id;
    @NotNull(message = "省份不能为空")
    @NotEmpty(message = "省份不能为空")
    private String province;
    @NotNull(message = "邮箱地址不能为空")
    @NotEmpty(message = "邮箱地址不能为空")
    @Email(message = "邮箱格式错误！")
    private String email;
    @NotNull(message = "电话不能为空")
    @NotEmpty(message = "电话不能为空")
    private String phone;
    @NotNull(message = "传真不能为空")
    @NotEmpty(message = "传真不能为空")
    private String fax;
    @NotNull(message = "地址不能为空")
    @NotEmpty(message = "地址不能为空")
    private String address;
    @NotNull(message = "邮编不能为空")
    @NotEmpty(message = "邮编不能为空")
    private String code;

    private String firstIndex;

    @Version
    private int version;

    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;
}
