package de.webtwob.agd.project.test;

import de.webtwob.agd.project.main.Setup;
import org.junit.Assert;
import org.junit.Test;

public class SetupTest {

	@Test
	public void testIsSetupWorking() {
		Assert.assertTrue(Setup.isSetupWorking());
	}

}
