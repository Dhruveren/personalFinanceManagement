package com.acme.pfm.cli.factory;

import com.acme.pfm.cli.config.Config;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import com.acme.pfm.cli.services.ServiceFactory;
import com.acme.pfm.cli.commands.*;

public class ServiceCommandFactory implements CommandLine.IFactory {
    private final ServiceFactory sf;
    public ServiceCommandFactory(Config cfg) {
        this.sf = ServiceFactory.getInstance(cfg);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <K> K create(Class<K> cls) throws Exception {
        if (cls == ListCommand.class) {
            return (K) new ListCommand(sf.getTransactionService());
        } else if (cls == AddCommand.class) {
            return (K) new AddCommand(sf.getTransactionService());
        } else if (cls == SummaryCommand.class) {
            return (K) new SummaryCommand(sf.getTransactionService());
        } else if (cls == ImportCommand.class) {
            return (K) new ImportCommand(sf
                    .getImportService());
        }
        // fallback to default factory for any other class
        return CommandLine.defaultFactory().create(cls);
    }
}
