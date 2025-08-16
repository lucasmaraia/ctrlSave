package com.lucas.ctrlSave.repository;

import com.lucas.ctrlSave.model.entity.SaveFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaveFileRepository extends JpaRepository<SaveFile,Long> {

    Optional<SaveFile> findById(Long id);
}
