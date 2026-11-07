package com.wunamor.lotterysystem.mapper.dataobject;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wunamor.lotterysystem.mapper.handler.EncryptTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "user", autoResultMap = true)
public class UserDO extends BaseDO {
    private String userName;
    private String email;

    @TableField(value = "phone_number", typeHandler = EncryptTypeHandler.class)
    private Encrypt phoneNumber;
    private String password;
    private Integer identity;
}
