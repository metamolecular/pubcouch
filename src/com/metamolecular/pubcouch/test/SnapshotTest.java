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
import com.metamolecular.pubcouch.record.DefaultRecordStreamer;
import com.metamolecular.pubcouch.pubchem.Snapshot;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;
import junit.framework.TestCase;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.apache.commons.net.ftp.FTP;
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
  public void testGetSubstancesChangesDirectory() throws Exception
  {
    snapshot.getSubstances();

    new Verifications()
    {
      {
        client.changeWorkingDirectory("/pubchem/Substance/CURRENT-Full/SDF");
      }
    };
  }

  public void testGetCompoundsChangesDirectory() throws Exception
  {
    snapshot.getCompounds();

    new Verifications()
    {
      {
        client.changeWorkingDirectory("/pubchem/Compound/CURRENT-Full/SDF");
      }
    };
  }

  public void testGetSubstancesSetsBinaryFileType() throws Exception
  {
    final String records = Molfiles.benzene + "\n$$$$\n";

    new Expectations()
    {
      {
        client.changeWorkingDirectory(anyString);
        client.listNames();
        result = new String[]{ "Compound_00000001_00025000.sdf.gz" };
        client.setFileType(FTP.BINARY_FILE_TYPE);
        client.retrieveFileStream(anyString);
        result = new ByteArrayInputStream(zipStringToBytes(records));
      }
    };

    snapshot.getCompounds();
  }

  public void testGetSubstancesStreamsAllRecords() throws Exception
  {
    final String records = Molfiles.benzene + "\n$$$$\n";

    new NonStrictExpectations()
    {
      {
        client.listNames();
        result = new String[]
        {
          "Readme.txt",
          "Compound_00000001_00025000.sdf.gz",
          "Compound_00025001_00050000.sdf.gz",
          "Compound_00050001_00075000.sdf.gz"
        };
        client.retrieveFileStream("Compound_00000001_00025000.sdf.gz");
        result = new ByteArrayInputStream(zipStringToBytes(records));
        client.retrieveFileStream("Compound_00025001_00050000.sdf.gz");
        result = new ByteArrayInputStream(zipStringToBytes(records));
        client.retrieveFileStream("Compound_00050001_00075000.sdf.gz");
        result = new ByteArrayInputStream(zipStringToBytes(records));
      }
    };

    DefaultRecordStreamer streamer = snapshot.getCompounds();
    Iterator<Record> it = streamer.iterator();
    assertEquals(Molfiles.benzene, it.next().getMolfile());
    assertTrue(it.hasNext());
    assertEquals(Molfiles.benzene, it.next().getMolfile());
    assertEquals(Molfiles.benzene, it.next().getMolfile());
    assertFalse(it.hasNext());
  }

  public void testGetCompoundsStreamsFromCID() throws Exception
  {
    final String chunk1 =
            Molfiles.benzene + "\n"+
            "> <PUBCHEM_COMPOUND_CID>\n" +
            "53000\n" +
            "\n$$$$\n" +
            Molfiles.benzene + "\n"+
            "> <PUBCHEM_COMPOUND_CID>\n" +
            "53001\n" +
            "\n$$$$\n";
    final String chunk2 =
            Molfiles.benzene + "\n"+
            "> <PUBCHEM_COMPOUND_CID>\n" +
            "75001\n" +
            "\n$$$$\n";

    new NonStrictExpectations()
    {
      {
        client.listNames();
        result = new String[]
        {
          "Compound_00000001_00025000.sdf.gz",
          "Compound_00025001_00050000.sdf.gz",
          "Compound_00050001_00075000.sdf.gz",
          "Compound_00075001_00100000.sdf.gz"
        };
        client.retrieveFileStream("Compound_00050001_00075000.sdf.gz");
        result = new ByteArrayInputStream(zipStringToBytes(chunk1));
        client.retrieveFileStream("Compound_00075001_00100000.sdf.gz");
        result = new ByteArrayInputStream(zipStringToBytes(chunk2));
      }
    };
    DefaultRecordStreamer streamer = snapshot.getCompounds(53000);
    Iterator<Record> it = streamer.iterator();
    assertTrue(it.hasNext());
    assertEquals("53001", it.next().get("PUBCHEM_COMPOUND_CID"));
    assertTrue(it.hasNext());
  }

  public void testGetSubstancesStreamsFromSID() throws Exception
  {
    final String chunk1 =
            Molfiles.benzene + "\n"+
            "> <PUBCHEM_SUBSTANCE_ID>\n" +
            "53000\n" +
            "\n$$$$\n" +
            Molfiles.benzene + "\n"+
            "> <PUBCHEM_SUBSTANCE_ID>\n" +
            "53001\n" +
            "\n$$$$\n";
    final String chunk2 =
            Molfiles.benzene + "\n"+
            "> <PUBCHEM_SUBSTANCE_ID>\n" +
            "75001\n" +
            "\n$$$$\n";

    new NonStrictExpectations()
    {
      {
        client.listNames();
        result = new String[]
        {
          "Substance_00000001_00025000.sdf.gz",
          "Substance_00025001_00050000.sdf.gz",
          "Substance_00050001_00075000.sdf.gz",
          "Substance_00075001_00100000.sdf.gz"
//          "Substance_0"
        };
        client.retrieveFileStream("Substance_00050001_00075000.sdf.gz");
        result = new ByteArrayInputStream(zipStringToBytes(chunk1));
        client.retrieveFileStream("Substance_00075001_00100000.sdf.gz");
        result = new ByteArrayInputStream(zipStringToBytes(chunk2));
      }
    };

    DefaultRecordStreamer streamer = snapshot.getSubstances(53000);
    Iterator<Record> it = streamer.iterator();
    assertTrue(it.hasNext());
    assertEquals("53001", it.next().get("PUBCHEM_SUBSTANCE_ID"));
    assertTrue(it.hasNext());
  }
}
