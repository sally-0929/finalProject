package com.treasuredigger.devel.service;

import com.treasuredigger.devel.component.SmsCertificationUtil;
import com.treasuredigger.devel.dto.MemberFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    private final SmsCertificationUtil smsCertificationUtil;

    //의존성 주입
    public SmsServiceImpl(@Autowired SmsCertificationUtil smsCertificationUtil) {
        this.smsCertificationUtil = smsCertificationUtil;
    }

    @Override // SmsService 인터페이스 메서드 구현
    public void sendSms(MemberFormDto memberFormDto) {
        String phoneNum = memberFormDto.getPhone();
        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        smsCertificationUtil.sendSMS(phoneNum, certificationCode); // SMS 인증 유틸리티를 사용하여 SMS 발송
    }
}
