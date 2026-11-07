package com.wunamor.lotterysystem.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.utils.RegexUtil;
import com.wunamor.lotterysystem.controller.request.UserRegisterRequest;
import com.wunamor.lotterysystem.mapper.UserMapper;
import com.wunamor.lotterysystem.mapper.dataobject.Encrypt;
import com.wunamor.lotterysystem.mapper.dataobject.UserDO;
import com.wunamor.lotterysystem.service.UserService;
import com.wunamor.lotterysystem.service.dto.UserRegisterDTO;
import com.wunamor.lotterysystem.service.enums.UserIdentityEnum;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserRegisterDTO register(UserRegisterRequest registerRequest) {
        // 检查一下参数
        checkRegisterInfo(registerRequest);
        // 执行注册的业务
        // 1. 先把内容给转换一下
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(registerRequest, userDO);
        // 还需要处理一下密码
        if (StringUtils.hasText(registerRequest.getPassword())) {
            userDO.setPassword(DigestUtil.sha256Hex(registerRequest.getPassword()));
        }
        userDO.setPhoneNumber(new Encrypt(registerRequest.getPhoneNumber()));
        // 2. 插入到数据库中
        userMapper.insert(userDO);

        // 3. 返回相关响应
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUserId(userDO.getId());
        return userRegisterDTO;
    }

    private void checkRegisterInfo(UserRegisterRequest registerRequest) {
        if (registerRequest == null) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_INFO_IS_EMPTY);
        }

        // 邮箱格式
        if (!RegexUtil.checkMail(registerRequest.getEmail())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_MAIL_PATTERN_ERROR);
        }

        // 手机号格式
        if (!RegexUtil.checkMobile(registerRequest.getPhoneNumber())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PHONE_PATTERN_ERROR);
        }

        // 身份信息验证
        if (UserIdentityEnum.getValueById(registerRequest.getIdentity()) == null) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_IDENTITY_PATTERN_ERROR);
        }

        // 管理员密码不能为空
        if (UserIdentityEnum.ADMIN.getId() == registerRequest.getIdentity() && registerRequest.getPassword().isEmpty()) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PASSWORD_IS_EMPTY);
        }

        // 密码强度验证
        if (!RegexUtil.checkPassword(registerRequest.getPassword())) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_PASSWORD_PATTERN_ERROR);
        }

        // 邮箱是否被使用
        checkEmailIsUsed(registerRequest.getEmail());

        // 电话是否被使用
        checkPhoneIsUsed(registerRequest.getPhoneNumber());
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
