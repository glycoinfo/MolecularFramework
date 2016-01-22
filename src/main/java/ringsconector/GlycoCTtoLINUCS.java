package ringsconector;

import java.io.IOException;

import org.eurocarbdb.MolecularFramework.io.SugarExporterException;
import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.Linucs.SugarExporterLinucs;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorFromGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.GlycanNamescheme;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConversion;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;

public class GlycoCTtoLINUCS {

	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException, SugarExporterException {
		System.out.print(convert(args)+"\n");
	}

	public static String convert(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException, SugarExporterException {
		SugarImporter t_objImporter = new SugarImporterGlycoCTCondensed();
		String t_strCode = args[0];

		Config t_objConf = new Config();
		MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
		//MonosaccharideConversion t_translator = new ResidueTranslator();
		Sugar g1 = t_objImporter.parse(t_strCode);

		//GlycoVisitorToGlycoCT t_objTo = new GlycoVisitorToGlycoCTextendMSDB(t_objTrans);
		GlycoVisitorFromGlycoCT t_objTo = new GlycoVisitorFromGlycoCT(t_objTrans);
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();

		SugarExporterLinucs exp = new SugarExporterLinucs();
		return exp.export(g1);
	}
}
