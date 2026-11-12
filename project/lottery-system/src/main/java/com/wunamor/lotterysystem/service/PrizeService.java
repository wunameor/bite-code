package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.common.pojo.PageResult;
import com.wunamor.lotterysystem.service.command.PrizeCreateCommand;
import com.wunamor.lotterysystem.service.command.PrizeListCommand;
import com.wunamor.lotterysystem.service.dto.PrizeCreateDTO;
import com.wunamor.lotterysystem.service.dto.PrizeDTO;
import org.springframework.web.multipart.MultipartFile;

public interface PrizeService {

    /**
     * 创建奖品：先上传图片，再把图片 URL 和奖品信息一起落库。
     *
     * @param command  奖品信息
     * @param prizePic 奖品图片，必填
     * @return 新奖品 id
     */
    PrizeCreateDTO createPrize(PrizeCreateCommand command, MultipartFile prizePic);

    /**
     * 分页查询奖品列表，按 id 倒序。
     *
     * @param command 分页参数
     * @return 分页后的奖品列表
     */
    PageResult<PrizeDTO> listPrizes(PrizeListCommand command);
}
