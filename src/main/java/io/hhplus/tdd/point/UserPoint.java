package io.hhplus.tdd.point;

import io.hhplus.tdd.CustomException;
import org.springframework.http.HttpStatus;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public void ensureUse(long userAmount) {
        if(userAmount > point) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "잔액이 부족합니다.");
        }
    }
}
