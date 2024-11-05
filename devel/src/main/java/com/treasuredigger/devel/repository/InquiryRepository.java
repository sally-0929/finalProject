package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.Inquiry;
import com.treasuredigger.devel.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Page<Inquiry> findByMember(Member member, Pageable pageable);
    Page<Inquiry> findByAnsweredFalse(Pageable pageable);
}