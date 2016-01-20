/**
 * 
 */
package ringsconector;

import java.io.IOException;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.Linucs.SugarImporterLinucs;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;


/**
 * @author yuki
 *
 */
public class LINUCStoGlycoCT {
	
	

	/**
	 * @param args
	 * @throws MonosaccharideException 
	 * @throws SugarImporterException 
	 * @throws GlycoVisitorException 
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException {
		SugarImporter t_objImporter = new SugarImporterLinucs();
		Config t_objConf = new Config();
		//g1
		String t_strCode = "[][D-Xyl]{[(4+1)][b-D-Gal]{[(3+1)][b-D-Gal]{[(3+1)][b-D-GlcA]{[(4+1)][b-D-GalNAc]{[(3+1)][b-D-GlcA]{[(4+1)][b-D-GalNAc]{[(3+1)][b-D-GlcA]{}}}}}}}}";
		//String t_strCode = args[0];
		
		
		MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
		Sugar g1 = t_objImporter.parse(t_strCode);
		
		
		GlycoVisitorToGlycoCT t_objTo = new GlycoVisitorToGlycoCT(t_objTrans);
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();
		
		
		SugarExporterGlycoCTCondensed exp = new SugarExporterGlycoCTCondensed ();
		exp.start(g1);
		
		System.out.print(exp.getHashCode()+"\n");
	}
}
