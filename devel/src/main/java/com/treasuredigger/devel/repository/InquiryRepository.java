package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}