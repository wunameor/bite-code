package com.wunamor.springtransdemo.model;


import com.wunamor.springtransdemo.core.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class LogInfo extends BaseModel {
    private int id;
    private String userName;
    private String op;

    public LogInfo(String userName, String op) {
        this.userName = userName;
        this.op = op;
    }
}
