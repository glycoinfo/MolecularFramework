/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.GlycoCT;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author sherget
 *
 */
public class LevelGZIPOutputStream extends GZIPOutputStream
{
/**
 * Creates a new output stream with a default buffer size and
 * sets the current compression level to the specified value.
 *
 * @param out the output stream.
 * @param level the new compression level (0-9).
 * @exception IOException If an I/O error has occurred.
 * @exception IllegalArgumentException if the compression level is invalid.
 */
public LevelGZIPOutputStream( OutputStream out, int compressionLevel )
    throws IOException
{
  super( out );
  def.setLevel( compressionLevel );
}
}
