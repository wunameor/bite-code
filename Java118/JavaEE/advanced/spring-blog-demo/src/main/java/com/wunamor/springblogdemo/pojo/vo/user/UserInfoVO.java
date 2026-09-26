package com.wunamor.springblogdemo.pojo.vo.user;


import com.wunamor.springblogdemo.pojo.entity.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserInfoVO {
    private Integer id;
    private String userName;
    private String githubUrl;

    public static UserInfoVO create(UserInfo userInfo) {
        UserInfoVO userInfoVO = new UserInfoVO();
        if (userInfo == null) {
            return userInfoVO;
        }

        BeanUtils.copyProperties(userInfo, userInfoVO);
        return userInfoVO;
    }
}
