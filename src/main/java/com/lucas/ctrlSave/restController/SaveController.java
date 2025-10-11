package com.lucas.ctrlSave.restController;

import com.lucas.ctrlSave.model.dto.SaveDto;
import com.lucas.ctrlSave.service.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/saves")
public class SaveController {

    @Autowired
    private SaveService saveService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadSave(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jogo") String jogo,
            @RequestParam("descricao") String descricao) {

      return saveService.uploadSave(new SaveDto(file,jogo,descricao));

    }

    @GetMapping("/user")
    public ResponseEntity<?> getSaves(){
        return saveService.getSavesByUser();
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteSave(@RequestParam("id") Long id){
        return saveService.delete(id);
    }

}