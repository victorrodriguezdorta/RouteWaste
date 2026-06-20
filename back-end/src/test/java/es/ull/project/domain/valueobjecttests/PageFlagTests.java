package es.ull.project.domain.valueobjecttests;

import es.ull.project.domain.valueobject.page.PageFlag;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class PageFlagTests {

    /**
     * Should create a page flag with a valid boolean value.
     */
    @Test
    void constructorValidValue() {
        PageFlag pageFlag = new PageFlag(true);
        assertThat(pageFlag.getValue()).isTrue();
    }

    /**
     * Should compare equal page flags as equal.
     */
    @Test
    void equalsEqualPageFlags() {
        PageFlag flag1 = new PageFlag(true);
        PageFlag flag2 = new PageFlag(true);
        assertThat(flag1).isEqualTo(flag2);
        assertThat(flag1.hashCode()).isEqualTo(flag2.hashCode());
    }

    /**
     * Should compare different page flags as not equal.
     */
    @Test
    void equalsDifferentPageFlags() {
        PageFlag flag1 = new PageFlag(true);
        PageFlag flag2 = new PageFlag(false);
        assertThat(flag1).isNotEqualTo(flag2);
    }
}
