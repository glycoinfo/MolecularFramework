package ringsconector;

import java.io.IOException;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.carbbank.SugarImporterCarbbank;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;

public class CarbBanktoGlycoCT {
	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException {
		System.out.print(convert(args)+"\n");
	}

	public static String convert(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException {
		SugarImporter t_objImporter = new SugarImporterCarbbank();
		Config t_objConf = new Config();
		String t_strCode = args[0];

		MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
		Sugar g1 = t_objImporter.parse(t_strCode);

		GlycoVisitorToGlycoCT t_objTo = new GlycoVisitorToGlycoCT(t_objTrans);
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();

		SugarExporterGlycoCTCondensed exp = new SugarExporterGlycoCTCondensed ();
		exp.start(g1);

		return exp.getHashCode();
	}
}
