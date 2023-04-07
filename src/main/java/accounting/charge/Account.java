package accounting.charge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import accounting.data.CsvData;

public class Account {
   
   protected Set<String> commiteeFamilyCodes;
   
   public Account() {
      super();
      commiteeFamilyCodes = Set.of("CRA1685", "DOW2188", "HAA2167", "JOL2087", 
                                   "KUN1244",  "RIC1259", "SAS1680", "SIM1949");
   }
   
   protected ChargeType getSkCredit(final String familyCode) {
      return commiteeFamilyCodes.contains(familyCode) ? ChargeType.SK100
                                                      : null;
   }
   
   private ChargeType getCredit(final int grade) {
      ChargeType credit = null;
      
      switch (grade) {
         case 11: credit = ChargeType.PCHal;
                  break;
         default: credit = ChargeType.PCFul;
                  break;
      }
      
      return credit;
   }
   
   private ChargeType getFee(final int grade) {
      ChargeType fee = null;
      
      switch (grade) {
         case -1:
         case 0: fee = ChargeType.PreKF;
                 break;
         case 1: fee = ChargeType._1grad;
                 break;
         case 9:
         case 10:
         case 11: fee = ChargeType.DSDSp;
                  break;
      }
      
      return fee;
   }
   
   private ChargeType getCharge(final int grade, final boolean first) {
      ChargeType charge;
      
      if (first) {
         charge = (grade != 11) ? ChargeType.PSDSD : ChargeType._1stDS;
      } else {
         charge = (grade != 11) ? ChargeType._2n3rP : ChargeType._2_3DS;
      }
      
      return charge;
   }
   
   protected List<ChargeType> createCharges(final List<Integer> classes, 
                                            final String familyCode,
                                            final boolean isHelper) {
      List<ChargeType> charges = new ArrayList<>();
      if (classes.size()>3) {
         charges.add(ChargeType.FAM);
      } else {
         for (int i=0; i< classes.size(); i++) {
            charges.add(getCharge(classes.get(i), i==0));
         }
      }
      
      classes.forEach(c -> {var fee = getFee(c);
                            if (fee != null) {
                               charges.add(fee);
                            } });
      
      if (isHelper) {
         charges.add(getCredit(classes.get(0)));
      }
      
      var skCredit = getSkCredit(familyCode);
      if (skCredit != null) {
         charges.add(skCredit);
      }
      return charges;
   }
   
   public List<ChargeType> calculateCharges(final String familyId, 
                                            final List<CsvData> records) {
      assert(records != null && !records.isEmpty());
      
      var choice = records.get(0).getParentHelperChoice();
      var familyCode = records.get(0).getFamilyCode();
      boolean isHelper = !choice.isEmpty() && !choice.equals("Non Participation");
      
      var classes = records.stream()
                           .map(r -> r.getGrade())
                           .map(g -> (g == 12) ? 8 : g) // 10th grade is charges as 8th grade
                           .sorted()
                           .collect(Collectors.toList());
      
      List<ChargeType> charges = createCharges(classes, familyCode, isHelper);
      
      return charges;
   }

}
