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

import com.metamolecular.pubcouch.record.RecordStreamer;
import com.metamolecular.pubcouch.pubchem.Archive;
import junit.framework.TestCase;
import mockit.Expectations;
import mockit.Mocked;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public  class ArchiveTest extends TestCase
{
  @Mocked private FTPClient client;
  private Archive archive;

  @Override
  protected void setUp() throws Exception
  {
    archive = new DummyArchive(client);
  }

  public void testConnectSetsUpConnection() throws Exception
  {
    new Expectations()
    {
      {
        client.connect("ftp.ncbi.nlm.nih.gov");
        client.login("user", "password");
      }
    };

    archive.connect("user", "password");
  }

  private class DummyArchive extends Archive
  {
    private DummyArchive(FTPClient client)
    {
      super(client);
    }

    @Override
    public RecordStreamer getCompounds()
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RecordStreamer getSubstances()
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }

  }
}