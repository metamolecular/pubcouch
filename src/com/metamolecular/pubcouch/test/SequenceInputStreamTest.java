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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import junit.framework.TestCase;

/**
 * Illustrates the use and functioning of fo SequenceInputStream.
 * 
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class SequenceInputStreamTest extends TestCase
{
  private SequenceInputStream stream;

  private byte[] zipStringToBytes(String input) throws IOException
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
    bufos.write(input.getBytes("UTF-8"));
    bufos.close();
    byte[] retval = bos.toByteArray();
    bos.close();
    return retval;
  }

  public void testItJoinsTwoStreams() throws Exception
  {
    List<InputStream> streams = new ArrayList();

    streams.add(new GZIPInputStream(new ByteArrayInputStream(zipStringToBytes("foo\n"))));
    streams.add(new GZIPInputStream(new ByteArrayInputStream(zipStringToBytes("bar\n"))));
    streams.add(new GZIPInputStream(new ByteArrayInputStream(zipStringToBytes("baz\n"))));

    stream = new SequenceInputStream(Collections.enumeration(streams));
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

    assertEquals("foo", reader.readLine());
    assertEquals("bar", reader.readLine());
    assertEquals("baz", reader.readLine());
  }
}
