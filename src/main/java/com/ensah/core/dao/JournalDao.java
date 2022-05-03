package com.ensah.core.dao;

import com.ensah.core.bo.Journal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalDao extends JpaRepository<Journal,Long> {
}
