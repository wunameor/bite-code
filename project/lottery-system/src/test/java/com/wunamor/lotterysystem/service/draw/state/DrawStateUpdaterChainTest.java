package com.wunamor.lotterysystem.service.draw.state;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DrawStateUpdaterChainTest {

    @Test
    void updatesAreSortedByPhaseAndOrder() {
        List<String> executed = new ArrayList<>();
        DrawStateUpdaterChain chain = new DrawStateUpdaterChain(Arrays.asList(
                new TestUpdater("late-prize", DrawStatePhase.PRIZE, 20, true, executed),
                new TestUpdater("record", DrawStatePhase.RECORD, 0, true, executed),
                new TestUpdater("early-prize", DrawStatePhase.PRIZE, 10, true, executed)));

        chain.update(new DrawContext());

        assertEquals(List.of("record", "early-prize", "late-prize"), executed);
    }

    @Test
    void skipsUpdaterWhenShouldExecuteFalse() {
        List<String> executed = new ArrayList<>();
        DrawStateUpdaterChain chain = new DrawStateUpdaterChain(Arrays.asList(
                new TestUpdater("run", DrawStatePhase.RECORD, 0, true, executed),
                new TestUpdater("skip", DrawStatePhase.PRIZE, 0, false, executed)));

        chain.update(new DrawContext());

        assertEquals(List.of("run"), executed);
    }

    @Test
    void duplicatePhaseAndOrderIsRejected() {
        List<String> executed = new ArrayList<>();

        assertThrows(IllegalStateException.class,
                () -> new DrawStateUpdaterChain(Arrays.asList(
                        new TestUpdater("one", DrawStatePhase.RECORD, 10, true, executed),
                        new TestUpdater("two", DrawStatePhase.RECORD, 10, true, executed))));
    }

    private static class TestUpdater implements DrawStateUpdater {

        private final String label;
        private final DrawStatePhase phase;
        private final int order;
        private final boolean shouldExecute;
        private final List<String> executed;

        private TestUpdater(String label,
                            DrawStatePhase phase,
                            int order,
                            boolean shouldExecute,
                            List<String> executed) {
            this.label = label;
            this.phase = phase;
            this.order = order;
            this.shouldExecute = shouldExecute;
            this.executed = executed;
        }

        @Override
        public DrawStatePhase phase() {
            return phase;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public boolean shouldExecute(DrawContext context) {
            return shouldExecute;
        }

        @Override
        public void execute(DrawContext context) {
            executed.add(label);
        }
    }
}
