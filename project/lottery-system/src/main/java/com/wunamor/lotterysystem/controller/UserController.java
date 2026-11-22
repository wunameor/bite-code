package com.wunamor.lotterysystem.controller;

import com.wunamor.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ControllerException;
import com.wunamor.lotterysystem.common.pojo.CommonResult;
import com.wunamor.lotterysystem.controller.request.EmailCodeRequest;
import com.wunamor.lotterysystem.controller.request.UserLoginRequest;
import com.wunamor.lotterysystem.controller.request.UserProfileUpdateRequest;
import com.wunamor.lotterysystem.controller.request.UserRegisterRequest;
import com.wunamor.lotterysystem.controller.request.UserResetPasswordRequest;
import com.wunamor.lotterysystem.controller.response.UserListResponse;
import com.wunamor.lotterysystem.controller.response.UserLoginResponse;
import com.wunamor.lotterysystem.controller.response.UserProfileResponse;
import com.wunamor.lotterysystem.controller.response.UserRegisterResponse;
import com.wunamor.lotterysystem.service.UserService;
import com.wunamor.lotterysystem.service.command.EmailCodeCommand;
import com.wunamor.lotterysystem.service.command.UserLoginCommand;
import com.wunamor.lotterysystem.service.command.UserProfileUpdateCommand;
import com.wunamor.lotterysystem.service.command.UserRegisterCommand;
import com.wunamor.lotterysystem.service.command.UserResetPasswordCommand;
import com.wunamor.lotterysystem.service.dto.UserListDTO;
import com.wunamor.lotterysystem.service.dto.UserLoginDTO;
import com.wunamor.lotterysystem.service.dto.UserProfileDTO;
import com.wunamor.lotterysystem.service.dto.UserRegisterDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取邮箱验证码：注册 / 登录 / 重置密码 / 修改资料共用一个接口，用 scene 区分
     * <ul>
     *     <li>REGISTER：account 必须是邮箱，验证码直接发到该地址</li>
     *     <li>LOGIN / RESET_PASSWORD：account 可以是用户名或邮箱，后端查出邮箱再发</li>
     *     <li>UPDATE_PROFILE：account 必须是待绑定的新邮箱</li>
     * </ul>
     */
    @PostMapping("/email-code")
    public CommonResult<Void> sendEmailCode(@Validated @RequestBody EmailCodeRequest emailCodeRequest) {
        log.info("发送验证码，scene: {}, account: {}",
                emailCodeRequest.getScene(), emailCodeRequest.getAccount());
        EmailCodeCommand command = new EmailCodeCommand();
        BeanUtils.copyProperties(emailCodeRequest, command);
        userService.sendEmailCode(command);
        return CommonResult.success(null);
    }

    @PostMapping("/register")
    public UserRegisterResponse register(@Validated @RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new ControllerException(ControllerErrorCodeConstants.REGISTER_ERROR);
        }

        log.info("用户注册，userName: {}, identity: {}", userRegisterRequest.getUserName(),
                userRegisterRequest.getIdentity());
        UserRegisterCommand command = new UserRegisterCommand();
        BeanUtils.copyProperties(userRegisterRequest, command);
        UserRegisterDTO register = userService.register(command);
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse();
        userRegisterResponse.setUserId(register.getUserId());
        return userRegisterResponse;
    }

    /**
     * 登录：密码登录和邮箱验证码登录共用一个接口，请求体用 type 区分
     */
    @PostMapping("/login")
    public UserLoginResponse login(@Validated @RequestBody UserLoginRequest userLoginRequest) {
        log.info("用户登录，account: {}, type: {}",
                userLoginRequest.getAccount(), userLoginRequest.getType());
        UserLoginCommand command = new UserLoginCommand();
        BeanUtils.copyProperties(userLoginRequest, command);
        UserLoginDTO login = userService.login(command);

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        BeanUtils.copyProperties(login, userLoginResponse);
        return userLoginResponse;
    }

    /**
     * 重置密码：通过重置密码场景的邮箱验证码设置新密码。
     *
     * @param request 账号、验证码和新密码
     * @return 空业务数据，成功只表示密码已更新
     */
    @PostMapping("/reset-password")
    public CommonResult<Void> resetPassword(@Validated @RequestBody UserResetPasswordRequest request) {
        log.info("重置密码，account: {}", request.getAccount());
        UserResetPasswordCommand command = new UserResetPasswordCommand();
        BeanUtils.copyProperties(request, command);
        userService.resetPassword(command);
        return CommonResult.success(null);
    }

    /**
     * 人员列表：仅管理员可访问，按 id 倒序
     */
    @GetMapping("/list")
    public List<UserListResponse> listUsers() {
        List<UserListDTO> users = userService.listUsers();
        return users.stream().map(user -> {
            UserListResponse response = new UserListResponse();
            BeanUtils.copyProperties(user, response);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 查询当前登录用户资料；邮箱和手机号返回脱敏值。
     *
     * @param userId 当前登录用户 id，由登录拦截器从 JWT 写入 request
     * @return 当前用户资料
     */
    @GetMapping("/profile")
    public UserProfileResponse getProfile(@RequestAttribute("userId") Long userId) {
        UserProfileDTO profile = userService.getUserProfile(userId);
        UserProfileResponse response = new UserProfileResponse();
        BeanUtils.copyProperties(profile, response);
        return response;
    }

    /**
     * 修改当前登录用户资料。
     *
     * @param userId  当前登录用户 id，由登录拦截器从 JWT 写入 request
     * @param request 资料修改请求
     * @return 修改后的用户资料，便于前端直接刷新回显
     */
    @PutMapping("/profile")
    public UserProfileResponse updateProfile(
            @RequestAttribute("userId") Long userId,
            @Validated @RequestBody UserProfileUpdateRequest request) {
        UserProfileUpdateCommand command = new UserProfileUpdateCommand();
        BeanUtils.copyProperties(request, command);
        command.setUserId(userId);
        userService.updateUserProfile(command);

        UserProfileDTO profile = userService.getUserProfile(userId);
        UserProfileResponse response = new UserProfileResponse();
        BeanUtils.copyProperties(profile, response);
        return response;
    }
}
