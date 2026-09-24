package com.wunamor.springtransdemo.model;


import com.wunamor.springtransdemo.core.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LogInfo extends BaseModel {
    private int id;
    private String userName;
    private String op;
}
