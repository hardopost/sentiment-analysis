package com.hardo.sentimentanalysis.processing;

import java.util.UUID;

public class StatementMapper {

    public static Statement fromDTO(StatementDTO dto) {
        return new Statement(
                UUID.randomUUID(),
                dto.type(),
                dto.companyName(),
                dto.industry(),
                dto.supersector(),
                dto.sector(),
                dto.category(),
                dto.content(),
                dto.sentiment().toLowerCase(),
                dto.period()
        );
    }
}