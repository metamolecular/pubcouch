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

import com.metamolecular.pubcouch.filter.RecordFilter;
import com.metamolecular.pubcouch.pubchem.Snapshot;
import com.metamolecular.pubcouch.record.FilterRecordStreamer;
import com.metamolecular.pubcouch.record.Record;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import org.jcouchdb.db.Database;
import org.jcouchdb.db.Options;
import org.jcouchdb.document.ValueRow;
import org.jcouchdb.document.ViewResult;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class PullCompounds
{

  private Snapshot snapshot;
  private Database db;
  private BitSet bitSet;

  public PullCompounds(String host, String databaseName) throws IOException
  {
    this.db = new Database(host, databaseName);
    this.snapshot = new Snapshot();
    this.bitSet = new BitSet(45000000);
    this.snapshot.connect("anonymous", "");
  }

  public void run() throws IOException
  {
    markCompounds();

    FilterRecordStreamer streamer = new FilterRecordStreamer(snapshot.getCompounds(), new PassAllFilter());

    for (Record record : streamer)
    {
      addStructureAttributes(record);
    }

    System.out.println("bits set: " + bitSet.cardinality());
  }

  private void markCompounds()
  {
    Options options = new Options();
    options.put("limit", 101);
    ViewResult<Map> result = db.queryView("cids/byID", Map.class, options, null);
    List<ValueRow<Map>> rows = result.getRows();

    while (rows.size() > 0)
    {
      for (int i = 0; i < rows.size() - 1; i++)
      {
        ValueRow<Map> vr = rows.get(i);
        String cid = (String) vr.getValue().get("cid");

        try
        {
          System.out.println("marking: " + cid);
          bitSet.set(Integer.parseInt(cid));
        }
        catch (Exception e)
        {
//        e.printStackTrace();
        System.out.println("ERROR parsing:" + vr.getId());
        }
      }

      if (rows.size() < 101)
      {
        break;
      }

      ValueRow<Map> lastRow = rows.get(rows.size() - 1);

      options.put("startkey", lastRow.getValue().get("_id"));

      System.out.println("options=" + options);

      result = db.queryView("cids/byID", Map.class, options, null);
      rows = result.getRows();
    }


  }

  private void markCompounds2()
  {
    Options options = new Options();
    options.put("limit", 101);
    db.queryView("cids/byID", Map.class, options, null);


    ViewResult<Map> result = db.queryView("cids/all", Map.class, null, null);
//    ViewResult<Map> result = db.queryViewByKeys("cids/all", Map.class, null, null, null);
    List<ValueRow<Map>> rows = result.getRows();

    for (ValueRow<Map> vr : rows)
    {
      String cid = (String) vr.getValue().get("cid");

//      System.out.println(cid);

      try
      {
        bitSet.set(Integer.parseInt(cid));
      }
      catch (Exception e)
      {
//        e.printStackTrace();
//        System.out.println("ERROR parsing:" + vr.getId());
      }
//      System.out.println(vr.getValue().get("cid"));
//      System.out.println("Need up update: " + vr.getId() + " with CID " + cid);
//      String id = vr.getId();
//      Map<String, String> doc = db.getDocument(Map.class, id);
//
//      doc.put("structure", record.getMolfile());
//      db.updateDocument(doc);
    }
  }

  private void addStructureAttributes(Record record)
  {
    int cid = 0;

    try
    {
      cid = Integer.parseInt(record.get("PUBCHEM_COMPOUND_CID"));
    }
    catch (Exception e)
    {
      return;
    }

    System.out.println("testing cid: " + cid);

    if (bitSet.get(cid))
    {
      ViewResult<Map> result = db.queryViewByKeys("cids/all", Map.class, Arrays.asList(String.valueOf(cid)), null, null);
      List<ValueRow<Map>> rows = result.getRows();

      for (ValueRow<Map> vr : rows)
      {
        System.out.println("Updating: " + vr.getId() + " with CID " + cid);
        String id = vr.getId();
        Map<String, String> doc = db.getDocument(Map.class, id);

        doc.put("structure", record.getMolfile());
        db.updateDocument(doc);
      }
    }
  }

  private class PassAllFilter implements RecordFilter
  {

    public boolean abort()
    {
      return false;
    }

    public boolean pass(Record record)
    {
      //test for CID in db.
      return true;
    }
  }
}
