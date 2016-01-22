package ringsconector;

import java.io.IOException;

import org.eurocarbdb.MolecularFramework.io.SugarExporterException;
import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.Glyde.SugarExporterGlydeII;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorFromGlycoCT;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCT;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCTextendMSDB;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.GlycanNamescheme;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConversion;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;
//import org.glycomedb.residuetranslator.ResidueTranslator;

public class GlycoCTtoGlyde {

	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException, SugarExporterException {
		System.out.print(convert(args) + "\n");
	}
	public static String convert(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException, SugarExporterException {

		SugarImporter t_objImporter = new SugarImporterGlycoCTCondensed();




		String t_strCode = "RES\n"
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

		//String t_strCode = args[0];
		Config t_objConf = new Config();
		MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
		//MonosaccharideConversion t_translator = new ResidueTranslator();
		Sugar g1 = t_objImporter.parse(t_strCode);

		GlycoVisitorToGlycoCT t_objTo = new GlycoVisitorToGlycoCTextendMSDB(t_objTrans);
		//GlycoVisitorFromGlycoCT t_objTo = new GlycoVisitorFromGlycoCT(t_translator,GlycanNamescheme.KEGG);
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();


		SugarExporterGlydeII exp = new SugarExporterGlydeII ();
		exp.start(g1);
		return exp.getXMLCode();
	}

}
