package org.eurocarbdb.MolecularFramework.io;

import org.eurocarbdb.MolecularFramework.sugar.Sugar;


/**
 * @author rene
 *
 */
public abstract class SugarImporter 
{
    // sugar object that is build
    protected Sugar m_objSugar = null;

	/**
	 * 
     * @author rene
	 */
	public abstract Sugar parse(String a_strStream) throws SugarImporterException;

}
