/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.similiarity.PairSimiliarity;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.Linucs.SugarImporterLinucs;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.analytical.disaccharide.Disaccharide;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.NodeComparatorWithSubstituents;
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
	 */
	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException {
		SugarImporter t_objImporter = new SugarImporterLinucs();
		Config t_objConf = new Config();
		//g1
		String t_strCode = "[][a-D-GALNAC]{[(4+1)][B-D-GLCP]{[(3+1)][A-D-MANP]{[(2+1)][A-D-MANP]{}}}}";
		MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
		Sugar g1 = t_objImporter.parse(t_strCode);
		GlycoVisitorToGlycoCT t_objTo = new GlycoVisitorToGlycoCT(t_objTrans);
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();
		
		//g2
		t_strCode = "[][a-D-GAL]{[(4+1)][B-D-GLCPNAC]{[(3+1)][A-D-MANP]{[(2+1)][A-D-MANP]{}}}}";
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
		
		
		// compare
		PairSimiliarity t_oComp = new PairSimiliarity ();
		t_oComp.setNodeComparator(new NodeComparatorWithSubstituents());
		t_oComp.calculate(g1, g2);
		System.out.println("Score: \t" + t_oComp.getScore());
		System.out.println("Normalized Score: " + t_oComp.getNormalizedScore());
		Integer counter =0;
		
		for (Disaccharide p : t_oComp.getPairs()){
			counter ++;
			Sugar t_oSug = new Sugar ();
			try {				
				t_oSug.addNode(p.getParent());
				t_oSug.addNode(p.getChild());
				t_oSug.addEdge(p.getParent(),p.getChild(),p.getLinkage());
				
				SugarExporterGlycoCTCondensed exp = new SugarExporterGlycoCTCondensed ();
				//exp.LineBreakOff();
				exp.start(t_oSug);
				
				System.out.print("PAIR "+counter+":  \n"+exp.getHashCode()+"\n");
				
			} catch (GlycoconjugateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}		
	}
	
}
