package com.wunamor.lotterysystem.mapper.dataobject;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Encrypt {
    private String value;

    public Encrypt(String value) {
        this.value = value;
    }
}
