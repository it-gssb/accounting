package org.gssb.accounting.charge;

public enum ChargeType {

   PSDSD  ("1st Child Preschool - Gr8, DSD1 & DSD2 (1st year)"),
   _2n3rP ("2nd & 3rd Child Preschool to DSD2 (1st year)"),
   _1stDS ("1st Child DSD2 (1/2 year)"),
   _2_3DS ("2nd & 3rd Child DSD2 (1/2 year)"),

   PreKF  ("PreSchool & Kindergarten Supplement"),
   _1grad ("1st Grade supplement"),
   DSDSp  ("DSD Supplement"),
   PCFul  ("Full Parent Participation Credit"),
   PCHal  ("Half Parent Participation Credit"),
   SK100  ("School Committee Member Credit"),
   RegFe  ("Registration Fee");

   public final String label;

   private ChargeType(final String label) {
       this.label  = label;
   }
}
