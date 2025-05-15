package com.hardo.sentimentanalysis.processing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatementValidationRepository extends JpaRepository<StatementValidation, Long> {
    // Custom query methods can be defined here if needed

}
