package org.gssb.accounting.charge;

import java.nio.file.Path;
import java.util.Set;

public class TestAccount extends Account {

   TestAccount(final Path path) {
      super(path);
      this.commiteeFamilyCodes = Set.of("SKM1234");
   }

}
