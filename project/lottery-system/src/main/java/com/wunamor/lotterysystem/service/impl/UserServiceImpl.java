package com.wunamor.lotterysystem.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.wunamor.lotterysystem.common.enums.EmailSceneEnum;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.utils.JWTUtil;
import com.wunamor.lotterysystem.common.utils.RegexUtil;
import com.wunamor.lotterysystem.mapper.UserMapper;
import com.wunamor.lotterysystem.mapper.dataobject.Encrypt;
import com.wunamor.lotterysystem.mapper.dataobject.UserDO;
import com.wunamor.lotterysystem.service.EmailCodeService;
import com.wunamor.lotterysystem.service.UserService;
import com.wunamor.lotterysystem.service.command.EmailCodeCommand;
import com.wunamor.lotterysystem.service.command.UserLoginCommand;
import com.wunamor.lotterysystem.service.command.UserRegisterCommand;
import com.wunamor.lotterysystem.service.dto.UserLoginDTO;
import com.wunamor.lotterysystem.service.dto.UserRegisterDTO;
import com.wunamor.lotterysystem.service.enums.UserIdentityEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailCodeService emailCodeService;

    @Override
    public UserRegisterDTO register(UserRegisterCommand registerCommand) {
        // 邮箱统一小写，避免 Redis key / 唯一索引因大小写产生分歧
        if (registerCommand != null) {
            registerCommand.setEmail(normalizeEmail(registerCommand.getEmail()));
        }
        // 检查一下参数
        checkRegisterInfo(registerCommand);
        // 校验邮箱验证码：通过后立即失效，防止一码多用
        emailCodeService.verifyRegisterCode(registerCommand.getEmail(), registerCommand.getEmailCode());
        // 执行注册的业务
        // 1. 先把内容给转换一下
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(registerCommand, userDO);
        // 还需要处理一下密码
        if (StringUtils.hasText(registerCommand.getPassword())) {
            userDO.setPassword(DigestUtil.sha256Hex(registerCommand.getPassword()));
        }
        userDO.setPhoneNumber(new Encrypt(registerCommand.getPhoneNumber()));
        // 2. 插入到数据库中
        userMapper.insert(userDO);

        // 3. 返回相关响应
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUserId(userDO.getId());
        return userRegisterDTO;
    }

    @Override
    public UserLoginDTO login(UserLoginCommand loginCommand) {
        if (loginCommand == null) {
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_INFO_IS_EMPTY);
        }
        if (loginCommand.getType() == null) {
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_TYPE_ERROR);
        }
        if (!StringUtils.hasText(loginCommand.getAccount())) {
            throw new ServiceException(ServiceErrorCodeConstants.ACCOUNT_IS_EMPTY);
        }
        // 账号归一化：邮箱小写、去掉首尾空格
        loginCommand.setAccount(normalizeAccount(loginCommand.getAccount()));
        // 枚举穷尽：以后新增登录方式，这里不补分支编译就会报错
        return switch (loginCommand.getType()) {
            case PASSWORD -> loginByPassword(loginCommand);
            case EMAIL_CODE -> loginByEmailCode(loginCommand);
        };
    }

    /**
     * 用户名 + 密码登录
     */
    private UserLoginDTO loginByPassword(UserLoginCommand loginCommand) {
        if (!StringUtils.hasText(loginCommand.getPassword())) {
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_PASSWORD_IS_EMPTY);
        }

        UserDO userDO = getUserByAccount(loginCommand.getAccount());
        // 用户不存在和密码错误返回同一文案，避免被用来枚举账号
        if (userDO == null) {
            log.warn("密码登录失败，账号不存在，account: {}", loginCommand.getAccount());
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_ACCOUNT_OR_PASSWORD_ERROR);
        }

        String encrypted = DigestUtil.sha256Hex(loginCommand.getPassword());
        if (!encrypted.equals(userDO.getPassword())) {
            log.warn("密码登录失败，密码错误，account: {}", loginCommand.getAccount());
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_ACCOUNT_OR_PASSWORD_ERROR);
        }

        return buildLoginDTO(userDO);
    }

    /**
     * 用户名 + 邮箱验证码登录
     */
    private UserLoginDTO loginByEmailCode(UserLoginCommand loginCommand) {
        if (!StringUtils.hasText(loginCommand.getEmailCode())) {
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_EMAIL_CODE_IS_EMPTY);
        }

        UserDO userDO = getUserByAccount(loginCommand.getAccount());
        if (userDO == null) {
            log.warn("验证码登录失败，账号不存在，account: {}", loginCommand.getAccount());
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_ACCOUNT_OR_CODE_ERROR);
        }

        // 用登录场景的验证码校验，和注册码互不影响；校验通过即失效
        emailCodeService.verifyCode(userDO.getEmail(), loginCommand.getEmailCode(), EmailSceneEnum.LOGIN);

        return buildLoginDTO(userDO);
    }

    @Override
    public void sendEmailCode(EmailCodeCommand emailCodeCommand) {
        if (!StringUtils.hasText(emailCodeCommand.getAccount())) {
            throw new ServiceException(ServiceErrorCodeConstants.ACCOUNT_IS_EMPTY);
        }
        String account = normalizeAccount(emailCodeCommand.getAccount());

        switch (emailCodeCommand.getScene()) {
            case REGISTER -> {
                // 注册场景没有用户记录，account 必须是邮箱本身
                if (!RegexUtil.checkMail(account)) {
                    throw new ServiceException(ServiceErrorCodeConstants.REGISTER_MAIL_PATTERN_ERROR);
                }
                emailCodeService.sendCode(account, EmailSceneEnum.REGISTER);
            }
            case LOGIN, RESET_PASSWORD -> {
                // 登录 / 重置密码：先按账号查到用户，再发到用户登记的邮箱
                UserDO userDO = getUserByAccount(account);
                if (userDO == null) {
                    // 静默返回：不暴露账号是否存在，避免被用来枚举用户
                    log.warn("发送{}验证码跳过，账号不存在，account: {}",
                            emailCodeCommand.getScene().getDesc(), account);
                    return;
                }
                emailCodeService.sendCode(userDO.getEmail(), emailCodeCommand.getScene());
            }
        }
    }

    /**
     * 账号可能是用户名，也可能是邮箱：含 @ 按邮箱查，否则按用户名查。
     * <p>
     * 注册时已禁止用户名包含 @，两个命名空间不会重叠，这里不会出现歧义。
     */
    private UserDO getUserByAccount(String account) {
        return account.contains("@")
                ? userMapper.getUserByEmail(account)
                : userMapper.getUserByUserName(account);
    }

    /**
     * 账号归一化：去掉首尾空格；邮箱转小写（用户名保持原样，由数据库的 ci 排序规则匹配）
     */
    private String normalizeAccount(String account) {
        if (!StringUtils.hasText(account)) {
            return account;
        }
        String trimmed = account.trim();
        return trimmed.contains("@") ? trimmed.toLowerCase() : trimmed;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private UserLoginDTO buildLoginDTO(UserDO userDO) {
        Map<String, Object> claim = new HashMap<>();
        claim.put("userId", userDO.getId());
        claim.put("identity", userDO.getIdentity());

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setToken(JWTUtil.genJwt(claim));
        userLoginDTO.setUserId(userDO.getId());
        userLoginDTO.setUserName(userDO.getUserName());
        userLoginDTO.setIdentity(userDO.getIdentity());
        return userLoginDTO;
    }

    private void checkRegisterInfo(UserRegisterCommand registerCommand) {
        if (registerCommand == null) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_INFO_IS_EMPTY);
        }

        // 邮箱格式
        if (!RegexUtil.checkMail(registerCommand.getEmail())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_MAIL_PATTERN_ERROR);
        }

        // 手机号格式
        if (!RegexUtil.checkMobile(registerCommand.getPhoneNumber())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PHONE_PATTERN_ERROR);
        }

        // 身份信息验证
        if (UserIdentityEnum.getValueById(registerCommand.getIdentity()) == null) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_IDENTITY_PATTERN_ERROR);
        }

        // 管理员密码不能为空
        if (UserIdentityEnum.ADMIN.getId() == registerCommand.getIdentity() && registerCommand.getPassword().isEmpty()) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PASSWORD_IS_EMPTY);
        }

        // 密码强度验证
        if (!RegexUtil.checkPassword(registerCommand.getPassword())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PASSWORD_PATTERN_ERROR);
        }

        // 用户名不能包含 @：登录时靠 @ 区分“邮箱/用户名”，必须保证两个命名空间不重叠
        if (registerCommand.getUserName().contains("@")) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_USER_NAME_CONTAINS_AT);
        }

        // 用户名是否被使用
        checkUserNameIsUsed(registerCommand.getUserName());

        // 邮箱是否被使用
        checkEmailIsUsed(registerCommand.getEmail());

        // 电话是否被使用
        checkPhoneIsUsed(registerCommand.getPhoneNumber());
    }

    private void checkUserNameIsUsed(String userName) {
        Integer userNameCount = userMapper.getUserCountByUserName(userName);
        if (userNameCount == null || userNameCount >= 1) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_USER_NAME_USED);
        }
    }

    private void checkPhoneIsUsed(@NotBlank(message = "手机号不能为空") String phoneNumber) {
        Integer phoneCount = userMapper.getUserCountByPhone(new Encrypt(phoneNumber));
        if (phoneCount == null || phoneCount >= 1) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PHONE_USED);
        }
    }

    private void checkEmailIsUsed(@NotBlank(message = "邮箱不能为空") String email) {
        Integer mailCount = userMapper.getUserCountByEmail(email);
        if (mailCount == null || mailCount >= 1) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_MAIL_USED);
        }
    }
}
