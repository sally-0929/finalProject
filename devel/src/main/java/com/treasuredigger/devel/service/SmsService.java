package com.treasuredigger.devel.service;

import com.treasuredigger.devel.dto.MemberFormDto;

public interface SmsService {
    void sendSms(MemberFormDto memberFormDto);
}
