package io.hhplus.tdd.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.hhplus.tdd.CustomException;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserPoint 단위 테스트")
public class UserPointUnitTest {

    @Test
    @DisplayName("empty 메서드로 초기 UserPoint를 생성할 수 있다")
    void empty() {
        // given
        long userId = 1L;

        // when
        UserPoint userPoint = UserPoint.empty(userId);

        // then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(0L);
        assertThat(userPoint.updateMillis()).isGreaterThan(0L);
    }

    @Test
    @DisplayName("포인트가 충분한 경우 ensureUse는 예외를 발생시키지 않는다")
    void ensureUse_sufficient() {
        // given
        UserPoint userPoint = new UserPoint(1L, 10000L, System.currentTimeMillis());
        long useAmount = 5000L;

        // when & then
        // 예외가 발생하지 않아야 함
        userPoint.ensureUse(useAmount);
    }

    @Test
    @DisplayName("포인트가 부족한 경우 ensureUse는 CustomException을 발생시킨다")
    void ensureUse_insufficient() {
        // given
        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());
        long useAmount = 5000L;

        // when & then
        assertThatThrownBy(() -> userPoint.ensureUse(useAmount))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("잔액이 부족합니다");
    }

    @Test
    @DisplayName("사용 포인트가 잔액과 정확히 같은 경우 ensureUse는 예외를 발생시키지 않는다")
    void ensureUse_exact() {
        // given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());
        long useAmount = 5000L;

        // when & then
        userPoint.ensureUse(useAmount);
    }

    @Test
    @DisplayName("UserPoint는 id, point, updateMillis를 가진다")
    void userPoint_creation() {
        // given
        long id = 1L;
        long point = 10000L;
        long updateMillis = System.currentTimeMillis();

        // when
        UserPoint userPoint = new UserPoint(id, point, updateMillis);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(point);
        assertThat(userPoint.updateMillis()).isEqualTo(updateMillis);
    }
}
