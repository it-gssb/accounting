package accounting.charge;

public enum ChargeType {
   
   PSDSD  ("1st Child Preschool - Gr8, DSD1 & DSD2 (1st year)", 1630),
   _2n3rP ("2nd & 3rd Child Preschool to DSD2 (1st year)",      1110),
   _1stDS ("1st Child DSD2 (1/2 year)",                          880),
   _2_3DS ("2nd & 3rd Child DSD2 (1/2 year)",                    600),
   FAM    ("Family (4 and more children)",                      4980),
   
   PreKF  ("PreSchool & Kindergarten Supplement",                180),
   _1grad ("1st Grade supplement",                               100),
   DSDSp  ("DSD Supplement",                                      50),
   PCFul  ("Full Parent Participation Credit",                  -315),
   PCHal  ("Half Parent Participation Credit",                  -157.50),
   SK100  ("School Committee Member Credit",                    -100);
   
   public final String label;
   public final double charge;

   private ChargeType(final String label, double charge) {
       this.label  = label;
       this.charge = charge;
   }
}
