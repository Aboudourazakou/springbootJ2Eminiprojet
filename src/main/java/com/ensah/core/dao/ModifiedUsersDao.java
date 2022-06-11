package com.ensah.core.dao;

import com.ensah.core.bo.ModifiedUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModifiedUsersDao  extends JpaRepository<ModifiedUsers,Long> {
}
