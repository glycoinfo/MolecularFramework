/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io;

import static org.junit.Assert.*;

import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.junit.Test;

/**
 * @author aoki
 *
 */
public class SugarImporterTextTest {

	/**
	 * Test method for {@link org.eurocarbdb.MolecularFramework.io.SugarImporterText#parse(java.lang.String)}.
	 */
	@Test
	public void testSugarImporterGlycoCTCondensed() throws Exception {
		SugarImporterGlycoCTCondensed importer = new SugarImporterGlycoCTCondensed();
        Sugar sugarStructure = importer.parse("RES\n1b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n2s:n-glycolyl\nLIN\n1:1d(5+1)2n\n");
        assert(importer.finished());
	}
	
	@Test
	public void testSugarImporterGlycoCTCondensed2() throws Exception {
		SugarImporterGlycoCTCondensed importer = new SugarImporterGlycoCTCondensed();
        Sugar sugarStructure = importer.parse("RES\n1b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n2s:n-glycolyl\nLIN\n1:1d(5+1)2n\n");
        assert(importer.finished());
	}
}
