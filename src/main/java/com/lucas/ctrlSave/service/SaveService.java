package com.lucas.ctrlSave.service;

import com.lucas.ctrlSave.model.dto.SaveDto;
import com.lucas.ctrlSave.model.entity.SaveFile;
import com.lucas.ctrlSave.model.entity.User;
import com.lucas.ctrlSave.repository.SaveFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SaveService {

    private S3SaveUploaderService uploaderService;

    private SaveFileRepository saveFileRepository;

    private UserService userService;

    public ResponseEntity<?> uploadSave(SaveDto saveDto){

        try {

            User user = getUser();

            if(user == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario não autentificado");
            }



           SaveFile saveFile = saveFileRepository.save(SaveFile.builder()
                    .jogo(saveDto.getJogo())
                    .descricao(saveDto.getDescricao())
                    .nameImageFile(saveDto.getCapa().getOriginalFilename())
                    .nameFile(saveDto.getFile().getOriginalFilename())
                    .user(user)
                    .build());

            String salveFileUrl = uploaderService.upload(saveDto.getFile(),
                    createSavePath(user, saveFile.getId(), saveFile.getNameFile()));

            String saveImageUrl = uploaderService.upload(saveDto.getCapa(),
                    createImagePath(user,saveFile.getId(),saveFile.getNameImageFile()));

           saveFile.setPath(salveFileUrl);
           saveFile.setImagePath(saveImageUrl);

           saveFileRepository.save(saveFile);

           return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Save enviado com sucesso!");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o save: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getSavesByUser(){

        try{

            List<SaveFile> saveFileList = saveFileRepository.findByUser(getUser());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(saveFileList);

        }catch(Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro resgatar saves " + e.getMessage());
        }


    }

    public List<SaveFile> getAllSaveFiles(){
        return saveFileRepository.findAll();
    }

    public ResponseEntity<?> delete(Long id){

        try{

            SaveFile saveFile = saveFileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Save não encontrado"));

            if(!saveFile.getUser().equals(getUser())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario não autentificado");
            }

            uploaderService.delete(saveFile.getPath());
            uploaderService.delete(saveFile.getImagePath());

            saveFileRepository.deleteById(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Save excluido");

        }catch(Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletear o save: " + e.getMessage());
        }

    }

    private User getUser(){
        return (userService.getUserAuthenticated() != null) ? userService.getUserAuthenticated() : null;
    }

    private String createSavePath(User user, Long id, String saveFileName){
        return "safeFiles/" + user.getUsername()+"/" + id + "/" + saveFileName;
    }

    private String createImagePath(User user, Long id, String imageFileName){
        return "safeFiles/" + user.getUsername()+"/" +id+"/" + imageFileName;
    }

}
