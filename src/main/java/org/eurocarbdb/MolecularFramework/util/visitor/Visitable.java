package org.eurocarbdb.MolecularFramework.util.visitor;


/**
 * @author rene
 *
 */
public interface Visitable
{
    public void accept (GlycoVisitor a_objVisitor) throws GlycoVisitorException;
}
