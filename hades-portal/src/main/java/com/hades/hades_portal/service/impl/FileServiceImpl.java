package com.hades.hades_portal.service.impl;

import com.hades.hades_portal.model.FileMeta;
import com.hades.hades_portal.repository.FileRepository;
import com.hades.hades_portal.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public FileMeta saveFileMeta(FileMeta fileMeta) {
        return fileRepository.save(fileMeta);
    }

    @Override
    public List<FileMeta> getFilesByUsername(String username) {
        return fileRepository.findByOwnerUsername(username);
    }

    @Override
    public FileMeta getFileById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteFileById(Long id) {
        fileRepository.deleteById(id);
    }
}
