package com.wunamor.lotterysystem.controller;


import com.wunamor.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ControllerException;
import com.wunamor.lotterysystem.controller.request.UserRegisterRequest;
import com.wunamor.lotterysystem.controller.response.UserRegisterResponse;
import com.wunamor.lotterysystem.service.UserService;
import com.wunamor.lotterysystem.service.dto.UserRegisterDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserRegisterResponse register(@Validated @RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new ControllerException(ControllerErrorCodeConstants.REGISTER_ERROR);
        }

        log.info("用户注册，userName: {}, identity: {}", userRegisterRequest.getUserName(),
                userRegisterRequest.getIdentity());
        UserRegisterDTO register = userService.register(userRegisterRequest);
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse();
        userRegisterResponse.setUserId(register.getUserId());
        return userRegisterResponse;
    }
}
