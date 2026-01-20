package com.b2b.b2b.modules.crm.pipeline.service;

import com.b2b.b2b.modules.crm.pipeline.payloads.PipelineMigrationRequestDTO;

public interface MigrationService {
    void migrateAndInactivate(Integer id, PipelineMigrationRequestDTO request);
}
