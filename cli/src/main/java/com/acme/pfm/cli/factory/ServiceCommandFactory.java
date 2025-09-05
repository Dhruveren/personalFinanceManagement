package com.acme.pfm.cli.factory;

import com.acme.pfm.cli.config.Config;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;
import com.acme.pfm.cli.services.ServiceFactory;
import com.acme.pfm.cli.commands.*;

import com.acme.pfm.cli.services.interfaces.BudgetService;
import com.acme.pfm.cli.commands.BudgetSetCommand;
import com.acme.pfm.cli.commands.BudgetGetCommand;
import com.acme.pfm.cli.commands.BudgetListCommand;
import com.acme.pfm.cli.commands.BudgetDeleteCommand;
import com.acme.pfm.cli.commands.BudgetReportCommand;


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
        // Budget commands
        else if (cls == BudgetSetCommand.class) {
            return (K) new BudgetSetCommand(sf.getBudgetService());
        } else if (cls == BudgetGetCommand.class) {
            return (K) new BudgetGetCommand(sf.getBudgetService());
        } else if (cls == BudgetListCommand.class) {
            return (K) new BudgetListCommand(sf.getBudgetService());
        } else if (cls == BudgetDeleteCommand.class) {
            return (K) new BudgetDeleteCommand(sf.getBudgetService());
        } else if (cls == BudgetReportCommand.class) {
            return (K) new BudgetReportCommand(sf.getBudgetService());
        }

        // fallback to default factory for any other class
        return CommandLine.defaultFactory().create(cls);
    }
}
