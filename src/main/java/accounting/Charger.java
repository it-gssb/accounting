package accounting;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.RemoteWebDriver;

import accounting.charge.Account;
import accounting.csv.CsvLoader;
import accounting.data.CsvData;
import accounting.web.WebCharger;

public class Charger {
   
   private final static Logger logger = LogManager.getLogger(Charger.class);
   
   private final CsvLoader loader     = new CsvLoader();
   private final Account account      = new Account();
   private final WebCharger charger;
   
   public Charger() {
      this(false);
   }
   
   public Charger(final boolean testRun) {
      super();
      charger = new WebCharger(testRun);
   }
   
   private void chargeFamily(final RemoteWebDriver driver, final String familyID,
                             final List<CsvData> records) {
      var charges = account.calculateCharges(familyID, records);
      var msg = records.get(0).getFamilyCode() + ": " +
                charges.stream()
                       .map(c -> c.toString())
                       .collect(Collectors.joining(", "));
      logger.debug(msg);
      charger.chargeAccountAndCreateInvoice(familyID, charges, driver);
   }
   
   public void charge(final String fileName,
                      final String userName, final String password) {
      var records = loader.load(fileName)
                          .entrySet()
                          .stream()
                          .filter(e -> e.getValue() != null && 
                                       !e.getValue().isEmpty())
                          .map(e -> e.getValue())
                          .sorted((e1,e2) -> e1.get(0).getFamilyCode()
                                               .compareTo(e2.get(0).getFamilyCode()))
                          .collect(Collectors.toList());
    
      // login to sycamore
      RemoteWebDriver driver = charger.login(userName, password);
      
      // charge every family and create invoice
      for (List<CsvData> entry : records) {
          chargeFamily(driver, entry.get(0).getFamilyId(), entry);
      }
      
      // logoff from Sycamore
      charger.logoffAndColseWindow(driver);
   }

}
