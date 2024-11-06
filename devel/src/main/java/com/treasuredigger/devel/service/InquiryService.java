package com.treasuredigger.devel.service;

import com.treasuredigger.devel.entity.Inquiry;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public List<Inquiry> getAllInquiries() {
        List<Inquiry> inquiries = inquiryRepository.findAll();
        inquiries.sort(Comparator.comparing(Inquiry::getCreatedDate).reversed());
        return inquiries;
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

    public Page<Inquiry> getInquiriesWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        return inquiryRepository.findAll(pageable);
    }

    public Page<Inquiry> getInquiriesByMemberWithPagination(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        return inquiryRepository.findByMember(member, pageable);
    }

    public Page<Inquiry> getUnansweredInquiriesWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        return inquiryRepository.findByAnsweredFalse(pageable);
    }
}
