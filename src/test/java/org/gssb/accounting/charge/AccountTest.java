package org.gssb.accounting.charge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;


public class AccountTest {

   private final static String CONFIG_ROOT_URI = new File("").getAbsolutePath();

   private final static Path   CONFIG_PATH =
         Path.of(CONFIG_ROOT_URI, "src", "test", "resources", "org", "gssb",
                "accounting", "charge", "config", "sample.properties");

   private Map<ChargeType, Long> groupChargeTypes(final List<ChargeType> chargeTypes) {
      return chargeTypes.stream()
                        .collect(Collectors.groupingBy(c -> c,
                                                       Collectors.counting()));
   }

   private boolean match(final Map<ChargeType, Integer> expected,
                         final List<ChargeType> result) {
      var grouped = groupChargeTypes(result);
      for (Entry<ChargeType, Integer> exp : expected.entrySet()) {
         assertNotNull(grouped.get(exp.getKey()), "Mismatch with charge type " + exp.getKey());
         assertEquals((int)exp.getValue(), (int)(long)grouped.get(exp.getKey()),
                      "Mismatch with charge type " + exp.getKey());
      }
      // make sure we do not have a disjoined result
      assertEquals(expected.size(), grouped.size());

      return true;
   }

   @Test
   public void testPreOrKGrade() {
      var fixture = new TestAccount(CONFIG_PATH);
      for (int i=-1; i<1; i++) {
         List<Integer> classes = List.of(i);
         var expected = new HashMap<ChargeType, Integer>();
         expected.put(ChargeType.RegFe, 1);
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
      var fixture = new TestAccount(CONFIG_PATH);
      List<Integer> classes = List.of(1);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.RegFe, 1);
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
   public void test2To8and10Grade() {
      for (int i : List.of(2,3,4,5,6,7,8,12)) {
         var fixture = new TestAccount(CONFIG_PATH);
         List<Integer> classes = List.of(i);
         var expected = new HashMap<ChargeType, Integer>();
         expected.put(ChargeType.RegFe, 1);
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
         var fixture = new TestAccount(CONFIG_PATH);
         List<Integer> classes = List.of(i);
         var expected = new HashMap<ChargeType, Integer>();
         expected.put(ChargeType.RegFe, 1);
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
      var fixture = new TestAccount(CONFIG_PATH);

      List<Integer> classes = List.of(-1,-1,0);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.RegFe, 1);
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
      var fixture = new TestAccount(CONFIG_PATH);

      List<Integer> classes = List.of(-1, 0, 1, 9, 11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.RegFe, 1);
      expected.put(ChargeType.PSDSD, 1);
      expected.put(ChargeType._2n3rP, 3);
      expected.put(ChargeType._2_3DS, 1);
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
      var fixture = new TestAccount(CONFIG_PATH);

      List<Integer> classes = List.of(-1, 0, 1, 11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.RegFe, 1);
      expected.put(ChargeType.PSDSD, 1);
      expected.put(ChargeType._2n3rP, 2);
      expected.put(ChargeType._2_3DS, 1);
      expected.put(ChargeType.PreKF, 2);
      expected.put(ChargeType._1grad, 1);
      expected.put(ChargeType.DSDSp, 1);

      var charges = fixture.createCharges(classes, "FAM0000", false);
      match(expected, charges);
   }

   @Test
   public void testKand5and11Grades() {
      var fixture = new TestAccount(CONFIG_PATH);

      List<Integer> classes = List.of(0, 5, 11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.RegFe, 1);
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
      var fixture = new TestAccount(CONFIG_PATH);

      List<Integer> classes = List.of(6, 11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.RegFe, 1);
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
      var fixture = new TestAccount(CONFIG_PATH);

      List<Integer> classes = List.of(11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.RegFe, 1);
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
      var fixture = new TestAccount(CONFIG_PATH);

      List<Integer> classes = List.of(11, 11);
      var expected = new HashMap<ChargeType, Integer>();
      expected.put(ChargeType.RegFe, 1);
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