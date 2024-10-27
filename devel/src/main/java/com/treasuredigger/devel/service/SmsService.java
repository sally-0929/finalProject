package com.treasuredigger.devel.service;

import com.treasuredigger.devel.dto.SmsVerificationDto;

public interface SmsService {
    void sendSms(SmsVerificationDto smsVerificationDto);
    boolean verifySms(String phoneNum, String code);
}
