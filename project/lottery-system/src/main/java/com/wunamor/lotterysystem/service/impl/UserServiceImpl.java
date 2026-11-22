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
import com.wunamor.lotterysystem.service.command.UserProfileUpdateCommand;
import com.wunamor.lotterysystem.service.command.UserRegisterCommand;
import com.wunamor.lotterysystem.service.command.UserResetPasswordCommand;
import com.wunamor.lotterysystem.service.dto.UserListDTO;
import com.wunamor.lotterysystem.service.dto.UserLoginDTO;
import com.wunamor.lotterysystem.service.dto.UserProfileDTO;
import com.wunamor.lotterysystem.service.dto.UserRegisterDTO;
import com.wunamor.lotterysystem.service.enums.UserIdentityEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        } else {
            // 普通用户可以不设密码，落库为 NULL 而不是空串
            userDO.setPassword(null);
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
            case UPDATE_PROFILE -> {
                // 修改资料：account 是用户填写的新邮箱，需要直接发到这个待绑定地址
                if (!RegexUtil.checkMail(account)) {
                    throw new ServiceException(ServiceErrorCodeConstants.REGISTER_MAIL_PATTERN_ERROR);
                }
                checkEmailIsUsed(account);
                emailCodeService.sendCode(account, EmailSceneEnum.UPDATE_PROFILE);
            }
        }
    }

    @Override
    public void resetPassword(UserResetPasswordCommand command) {
        // 1. 基础参数校验；重置密码必须同时提供账号、验证码和新密码
        if (command == null || !StringUtils.hasText(command.getAccount())
                || !StringUtils.hasText(command.getEmailCode())
                || !StringUtils.hasText(command.getNewPassword())) {
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_INFO_IS_EMPTY);
        }

        // 2. 账号归一化，邮箱统一小写，避免大小写导致查不到用户
        String account = normalizeAccount(command.getAccount());

        // 3. 查用户；账号不存在时返回和验证码错误一致的文案，避免枚举账号
        UserDO userDO = getUserByAccount(account);
        if (userDO == null) {
            log.warn("重置密码失败，账号不存在，account: {}", account);
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_ACCOUNT_OR_CODE_ERROR);
        }

        // 4. 校验重置密码场景验证码；校验通过后验证码立即失效
        emailCodeService.verifyCode(userDO.getEmail(), command.getEmailCode(),
                EmailSceneEnum.RESET_PASSWORD);

        // 5. 校验新密码强度
        if (!RegexUtil.checkPassword(command.getNewPassword())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PASSWORD_PATTERN_ERROR);
        }

        // 6. 新密码按登录逻辑一致做 SHA-256 后落库
        userDO.setPassword(DigestUtil.sha256Hex(command.getNewPassword()));
        userMapper.updateById(userDO);
    }

    @Override
    public List<UserListDTO> listUsers() {
        return userMapper.listUsers().stream()
                .map(this::buildUserListDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserProfileDTO getUserProfile(Long userId) {
        // 1. 只允许读取当前登录用户资料
        UserDO userDO = getExistingUser(userId);

        // 2. 敏感字段只返回脱敏值，前端回显时不暴露完整邮箱和手机号
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(userDO.getId());
        dto.setUserName(userDO.getUserName());
        dto.setEmailMasked(maskEmail(userDO.getEmail()));
        dto.setPhoneMasked(maskPhone(userDO.getPhoneNumber() == null
                ? null : userDO.getPhoneNumber().getValue()));
        dto.setIdentity(userDO.getIdentity());
        dto.setIdentityDesc(buildIdentityDesc(userDO.getIdentity()));
        return dto;
    }

    @Override
    public void updateUserProfile(UserProfileUpdateCommand command) {
        // 1. 基础参数校验
        if (command == null || command.getUserId() == null) {
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_INFO_IS_EMPTY);
        }
        UserDO userDO = getExistingUser(command.getUserId());

        // 2. 修改用户名 / 昵称
        updateUserName(command, userDO);

        // 3. 修改邮箱；前端传脱敏值或空值时保持原邮箱
        updateEmail(command, userDO);

        // 4. 修改手机号；前端传脱敏值或空值时保持原手机号
        updatePhoneNumber(command, userDO);

        // 5. 修改密码；不填新密码时保持原密码
        updatePassword(command, userDO);

        // 6. 一次性更新资料字段，避免多次写库
        userMapper.updateById(userDO);
    }

    private UserListDTO buildUserListDTO(UserDO userDO) {
        UserListDTO userListDTO = new UserListDTO();
        userListDTO.setUserId(userDO.getId());
        userListDTO.setUserName(userDO.getUserName());
        userListDTO.setIdentity(userDO.getIdentity());

        UserIdentityEnum identityEnum = userDO.getIdentity() == null
                ? null
                : UserIdentityEnum.getValueById(userDO.getIdentity());
        userListDTO.setIdentityDesc(identityEnum == null ? "未知" : identityEnum.getIdentity());
        return userListDTO;
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

        // 管理员必须有密码；普通用户可以没有密码（只能走邮箱验证码登录）
        boolean isAdmin = UserIdentityEnum.ADMIN.getId() == registerCommand.getIdentity();
        String password = registerCommand.getPassword();
        if (isAdmin && !StringUtils.hasText(password)) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PASSWORD_IS_EMPTY);
        }

        // 填了密码才校验强度
        if (StringUtils.hasText(password) && !RegexUtil.checkPassword(password)) {
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

    /**
     * 按当前登录用户 id 查询用户，不存在时抛业务异常。
     */
    private UserDO getExistingUser(Long userId) {
        UserDO userDO = userMapper.selectById(userId);
        if (userDO == null) {
            throw new ServiceException(ServiceErrorCodeConstants.LOGIN_ACCOUNT_OR_PASSWORD_ERROR);
        }
        return userDO;
    }

    /**
     * 更新用户名；用户名与登录命名空间有关，必须保持非空且不含 @。
     */
    private void updateUserName(UserProfileUpdateCommand command, UserDO userDO) {
        String userName = command.getUserName() == null ? null : command.getUserName().trim();
        if (!StringUtils.hasText(userName)) {
            throw new ServiceException(ServiceErrorCodeConstants.PROFILE_USER_NAME_IS_EMPTY);
        }
        if (userName.contains("@")) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_USER_NAME_CONTAINS_AT);
        }
        if (userName.equals(userDO.getUserName())) {
            return;
        }

        // 只排除自己，避免把“用户名没改”误判成重复
        UserDO sameNameUser = userMapper.getUserByUserName(userName);
        if (sameNameUser != null && !sameNameUser.getId().equals(userDO.getId())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_USER_NAME_USED);
        }
        userDO.setUserName(userName);
    }

    /**
     * 更新邮箱；空值或脱敏值表示不修改。
     */
    private void updateEmail(UserProfileUpdateCommand command, UserDO userDO) {
        if (isMaskedOrBlank(command.getEmail())) {
            return;
        }

        String email = normalizeEmail(command.getEmail().trim());
        if (!RegexUtil.checkMail(email)) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_MAIL_PATTERN_ERROR);
        }
        if (email.equals(userDO.getEmail())) {
            return;
        }
        if (!StringUtils.hasText(command.getEmailCode())) {
            throw new ServiceException(ServiceErrorCodeConstants.PROFILE_EMAIL_CODE_REQUIRED);
        }

        emailCodeService.verifyCode(email, command.getEmailCode(), EmailSceneEnum.UPDATE_PROFILE);

        UserDO sameEmailUser = userMapper.getUserByEmail(email);
        if (sameEmailUser != null && !sameEmailUser.getId().equals(userDO.getId())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_MAIL_USED);
        }
        userDO.setEmail(email);
    }

    /**
     * 更新手机号；空值或脱敏值表示不修改。
     */
    private void updatePhoneNumber(UserProfileUpdateCommand command, UserDO userDO) {
        if (isMaskedOrBlank(command.getPhoneNumber())) {
            return;
        }

        String phoneNumber = command.getPhoneNumber().trim();
        if (!RegexUtil.checkMobile(phoneNumber)) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PHONE_PATTERN_ERROR);
        }

        String oldPhoneNumber = userDO.getPhoneNumber() == null ? null : userDO.getPhoneNumber().getValue();
        if (phoneNumber.equals(oldPhoneNumber)) {
            return;
        }

        // 手机号唯一性只排除自己，便于用户只改其它资料时不被自己占用
        UserDO samePhoneUser = findUserByPhone(phoneNumber);
        if (samePhoneUser != null && !samePhoneUser.getId().equals(userDO.getId())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PHONE_USED);
        }
        userDO.setPhoneNumber(new Encrypt(phoneNumber));
    }

    /**
     * 更新密码；不填新密码表示不修改。
     */
    private void updatePassword(UserProfileUpdateCommand command, UserDO userDO) {
        if (!StringUtils.hasText(command.getNewPassword())) {
            return;
        }
        if (!RegexUtil.checkPassword(command.getNewPassword())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PASSWORD_PATTERN_ERROR);
        }

        // 已有密码的用户必须验证原密码；无密码的普通用户允许首次设置密码
        if (StringUtils.hasText(userDO.getPassword())) {
            String oldEncrypted = DigestUtil.sha256Hex(command.getOldPassword());
            if (!oldEncrypted.equals(userDO.getPassword())) {
                throw new ServiceException(ServiceErrorCodeConstants.PROFILE_OLD_PASSWORD_ERROR);
            }
        }

        userDO.setPassword(DigestUtil.sha256Hex(command.getNewPassword()));
    }

    /**
     * 身份 code 转中文描述。
     */
    private String buildIdentityDesc(Integer identity) {
        UserIdentityEnum identityEnum = identity == null
                ? null
                : UserIdentityEnum.getValueById(identity);
        return identityEnum == null ? "未知" : identityEnum.getIdentity();
    }

    /**
     * 判断前端是否传了空值或脱敏值；这类值表示“不修改”。
     */
    private boolean isMaskedOrBlank(String value) {
        return !StringUtils.hasText(value) || value.contains("*");
    }

    /**
     * 邮箱脱敏：保留首字符和域名，中间用 * 替代。
     */
    private String maskEmail(String email) {
        if (!StringUtils.hasText(email) || !email.contains("@")) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        String prefix = email.substring(0, Math.min(1, atIndex));
        String domain = email.substring(atIndex);
        return prefix + "***" + domain;
    }

    /**
     * 手机号脱敏：保留前 3 位和后 4 位。
     */
    private String maskPhone(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() < 7) {
            return "***";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 按明文手机号查询用户；EncryptTypeHandler 会把 Encrypt 转成密文条件。
     */
    private UserDO findUserByPhone(String phoneNumber) {
        return userMapper.getUserByPhone(new Encrypt(phoneNumber));
    }
}
