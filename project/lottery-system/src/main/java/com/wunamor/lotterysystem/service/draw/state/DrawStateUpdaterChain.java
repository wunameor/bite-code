package com.wunamor.lotterysystem.service.draw.state;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 抽奖状态更新链：按阶段和优先级执行状态更新步骤。
 *
 * <p>本组件只负责顺序执行，不声明事务；调用方必须把执行过程放在同一事务内。</p>
 */
@Slf4j
@Component
public class DrawStateUpdaterChain {

    /** 已按阶段和优先级排序的状态更新步骤。 */
    private final List<DrawStateUpdater> updaters;

    /**
     * 构造状态更新链，并在启动时完成排序和优先级校验。
     *
     * @param updaters Spring 注入的状态更新步骤
     */
    public DrawStateUpdaterChain(List<DrawStateUpdater> updaters) {
        this.updaters = sortAndValidate(updaters);
    }

    /**
     * 依次执行抽奖状态更新。
     *
     * @param context 抽奖上下文
     */
    public void update(DrawContext context) {
        // 1. 上下文是更新链执行的最低要求
        if (context == null) {
            throw new IllegalArgumentException("抽奖状态上下文不能为空");
        }

        // 2. 按启动时确定的顺序执行，异常会中断后续步骤并向上抛出
        for (DrawStateUpdater updater : updaters) {
            if (!updater.shouldExecute(context)) {
                log.debug("跳过抽奖状态更新，requestId: {}, activityId: {}, updater: {}",
                        context.getRequestId(), context.getActivityId(), updaterName(updater));
                continue;
            }

            log.debug("执行抽奖状态更新，requestId: {}, activityId: {}, phase: {}, order: {}, updater: {}",
                    context.getRequestId(), context.getActivityId(),
                    updater.phase(), updater.order(), updaterName(updater));
            updater.execute(context);
        }
    }

    /**
     * 按阶段和阶段内优先级排序，并拒绝重复优先级。
     *
     * @param updaters 待排序的状态更新步骤
     * @return 排序后的状态更新步骤
     */
    private List<DrawStateUpdater> sortAndValidate(List<DrawStateUpdater> updaters) {
        if (updaters == null || updaters.isEmpty()) {
            return List.of();
        }

        List<DrawStateUpdater> sorted = updaters.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparingInt((DrawStateUpdater updater) -> updater.phase().getIndex())
                        .thenComparingInt(DrawStateUpdater::order))
                .toList();
        validateNoDuplicatePriority(sorted);
        return sorted;
    }

    /**
     * 校验同一阶段内不存在重复优先级，避免执行顺序不确定。
     *
     * @param updaters 已排序的状态更新步骤
     */
    private void validateNoDuplicatePriority(List<DrawStateUpdater> updaters) {
        Set<String> priorities = new HashSet<>();
        for (DrawStateUpdater updater : updaters) {
            String key = updater.phase() + ":" + updater.order();
            if (!priorities.add(key)) {
                throw new IllegalStateException("重复的抽奖状态更新优先级: " + key);
            }
        }
    }

    /**
     * 获取更新器简名，用于日志输出。
     *
     * @param updater 状态更新步骤
     * @return 类简名
     */
    private String updaterName(DrawStateUpdater updater) {
        return updater.getClass().getSimpleName();
    }
}
