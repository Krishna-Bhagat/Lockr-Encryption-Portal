package com.hades.hades_portal.service;

import com.hades.hades_portal.model.FileMeta;

import java.util.List;

public interface FileService {
    FileMeta saveFileMeta(FileMeta fileMeta);
    List<FileMeta> getFilesByUsername(String username);
    FileMeta getFileById(Long id);
    void deleteFileById(Long id);
}
