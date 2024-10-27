package com.treasuredigger.devel.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsVerificationDto {

    @NotEmpty(message = "전화번호는 필수 입력 값입니다.")
    private String phone;

    @NotEmpty(message = "인증 코드는 필수 입력 값입니다.")
    private String verificationCode;
}