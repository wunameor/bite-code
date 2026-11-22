package com.wunamor.lotterysystem.service.draw.state;

/**
 * 抽奖状态更新步骤。
 *
 * <p>实现类只负责事务内的状态落库，不负责抽奖资格校验、缓存清理或消息投递。</p>
 */
public interface DrawStateUpdater {

    /**
     * 当前步骤所属阶段。
     *
     * @return 状态阶段
     */
    DrawStatePhase phase();

    /**
     * 同阶段内的执行顺序，数值越小越先执行。
     *
     * @return 阶段内优先级
     */
    default int order() {
        return 0;
    }

    /**
     * 判断当前上下文是否需要执行该步骤。
     *
     * @param context 抽奖上下文
     * @return true 表示执行，false 表示跳过
     */
    default boolean shouldExecute(DrawContext context) {
        return true;
    }

    /**
     * 执行状态翻转。
     *
     * @param context 抽奖上下文
     */
    void execute(DrawContext context);
}
