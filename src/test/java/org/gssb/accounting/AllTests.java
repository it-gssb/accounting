package org.gssb.accounting;

import org.gssb.accounting.charge.AccountTest;
import org.gssb.accounting.web.WebChargerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({WebChargerTest.class, AccountTest.class})
public class AllTests {
}
