package org.gssb.accounting.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.file.Path;

import org.gssb.accounting.charge.ChargeType;
import org.junit.Test;

public class WebChargerTest {

   private final static String CONFIG_ROOT_URI = new File("").getAbsolutePath();

   private final static Path   CONFIG_PATH =
         Path.of(CONFIG_ROOT_URI, "src", "test", "resources", "org", "gssb",
                "accounting", "charge", "config", "sample.properties");

   @Test
   public void validateCharge() {
      var fixture = new TestWebCharger(CONFIG_PATH, true);
      var charges = fixture.getCharges();
      assertNotNull(charges);
      assertEquals(ChargeType.values().length, charges.size());
      assertEquals(1630, charges.get(ChargeType.PSDSD.name()));
   }

}
