package org.gssb.accounting.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gssb.accounting.exception.UnrecoverableException;

public class AppProperties extends AbstractConfiguration {

   private static final Logger logger = LogManager.getLogger(AppProperties.class);

   private static final String MISSING_CONFIGURATION =
         "The mandatory property '%s' is not defined in the properties file.";

   private final static String SK_FAMILIY_CODES     = "sk.family_codes";

   private final static String CHARGE_BASE          = "charge";


   public AppProperties(final Path propertyFile) {
      super(propertyFile);
   }

   private void createError(final String missingProperty) {
      String msg = String.format(MISSING_CONFIGURATION, missingProperty);
      logger.error(msg);
      throw new UnrecoverableException(msg);
   }

   private String getMandatoryProperty(final String propertyKey) {
      String value = getProperty(propertyKey);
      if (value==null) {
         createError(propertyKey);
      }
      return value;
   }

   //
   // Define Committee Members by their family code
   //

   public List<String> getSkFamilyCodes() {
      return getPropertyList(SK_FAMILIY_CODES);
   }

   //
   // Charges
   //

//   private List<String> getTemplateKeys() {
//      return Collections.unmodifiableList(getKeysWithBase(MAPPINGS_BASE));
//   }

   private String getMappings(final String templateKey) {
      return getMandatoryProperty(CHARGE_BASE + "." +
                                  Objects.requireNonNull(templateKey));
   }

   public Map<String, Double> getChargesMap(final List<String> codes) {
      return codes.stream()
                  .collect(Collectors.toMap(k -> k,
                                            k -> Double.valueOf(getMappings(k))));
   }

}
