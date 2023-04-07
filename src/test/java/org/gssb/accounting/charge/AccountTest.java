package org.gssb.accounting.charge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Test;

public class AccountTest {

   private Map<ChargeType, Long> groupChargeTypes(final List<ChargeType> chargeTypes) {
      return chargeTypes.stream()
                        .collect(Collectors.groupingBy(c -> c,
                                                       Collectors.counting()));
   }

   private boolean match(final Map<ChargeType, Integer> expected,
                         final List<ChargeType> result) {
      var grouped = groupChargeTypes(result);
      for (Entry<ChargeType, Integer> exp : expected.entrySet()) {
         assertNotNull("Mismatch with charge type " + exp.getKey(),
                       grouped.get(exp.getKey()));
         assertEquals("Mismatch with charge type " + exp.getKey(),
                      (int)exp.getValue(), (int)(long)grouped.get(exp.getKey()));
      }
      // make sure we do not have a disjoined result
      assertEquals(expected.size(), grouped.size());

      return true;
   }

   @Test
   public void testPreOrKGrade() {
      var fixture = new TestAccount();
      for (int i=-1; i<1; i++) {
         List<Integer> classes = List.of(i);
         var expected = new HashMap<ChargeType, Integer>();
         expected.put(ChargeType.PSDSD, 1);
         expected.put(ChargeType.PreKF, 1);

         var charges = fixture.createCharges(classes, "FAM0000", false);
         match(expected, charges);

         expected.put(ChargeType.PCFul, 1);
         var charges2 = fixture.createCharges(classes, "FAM0000", true);
         match(expected, charges2);

         expected.put(ChargeType.SK100, 1);
         var charges3 = fixture.createCharges(classes, "SKM1234", true);
         match(expected, charges3);
      }
   }

   @Test
   public void test1stGrade() {
      var fixture = new TestAccount();
      List<Integer> classes = List.of(1);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.PSDSD, 1);
      expected.put(ChargeType._1grad, 1);

      var charges = fixture.createCharges(classes, "FAM0000", false);
      match(expected, charges);

      expected.put(ChargeType.PCFul, 1);
      var charges2 = fixture.createCharges(classes, "FAM0000", true);
      match(expected, charges2);

      expected.put(ChargeType.SK100, 1);
      var charges3 = fixture.createCharges(classes, "SKM1234", true);
      match(expected, charges3);
   }

   @Test
   public void test2To10Grade() {
      for (int i : List.of(2,3,4,5,6,7,8,12)) {
         var fixture = new TestAccount();
         List<Integer> classes = List.of(i);
         var expected = new HashMap<ChargeType, Integer>();
         expected.put(ChargeType.PSDSD, 1);

         var charges = fixture.createCharges(classes, "FAM0000", false);
         match(expected, charges);

         expected.put(ChargeType.PCFul, 1);
         var charges2 = fixture.createCharges(classes, "FAM0000", true);
         match(expected, charges2);

         expected.put(ChargeType.SK100, 1);
         var charges3 = fixture.createCharges(classes, "SKM1234", true);
         match(expected, charges3);
      }
   }

   @Test
   public void testDSD1Grade() {
      for (int i : List.of(9,10)) {
         var fixture = new TestAccount();
         List<Integer> classes = List.of(i);
         var expected = new HashMap<ChargeType, Integer>();
         expected.put(ChargeType.PSDSD, 1);
         expected.put(ChargeType.DSDSp, 1);

         var charges = fixture.createCharges(classes, "FAM0000", false);
         match(expected, charges);

         expected.put(ChargeType.PCFul, 1);
         var charges2 = fixture.createCharges(classes, "FAM0000", true);
         match(expected, charges2);

         expected.put(ChargeType.SK100, 1);
         var charges3 = fixture.createCharges(classes, "SKM1234", true);
         match(expected, charges3);
      }
   }

   @Test
   public void testThreeBelowGradeTwo() {
      var fixture = new TestAccount();

      List<Integer> classes = List.of(-1,-1,0);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.PSDSD, 1);
      expected.put(ChargeType._2n3rP, 2);
      expected.put(ChargeType.PreKF, 3);

      var charges = fixture.createCharges(classes, "FAM0000", false);
      match(expected, charges);

      expected.put(ChargeType.PCFul, 1);
      var charges2 = fixture.createCharges(classes, "FAM0000", true);
      match(expected, charges2);

      expected.put(ChargeType.SK100, 1);
      var charges3 = fixture.createCharges(classes, "SKM1234", true);
      match(expected, charges3);
   }

   @Test
   public void testFiveVariousGrades() {
      var fixture = new TestAccount();

      List<Integer> classes = List.of(-1, 0, 1, 9, 11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.FAM, 1);
      expected.put(ChargeType.PreKF, 2);
      expected.put(ChargeType._1grad, 1);
      expected.put(ChargeType.DSDSp, 2);

      var charges = fixture.createCharges(classes, "FAM0000", false);
      match(expected, charges);

      expected.put(ChargeType.PCFul, 1);
      var charges2 = fixture.createCharges(classes, "FAM0000", true);
      match(expected, charges2);

      expected.put(ChargeType.SK100, 1);
      var charges3 = fixture.createCharges(classes, "SKM1234", true);
      match(expected, charges3);
   }

   @Test
   public void testFourVariousGrades() {
      var fixture = new TestAccount();

      List<Integer> classes = List.of(-1, 0, 1, 11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.FAM, 1);
      expected.put(ChargeType.PreKF, 2);
      expected.put(ChargeType._1grad, 1);
      expected.put(ChargeType.DSDSp, 1);

      var charges = fixture.createCharges(classes, "FAM0000", false);
      match(expected, charges);
   }

   @Test
   public void testKand5and11Grades() {
      var fixture = new TestAccount();

      List<Integer> classes = List.of(0, 5, 11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.PSDSD, 1);
      expected.put(ChargeType._2n3rP, 1);
      expected.put(ChargeType._2_3DS, 1);
      expected.put(ChargeType.PreKF, 1);
      expected.put(ChargeType.DSDSp, 1);

      var charges = fixture.createCharges(classes, "FAM0000", false);
      match(expected, charges);

      expected.put(ChargeType.PCFul, 1);
      var charges2 = fixture.createCharges(classes, "FAM0000", true);
      match(expected, charges2);
   }

   @Test
   public void test6And11Grades() {
      var fixture = new TestAccount();

      List<Integer> classes = List.of(6, 11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.PSDSD, 1);
      expected.put(ChargeType._2_3DS, 1);
      expected.put(ChargeType.DSDSp, 1);

      var charges = fixture.createCharges(classes, "FAM0000", false);
      match(expected, charges);

      expected.put(ChargeType.PCFul, 1);
      var charges2 = fixture.createCharges(classes, "FAM0000", true);
      match(expected, charges2);
   }

   @Test
   public void test11Grades() {
      var fixture = new TestAccount();

      List<Integer> classes = List.of(11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType._1stDS, 1);
      expected.put(ChargeType.DSDSp, 1);

      var charges = fixture.createCharges(classes, "FAM0000", false);
      match(expected, charges);

      expected.put(ChargeType.PCHal, 1);
      var charges2 = fixture.createCharges(classes, "FAM0000", true);
      match(expected, charges2);
   }

   @Test
   public void testTwo11Grades() {
      var fixture = new TestAccount();

      List<Integer> classes = List.of(11, 11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType._1stDS, 1);
      expected.put(ChargeType._2_3DS, 1);
      expected.put(ChargeType.DSDSp, 2);

      var charges = fixture.createCharges(classes, "FAM0000", false);
      match(expected, charges);

      expected.put(ChargeType.PCHal, 1);
      var charges2 = fixture.createCharges(classes, "FAM0000", true);
      match(expected, charges2);
   }

}