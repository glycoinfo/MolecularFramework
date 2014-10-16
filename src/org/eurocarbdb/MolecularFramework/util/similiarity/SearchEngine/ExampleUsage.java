/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCT;
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
public class ExampleUsage {

	/**
	 * @param args
	 * @throws MonosaccharideException 
	 * @throws SugarImporterException 
	 * @throws GlycoVisitorException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SQLException 
	 * @throws SearchEngineException 
	 */
	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, SearchEngineException {
		Class.forName("org.postgresql.Driver").newInstance();     
		Connection conn1 = DriverManager.getConnection("jdbc:postgresql://zsweb3/glycomedb?user=postgres&password=postgres");
		// 25630
		String t_strQuery1 = "SELECT xml from core.structure_glycoct_xml where structure_id>4715 AND structure_id<4717";
		ResultSet rs1 = conn1.createStatement().executeQuery( t_strQuery1 );
		rs1.next();		
		String glycoCT = rs1.getString(1);	
		
		SugarImporterGlycoCT t_oCTimporter = new SugarImporterGlycoCT();
		Sugar g1 = t_oCTimporter.parse(glycoCT);
		SugarExporterGlycoCTCondensed g_out = new SugarExporterGlycoCTCondensed();
		g_out.start(g1);
		System.out.println(g_out.getHashCode());
		//g2 -3)bDGlcp(1-6)bDGlcpNAc
		//[][b-d-glcp]{[(3+1)][b-d-glcp]{[(3+1)][b-d-glcp]{[(3+1)][b-d-glcp]{[(3+1)][b-d-glcp]{}}}}}
		//[][a-l-Rhap]{[(2+1)][b-d-manp]{[(2+1)][a-l-Rhap]{[(4+1)][a-l-Rhap]{[(4+1)][a-l-Rhap]{[(2+1)][b-d-manp]{}}}}}}
		String t_strCode = "[][b-d-xylp]{[(3+1)][b-d-manp]{[(3+1)][a-d-manp]{[(4+1)][a-d-manp]{}}[(4+1)][b-d-glcpnac]{}}}";
		SugarImporter t_objImporter2 = new SugarImporterLinucs();
		Config t_objConf2 = new Config();
		MonosaccharideConverter t_objTrans2 = new MonosaccharideConverter(t_objConf2);
		t_objTrans2 = new MonosaccharideConverter(t_objConf2);
		Sugar g2 = t_objImporter2.parse(t_strCode);
		GlycoVisitorToGlycoCT t_objTo2 = new GlycoVisitorToGlycoCT(t_objTrans2);
		
		t_objTo2.start(g2);
		g2 = t_objTo2.getNormalizedSugar();
		
		//g3
		t_strQuery1 = "SELECT xml from core.structure_glycoct_xml where structure_id>19 AND structure_id<21";
		rs1 = conn1.createStatement().executeQuery( t_strQuery1 );
		rs1.next();		
		glycoCT = rs1.getString(1);	
		
		t_oCTimporter = new SugarImporterGlycoCT();
		Sugar g3 = t_oCTimporter.parse(glycoCT);
		g_out = new SugarExporterGlycoCTCondensed();
		g_out.start(g2);
		System.out.println(g_out.getHashCode());
		g_out.start(g3);
		//System.out.println(g_out.getHashCode());
		
		// compare
		SearchEngine search = new SearchEngine ();
		//search.restrictToReducingEnds();
		search.setQueriedStructure(g1);
		search.setQueryStructure(g2);
		try {
			search.match();
		} catch (GlycoconjugateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SearchEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		search.plotMatrix();
		// search.restrictToReducingEnds();
		
			if (search.isExactMatch()){
				
				SugarExporterGlycoCTCondensed t_exporter = new SugarExporterGlycoCTCondensed ();
				t_exporter.start(g2);
			
			System.out.println("Is contained \n"+t_exporter.getHashCode()+"\n");
			}
			else {
				System.out.println("No common substructure");
			}
		
		
	}

}
