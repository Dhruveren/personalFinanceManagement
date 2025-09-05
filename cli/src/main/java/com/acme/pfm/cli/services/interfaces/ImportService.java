package com.acme.pfm.cli.services.interfaces;

import java.nio.file.Path;

public interface ImportService {
    int importCsv(Path csv) throws Exception;
}
