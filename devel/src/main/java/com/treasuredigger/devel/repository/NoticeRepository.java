package com.treasuredigger.devel.repository;

import com.treasuredigger.devel.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
