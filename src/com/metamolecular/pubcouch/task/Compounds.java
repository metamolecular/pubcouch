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

import com.metamolecular.pubcouch.filter.CountingFilter;
import com.metamolecular.pubcouch.filter.RecordFilter;
import com.metamolecular.pubcouch.pubchem.Snapshot;
import com.metamolecular.pubcouch.record.FilterRecordStreamer;
import com.metamolecular.pubcouch.record.Record;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jcouchdb.db.Database;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class Compounds
{

  private static String PUBCHEM_COMPOUND_CID = "PUBCHEM_COMPOUND_CID";
  private static String PUBCHEM_NIST_INCHI = "PUBCHEM_NIST_INCHI";
  private static String PUBCHEM_TOTAL_CHARGE = "PUBCHEM_TOTAL_CHARGE";
  private static String PUBCHEM_BONDANNOTATIONS = "PUBCHEM_BONDANNOTATIONS";
  private static String AROMATIC = "8";
  private static String ZERO = "0";
  private static String QUESTION = "?";
  private static String DOT = ".";
  private static String ID = "_id";
  private static String INCHI = "inchi";
  private static String SERIALIZATION = "serialization";
  
  private Snapshot snapshot;
  private CountingFilter countingFilter;
  private Database db;

  public Compounds(String host, String databaseName) throws IOException
  {
    this.db = new Database(host, databaseName);
    this.countingFilter = new CountingFilter(500);
    this.snapshot = new Snapshot();
    this.snapshot.connect("anonymous", "");
  }

  public void setMaxRecords(int maxRecords)
  {
    countingFilter.setMaxRecords(maxRecords);
  }

  public void snapshot() throws IOException
  {
    FilterRecordStreamer streamer = new FilterRecordStreamer(snapshot.getCompounds(), new StrictFilter());
    Map<String, String> doc = new HashMap();

    for (Record record : streamer)
    {
      writeDocument(record, doc);
      db.createDocument(doc);

      doc.clear();
    }
  }

  private void writeDocument(Record record, Map<String, String> doc)
  {
    System.out.println("Writing CID " + record.get(PUBCHEM_COMPOUND_CID));

    doc.put(ID, record.get(PUBCHEM_COMPOUND_CID));
    doc.put(INCHI, record.get(PUBCHEM_NIST_INCHI));
    doc.put(SERIALIZATION, record.getMolfile());
  }

  private class StrictFilter implements RecordFilter
  {

    public boolean abort()
    {
      return false;
    }

    public boolean pass(Record record)
    {
      System.out.println("checking: " + record.get(PUBCHEM_COMPOUND_CID));
      String inchi = record.get(PUBCHEM_NIST_INCHI);

      if (inchi == null)
      {
        System.out.println("no inchi");
        return false;
      }

      boolean pass = !undefinedStereo(inchi) &&
             !multicomponent(inchi) &&
             !charged(record) &&
             !badBondAnnotations(record);

      return pass;
    }

    private boolean undefinedStereo(String inchi)
    {
      return inchi.contains(QUESTION);
    }

    private boolean multicomponent(String inchi)
    {
      return inchi.contains(DOT);
    }

    private boolean charged(Record record)
    {
      String totalCharge = record.get(PUBCHEM_TOTAL_CHARGE);

      if (totalCharge == null)
      {
        return true; // shouldn't happen, but might
      }

      return !ZERO.equals(totalCharge);
    }

    private boolean badBondAnnotations(Record record)
    {
      String field = record.get(PUBCHEM_BONDANNOTATIONS);

      if (field == null)
      {
        return false;
      }
      
      String[] lines = field.split("\\n");

      for (String annotation : lines)
      {
        String[] elements = annotation.split("  ");
        String type = null;

        try
        {
          type = elements[2];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
          System.out.println("oops...");
          return true;
        }
        
        if (!keepBond(type))
        {
          return true;
        }
      }

      return false;
    }

    private boolean keepBond(String type)
    {
      return type.equals(AROMATIC);
    }
  }
}
