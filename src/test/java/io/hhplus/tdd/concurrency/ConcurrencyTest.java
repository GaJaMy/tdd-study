package io.hhplus.tdd.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.PointUseCase;
import io.hhplus.tdd.point.UserPoint;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@DisplayName("동시성 및 락 테스트")
public class ConcurrencyTest {

    private PointUseCase pointUseCase;

    @BeforeEach
    void setUp() {
        UserPointTable userPointTable = new UserPointTable();
        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        PointService pointService = new PointService(pointHistoryTable, userPointTable);
        pointUseCase = new PointUseCase(pointService);
    }


    /***
     * 원하는 결과를 얻지 못한다.
     * 첫 스레드가 요청을 처리하여 10을 감소 키고 끝나기 전에 두번째 스레드의 요청이 시작되며
     * 다시 초기 상태의 값으로 간다. 그래서 의도치 않은 결과가 나온다.
     */
    @Test
    @DisplayName("1000원 충전하고 동시에 100개의 요청으로 10원씩 감소 시킨다.")
    void concurrent_100_use_requests_with_1000_point() throws InterruptedException {
        // given
        long userId = 1L;
        long initialCharge = 1000L;
        int threadCount = 100;
        long useAmount = 10L;

        // 초기 충전
        pointUseCase.charge(userId, initialCharge);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 100개의 스레드가 동시에 10원씩 사용
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointUseCase.commonUse(userId, useAmount);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 완료 대기
        latch.await();
        executorService.shutdown();

        // then
        UserPoint finalPoint = pointUseCase.getPoint(userId);

        // 0이 아닌 걸로 나옴
        assertThat(finalPoint.point()).isNotEqualTo(0L);
        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(0);
    }

    /***
     * 원하는 결과가 나오지만 굉장히 오래 걸린다.
     * 하나의 스레드가 하나씩 공유자원을 사용하기 때문에, 다른 스레드가 종료되길 기다리기 때문에 느리다.
     */
    @Test
    @DisplayName("1000원 충전하고 동시에 100개의 요청으로 10원씩 감소 시킨다. - synchronized 키워드 사용(뮤텍스처럼 동작)")
    void concurrent_100_use_requests_with_1000_point_synchronized() throws InterruptedException {
        // given
        long userId = 1L;
        long initialCharge = 1000L;
        int threadCount = 100;
        long useAmount = 10L;

        // 초기 충전
        pointUseCase.charge(userId, initialCharge);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 100개의 스레드가 동시에 10원씩 사용
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointUseCase.synchronizedUse(userId, useAmount);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 완료 대기
        latch.await();
        executorService.shutdown();

        // then
        UserPoint finalPoint = pointUseCase.getPoint(userId);

        // 전부 소모됨
        assertThat(finalPoint.point()).isEqualTo(0L);
        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(0);
    }

    /***
     * synchronized 키워드와 비슷하게 동작 되지만 약간 빠르다
     * 약간 빠른 이유는 synchronized는 lock 걸리면 다른 스레드들이 무한 대기 상태(BLOCK)에 들어가지만 Reentrant는 WATING으로
     * 상태에 들어가기 때문에 약간 빠르다.
     */
    @Test
    @DisplayName("1000원 충전하고 동시에 100개의 요청으로 10원씩 감소 시킨다. - ReentrantLock 사용")
    void concurrent_100_use_requests_with_1000_point_reentrant() throws InterruptedException {
        // given
        long userId = 1L;
        long initialCharge = 1000L;
        int threadCount = 100;
        long useAmount = 10L;

        // 초기 충전
        pointUseCase.charge(userId, initialCharge);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 100개의 스레드가 동시에 10원씩 사용
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    pointUseCase.reentrantUse(userId, useAmount);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 완료 대기
        latch.await();
        executorService.shutdown();

        // then
        UserPoint finalPoint = pointUseCase.getPoint(userId);

        // 전부 소모됨
        assertThat(finalPoint.point()).isEqualTo(0L);
        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(0);
    }
}
