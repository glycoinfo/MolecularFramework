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

		String t_strCode = "ENTRY     09          Glycan\n"+
"NODE      5\n"+
"            1     GlcNAc     15.0     7.0\n"+
"            2     GlcNAc      8.0     7.0\n"+
"            3     Man         1.0     7.0\n"+
"            4     Man        -6.0    12.0\n"+
"            5     Man        -6.0     2.0\n"+
"EDGE      4\n"+
"            1     2:b1       1:4\n"+
"            2     3:b1       2:4\n"+
"            3     5:a1       3:3\n"+
"            4     4:a1       3:6\n"+
  "///";
		
		//�����\���ȊO�̏�����������
		//t_strCode = t_strCode.replaceAll("Glycan(.|\n)*NODE", "Glycan\nNODE");
		Config t_objConf = new Config();
		SugarImporter t_objImporter = new SugarImporterKCF();		
		Sugar g1 = t_objImporter.parse(t_strCode);
		
		MonosaccharideConversion t_objTrans = new MonosaccharideConverter(t_objConf);
		//MonosaccharideConversion t_translator = new ResidueTranslator();
		
		GlycoVisitorToGlycoCT t_objTo = new GlycoVisitorToGlycoCT(t_objTrans,GlycanNamescheme.KEGG);
		//GlycoVisitorToGlycoCTforKCF t_objTo = new GlycoVisitorToGlycoCTforKCF(t_translator,GlycanNamescheme.KEGG);
		
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();
		
		SugarExporterGlycoCTCondensed exp = new SugarExporterGlycoCTCondensed ();
		exp.start(g1);
		
		System.out.print(exp.getHashCode()+"\n");
	}
	public static String Test(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException {

		String t_strCode = args[0];

		//�����\���ȊO�̏�����������
		t_strCode = t_strCode.replaceAll("Glycan(.|\n)*NODE", "Glycan\nNODE");
		Config t_objConf = new Config();
		SugarImporter t_objImporter = new SugarImporterKCF();		
		Sugar g1 = t_objImporter.parse(t_strCode);
		
		MonosaccharideConversion t_translator = new MonosaccharideConverter(t_objConf);
		GlycoVisitorToGlycoCTforKCF t_objTo = new GlycoVisitorToGlycoCTforKCF(t_translator,GlycanNamescheme.KEGG);
		
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();
		
		SugarExporterGlycoCTCondensed exp = new SugarExporterGlycoCTCondensed ();
		exp.start(g1);
		
		return exp.getHashCode();
	}
	
}

	