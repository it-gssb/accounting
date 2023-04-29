package org.gssb.accounting;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gssb.accounting.charge.Account;
import org.gssb.accounting.csv.CsvLoader;
import org.gssb.accounting.data.CsvData;
import org.gssb.accounting.web.WebCharger;
import org.openqa.selenium.remote.RemoteWebDriver;

@SuppressWarnings("deprecation")
public class Charger {

   private final static Logger logger = LogManager.getLogger(Charger.class);

   //
   // setup for command line
   //

   private static class Configuration {
      String  userName = null;
      String  password = null;
      String  fileName = null;
      boolean simulate = false;
   }

   //
   // Injected Services
   //

   private final CsvLoader loader;
   private final Account account;
   private final WebCharger charger;

   public Charger(final Configuration config) {
      super();
      this.loader  = new CsvLoader();
      this.account = new Account();
      this.charger = new WebCharger(config.simulate);
   }

   private void chargeFamily(final RemoteWebDriver driver, final String familyID,
                             final List<CsvData> records) {
      var charges = this.account.calculateCharges(familyID, records);
      var msg = records.get(0).getFamilyCode() + ": " +
                charges.stream()
                       .map(c -> c.toString())
                       .collect(Collectors.joining(", "));
      logger.debug(msg);
      this.charger.chargeAccountAndCreateInvoice(familyID, charges, driver);
   }

   public void charge(final String fileName,
                      final String userName, final String password) {
      var records = this.loader.load(fileName)
                          .entrySet()
                          .stream()
                          .filter(e -> e.getValue() != null &&
                                       !e.getValue().isEmpty())
                          .map(e -> e.getValue())
                          .sorted((e1,e2) -> e1.get(0).getFamilyCode()
                                               .compareTo(e2.get(0).getFamilyCode()))
                          .collect(Collectors.toList());

      // login to sycamore
      RemoteWebDriver driver = this.charger.login(userName, password);

      // charge every family and create invoice
      for (List<CsvData> entry : records) {
          chargeFamily(driver, entry.get(0).getFamilyId(), entry);
      }

      // logoff from Sycamore
      this.charger.logoffAndColseWindow(driver);
   }

   private void run(final Configuration config) {
      charge(config.fileName, config.userName, config.password);
      System.exit(0);
   }

   //
   // Process command line
   //

   private final static Options options = new Options();

   private static void help() {
      HelpFormatter formater = new HelpFormatter();
      formater.printHelp(Charger.class.getCanonicalName(), options);
      System.exit(1);
   }

   private static Configuration parse(final String[] args) {
      Configuration config = new Configuration();
      CommandLineParser parser = new BasicParser();
      CommandLine cmd = null;
      try {
         cmd = parser.parse(options, args);

         if (cmd.hasOption("h"))
            help();

         if (cmd.hasOption("f")) {
            config.fileName = cmd.getOptionValue("f");
         } else {
            logger.error("Missing CSV file.");
            help();
         }

         if (cmd.hasOption("u")) {
            config.userName = cmd.getOptionValue("u");
         } else {
            logger.error("Missing Sycamore user name.");
            help();
         }

         if (cmd.hasOption("p")) {
            config.password = cmd.getOptionValue("p");
         } else {
            logger.error("Missing Sycamore password.");
            help();
         }

         if (cmd.hasOption("s")) {
            config.simulate = true;
         }
      } catch (ParseException e) {
         logger.error("Failed to parse comand line properties", e);
         help();
      }
      return config;
   }

   /**
    *
    * @param args
    */
   public static void main(final String[] args) {
      options.addOption("h", "help", false, "Show help.");
      options.addOption("u", "username", true, "Sycamore user");
      options.addOption("p", "password", true, "Sycamore user password.");
      options.addOption("s", "simulate", false,
            "Simulation: Biller will not commit charges.");
      options.addOption("f", "file", true,
            "CSV File with family and student class details.");

      Configuration config = parse(args);
      Charger charger = new Charger(config);
      charger.run(config);
   }

}
