package com.ensah.core.services;

import com.ensah.core.bo.Module;
import com.ensah.core.bo.Niveau;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModuleService {

    public List<Module> moduleByNiveau(Niveau niveau);
}
