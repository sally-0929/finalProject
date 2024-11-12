package com.treasuredigger.devel.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamportConfig {

    // @Value => Spring이 application.properties 파일에서 해당 키에 대한 값을 읽어와서 apiKey와
    // apiSecret 변수에 주입
    @Value("${iamport.api.key}") // 실제 값 넣는 것X -> 안전하게 관리하기 위함
    private String apiKey;

    @Value("${iamport.api.secret}")
    private String apiSecret;

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apiKey, apiSecret);
    }

}
