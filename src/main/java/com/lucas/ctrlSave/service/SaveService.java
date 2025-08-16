package com.lucas.ctrlSave.service;

import com.lucas.ctrlSave.model.dto.SaveDto;
import com.lucas.ctrlSave.model.entity.SaveFile;
import com.lucas.ctrlSave.repository.SaveFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SaveService {

    private S3SaveUploaderService uploaderService;

    private SaveFileRepository saveFileRepository;

    public ResponseEntity<?> uploadSave(SaveDto saveDto){

        try {
            String url = uploaderService.upload(saveDto.getFile());

            saveFileRepository.save(SaveFile.builder()
                    .jogo(saveDto.getJogo())
                    .descricao(saveDto.getDescricao())
                    .usuario(saveDto.getUsuario())
                    .path(url)
                    .nameFile(saveDto.getFile().getOriginalFilename())
                    .build());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Save enviado com sucesso! URL: " + url);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o save: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getSaves(){

        try{

            return ResponseEntity.status(HttpStatus.OK)
                    .body(saveFileRepository.findAll());

        }catch(Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro resgatar saves " + e.getMessage());
        }


    }

    public ResponseEntity<?> delete(Long id){

        try{

            SaveFile saveFile = saveFileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Save não encontrado"));

            uploaderService.delete(saveFile.getNameFile());

            saveFileRepository.deleteById(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Save excluido");

        }catch(Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletear o save: " + e.getMessage());
        }

    }

}
