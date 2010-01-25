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

package com.metamolecular.pubcouch.record;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Richard L. Apodaca <rapodaca at metamolecular.com>
 */
public class Record
{
  private static String M_END = "M  END";
  private static String END_OF_RECORD = "$$$$";
  private Pattern keyPattern;
  private static String BLANK = "";
  private String molfile = null;
  private Map properties;

  public Record(BufferedReader reader) throws IOException
  {
    properties = new HashMap();
    keyPattern = Pattern.compile("^> *?<(.*?)>", Pattern.MULTILINE);
    readLines(reader);

    if (molfile == null)
    {
      throw new InvalidRecordException("No molfile found.");
    }
  }

  public String getMolfile()
  {
    return molfile;
  }

  public Collection<String> getKeys()
  {
    return properties.keySet();
  }

  public String get(String key)
  {
    return (String) properties.get(key);
  }

  private void readLines(BufferedReader reader) throws IOException
  {
    readMolfile(reader);
    readProperties(reader);
  }

  private void readMolfile(BufferedReader reader) throws IOException
  {
    StringBuffer buffer = new StringBuffer();
    String line = reader.readLine();

    while (line != null)
    {
      buffer.append(line);

      if (M_END.equals(line))
      {
        this.molfile = buffer.toString();
        break;
      }
      else
      {
        buffer.append("\n");
        line = reader.readLine();
      }
    }
  }

  private void readProperties(BufferedReader reader) throws IOException
  {
    while(readProperty(reader))
    {

    }
  }

  private boolean readProperty(BufferedReader reader) throws IOException, InvalidRecordException
  {
    StringBuffer buffer = new StringBuffer();
    String line = reader.readLine();

    if (END_OF_RECORD.equals(line))
    {
      return false;
    }

    if (line == null)
    {
      throw new InvalidRecordException("Missing $$$$ record terminator.");
    }

    String name = parseProperty(line);
    line = reader.readLine();

    while (!BLANK.equals(line))
    {
      buffer.append(line);
      line = reader.readLine();

      if (!BLANK.equals(line))
      {
        buffer.append("\n");
      }
    }

    properties.put(name, buffer.toString());

    return true;
  }

  private String parseProperty(String line)
  {
    Matcher matcher = keyPattern.matcher(line);

    if (matcher.matches())
    {
      return matcher.group(1);
    }

    throw new InvalidRecordException("Expected property line to start with >  <PROPERTY>. Got: " + line);
  }
}
