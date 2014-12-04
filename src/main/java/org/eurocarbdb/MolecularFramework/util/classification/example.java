/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.classification;

import java.io.IOException;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCT;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCT;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.Linucs.SugarImporterLinucs;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngineException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;
import org.jdom.JDOMException;

/**
 * @author sherget
 *
 */
public class example {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws ResourcesDbException 
	 * @throws SearchEngineException 
	 */
	public static void main(String[] args) throws SugarImporterException, GlycoVisitorException, JDOMException, IOException, ResourcesDbException, SearchEngineException {
		// TODO Auto-generated method stub
		SugarImporter t_objImporter = new SugarImporterLinucs();
		Config t_objConf = new Config();
		//g1 [][D-GLCNAC]{[(4+1)][B-D-GLCPNAC]{[(4+1)][B-D-MANP]{[(3+1)][A-D-MANP]{}[(6+1)][A-D-MANP]{}}}}
		String t_strCode = "[][b-D-GLCNAC]{[(4+1)][B-D-GLCPNAC]{[(4+1)][B-D-MANP]{[(3+1)][A-D-MANP]{}[(6+1)][A-D-MANP]{}}}}";
		MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
		Sugar g1 = t_objImporter.parse(t_strCode);
		GlycoVisitorToGlycoCT t_objTo = new GlycoVisitorToGlycoCT(t_objTrans);
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();
		
		SugarExporterGlycoCT t_exporter = new SugarExporterGlycoCT ();
		t_exporter.start(g1);
		try {
			System.out.println(t_exporter.getXMLCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		SubstructureClassCompare sub = new SubstructureClassCompare("D:/workspace/MolecularFramwork/src/org/eurocarbdb/MolecularFramework/util/classification/motifs.xml");
		
		for (String a : sub.getDefinedMotifNames()){
			System.out.println(a);
		}
	
        System.out.println();
        System.out.println();
        
		sub.scanForMotif(g1);
		
		for (String a : sub.getMotifNames()){
			System.out.println(a);
		}
		
		for (String a : sub.getMotifExplaination()){
			System.out.println(a);
		}
		
		for (String a : sub.getClassNames()){
			System.out.println(a);
		}
		for (String a : sub.getCategoryNames()){
			System.out.println(a);
		}
		
		for (ResultsPerMotif a : sub.getMotifStructures()){
			System.out.println(a.motifID.toString()+"Motif ID \t"+ a.sequenceID.toString());
			
		}
	}

}
