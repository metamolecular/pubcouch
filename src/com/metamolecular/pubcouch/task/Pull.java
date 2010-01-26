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

package com.metamolecular.pubcouch.task;

import com.metamolecular.pubcouch.record.Record;
import com.metamolecular.pubcouch.record.RecordStreamer;
import java.util.HashMap;
import java.util.Map;
import org.jcouchdb.db.Database;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class Pull
{
  private static String CID_PREFIX = "C";
  private static String SID_PREFIX = "S";
  private static String PUBCHEM_COMPOUND_CID = "PUBCHEM_COMPOUND_CID";
  private static String PUBCHEM_SUBSTANCE_ID = "PUBCHEM_SUBSTANCE_ID";
  private static String ID = "_id";
  private Database db;
  private RecordStreamer streamer;
  private int maxRecords;

  public Pull(Database db, RecordStreamer streamer)
  {
    this.db = db;
    this.streamer = streamer;
    this.maxRecords = -1;
  }

  public void setMaxRecords(int max)
  {
    this.maxRecords = max;
  }

  public void run()
  {
    int count = 0;
    Map<String, String> atts = new HashMap();

    for (Record record : streamer)
    {
      for (String key : record.getKeys())
      {
        atts.put("MOLFILE", record.getMolfile());
        atts.put(key, record.get(key));
      }

      String cid = null;
      String sid = null;

      if ((cid = atts.get(PUBCHEM_COMPOUND_CID)) != null)
      {
        atts.put(ID, CID_PREFIX + cid);
      }
      else if ((sid = atts.get(PUBCHEM_SUBSTANCE_ID)) != null)
      {
        atts.put(ID, SID_PREFIX + sid);
      }
      else
      {
        throw new RuntimeException("Cant't determine document type for " + atts);
      }

      db.createDocument(atts);

      count++;
      if (maxRecords == count)
      {
        break;
      }

      atts.clear();
    }
  }
}
