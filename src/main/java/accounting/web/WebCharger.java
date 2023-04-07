package accounting.web;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import accounting.charge.ChargeType;

public class WebCharger {
   
   private static final String SYCAMORE_BASE_URL =
         "https://app.sycamoreschool.com";
   private static final String GSSB_LOGIN_URL = 
         SYCAMORE_BASE_URL + "/index.php?task=newlogin&schoolid=2132";
   private static final String LOGOFF_URL = 
         SYCAMORE_BASE_URL + "/logout.php";
   private static final String FAMILIY_CHARGE =
         SYCAMORE_BASE_URL + "/familycharge.php";
   
   private final boolean testRun;
   
   public WebCharger(final boolean testRun) {
      super();
      this.testRun = testRun;
   }

   public RemoteWebDriver login(final String userName, final String password) {
      RemoteWebDriver driver = new FirefoxDriver();
      driver.manage().window().maximize();
      
      // got to sycamore school
      driver.get(GSSB_LOGIN_URL);
      
      // login to sycamore
      WebElement usernameElement = driver.findElement(By.name("entered_login"));
      usernameElement.sendKeys(userName); 
      
      WebElement passwordElement = driver.findElement(By.name("entered_password"));
      passwordElement.sendKeys(password);
      
      WebElement login = driver.findElement(By.cssSelector(".loginLoginButtonDiv > h3"));
      login.click();
      return driver;
   }

   public void logoffAndColseWindow(final RemoteWebDriver driver) {
      // logoff
      driver.get(LOGOFF_URL);
      
      // close browser window
      driver.close();
   }

   private Map<ChargeType, Long> groupChargeTypes(final List<ChargeType> chargeTypes) {
      return chargeTypes.stream()
                        .collect(Collectors.groupingBy(c -> c,
                                                       Collectors.counting()));
   }
   
   private void selectCharge(final int i, final String label, final Long value,
                             final RemoteWebDriver driver) {
      var countName  = "cc" + i + "count";
      var chargeName = "ccid" + i;
      
      var countVal = new DecimalFormat("#").format(value);
      var chargeVal = label;
      
      var count = driver.findElement(By.name(countName));
      count.clear();
      count.sendKeys(countVal);
      
      Select charge = new Select(driver.findElement(By.name(chargeName)));
      charge.selectByVisibleText(chargeVal);
   }
   
   private void writeCharge(final int i, final String label, final double value,
                            final RemoteWebDriver driver) {
      var chargeName = "comment" + i;
      var amountName = "amount"  + i;
      
      var textBoxName = driver.findElement(By.name(chargeName));
      textBoxName.sendKeys(label);
      
      var textBoxValue = driver.findElement(By.name(amountName));
      var val = new DecimalFormat("#.0#").format(value);
      textBoxValue.clear();
      textBoxValue.sendKeys(val);
      
   }
   
   public void chargeAccountAndCreateInvoice(final String familyId,
                                             final List<ChargeType> chargeTypes,
                                             final RemoteWebDriver driver) {
      //  open char UI based on family ID
      driver.get(FAMILIY_CHARGE + "?fid=" + familyId + "&update=familysummary");
      

      // set the account for the charges, which is always '1 : Registration'
      Select account = new Select( driver.findElement(By.name("acctid")));
      account.selectByVisibleText("1 : Registration");
      
      var sorted = groupChargeTypes(chargeTypes)
                        .entrySet()
                        .stream()
                        .sorted((g1, g2) -> g1.getKey().compareTo(g2.getKey()) )
                        .collect(Collectors.toList());
      int i = 1;
      for (Entry<ChargeType, Long> entry: sorted) {
         if (i<6) {
            selectCharge(i++, entry.getKey().label, entry.getValue(), driver);
         } else {
            var amount = entry.getKey().charge;
            for (int j = 0; j < entry.getValue(); j++) {
               writeCharge(i-5, entry.getKey().label, amount, driver);
               i++;
            }
         }
      }
      
//      var maxCharges = Math.min(5, chargeTypes.size());
//      for (int i=0; i < maxCharges; i++) {
//         var chargeName = "ccid" + (i+1);
//         var chargeVal = chargeTypes.get(i).label;
//         Select charge = new Select(driver.findElement(By.name(chargeName)));
//         charge.selectByVisibleText(chargeVal);
//      }
      
//      if (chargeTypes.size() > 5) {
//         var otherCharges = chargeTypes.subList(5, chargeTypes.size());
//         for (int i=0; i < otherCharges.size(); i++) {
//            var chargeName = "comment" + (i+1);
//            var amountName = "amount"  + (i+1);
//            
//            var chargeNameVal   = otherCharges.get(i).label;
//            var chargeAmountVal = otherCharges.get(i).charge;
//            
//            var textBoxName = driver.findElement(By.name(chargeName));
//            textBoxName.sendKeys(chargeNameVal);
//            
//            var textBoxValue = driver.findElement(By.name(amountName));
//            var val = new DecimalFormat("#.0#").format(chargeAmountVal);
//            textBoxValue.clear();
//            textBoxValue.sendKeys(val);
//         }
//      }
      
      if (!testRun) {
         // fail right now to prevent accidental transactions
         assert(1!=0);
         WebElement submit = driver.findElement(By.name("thesubmit"));
         submit.click();
      }
      
   }

}
