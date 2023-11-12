package org.gssb.accounting;

import org.gssb.accounting.charge.AccountTest;
import org.gssb.accounting.web.WebChargerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({WebChargerTest.class, AccountTest.class})
public class AllTests {
}
