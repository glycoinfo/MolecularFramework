package ringsconector;

import java.io.IOException;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.kcf.SugarImporterKCF;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCT;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCTforKCF;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.GlycanNamescheme;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConversion;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;
//import org.glycomedb.residuetranslator.ResidueTranslator;
import org.glycomedb.residuetranslator.ResidueTranslator;

public class KCFtoGlycoCT {
/**
 * @author yuki
 *
 */
	/**
	 * @param args
	 * @return
	 * @throws MonosaccharideException
	 * @throws SugarImporterException
	 * @throws GlycoVisitorException
	 * @throws IOException
	 */

	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException {
		System.out.print(convert(args)+"\n");
	}
	public static String convert(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException {

		String t_strCode = args[0];
		t_strCode = t_strCode.replaceAll("Glycan(.|\n)*NODE", "Glycan\nNODE");
		//Config t_objConf = new Config();
		SugarImporter t_objImporter = new SugarImporterKCF();
		MonosaccharideConversion t_translator = new ResidueTranslator();
		Sugar g1 = t_objImporter.parse(t_strCode);

		//MonosaccharideConversion t_translator = new MonosaccharideConverter(t_objConf);

		GlycoVisitorToGlycoCTforKCF t_objTo = new GlycoVisitorToGlycoCTforKCF(t_translator,GlycanNamescheme.KEGG);

		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();

		SugarExporterGlycoCTCondensed exp = new SugarExporterGlycoCTCondensed ();
		exp.start(g1);

		return exp.getHashCode();
	}

}

