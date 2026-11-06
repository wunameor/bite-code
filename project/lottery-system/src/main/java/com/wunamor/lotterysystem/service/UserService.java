package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.controller.request.UserRegisterRequest;
import com.wunamor.lotterysystem.service.dto.UserRegisterDTO;

public interface UserService {
    UserRegisterDTO register(UserRegisterRequest registerRequest);
}
