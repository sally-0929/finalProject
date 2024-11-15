package com.treasuredigger.devel.constant;

public enum MemberGradeStatus {
    NORMAL(1), SILVER(3), GOLD(7), VIP(10), VVIP(15);

    private final int pointRate;

    MemberGradeStatus(int pointRate) {
        this.pointRate = pointRate;
    }

    public int getPointRate() {
        return pointRate;
    }
}
