package org.eurocarbdb.MolecularFramework.io;
import org.junit.Test;

import ringsconector.IUPACtoGlycoCT;
import ringsconector.KCFtoGlycoCT;

public class RingsConectorTest {
	
	@SuppressWarnings("null")
	@Test
	public void testGlycoCTtoIUPAC() throws Exception {
		
		String[] t_strCodes = {"",""};
		t_strCodes[0] =  "Galb1-4(Fuca1-3)Glcb1-2Mana";
		System.out.print( t_strCodes.toString());
 		String t_strCode2 = "RES"
				+"1b:a-xman-HEX-1:5"
				+"2b:b-xglc-HEX-1:5"
				+"3b:a-xgal-HEX-1:5|6:d"
				+"4b:b-xgal-HEX-1:5"
				+"LIN"
				+"1:1o(2+1)2d"
				+"2:2o(3+1)3d"
				+"3:2o(4+1)4d";

		assert(IUPACtoGlycoCT.Test(t_strCodes) == t_strCode2);
	}
	
	
	

	@Test
	public void testKCFtoGlycoCT() throws Exception {

		String[] t_strCodes = {"",""};
		t_strCodes[0] = "ENTRY     09          Glycan\n"+
"NODE      5\n"+
"            1     Glc     15.0     7.0\n"+
"            2     Glc      8.0     7.0\n"+
"            3     Man         1.0     7.0\n"+
"            4     Man        -6.0    12.0\n"+
"            5     Man        -6.0     2.0\n"+
"EDGE      4\n"+
"            1     2:b1       1:4\n"+
"            2     3:b1       2:4\n"+
"            3     5:a1       3:3\n"+
"            4     4:a1       3:6\n"+
  "///";
		
		String t_strCode2 = "RES\n"
+"1b:x-dglc-HEX-1:5\n"
+"2s:n-acetyl\n"
+"3b:b-dglc-HEX-1:5\n"
+"4s:n-acetyl\n"
+"5b:b-dman-HEX-1:5\n"
+"6b:a-dman-HEX-1:5\n"
+"7b:a-dman-HEX-1:5\n"
+"LIN\n"
+"1:1d(2+1)2n\n"
+"2:1o(4+1)3d\n"
+"3:3d(2+1)4n\n"
+"4:3o(4+1)5d\n"
+"5:5o(3+1)6d\n"
+"6:5o(6+1)7d";
		
		assert(KCFtoGlycoCT.Test(t_strCodes) == t_strCode2);
	}
	

	
}
