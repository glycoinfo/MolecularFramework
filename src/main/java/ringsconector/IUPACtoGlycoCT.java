package ringsconector;
/**
 * 
 */


import java.io.IOException;
import java.util.logging.Logger;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.iupac.SugarImporterIupacShortV1;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCTforIUPAC;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;

public class IUPACtoGlycoCT {
/**
 * @author yuki
 *
 */
	/**
	 * @param args
	 * @throws MonosaccharideException 
	 * @throws SugarImporterException 
	 * @throws GlycoVisitorException 
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException {
		System.out.print(convert(args)+"\n");
	}

	public static String convert(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException {
		SugarImporter t_objImporter = new SugarImporterIupacShortV1();
		Config t_objConf = new Config();
		String t_strCode = args[0];
		
		MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
		Sugar g1 = t_objImporter.parse(t_strCode);

		GlycoVisitorToGlycoCTforIUPAC t_objTo = new GlycoVisitorToGlycoCTforIUPAC(t_objTrans);
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();
		
		SugarExporterGlycoCTCondensed exp = new SugarExporterGlycoCTCondensed ();
		exp.start(g1);
		return exp.getHashCode();
	}
}	





