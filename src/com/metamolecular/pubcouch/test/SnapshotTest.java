/*
 * PubCouch - A CouchDB Interface for PubChem
 *
 * Copyright (c) 2010 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.metamolecular.pubcouch.test;

import com.metamolecular.pubcouch.record.Record;
import com.metamolecular.pubcouch.record.RecordStreamer;
import com.metamolecular.pubcouch.archive.Snapshot;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;
import junit.framework.TestCase;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class SnapshotTest extends TestCase
{

  private Snapshot snapshot;
  @Mocked
  private FTPClient client;
  private Enumeration<InputStream> chunks;

  @Override
  protected void setUp() throws Exception
  {
    snapshot = new Snapshot(client);
  }

  private byte[] zipStringToBytes(String input) throws IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
    bufos.write(input.getBytes("UTF-8"));
    bufos.close();
    byte[] retval = bos.toByteArray();
    bos.close();
    return retval;
  }

  // ftp.ncbi.nlm.nih.gov/pubchem/Substance/CURRENT-Full/SDF/Substance_00000001_00025000.sdf.gz
  // ftp.ncbi.nlm.nih.gov/pubchem/Compound/CURRENT-Full/SDF/Compound_00000001_00025000.sdf.gz
  public void testGetStructuresChangesDirectory() throws Exception
  {
    snapshot.getStructures();

    new Verifications()
    {
      {
        client.changeWorkingDirectory("/pubchem/Substance/CURRENT-Full/SDF");
      }
    };
  }

  public void testGetStructuresStreamsAllRecords() throws Exception
  {
    final String records = Molfiles.benzene + "\n$$$$\n";

    new NonStrictExpectations()
    {
      {
        client.listNames();
        result = new String[]
                {
                  "foo", "bar", "baz"
                };
        client.retrieveFileStream(anyString);
        result = new ByteArrayInputStream(zipStringToBytes(records));
        result = new ByteArrayInputStream(zipStringToBytes(records));
        result = new ByteArrayInputStream(zipStringToBytes(records));
      }
    };

    RecordStreamer streamer = snapshot.getStructures();
    Iterator<Record> it = streamer.iterator();
    assertEquals(Molfiles.benzene, it.next().getMolfile());
    assertTrue(it.hasNext());
    assertEquals(Molfiles.benzene, it.next().getMolfile());
    assertEquals(Molfiles.benzene, it.next().getMolfile());
    assertFalse(it.hasNext());
  }
}
