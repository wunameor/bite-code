package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.service.command.DrawPrizeCommand;
import com.wunamor.lotterysystem.service.dto.DrawPrizeDTO;

/**
 * 抽奖服务。
 */
public interface DrawPrizeService {

    /**
     * 执行一次抽奖：写中奖记录、更新活动状态，并在提交后异步发通知。
     *
     * @param command 抽奖命令
     * @return 抽奖结果
     */
    DrawPrizeDTO drawPrize(DrawPrizeCommand command);
}
