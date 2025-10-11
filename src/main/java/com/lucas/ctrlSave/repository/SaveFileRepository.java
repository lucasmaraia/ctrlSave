package com.lucas.ctrlSave.repository;

import com.lucas.ctrlSave.model.entity.SaveFile;
import com.lucas.ctrlSave.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaveFileRepository extends JpaRepository<SaveFile,Long> {

    Optional<SaveFile> findById(Long id);

    List<SaveFile> findByUser(User user);

    Optional<SaveFile> findOneByUser(User user);

}
