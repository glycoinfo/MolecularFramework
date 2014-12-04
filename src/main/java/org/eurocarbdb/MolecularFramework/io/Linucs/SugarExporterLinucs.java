/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.Linucs;

import org.eurocarbdb.MolecularFramework.io.SugarExporter;
import org.eurocarbdb.MolecularFramework.io.SugarExporterException;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;

/**
 * @author Logan
 *
 */
public class SugarExporterLinucs extends SugarExporter 
{

	/**
	 * @see org.eurocarbdb.MolecularFramework.io.SugarExporter#export(org.eurocarbdb.MolecularFramework.sugar.Sugar)
	 */
	@Override
	public String export(Sugar a_objSugar) throws SugarExporterException 
	{
		try 
		{
			GlycoVisitorExport t_objVisitor = new GlycoVisitorExport();
			t_objVisitor.start(a_objSugar);
			return t_objVisitor.getCode();
		} 
		catch (Exception e) 
		{
			throw new SugarExporterException(e.getMessage(),e);
		}
	}

}
