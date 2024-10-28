package com.treasuredigger.devel.service;

import com.treasuredigger.devel.entity.Notice;
import com.treasuredigger.devel.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<Notice> getAllNotices() {
        List<Notice> notices = noticeRepository.findAll();
        notices.sort(Comparator.comparing(Notice::getId).reversed()); // ID 기준으로 정렬
        return notices;
    }

    public Notice saveNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    public Notice findNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice가 존재하지 않습니다.")); // 예외 처리
    }

    public Notice updateNotice(Long id, Notice updatedNotice) {
        Notice notice = findNoticeById(id);
        notice.setTitle(updatedNotice.getTitle());
        notice.setContent(updatedNotice.getContent());
        return noticeRepository.save(notice);
    }

    public void deleteNotice(Long id) {
        Notice notice = findNoticeById(id);
        noticeRepository.delete(notice);
    }
}
