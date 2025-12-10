package com.github.arseeenyyy.repository;

import com.github.arseeenyyy.models.ImportOperation;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ImportRepository extends GenericRepository<ImportOperation, Long> {
    
}