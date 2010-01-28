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
import com.metamolecular.pubcouch.record.FilterRecordStreamer;
import com.metamolecular.pubcouch.record.Record;
import com.metamolecular.pubcouch.filter.RecordFilter;
import com.metamolecular.pubcouch.record.RecordStreamer;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import junit.framework.TestCase;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class FilterRecordStreamerTest extends TestCase
{
  private FilterRecordStreamer filterStreamer;
  private RecordStreamer streamer;
  private BufferedReader reader;
  private RecordFilter filter;

  @Override
  protected void setUp() throws Exception
  {
    String records =  "";

    for (int i = 0; i < 10; i++)
    {
      records += Molfiles.benzene + "\n" + 
              "> <ATT>\n" +
              (i % 2 == 0 ? "pass" : "fail") + "\n\n" +
              "$$$$\n";
    }

    streamer = new DefaultRecordStreamer(new ByteArrayInputStream(records.getBytes("UTF-8")));
  }

  private void passAllFilter()
  {
    filter = new RecordFilter()
    {
      public boolean pass(Record record)
      {
        return true;
      }

      public boolean abort()
      {
        return false;
      }

    };
  }

  private void passNoneFilter()
  {
    filter = new RecordFilter()
    {
      public boolean pass(Record record)
      {
        return false;
      }

      public boolean abort()
      {
        return false;
      }

    };
  }

  private void passAttributeFilter()
  {
    filter = new RecordFilter()
    {
      int count = 0;

      public boolean pass(Record record)
      {
        return "pass".equals(record.get("ATT"));
      }

      public boolean abort()
      {
        return false;
      }

    };
  }

  private void doNew()
  {
    filterStreamer = new FilterRecordStreamer(streamer, filter);
  }

  public void testItFiltersNoneWhenPassAll() throws Exception
  {
    passAllFilter();
    doNew();
    int count = 0;
    for (Record record : filterStreamer)
    {
      count++;
    }
    assertEquals(10, count);
  }

  public void testItFiltersAllWhenPassNone() throws Exception
  {
    passNoneFilter();
    doNew();

    assertFalse(filterStreamer.iterator().hasNext());
  }

  public void testItFiltersAlternating() throws Exception
  {
    passAttributeFilter();
    doNew();
    int count = 0;
    for (Record record : filterStreamer)
    {
      count++;
      assertEquals(record.get("ATT"), "pass");
    }
    assertEquals(5, count);
    assertFalse(filterStreamer.iterator().hasNext());
  }
}
