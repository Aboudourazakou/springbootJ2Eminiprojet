package com.ensah.core.dao;

import  com.ensah.core.bo.Module;
import com.ensah.core.bo.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleDao extends JpaRepository<Module,Long> {
    public  List<Module>findModulesByNiveau(Niveau niveau);


}
