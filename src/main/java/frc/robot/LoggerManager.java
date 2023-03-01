package frc.robot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj.Filesystem;

public class LoggerManager {
  private static Path operatingDirectory = Filesystem.getOperatingDirectory().toPath();
  private static Path archiveDirectory = operatingDirectory.resolve(Constants.ARCHIVE_DIRECTORY_NAME);
  private static Logger logger;

  static {
    logger = Logger.getLogger("frc.robot");
  }

  /**
   * Backs up the logs saved in ${LAUNCH}/logs into a gzip compressed tar file.
   */
  public static void saveLastBootLogs() throws IOException {
    int greatestLogNumber = -1;

    archiveDirectory.toFile().mkdirs();

    String[] archiveFiles = archiveDirectory.toFile().list();
    
    for (String fileName : archiveFiles) {
      Scanner nameMatcher = new Scanner(fileName);

      try {
        String intermediateString;
        int parsedLogNumber;

        nameMatcher.findInLine("log-archive-(\\d+).tar.gz");
        nameMatcher.match();

        // lol
        intermediateString = fileName.substring("log-archive-".length());
        parsedLogNumber = Integer.parseInt(intermediateString.substring(0, intermediateString.indexOf(".tar.gz")));

        if (parsedLogNumber > greatestLogNumber)
          greatestLogNumber = parsedLogNumber;
      }
      catch (IllegalStateException e) {
        // Meh
      }

      nameMatcher.close();
    }

    String logArchiveName = String.format("log-archive-%d.tar.gz", greatestLogNumber + 1);

    ProcessBuilder processBuilder = new ProcessBuilder(
      "/usr/bin/tar", 
      "-czf", 
      operatingDirectory
        .resolve(Constants.ARCHIVE_DIRECTORY_NAME)
        .resolve(logArchiveName)
        .toString(), 
      operatingDirectory.resolve(Constants.LOG_DIRECTORY_NAME).toString()
    );
    
    logger.fine(
      String.format("Spawned process %d to backup logs into `%s`.", 
      processBuilder.start().pid(), 
      logArchiveName)
    );
  }

  private static long getNonRecursiveDirSize(File directory) {
    long totalSize = 0;

    for (File file : directory.listFiles())
      totalSize += file.length();
    
    return totalSize;
  }

  /**
   * Checks whether or not 
   */
  public static void deleteOldLogs() throws IOException {
    File archiveDirectoryFile = archiveDirectory.toFile();

    if (!archiveDirectoryFile.exists()) {
      logger.severe("Archive directory does not exist!");
      return;
    }

    File[] archiveFiles = archiveDirectoryFile.listFiles();
    
    int i = 0;
    while (getNonRecursiveDirSize(archiveDirectoryFile) > Constants.MAX_LOG_DIR_SIZE_BYTES && i < archiveFiles.length)
      archiveFiles[i++].delete();
  }
  
  /**
   * Initializes the handler which logs to the file system
   */
  public static void initializeFileLogger() throws IOException {
    FileHandler fileHandler;
    Logger globalLogger = Logger.getLogger("");
    String logDir = Filesystem.getOperatingDirectory().toPath().resolve(Constants.LOG_DIRECTORY_NAME).toString();

    // Creates the directory that we put the logs into :p
    new File(logDir).mkdirs();

    // Actually initializes the handler
    fileHandler = new FileHandler(logDir + "/rio%g-%u.log");
    globalLogger.addHandler(fileHandler);
  }

  /**
   * Initializes the base logger, console logging, and file logging facilties
   */
  public static void initializeLogger() {
    LogManager logManager = LogManager.getLogManager();
    
    // Gets rid of the default console handler so that we can give it our own.
    logManager.reset();

    Logger globalLogger = Logger.getLogger("");
    
    // Reads the log configuration in `{DEPLOY}/logger_config.txt` into the system configuration.
    try {
      FileInputStream configStream = new FileInputStream(new File(Filesystem.getDeployDirectory(), "logger_config.txt"));
      logManager.readConfiguration(configStream);
      configStream.close();
    } catch (IOException e) {
      // Here we can't use our logger since we removed the ConsoleHandler from it, so it'll just print into the void.
      System.out.println("Failed to initialize configuration for log manager! Falling back to defaults...");
      System.out.println("\n==== BEGIN ERROR MESSAGE ====");
      e.printStackTrace();
      System.out.println("==== END ERROR MESSAGE ====\n");
    }

    // This has to be set after the logger's configuration is loaded for reasons
    globalLogger.setLevel(Level.ALL);

    // Initializes the handler which logs to the console
    ConsoleHandler consoleHandler = new ConsoleHandler();
    globalLogger.addHandler(consoleHandler);
  }
}
