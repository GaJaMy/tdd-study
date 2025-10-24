package io.hhplus.tdd.point;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;

@Service
public class PointUseCase {
//    private final ReentrantLock lock = new ReentrantLock();

    private final ConcurrentHashMap<Long, Lock> userLocks = new ConcurrentHashMap<>();

    private final PointService pointService;

    public PointUseCase(PointService pointService) {
        this.pointService = pointService;
    }

    public UserPoint charge(long userId, long chargeAmount) {
        return pointService.charge(userId, chargeAmount);
    }

    public UserPoint getPoint(long userId) {
        return pointService.getPoint(userId);
    }

    public UserPoint commonUse(long userId, long useAmount) {
        return pointService.use(userId, useAmount);
    }

    public synchronized UserPoint synchronizedUse(long userId, long useAmount) {
        return pointService.use(userId, useAmount);
    }

    public UserPoint reentrantUse(long userId, long useAmount) {
        Lock lock = userLocks.computeIfAbsent(userId, id -> new ReentrantLock());
        lock.lock();
        try {
            return pointService.use(userId, useAmount);
        } finally {
            lock.unlock();
        }
    }
//
//    public UserPoint commonUse(long userId, long useAmount) {
//        return pointService.use(userId, useAmount);
//    }
}
