package ringsconector;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.cfg.SugarImporterCFG;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCTforLinearCode;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;

public class LinearCodetoGlycoCT {

	public static void main(String[] args) throws ResourcesDbException,
			SugarImporterException, GlycoVisitorException, IOException {
		SugarImporter t_objImporter = new SugarImporterCFG();
		Config t_objConf = new Config();
		//String t_strCode = args[0];
		String t_strCode = "NN??A??GN??(NN??A??GN??)Ma3(NN??A??GN??(NN??A??GN??)Ma6)Mb4GNb4(Fa6)GN";
		//行頭に括弧があり、その後も括弧が続く場合、行頭の括弧のみ除去する  ^($1)($2)$3  =>  ^$1($2)$3;
		t_strCode = t_strCode.replaceAll("^\\((.+?)\\)\\((.+?)\\)(.*)","$1($2)$3");
		
		//行頭に括弧がある場合括弧を除去する  ^($1)$2  =>  ^$1$2/;
		t_strCode = t_strCode.replaceAll("^\\((.+?)\\)(.*)","$1$2" );

		//結合情報やアノマー情報が抜け落ちている場合、不明であるとして'?'を挿入する  GNa= or A?=   =>    GNa?= or A??
		t_strCode = t_strCode.replaceAll("([A-Z]+[ab\\?])=", "$1?=");

		//バーティカルバーの直後に括弧がある場合、括弧をずらす  |($1)$2(  =>  |$1($2)(
		t_strCode = t_strCode.replaceAll("\\|\\((.+?)\\)(.+?)\\(", "|$1($2)(");

		//行末の結合情報を削除する　$1a3 or $1b? or $1??  => $1
		t_strCode = t_strCode.replaceAll("(.*)[ab\\?][0-9]?\\?*$", "$1");
		
		
		String t_strCheck = t_strCode;
		String t_regex = "\\(";
		Pattern t_pattern = Pattern.compile(t_regex);
		Matcher t_matcher = t_pattern.matcher(t_strCheck);
		while(t_matcher.find()){
			t_strCheck = t_strCheck.replaceAll("\\((.+?)\\)", "$1");
		}
		if(t_strCheck.contains(")")){//入力内の'('と')'の数が同じでない場合、エラーを返す
			System.out.print("ERROR! The input structure is wrong. ( The numbers of  \'(\' and \')\' are did not matched. )");
			System.exit(-1);
		}
		if(t_strCode.contains("*")){//行末に不明なノードがある場合
			System.out.print("ERROR! The input structure is wrong. ( Can not convert residure \'*\')");
			System.exit(-1);
		}
		
		
		Sugar g1 = t_objImporter.parse(t_strCode);
		
		MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
		GlycoVisitorToGlycoCTforLinearCode t_objTo = new GlycoVisitorToGlycoCTforLinearCode(t_objTrans);
		
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();
		
		SugarExporterGlycoCTCondensed exp = new SugarExporterGlycoCTCondensed();
		exp.start(g1);
		
		System.out.print(exp.getHashCode() + "\n");
	}
}
