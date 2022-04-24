package com.ensah.core.web.controllers;

import com.ensah.core.services.EtudiantService;
import com.ensah.core.services.impl.EtudiantServiceImpl;
import com.ensah.core.utils.ExcellFileRowObject;
import com.ensah.core.utils.ExcellImporter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Controller
@MultipartConfig
public class InscriptionController {

    @Autowired
    public EtudiantServiceImpl etudiantServiceImpl;


    @GetMapping("/admin/inscription")
    public  String importExcellGet(){

        return "/admin/inscription";

    }


    @PostMapping("/admin/inscription")
    public  String importExcellPost(@RequestParam("file") MultipartFile file) throws IOException {


        if(ExcellImporter.hasExcelFormat(file))
          {      ExcellImporter excellImporter=new ExcellImporter();
                excellImporter.excellFileDataPreprocessing(etudiantServiceImpl,file.getInputStream());
                return  "error";
          }
        else {
            System.out.println("Le fichier n'est pas excell");
            return "error";
        }



    }



}
