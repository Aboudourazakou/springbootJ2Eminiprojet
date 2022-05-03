package com.ensah.core.services.impl;

import com.ensah.core.bo.Niveau;
import com.ensah.core.dao.ModuleDao;
import  com.ensah.core.bo.Module;
import com.ensah.core.services.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    ModuleDao moduleDao;

    public List<Module> moduleByNiveau(Niveau niveau){
        System.out.println(niveau.getIdNiveau()+" est le niveau");
           return moduleDao.findModulesByNiveau(niveau);



    }
}
