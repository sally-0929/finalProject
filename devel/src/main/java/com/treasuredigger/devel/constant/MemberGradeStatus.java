package com.treasuredigger.devel.constant;

public enum MemberGradeStatus {
    NORMAL(0), SILVER(6), GOLD(9), VIP(12), VVIP(15);

    private final int pointRate;

    MemberGradeStatus(int pointRate) {
        this.pointRate = pointRate;
    }

    public int getPointRate() {
        return pointRate;
    }
}
