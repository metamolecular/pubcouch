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

import com.metamolecular.pubcouch.model.Record;
import com.metamolecular.pubcouch.model.RecordSet;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Iterator;
import junit.framework.TestCase;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class RecordSetTest extends TestCase
{

  private RecordSet set;
  private String records;

  @Override
  protected void setUp() throws Exception
  {
    records = "";
  }

  private void loadRecords(int count)
  {
    for (int i = 0; i < count; i++)
    {
      records += Molfiles.benzene + "\n" + "$$$$\n";
    }

    set = new RecordSet(new BufferedReader(new StringReader(records)));
  }

  public void testNoRecordsDoesNotHaveNext() throws Exception
  {
    loadRecords(0);
    assertFalse(set.iterator().hasNext());
  }

  public void testOneRecordHasNext() throws Exception
  {
    loadRecords(1);
    assertTrue(set.iterator().hasNext());
  }

  public void testOneRecordNextReturnsNext() throws Exception
  {
    loadRecords(2);
    assertEquals(Molfiles.benzene, set.iterator().next().getMolfile());
  }

  public void testOneRecordDoesNotHaveNextAfterNext() throws Exception
  {
    loadRecords(1);
    set.iterator().next();
    assertFalse(set.iterator().hasNext());
  }

  public void testNextTwiceReturnsRecords() throws Exception
  {
    loadRecords(2);
    assertEquals(Molfiles.benzene, set.iterator().next().getMolfile());
    assertEquals(Molfiles.benzene, set.iterator().next().getMolfile());
  }
}
