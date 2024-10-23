package com.treasuredigger.devel.service;

import com.treasuredigger.devel.entity.Inquiry;
import com.treasuredigger.devel.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    public Inquiry saveInquiry(Inquiry inquiry) {
        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        return savedInquiry;
    }

    public Inquiry findInquiryById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다.")); // 예외 처리
    }

    public Inquiry updateInquiry(Long id, Inquiry updatedInquiry) {
        Inquiry inquiry = findInquiryById(id);
        inquiry.setTitle(updatedInquiry.getTitle());
        inquiry.setContent(updatedInquiry.getContent());
        // 필요한 추가 필드 업데이트
        return inquiryRepository.save(inquiry);
    }

    public void deleteInquiry(Long id) {
        Inquiry inquiry = findInquiryById(id);
        inquiryRepository.delete(inquiry);
    }
}
