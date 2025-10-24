package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    public PointService(PointHistoryTable pointHistoryTable, UserPointTable userPointTable) {
        this.pointHistoryTable = pointHistoryTable;
        this.userPointTable = userPointTable;
    }

    public UserPoint getPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    public UserPoint charge(long userId, long chargeAmount) {
        UserPoint point = getPoint(userId);
        pointHistoryTable.insert(userId, chargeAmount, TransactionType.CHARGE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(userId, chargeAmount + point.point());
    }

    public UserPoint use(long userId, long useAmount) {
        UserPoint point = getPoint(userId);
        point.ensureUse(useAmount);
        pointHistoryTable.insert(userId, useAmount, TransactionType.USE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(userId, point.point() - useAmount);
    }

    public List<PointHistory> getHistories(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
