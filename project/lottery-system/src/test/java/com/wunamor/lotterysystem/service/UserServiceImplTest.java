package com.wunamor.lotterysystem.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.wunamor.lotterysystem.common.enums.EmailSceneEnum;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.UserMapper;
import com.wunamor.lotterysystem.mapper.dataobject.Encrypt;
import com.wunamor.lotterysystem.mapper.dataobject.UserDO;
import com.wunamor.lotterysystem.service.command.UserProfileUpdateCommand;
import com.wunamor.lotterysystem.service.command.UserResetPasswordCommand;
import com.wunamor.lotterysystem.service.dto.UserProfileDTO;
import com.wunamor.lotterysystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 用户资料服务测试。
 */
class UserServiceImplTest {

    private UserMapper userMapper;
    private EmailCodeService emailCodeService;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        emailCodeService = mock(EmailCodeService.class);
        userService = new UserServiceImpl();
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
        ReflectionTestUtils.setField(userService, "emailCodeService", emailCodeService);
    }

    /**
     * 资料回显只返回脱敏邮箱和脱敏手机号。
     */
    @Test
    void getUserProfileMasksSensitiveFields() {
        when(userMapper.selectById(53L)).thenReturn(user(
                53L, "admin", "admin@lottery.com", "13800000000", "hashed"));

        UserProfileDTO profile = userService.getUserProfile(53L);

        assertEquals(53L, profile.getUserId());
        assertEquals("admin", profile.getUserName());
        assertEquals("a***@lottery.com", profile.getEmailMasked());
        assertEquals("138****0000", profile.getPhoneMasked());
        assertEquals(0, profile.getIdentity());
        assertEquals("管理员", profile.getIdentityDesc());
    }

    /**
     * 提交脱敏邮箱和脱敏手机号时不修改敏感字段。
     */
    @Test
    void updateUserProfileIgnoresMaskedEmailAndPhone() {
        UserDO user = user(53L, "admin", "admin@lottery.com", "13800000000", "hashed");
        when(userMapper.selectById(53L)).thenReturn(user);

        UserProfileUpdateCommand command = new UserProfileUpdateCommand();
        command.setUserId(53L);
        command.setUserName("newAdmin");
        command.setEmail("a***@lottery.com");
        command.setPhoneNumber("138****0000");

        userService.updateUserProfile(command);

        ArgumentCaptor<UserDO> captor = ArgumentCaptor.forClass(UserDO.class);
        verify(userMapper).updateById(captor.capture());

        UserDO updated = captor.getValue();
        assertEquals("newAdmin", updated.getUserName());
        assertEquals("admin@lottery.com", updated.getEmail());
        assertEquals("13800000000", updated.getPhoneNumber().getValue());
        assertEquals("hashed", updated.getPassword());
    }

    /**
     * 修改邮箱需要校验验证码，并且只能绑定未被占用的邮箱。
     */
    @Test
    void updateUserProfileChangesEmailWithCode() {
        UserDO user = user(53L, "admin", "admin@lottery.com", "13800000000", "hashed");
        when(userMapper.selectById(53L)).thenReturn(user);
        when(userMapper.getUserByEmail("new@lottery.com")).thenReturn(null);

        UserProfileUpdateCommand command = new UserProfileUpdateCommand();
        command.setUserId(53L);
        command.setUserName("admin");
        command.setEmail("new@lottery.com");
        command.setEmailCode("123456");

        userService.updateUserProfile(command);

        verify(emailCodeService).verifyCode("new@lottery.com", "123456",
                EmailSceneEnum.UPDATE_PROFILE);
        ArgumentCaptor<UserDO> captor = ArgumentCaptor.forClass(UserDO.class);
        verify(userMapper).updateById(captor.capture());
        assertEquals("new@lottery.com", captor.getValue().getEmail());
    }

    /**
     * 修改邮箱但没有填写验证码时拒绝。
     */
    @Test
    void updateUserProfileRejectsEmailChangeWithoutCode() {
        UserDO user = user(53L, "admin", "admin@lottery.com", "13800000000", "hashed");
        when(userMapper.selectById(53L)).thenReturn(user);

        UserProfileUpdateCommand command = new UserProfileUpdateCommand();
        command.setUserId(53L);
        command.setUserName("admin");
        command.setEmail("new@lottery.com");

        ServiceException e = assertThrows(ServiceException.class,
                () -> userService.updateUserProfile(command));

        assertEquals(ServiceErrorCodeConstants.PROFILE_EMAIL_CODE_REQUIRED.getCode(),
                e.getCode());
        verify(userMapper, never()).updateById(any(UserDO.class));
    }

    /**
     * 已有密码的用户修改密码时必须校验原密码。
     */
    @Test
    void updateUserProfileRejectsWrongOldPassword() {
        String oldHash = DigestUtil.sha256Hex("12345678");
        UserDO user = user(53L, "admin", "admin@lottery.com", "13800000000", oldHash);
        when(userMapper.selectById(53L)).thenReturn(user);

        UserProfileUpdateCommand command = new UserProfileUpdateCommand();
        command.setUserId(53L);
        command.setUserName("admin");
        command.setOldPassword("wrong-password");
        command.setNewPassword("87654321");

        ServiceException e = assertThrows(ServiceException.class,
                () -> userService.updateUserProfile(command));

        assertEquals(ServiceErrorCodeConstants.PROFILE_OLD_PASSWORD_ERROR.getCode(),
                e.getCode());
        verify(userMapper, never()).updateById(any(UserDO.class));
    }

    /**
     * 已有密码的用户原密码正确时可以修改密码。
     */
    @Test
    void updateUserProfileChangesPasswordWithOldPassword() {
        String oldHash = DigestUtil.sha256Hex("12345678");
        UserDO user = user(53L, "admin", "admin@lottery.com", "13800000000", oldHash);
        when(userMapper.selectById(53L)).thenReturn(user);

        UserProfileUpdateCommand command = new UserProfileUpdateCommand();
        command.setUserId(53L);
        command.setUserName("admin");
        command.setOldPassword("12345678");
        command.setNewPassword("87654321");

        userService.updateUserProfile(command);

        ArgumentCaptor<UserDO> captor = ArgumentCaptor.forClass(UserDO.class);
        verify(userMapper).updateById(captor.capture());
        assertEquals(DigestUtil.sha256Hex("87654321"), captor.getValue().getPassword());
    }

    /**
     * 无密码普通用户可以直接设置新密码。
     */
    @Test
    void updateUserProfileSetsPasswordForUserWithoutOldPassword() {
        UserDO user = user(54L, "normal", "normal@lottery.com", "13900000000", null);
        when(userMapper.selectById(54L)).thenReturn(user);

        UserProfileUpdateCommand command = new UserProfileUpdateCommand();
        command.setUserId(54L);
        command.setUserName("normal");
        command.setNewPassword("87654321");

        userService.updateUserProfile(command);

        ArgumentCaptor<UserDO> captor = ArgumentCaptor.forClass(UserDO.class);
        verify(userMapper).updateById(captor.capture());
        assertEquals(DigestUtil.sha256Hex("87654321"), captor.getValue().getPassword());
    }

    /**
     * 重置密码成功后新密码按 SHA-256 落库。
     */
    @Test
    void resetPasswordUpdatesHashedPassword() {
        UserDO user = user(53L, "admin", "admin@lottery.com", "13800000000", "old-hash");
        when(userMapper.getUserByUserName("admin")).thenReturn(user);

        UserResetPasswordCommand command = new UserResetPasswordCommand();
        command.setAccount("admin");
        command.setEmailCode("123456");
        command.setNewPassword("87654321");

        userService.resetPassword(command);

        verify(emailCodeService).verifyCode("admin@lottery.com", "123456",
                EmailSceneEnum.RESET_PASSWORD);
        ArgumentCaptor<UserDO> captor = ArgumentCaptor.forClass(UserDO.class);
        verify(userMapper).updateById(captor.capture());
        assertEquals(DigestUtil.sha256Hex("87654321"), captor.getValue().getPassword());
    }

    /**
     * 账号不存在时返回统一错误，避免枚举账号。
     */
    @Test
    void resetPasswordRejectsUnknownAccount() {
        when(userMapper.getUserByUserName("missing")).thenReturn(null);

        UserResetPasswordCommand command = new UserResetPasswordCommand();
        command.setAccount("missing");
        command.setEmailCode("123456");
        command.setNewPassword("87654321");

        ServiceException e = assertThrows(ServiceException.class,
                () -> userService.resetPassword(command));

        assertEquals(ServiceErrorCodeConstants.LOGIN_ACCOUNT_OR_CODE_ERROR.getCode(),
                e.getCode());
        verify(userMapper, never()).updateById(any(UserDO.class));
    }

    /**
     * 重置密码验证码错误时不更新密码。
     */
    @Test
    void resetPasswordRejectsWrongEmailCode() {
        UserDO user = user(53L, "admin", "admin@lottery.com", "13800000000", "old-hash");
        when(userMapper.getUserByUserName("admin")).thenReturn(user);
        org.mockito.Mockito.doThrow(new ServiceException(ServiceErrorCodeConstants.EMAIL_CODE_ERROR))
                .when(emailCodeService).verifyCode("admin@lottery.com", "000000",
                        EmailSceneEnum.RESET_PASSWORD);

        UserResetPasswordCommand command = new UserResetPasswordCommand();
        command.setAccount("admin");
        command.setEmailCode("000000");
        command.setNewPassword("87654321");

        ServiceException e = assertThrows(ServiceException.class,
                () -> userService.resetPassword(command));

        assertEquals(ServiceErrorCodeConstants.EMAIL_CODE_ERROR.getCode(), e.getCode());
        verify(userMapper, never()).updateById(any(UserDO.class));
    }

    /**
     * 新密码格式错误时不更新密码。
     */
    @Test
    void resetPasswordRejectsInvalidNewPassword() {
        UserDO user = user(53L, "admin", "admin@lottery.com", "13800000000", "old-hash");
        when(userMapper.getUserByUserName("admin")).thenReturn(user);

        UserResetPasswordCommand command = new UserResetPasswordCommand();
        command.setAccount("admin");
        command.setEmailCode("123456");
        command.setNewPassword("short");

        ServiceException e = assertThrows(ServiceException.class,
                () -> userService.resetPassword(command));

        assertEquals(ServiceErrorCodeConstants.REGISTER_PASSWORD_PATTERN_ERROR.getCode(),
                e.getCode());
        verify(userMapper, never()).updateById(any(UserDO.class));
    }

    private UserDO user(Long id, String userName, String email, String phone, String password) {
        UserDO user = new UserDO();
        user.setId(id);
        user.setUserName(userName);
        user.setEmail(email);
        user.setPhoneNumber(new Encrypt(phone));
        user.setPassword(password);
        user.setIdentity(0);
        return user;
    }
}
