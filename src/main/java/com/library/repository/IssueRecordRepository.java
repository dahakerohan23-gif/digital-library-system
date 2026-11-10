package com.library.repository;

import com.library.model.IssueRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {
    List<IssueRecord> findByUserId(Long userId);
    List<IssueRecord> findByBookIdAndStatusNot(Long bookId, String status);
    List<IssueRecord> findByStatus(String status);
}
