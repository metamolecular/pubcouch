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

import com.metamolecular.pubcouch.record.DefaultRecordStreamer;
import com.metamolecular.pubcouch.task.Pull;
import java.io.ByteArrayInputStream;
import junit.framework.TestCase;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.jcouchdb.db.Database;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class PullTest extends TestCase
{
  private Pull pull;
  private DefaultRecordStreamer streamer;
  @Mocked
  private Database db;

  private void loadRecords(int count) throws Exception
  {
    String records = "";

    for (int i = 0; i < count; i++)
    {
      records += Molfiles.benzene + "\n" +
      "> <PUBCHEM_COMPOUND_CID>\n" +
      "1234\n\n" +
      "$$$$\n";
    }

    ByteArrayInputStream stream = new ByteArrayInputStream(records.getBytes("UTF-8"));
    streamer = new DefaultRecordStreamer(stream);
  }

  public void testRunCreatesMax2Records() throws Exception
  {
    loadRecords(10);

    new NonStrictExpectations()
    {
      {
        db.createDocument(any); times = 2;
      }
    };

    pull = new Pull(db, streamer, 2);
    pull.run();
  }

  public void testRunCreatesAllRecordsByDefault() throws Exception
  {
    loadRecords(10);

    new NonStrictExpectations()
    {
      {
        db.createDocument(any); times = 10;
      }
    };

    pull = new Pull(db, streamer, -1);
    pull.run();
  }
}
