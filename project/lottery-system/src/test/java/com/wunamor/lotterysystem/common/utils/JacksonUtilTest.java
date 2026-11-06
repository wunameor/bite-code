package com.wunamor.lotterysystem.common.utils;


import com.wunamor.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.wunamor.lotterysystem.common.errorcode.ErrorCode;
import com.wunamor.lotterysystem.common.errorcode.GlobalErrorCodeConstants;
import com.wunamor.lotterysystem.common.pojo.CommonResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class JacksonUtilTest {

    @Test
    void testValue() {
        CommonResult<String> result = CommonResult.success("123");
        System.out.println(JacksonUtil.writeObjectAsString(result));
    }

    @Test
    void testList() {
        List<CommonResult<String>> list = List.of(
                CommonResult.success("123"),
                CommonResult.error(GlobalErrorCodeConstants.ERROR)
        );
        String str = JacksonUtil.writeObjectAsString(list);
        System.out.println(str);

        System.out.println(JacksonUtil.readListValue(str, CommonResult.class));
    }
}
