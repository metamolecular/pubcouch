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

import com.metamolecular.pubcouch.model.InvalidRecordException;
import com.metamolecular.pubcouch.model.Record;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import junit.framework.TestCase;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class RecordTest extends TestCase
{

  private Record record;
  private String sd;
  private BufferedReader bufferedReader;
  private Reader reader;

  @Override
  protected void setUp() throws Exception
  {
  }

  private void invalidRecord()
  {
    sd = "foobar";
    newReader();
  }

  private void validRecord()
  {
    sd = Molfiles.benzene + "\n";
    sd += "> <PUBCHEM_MOLECULAR_WEIGHT>\n";
    sd += "178.6565\n\n";
    sd += "> <PUBCHEM_COORDINATE_TYPE>\n";
    sd += "1\n5\n255\n\n";
    sd += "$$$$";

    newReader();
  }

  private void newReader()
  {
    reader = new StringReader(sd);
    bufferedReader = new BufferedReader(reader);
  }

  public void testConstructorWithInvalidRecordThrows() throws Exception
  {
    invalidRecord();

    try
    {
      record = new Record(bufferedReader);
      fail();
    }
    catch (InvalidRecordException e)
    {
    }
  }

  public void testGetMolfileReturnsMolfile() throws Exception
  {
    validRecord();

    record = new Record(bufferedReader);

    assertEquals(Molfiles.benzene, record.getMolfile());
  }

  public void testGetMolecularWeightReturns() throws Exception
  {
    validRecord();

    record = new Record(bufferedReader);

    assertEquals("178.6565", record.get("PUBCHEM_MOLECULAR_WEIGHT"));
  }

  public void testGetMultilinePropertyReturns() throws Exception
  {
    validRecord();

    record = new Record(bufferedReader);

    assertEquals("1\n5\n255", record.get("PUBCHEM_COORDINATE_TYPE"));
  }

  public void testRecordWithoutEndOfRecordThrows() throws Exception
  {
    sd = Molfiles.benzene;
    newReader();

    try
    {
      record = new Record(bufferedReader);
      fail();
    }
    catch (InvalidRecordException e)
    {
    }
  }

  public void testRecordWithInvalidPropertyDefThrows() throws Exception
  {
    sd = Molfiles.benzene;
    sd += "> <PUBCHEM_MOLECULAR_WEIGHT\n";
    sd += "178.6565\n\n";
    newReader();

    try
    {
      record = new Record(bufferedReader);
      fail();
    }
    catch(InvalidRecordException e)
    {
      
    }
  }
}
