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

import com.metamolecular.pubcouch.filter.CompositeFilter;
import com.metamolecular.pubcouch.filter.CountingFilter;
import com.metamolecular.pubcouch.filter.RecordFilter;
import com.metamolecular.pubcouch.pubchem.Snapshot;
import com.metamolecular.pubcouch.record.FilterRecordStreamer;
import com.metamolecular.pubcouch.record.Record;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jcouchdb.db.Database;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class PullSynonyms
{

  private static String PUBCHEM_GENERIC_REGISTRY_NAME = "PUBCHEM_GENERIC_REGISTRY_NAME";
  private static String PUBCHEM_EXT_DATASOURCE_NAME = "PUBCHEM_EXT_DATASOURCE_NAME";
  private static String PUBCHEM_SUBSTANCE_ID = "PUBCHEM_SUBSTANCE_ID";
  private static String PUBCHEM_EXT_SUBSTANCE_URL = "PUBCHEM_EXT_SUBSTANCE_URL";
  private static String PUBCHEM_CID_ASSOCIATIONS = "PUBCHEM_CID_ASSOCIATIONS";
  private CompositeFilter compositeFilter;
  private CountingFilter countingFilter;
  private Snapshot snapshot;
  private Database db;

  public PullSynonyms(String host, String databaseName) throws IOException
  {
    this.compositeFilter = new CompositeFilter();
    this.db = new Database(host, databaseName);
    this.snapshot = new Snapshot();
    this.snapshot.connect("anonymous", "");
    this.countingFilter = new CountingFilter(500);

    this.compositeFilter.addFilter(new SynonymFilter());
    this.compositeFilter.addFilter(countingFilter);
  }

  public void setMaxRecords(int maxRecords)
  {
    countingFilter.setMaxRecords(maxRecords);
  }

  public void run() throws IOException
  {
    FilterRecordStreamer streamer = new FilterRecordStreamer(snapshot.getSubstances(), compositeFilter);
    Map<String, String> doc = new HashMap();
    Set<String> synonyms = new HashSet();

    for (Record record : streamer)
    {
      writeDocuments(record, synonyms, doc);

      if (compositeFilter.abort())
      {
        break;
      }
    }
  }

  private class SynonymFilter implements RecordFilter
  {

    public boolean abort()
    {
      return false;
    }

    public boolean pass(Record record)
    {
      String field = record.get(PUBCHEM_GENERIC_REGISTRY_NAME);
      System.out.println("testing..." + field);
      return field != null;
    }
  }

  private void writeDocuments(Record record, Set<String> synonyms, Map<String, String> doc)
  {
    String registryNameField = record.get(PUBCHEM_GENERIC_REGISTRY_NAME);

    if (registryNameField != null)
    {
      synonyms.addAll(Arrays.asList(registryNameField.split("\r\n|\r|\n")));
    }

    doc.put("submitter", record.get(PUBCHEM_EXT_DATASOURCE_NAME));
    doc.put("sid", record.get(PUBCHEM_SUBSTANCE_ID));
    doc.put("uri", record.get(PUBCHEM_EXT_SUBSTANCE_URL));

    String cidAssociationsField = record.get(PUBCHEM_CID_ASSOCIATIONS);
    String cid = null;

    if (cidAssociationsField != null)
    {
      for (String association : cidAssociationsField.split("\r\n|\r|\n"))
      {
        String[] split = association.split("  ");

        if ("1".equals(split[1]))
        {
          cid = split[0];
          break;
        }
      }
    }

    doc.put("cid", cid);

    for (String synonym : synonyms)
    {
      doc.put("synonym", synonym.toLowerCase());
      db.createDocument(doc);
      doc.remove("_id");
      doc.remove("_rev");
    }

    doc.clear();
    synonyms.clear();
  }
}
