package com.lucas.ctrlSave.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jogo;
    private String descricao;
    private String path;
    private String nameFile;
    private String imagePath;
    private String nameImageFile;

    @ManyToOne
    private User user;
}
