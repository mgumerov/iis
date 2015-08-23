import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;

public class Test {
  private static final Logger log = LoggerFactory.getLogger(Test.class);

  public static void main(String[] args) {
    final FileSystemXmlApplicationContext context;
    try {
      context = new FileSystemXmlApplicationContext("context.xml");
      try {
        context.registerShutdownHook();
        final Test test = context.getBean("test", Test.class);
        test.run(args);
      } finally {
        context.close();
      }
    }
    catch (Exception e) {
      final String errMsg = "Failure: " + e.getMessage();
      log.error(errMsg, e);
      System.err.println(errMsg);
    }
  }

  private Map<Option, Command> commands;
  public void setCommands(final Map<Option, Command> commands) { this.commands = commands; }

  private Printer printer;
  public void setPrinter(final Printer printer) { this.printer = printer; }

  private void run(final String[] args) {
    final CommandLine commandLine;
    final Options posixOptions;
    try {
      final OptionGroup optionGroup = new OptionGroup(); //mutually exclusive options!
      for (final Option option : commands.keySet())
        optionGroup.addOption(option);

      posixOptions = new Options();
      posixOptions.addOptionGroup(optionGroup);

      final CommandLineParser cmdLinePosixParser = new PosixParser();
      commandLine = cmdLinePosixParser.parse(posixOptions, args);
    } catch (ParseException e) {
//TODO why no errors get caught for invalid short-form options?
         log.error(e.getMessage(), e);
         System.err.println(e.getMessage());
         return;
    }

    final Option[] cmdLineOptions = commandLine.getOptions();
    if (cmdLineOptions.length == 0) {
      new HelpFormatter().printHelp( "run.cmd", posixOptions );
      return;
    }

    try {
      for (final Option option : cmdLineOptions) {
        if (commandLine.hasOption(option.getOpt())) {
          log.info(option.toString());
          final String[] arguments = commandLine.getOptionValues(option.getOpt());
          commands.get(option).execute(arguments);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      System.err.println(e.getMessage());
    }
    printer.printout();
  }
}