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
package com.metamolecular.pubcouch.pubchem;

import com.metamolecular.pubcouch.record.DefaultRecordStreamer;
import com.metamolecular.pubcouch.record.Record;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class Snapshot extends Archive
{

  private static String SUBSTANCES_DIR = "/pubchem/Substance/CURRENT-Full/SDF";
  private static String COMPOUNDS_DIR = "/pubchem/Compound/CURRENT-Full/SDF";

  public Snapshot()
  {
    super();
  }

  public Snapshot(FTPClient client)
  {
    super(client);
  }

  @Override
  public DefaultRecordStreamer getCompounds() throws IOException
  {
    client.changeWorkingDirectory(COMPOUNDS_DIR);

    return new DefaultRecordStreamer(getStream());
  }

  @Override
  public DefaultRecordStreamer getSubstances() throws IOException
  {
    client.changeWorkingDirectory(SUBSTANCES_DIR);

    return new DefaultRecordStreamer(getStream());
  }

  @Override
  public DefaultRecordStreamer getSubstances(int beginAfter) throws IOException
  {
    client.changeWorkingDirectory(SUBSTANCES_DIR);
    DefaultRecordStreamer result = new DefaultRecordStreamer(getStream(beginAfter));
    String key = "PUBCHEM_SUBSTANCE_ID";
    String value = String.valueOf(beginAfter);

    for (Record record : result)
    {
      if (record.get(key).equals(value))
      {
        break;
      }
    }

    return result;
  }

  public boolean completePendingCommand() throws IOException
  {
    return client.completePendingCommand();
  }

  public InputStream getStream() throws IOException
  {
    return new SequenceInputStream(new StreamEnumerator(client.listNames()));
  }

  private InputStream getStream(int beginAfter) throws IOException
  {
//    Compound_00000001_00025000.sdf.gz
    Pattern pattern = Pattern.compile("^(Substance|Compound)_(\\d{8})_(\\d{8}).sdf.gz");
    List<String> names = new ArrayList();
    String[] allNames = client.listNames();
    boolean check = true;

    for (String name : allNames)
    {
      Matcher matcher = pattern.matcher(name);

      if (matcher.matches())
      {
        if (check)
        {
          int start = Integer.parseInt(matcher.group(2));
          int end = Integer.parseInt(matcher.group(3));

          if (beginAfter >= start && beginAfter < end)
          {
            names.add(name);
            check = false;
          }
        }
        else
        {
          names.add(name);
        }
      }
    }

    return new SequenceInputStream(new StreamEnumerator((String[]) names.toArray(new String[0])));
  }

  private class StreamEnumerator implements Enumeration<InputStream>
  {

    private String[] filenames;
    private int index;

    private StreamEnumerator(String[] filenames)
    {
      this.filenames = filenames;
      this.index = 0;
    }

    public boolean hasMoreElements()
    {
      return index < filenames.length;
    }

    public InputStream nextElement()
    {
      try
      {
        if (index != 0)
        {
          client.completePendingCommand();
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      InputStream result = null;

      try
      {
        client.setFileType(FTP.BINARY_FILE_TYPE);
        result = new GZIPInputStream(client.retrieveFileStream(filenames[index]));
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }

      index++;

      return result;
    }
  }
}
