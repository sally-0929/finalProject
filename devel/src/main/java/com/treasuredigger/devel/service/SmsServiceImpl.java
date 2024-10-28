package com.treasuredigger.devel.service;

import com.treasuredigger.devel.component.SmsCertificationUtil;
import com.treasuredigger.devel.dto.MemberFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {

    private final SmsCertificationUtil smsCertificationUtil;
    private final Map<String, String> verificationCodes = new HashMap<>();

    //의존성 주입
    public SmsServiceImpl(@Autowired SmsCertificationUtil smsCertificationUtil) {
        this.smsCertificationUtil = smsCertificationUtil;
    }

    @Override
    public void sendSms(MemberFormDto memberFormDto) {
        String phone = memberFormDto.getPhone();
        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000);
        verificationCodes.put(phone, certificationCode);
        smsCertificationUtil.sendSMS(phone, certificationCode);
    }

    @Override
    public boolean verifySms(String phoneNum, String code) {
        String storedCode = verificationCodes.get(phoneNum);
        return storedCode != null && storedCode.equals(code);
    }
}
