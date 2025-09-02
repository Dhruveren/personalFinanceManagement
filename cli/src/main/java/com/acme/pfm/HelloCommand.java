package com.acme.pfm;


import picocli.CommandLine;

@CommandLine.Command(name = "pfm", mixinStandardHelpOptions = true, version = "0.1.0", description = "Says hello")

public class HelloCommand implements Runnable {
    public void run() {
        System.out.println("Hello World!");
    }

    public static void main(String[] args) {
        picocli.CommandLine.run(new HelloCommand(), args);
    }
}