package com.treasuredigger.devel.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IamportService {

    private final IamportClient iamportClient;

    @Autowired
    public IamportService(IamportClient iamportClient) {
        this.iamportClient = iamportClient;
    }

    public IamportResponse<Payment> getPaymentInfo(String impUid) throws IamportResponseException, IOException {
        return iamportClient.paymentByImpUid(impUid); // imp_uid를 이용해 결제 정보 조회
    }

}
