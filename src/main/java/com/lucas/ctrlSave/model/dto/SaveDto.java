package com.lucas.ctrlSave.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveDto {

    private MultipartFile file;
    private String jogo;
    private String descricao;
}
