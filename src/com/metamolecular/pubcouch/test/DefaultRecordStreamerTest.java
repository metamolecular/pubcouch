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
import com.metamolecular.pubcouch.record.Record;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;

/**
 * 
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class DefaultRecordStreamerTest extends TestCase
{
  private DefaultRecordStreamer streamer;
  private String records;

  @Override
  protected void setUp() throws Exception
  {
    records = "";
  }

  private void loadRecords(int count) throws Exception
  {
    for (int i = 0; i < count; i++)
    {
      records += Molfiles.benzene + "\n" + "$$$$\n";
    }

    streamer = new DefaultRecordStreamer(new ByteArrayInputStream(records.getBytes("UTF-8")));
  }

  private void loadRecordsAsSeparateStreams(int count) throws Exception
  {
    List<InputStream> streams = new ArrayList();
    String record = Molfiles.benzene + "\n$$$$\n";

    for (int i = 0; i < count; i++)
    {
      streams.add(new ByteArrayInputStream(record.getBytes("UTF-8")));
    }

    InputStream stream = new SequenceInputStream(Collections.enumeration(streams));
    streamer = new DefaultRecordStreamer(stream);
  }

  public void testNoRecordsDoesNotHaveNext() throws Exception
  {
    loadRecords(0);
    assertFalse(streamer.iterator().hasNext());
  }

  public void testOneRecordHasNext() throws Exception
  {
    loadRecords(1);
    assertTrue(streamer.iterator().hasNext());
  }

  public void testOneRecordNextReturnsNext() throws Exception
  {
    loadRecords(2);
    assertEquals(Molfiles.benzene, streamer.iterator().next().getMolfile());
  }

  public void testOneRecordDoesNotHaveNextAfterNext() throws Exception
  {
    loadRecords(1);
    streamer.iterator().next();
    assertFalse(streamer.iterator().hasNext());
  }

  public void testNextTwiceReturnsRecords() throws Exception
  {
    loadRecords(2);
    assertEquals(Molfiles.benzene, streamer.iterator().next().getMolfile());
    assertEquals(Molfiles.benzene, streamer.iterator().next().getMolfile());
  }

//  public void testNextUsingKeyValueAdvancesIntoRecords() throws Exception
//  {
//    final String chunk =
//            Molfiles.benzene + "\n"+
//            "> <PUBCHEM_SUBSTANCE_ID>\n" +
//            "6501\n" +
//            "\n$$$$\n" +
//            Molfiles.benzene + "\n"+
//            "> <PUBCHEM_SUBSTANCE_ID>\n" +
//            "6502\n" +
//            "\n$$$$\n" +
//            Molfiles.benzene + "\n"+
//            "> <PUBCHEM_SUBSTANCE_ID>\n" +
//            "6503\n" +
//            "\n$$$$\n";
//
//    streamer = new DefaultRecordStreamer(new ByteArrayInputStream(chunk.getBytes("UTF-8")), "PUBCHEM_SUBSTANCE_ID", "6503");
//    Iterator<Record> it = streamer.iterator();
//
//    assertTrue(it.hasNext());
//  }

  public void testTwoRecordsAsSeparateStreamsHasNext() throws Exception
  {
    this.loadRecordsAsSeparateStreams(2);
    Iterator it = streamer.iterator();
    assertTrue(it.hasNext());
  }

  public void testTwoRecordsAsSeparateStreamsReturnsRecords() throws Exception
  {
    this.loadRecordsAsSeparateStreams(2);
    assertEquals(Molfiles.benzene, streamer.iterator().next().getMolfile());
    assertEquals(Molfiles.benzene, streamer.iterator().next().getMolfile());
  }
}
