package org.eurocarbdb.MolecularFramework.io;
import static org.junit.Assert.*;

import org.junit.Test;

import ringsconector.CarbBanktoGlycoCT;
import ringsconector.GlycamtoGlycoCT;
import ringsconector.GlycoCTtoCarbBank;
import ringsconector.GlycoCTtoGlycam;
import ringsconector.GlycoCTtoGlyde;
import ringsconector.GlycoCTtoKCF;
import ringsconector.GlycoCTtoLINUCS;
import ringsconector.IUPACtoGlycoCT;
import ringsconector.KCFtoGlycoCT;

public class RingsConectorTest {

	@Test
	public void testIUPACtoglycoCT() throws Exception {

		String[] t_inStr = {"",""};
		t_inStr[0] =  "Galb1-4(Fuca1-3)Glcb1-2Mana";
 		String t_checkStr = "RES\n"
				+"1b:a-xman-HEX-1:5\n"
				+"2b:b-xglc-HEX-1:5\n"
				+"3b:a-xgal-HEX-1:5|6:d\n"
				+"4b:b-xgal-HEX-1:5\n"
				+"LIN\n"
				+"1:1o(2+1)2d\n"
				+"2:2o(3+1)3d\n"
				+"3:2o(4+1)4d\n";

		assertTrue(IUPACtoGlycoCT.convert(t_inStr).equals(t_checkStr));
	}

	@Test
	public void testKCFtoGlycoCT() throws Exception {

		String[] t_inStr = {"",""};
		t_inStr[0] = "ENTRY     09          Glycan\n"+
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

		String t_checkStr = "RES\n"+
				"1b:x-dglc-HEX-1:5\n"+
				"2b:b-dglc-HEX-1:5\n"+
				"3b:b-dman-HEX-1:5\n"+
				"4b:a-dman-HEX-1:5\n"+
				"5b:a-dman-HEX-1:5\n"+
				"LIN\n"+
				"1:1o(4+1)2d\n"+
				"2:2o(4+1)3d\n"+
				"3:3o(3+1)4d\n"+
				"4:3o(6+1)5d\n";

		assertTrue(KCFtoGlycoCT.convert(t_inStr).equals(t_checkStr));
	}

	@Test
	public void testGlycoCTtoKCF() throws Exception {

		String[] t_inStr = {"",""};
		t_inStr[0] = "RES\n"
				+"1b:b-dglc-HEX-1:5\n"
				+"2s:n-acetyl\n"
				+"3b:b-dglc-HEX-1:5\n"
				+"4s:n-acetyl\n"
				+"5b:b-dman-HEX-1:5\n"
				+"6b:a-dman-HEX-1:5\n"
				+"7b:b-dglc-HEX-1:5\n"
				+"8s:n-acetyl\n"
				+"9b:a-dman-HEX-1:5\n"
				+"10b:b-dglc-HEX-1:5\n"
				+"11s:n-acetyl\n"
				+"LIN\n"
				+"1:1d(2+1)2n\n"
				+"2:1o(4+1)3d\n"
				+"3:3d(2+1)4n\n"
				+"4:3o(4+1)5d\n"
				+"5:5o(3+1)6d\n"
				+"6:6o(2+1)7d\n"
				+"7:7d(2+1)8n\n"
				+"8:5o(6+1)9d\n"
				+"9:9o(2+1)10d\n"
				+"10:10d(2+1)11n\n";

		String t_checkStr = "ENTRY       G99999                      Glycan\n"
				+ "NODE        7\n"
				+ "            1         b-GlcNAc   0.0     0.0\n"
				+ "            2         b-GlcNAc   -4.0     0.0\n"
				+ "            3         b-Man   -8.0     0.0\n"
				+ "            4         a-Man   -12.0     4.0\n"
				+ "            5         a-Man   -12.0     -4.0\n"
				+ "            6         b-GlcNAc   -16.0     4.0\n"
				+ "            7         b-GlcNAc   -16.0     -4.0\n"
				+ "EDGE        6\n"
				+ "            1         2:1    1:4\n"
				+ "            2         3:1    2:4\n"
				+ "            3         4:1    3:3\n"
				+ "            4         5:1    3:6\n"
				+ "            5         6:1    4:2\n"
				+ "            6         7:1    5:2\n"
				+ "///";

		assertTrue(GlycoCTtoKCF.convert(t_inStr).equals(t_checkStr));
	}



	@Test
	public void testGlycoCTtoGlycam() throws Exception {

		String[] t_inStr = {"",""};
		t_inStr[0] = "RES\n"
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

		String t_checkStr = "a-D-Manp1-6[a-D-Manp1-3]b-D-Manp1-4b-D-GlcpNAc1-4?-D-GlcpNAc1-OH";

		assertTrue(GlycoCTtoGlycam.convert(t_inStr).equals(t_checkStr));
	}


	@Test
	public void testGlycamtoGlycoCT() throws Exception {

		String[] t_inStr = {"",""};
		t_inStr[0] = "b-GlcNAc1-2a-Man1-6[b-GlcNAc1-2a-Man1-3]b-Man1-4b-GlcNAc1-4b-GlcNAc1-OH";

		String t_checkStr = "RES\n"
				+"1b:b-xglc-HEX-x:x\n"
				+"2s:n-acetyl\n"
				+"3b:b-xglc-HEX-x:x\n"
				+"4s:n-acetyl\n"
				+"5b:b-xman-HEX-x:x\n"
				+"6b:a-xman-HEX-x:x\n"
				+"7b:b-xglc-HEX-x:x\n"
				+"8s:n-acetyl\n"
				+"9b:a-xman-HEX-x:x\n"
				+"10b:b-xglc-HEX-x:x\n"
				+"11s:n-acetyl\n"
				+"LIN\n"
				+"1:1d(2+1)2n\n"
				+"2:1o(4+1)3d\n"
				+"3:3d(2+1)4n\n"
				+"4:3o(4+1)5d\n"
				+"5:5o(3+1)6d\n"
				+"6:6o(2+1)7d\n"
				+"7:7d(2+1)8n\n"
				+"8:5o(6+1)9d\n"
				+"9:9o(2+1)10d\n"
				+"10:10d(2+1)11n\n";

		assertTrue(GlycamtoGlycoCT.convert(t_inStr).equals(t_checkStr));
	}
	@Test
	public void testGlycoCTtoLINUCS() throws Exception {

		String[] t_inStr = {"",""};
		t_inStr[0] = "RES\n"
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

		String t_checkStr = "[][?-D-GlcpNAc]{[(4+1)][b-D-GlcpNAc]{[(4+1)][b-D-Manp]{[(3+1)][a-D-Manp]{}[(6+1)][a-D-Manp]{}}}}";

		assertTrue(GlycoCTtoLINUCS.convert(t_inStr).equals(t_checkStr));
	}

	@Test
	public void testGlycoCTtoCarbBank() throws Exception {

		String[] t_inStr = {"",""};
		t_inStr[0] = "RES\n"
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

		String t_checkStr = " a-D-Manp-(1-3)+\n"
				+ "               |\n"
				+ "a-D-Manp-(1-6)-b-D-Manp-(1-4)-b-D-GlcpNAc-(1-4)-?-D-GlcpNAc\n";

		assertTrue(GlycoCTtoCarbBank.convert(t_inStr).equals(t_checkStr));
	}
	@Test
	public void testCarbBanktoGlycoCT() throws Exception {

		String[] t_inStr = {"",""};
		t_inStr[0] = " a-D-Manp-(1-3)+\n"
				+ "               |\n"
				+ "a-D-Manp-(1-6)-b-D-Manp-(1-4)-b-D-GlcpNAc-(1-4)-?-D-GlcpNAc\n";
		String t_checkStr = "RES\n"
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
				+"6:5o(6+1)7d\n";

		assertTrue(CarbBanktoGlycoCT.convert(t_inStr).equals(t_checkStr));
	}

//	@Test
//	public void testGlycoCTtoGlyde() throws Exception {
//
//		String[] t_inStr = {"",""};
//		t_inStr[0] = "RES\n"
//				+"1b:b-dglc-HEX-1:5\n"
//				+"2s:n-acetyl\n"
//				+"3b:b-dglc-HEX-1:5\n"
//				+"4s:n-acetyl\n"
//				+"5b:b-dman-HEX-1:5\n"
//				+"6b:a-dman-HEX-1:5\n"
//				+"7b:b-dglc-HEX-1:5\n"
//				+"8s:n-acetyl\n"
//				+"9b:a-dman-HEX-1:5\n"
//				+"10b:b-dglc-HEX-1:5\n"
//				+"11s:n-acetyl\n"
//				+"LIN\n"
//				+"1:1d(2+1)2n\n"
//				+"2:1o(4+1)3d\n"
//				+"3:3d(2+1)4n\n"
//				+"4:3o(4+1)5d\n"
//				+"5:5o(3+1)6d\n"
//				+"6:6o(2+1)7d\n"
//				+"7:7d(2+1)8n\n"
//				+"8:5o(6+1)9d\n"
//				+"9:9o(2+1)10d\n"
//				+"10:10d(2+1)11n\n";
//
//		String t_checkStr = "	&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;"+
//				"		&lt;GlydeII&gt;"+
//				"		  &lt;molecule subtype=&quot;glycan&quot; id=&quot;From_GlycoCT_Translation&quot;&gt;"+
//				"		    &lt;residue subtype=&quot;base_type&quot; partid=&quot;1&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=b-dglc-HEX-1:5&quot; /&gt;"+
//				"		    &lt;residue subtype=&quot;substituent&quot; partid=&quot;2&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=n-acetyl&quot; /&gt;"+
//				"		    &lt;residue subtype=&quot;base_type&quot; partid=&quot;3&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=b-dglc-HEX-1:5&quot; /&gt;"+
//				"		    &lt;residue subtype=&quot;substituent&quot; partid=&quot;4&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=n-acetyl&quot; /&gt;"+
//				"		    &lt;residue subtype=&quot;base_type&quot; partid=&quot;5&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=b-dman-HEX-1:5&quot; /&gt;"+
//				"		    &lt;residue subtype=&quot;base_type&quot; partid=&quot;6&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=a-dman-HEX-1:5&quot; /&gt;"+
//				"		    &lt;residue subtype=&quot;base_type&quot; partid=&quot;7&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=b-dglc-HEX-1:5&quot; /&gt;"+
//				"		    &lt;residue subtype=&quot;substituent&quot; partid=&quot;8&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=n-acetyl&quot; /&gt;"+
//				"		    &lt;residue subtype=&quot;base_type&quot; partid=&quot;9&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=a-dman-HEX-1:5&quot; /&gt;"+
//				"		    &lt;residue subtype=&quot;base_type&quot; partid=&quot;10&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=b-dglc-HEX-1:5&quot; /&gt;"+
//				"		    &lt;residue subtype=&quot;substituent&quot; partid=&quot;11&quot; ref=&quot;http://www.monosaccharideDB.org/GLYDE-II.jsp?G=n-acetyl&quot; /&gt;"+
//				"		    &lt;residue_link from=&quot;2&quot; to=&quot;1&quot;&gt;"+
//				"		      &lt;atom_link from=&quot;N1H&quot; to=&quot;C2&quot; to_replace=&quot;O2&quot; bond_order=&quot;1&quot; /&gt;"+
//				"		    &lt;/residue_link&gt;"+
//				"		    &lt;residue_link from=&quot;3&quot; to=&quot;1&quot;&gt;"+
//				"		      &lt;atom_link from=&quot;C1&quot; to=&quot;O4&quot; from_replace=&quot;O1&quot; bond_order=&quot;1&quot; /&gt;"+
//				"		    &lt;/residue_link&gt;"+
//				"		    &lt;residue_link from=&quot;4&quot; to=&quot;3&quot;&gt;"+
//				"		      &lt;atom_link from=&quot;N1H&quot; to=&quot;C2&quot; to_replace=&quot;O2&quot; bond_order=&quot;1&quot; /&gt;"+
//				"		    &lt;/residue_link&gt;"+
//				"		    &lt;residue_link from=&quot;5&quot; to=&quot;3&quot;&gt;"+
//				"		      &lt;atom_link from=&quot;C1&quot; to=&quot;O4&quot; from_replace=&quot;O1&quot; bond_order=&quot;1&quot; /&gt;"+
//				"		    &lt;/residue_link&gt;"+
//				"		    &lt;residue_link from=&quot;6&quot; to=&quot;5&quot;&gt;"+
//				"		      &lt;atom_link from=&quot;C1&quot; to=&quot;O3&quot; from_replace=&quot;O1&quot; bond_order=&quot;1&quot; /&gt;"+
//				"		    &lt;/residue_link&gt;"+
//				"		    &lt;residue_link from=&quot;7&quot; to=&quot;6&quot;&gt;"+
//				"		      &lt;atom_link from=&quot;C1&quot; to=&quot;O2&quot; from_replace=&quot;O1&quot; bond_order=&quot;1&quot; /&gt;"+
//				"		    &lt;/residue_link&gt;"+
//				"		    &lt;residue_link from=&quot;8&quot; to=&quot;7&quot;&gt;"+
//				"		      &lt;atom_link from=&quot;N1H&quot; to=&quot;C2&quot; to_replace=&quot;O2&quot; bond_order=&quot;1&quot; /&gt;"+
//				"		    &lt;/residue_link&gt;"+
//				"		    &lt;residue_link from=&quot;9&quot; to=&quot;5&quot;&gt;"+
//				"		      &lt;atom_link from=&quot;C1&quot; to=&quot;O6&quot; from_replace=&quot;O1&quot; bond_order=&quot;1&quot; /&gt;"+
//				"		    &lt;/residue_link&gt;"+
//				"		    &lt;residue_link from=&quot;10&quot; to=&quot;9&quot;&gt;"+
//				"		      &lt;atom_link from=&quot;C1&quot; to=&quot;O2&quot; from_replace=&quot;O1&quot; bond_order=&quot;1&quot; /&gt;"+
//				"		    &lt;/residue_link&gt;"+
//				"		    &lt;residue_link from=&quot;11&quot; to=&quot;10&quot;&gt;"+
//				"		      &lt;atom_link from=&quot;N1H&quot; to=&quot;C2&quot; to_replace=&quot;O2&quot; bond_order=&quot;1&quot; /&gt;"+
//				"		    &lt;/residue_link&gt;"+
//				"		  &lt;/molecule&gt;"+
//				"		&lt;/GlydeII&gt;";
//		String t_outStr = GlycoCTtoGlyde.convert(t_inStr);
//		assertTrue(t_outStr.equals(t_checkStr) );
//	}
}
