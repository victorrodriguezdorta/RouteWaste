package es.ull.project.domain.valueobject.page;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PageFlagTests {

    @Test
    void givenValidPageFlag_whenInstantiating_thenSuccess() {
        PageFlag pageFlag = new PageFlag(true);
        assertThat(pageFlag.getValue()).isTrue();
    }

    @Test
    void givenEqualPageFlag_whenEquals_thenTrue() {
        PageFlag flag1 = new PageFlag(true);
        PageFlag flag2 = new PageFlag(true);
        assertThat(flag1).isEqualTo(flag2);
        assertThat(flag1.hashCode()).isEqualTo(flag2.hashCode());
    }

    @Test
    void givenDifferentPageFlag_whenEquals_thenFalse() {
        PageFlag flag1 = new PageFlag(true);
        PageFlag flag2 = new PageFlag(false);
        assertThat(flag1).isNotEqualTo(flag2);
    }
}
