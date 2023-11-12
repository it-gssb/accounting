package org.gssb.accounting.web;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.gssb.accounting.config.AppProperties;

public class TestWebCharger extends WebCharger {

   private Map<String, Double> charges;

   public TestWebCharger(final Path configPath, final boolean testRun) {
      super(configPath, testRun);
   }

   @Override
   Map<String, Double> createChargeMapping(final AppProperties properties,
                                           final List<String> chargeNames) {
      var values = super.createChargeMapping(properties, chargeNames);
      this.charges = values;
      return values;
   }

   Map<String, Double> getCharges() {
      return this.charges;
   }

}
