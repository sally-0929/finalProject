package com.treasuredigger.devel.service;

import com.treasuredigger.devel.entity.FAQ;
import com.treasuredigger.devel.repository.FAQRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FAQService {

    private final FAQRepository faqRepository;

    public List<FAQ> getAllFAQs() {
        List<FAQ> faqs = faqRepository.findAll();
        faqs.sort(Comparator.comparing(FAQ::getId).reversed()); // ID 기준으로 정렬
        return faqs;
    }

    public FAQ saveFAQ(FAQ faq) {
        return faqRepository.save(faq);
    }

    public FAQ findFAQById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ가 존재하지 않습니다.")); // 예외 처리
    }

    public FAQ updateFAQ(Long id, FAQ updatedFAQ) {
        FAQ faq = findFAQById(id);
        faq.setQuestion(updatedFAQ.getQuestion());
        faq.setAnswer(updatedFAQ.getAnswer());
        return faqRepository.save(faq);
    }

    public void deleteFAQ(Long id) {
        FAQ faq = findFAQById(id);
        faqRepository.delete(faq);
    }
}
