package io.hhplus.tdd.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.hhplus.tdd.CustomException;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PointService 단위 테스트")
public class PointServiceUnitTest {

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("포인트를 조회할 수 있다")
    void getPoint() {
        // given
        long userId = 1L;
        UserPoint expectedPoint = new UserPoint(userId, 5000L, System.currentTimeMillis());
        given(userPointTable.selectById(userId)).willReturn(expectedPoint);

        // when
        UserPoint result = pointService.getPoint(userId);

        // then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.point()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("포인트를 충전할 수 있다")
    void charge() {
        // given
        long userId = 1L;
        long currentPoint = 1000L;
        long chargeAmount = 5000L;
        long expectedPoint = 6000L;

        given(userPointTable.selectById(userId))
                .willReturn(new UserPoint(userId, currentPoint, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(eq(userId), eq(expectedPoint)))
                .willReturn(new UserPoint(userId, expectedPoint, System.currentTimeMillis()));

        // when
        UserPoint result = pointService.charge(userId, chargeAmount);

        // then
        assertThat(result.point()).isEqualTo(expectedPoint);
        verify(pointHistoryTable).insert(eq(userId), eq(chargeAmount), eq(TransactionType.CHARGE), anyLong());
    }

    @Test
    @DisplayName("포인트를 사용할 수 있다")
    void use() {
        // given
        long userId = 1L;
        long currentPoint = 10000L;
        long useAmount = 3000L;
        long expectedPoint = 7000L;

        given(userPointTable.selectById(userId))
                .willReturn(new UserPoint(userId, currentPoint, System.currentTimeMillis()));
        given(userPointTable.insertOrUpdate(eq(userId), eq(expectedPoint)))
                .willReturn(new UserPoint(userId, expectedPoint, System.currentTimeMillis()));

        // when
        UserPoint result = pointService.use(userId, useAmount);

        // then
        assertThat(result.point()).isEqualTo(expectedPoint);
        verify(pointHistoryTable).insert(eq(userId), eq(useAmount), eq(TransactionType.USE), anyLong());
    }

    @Test
    @DisplayName("잔고가 부족할 경우 포인트 사용은 실패한다")
    void use_insufficientBalance() {
        // given
        long userId = 1L;
        long currentPoint = 1000L;
        long useAmount = 5000L;

        given(userPointTable.selectById(userId))
                .willReturn(new UserPoint(userId, currentPoint, System.currentTimeMillis()));

        // when & then
        assertThatThrownBy(() -> pointService.use(userId, useAmount))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("포인트 내역을 조회할 수 있다")
    void getHistories() {
        // given
        long userId = 1L;
        long currentTime = System.currentTimeMillis();
        List<PointHistory> expectedHistories = List.of(
                new PointHistory(1L, userId, 5000L, TransactionType.CHARGE, currentTime),
                new PointHistory(2L, userId, 2000L, TransactionType.USE, currentTime)
        );

        given(pointHistoryTable.selectAllByUserId(userId)).willReturn(expectedHistories);

        // when
        List<PointHistory> result = pointService.getHistories(userId);

        // then
        assertThat(result).hasSize(2);
    }
}
