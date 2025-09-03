package com.acme.pfm;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvTransactionParser {
    public List<CsvTxnRow> parse(Path path) throws IOException {
        HeaderColumnNameMappingStrategy<CsvTxnRow> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(CsvTxnRow.class);
        try (var br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return new CsvToBeanBuilder<CsvTxnRow>(br)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build()
                    .parse();
        }
    }

}
