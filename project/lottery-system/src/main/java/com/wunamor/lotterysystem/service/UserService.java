package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.service.command.EmailCodeCommand;
import com.wunamor.lotterysystem.service.command.UserLoginCommand;
import com.wunamor.lotterysystem.service.command.UserRegisterCommand;
import com.wunamor.lotterysystem.service.dto.UserListDTO;
import com.wunamor.lotterysystem.service.dto.UserLoginDTO;
import com.wunamor.lotterysystem.service.dto.UserRegisterDTO;

import java.util.List;

public interface UserService {
    UserRegisterDTO register(UserRegisterCommand registerCommand);

    /**
     * 登录：根据 command 里的 type 分发到密码登录或邮箱验证码登录
     */
    UserLoginDTO login(UserLoginCommand loginCommand);

    /**
     * 发送邮箱验证码：注册直接用命令里的邮箱，登录则按账号查出邮箱。
     * <p>
     * 登录场景下用户不存在时静默返回，避免该接口被用来枚举账号。
     */
    void sendEmailCode(EmailCodeCommand emailCodeCommand);

    /**
     * 人员列表：按 id 倒序返回 id、姓名、身份
     */
    List<UserListDTO> listUsers();
}
