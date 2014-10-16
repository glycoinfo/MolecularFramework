/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.analytical.GraphFragmenter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCT;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.Linucs.SugarImporterLinucs;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;

/**
 * @author sherget
 *
 */
public class example {

	/**
	 * @param args
	 * @throws MonosaccharideException 
	 * @throws SugarImporterException 
	 * @throws GlycoVisitorException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 * @throws GlycoconjugateException 
	 */
	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		SugarImporter t_objImporter = new SugarImporterLinucs();
		Config t_objConf = new Config();
		//g1
		String t_strCode = "[][A-D-MANPnac3ac]{[(4+1)][b-D-MANP]{[(4+1)][a-L-MANP]{[(3+1)][b-L-MANP3gc]{}[(3+1)][A-D-GLCP3gc]{}}}}";
		MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
		Sugar g1 = t_objImporter.parse(t_strCode);
		GlycoVisitorToGlycoCT t_objTo = new GlycoVisitorToGlycoCT(t_objTrans);
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();
		
//		g2 
//		 RES
//		 1r:r1
//		 REP
//		 REP1:2o(3+1)2d=-1--1
//		 RES
//		 2b:b-dglc-HEX-1:5
		t_strCode = "[][A-D-MANPNAC]{[(2+1)][A-D-FUCP]{[(4+1)][A-D-IDOP]{}}[(4+1)][A-D-GULP]{[(2+1)][A-D-ARAF]{}}}";
		SugarImporter t_objImporter2 = new SugarImporterLinucs();
		Config t_objConf2 = new Config();
		MonosaccharideConverter t_objTrans2 = new MonosaccharideConverter(t_objConf2);
		t_objTrans2 = new MonosaccharideConverter(t_objConf2);
		Sugar g2 = t_objImporter2.parse(t_strCode);
		GlycoVisitorToGlycoCT t_objTo2 = new GlycoVisitorToGlycoCT(t_objTrans2);
		t_objTo2.start(g1);
		g1 = t_objTo2.getNormalizedSugar();
		t_objTo2.start(g2);
		g2 = t_objTo2.getNormalizedSugar();
		
		SugarFragmenter t_Frag = new SugarFragmenter();
		
		Class.forName("org.postgresql.Driver").newInstance();     
		Connection conn1 = DriverManager.getConnection("jdbc:postgresql://zsweb3/glycomedb?user=postgres&password=postgres");
		// 25630 27491 157 2277 771 175
		String t_strQuery1 = "SELECT xml from core.structure_glycoct_xml where structure_id=27491";
		ResultSet rs1 = conn1.createStatement().executeQuery( t_strQuery1 );
		rs1.next();		
		String glycoCT = rs1.getString(1);
		SugarImporterGlycoCT t_oCTimporter = new SugarImporterGlycoCT();
		Sugar g4 = t_oCTimporter.parse(glycoCT);
		
		
		String CondensedCodes =
				"RES\n" +
				"1b:a-dido-HEX-1:5\n" +
				"2r:r1\n" +
				"3b:b-dxyl-HEX-1:5\n" +
				"LIN\n" +
				"1:1o(4+1)2n" +
				"2:2n(4+1)3d" +
				"REP\n" +
				"REP1:4o(2+1)4d=-1--1\n" +
				"RES\n" +
				"4b:x-dglc-HEX-1:5\n";
		SugarImporterGlycoCTCondensed g_in = new SugarImporterGlycoCTCondensed ();
		Sugar t_sug =g_in.parse(CondensedCodes);
		t_Frag.setLowerSizeLimit(0);

		
		
		
	}

}
