package com.acme.pfm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "pfm", mixinStandardHelpOptions = true, version = "0.1.0", description = "Says hello")

public class HelloCommand implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(HelloCommand.class);

    public void run() {
        log.info("HelloCommand started");
        System.out.println("Hello World!");
    }


    public static void main(String[] args) {
        picocli.CommandLine.run(new HelloCommand(), args);
    }
}