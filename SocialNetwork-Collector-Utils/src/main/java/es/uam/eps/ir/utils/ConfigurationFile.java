/* 
 * Copyright (C) 2018 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es
 * 
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.utils;
import es.uam.eps.ir.utils.exceptions.*;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Auxiliary class for reading configuration files. Configuration files are assumed to have a property in each line.
 * Each property will be composed by a property name and a property value, separated by '\t'. In case a property value
 * is an array, values will be separate by double spaces. No more '\t' will appear in the file.
 * @author Javier Sanz-Cruzado
 */
public class ConfigurationFile 
{
    /**
     * Auxiliary map for storing the configuration file values
     */
    private HashMap<String, String> values;
    
    /**
     * Value of true
     */
    private final static String TRUE = "true";
    /**
     * Value of false
     */
    private final static String FALSE = "false";
    
    
    public ConfigurationFile()
    {
        this.values = null;
    }
   
    /**
     * Reads the configuration file, and stores the fields into a table.
     * @param filename Route of the configuration file.
     * @throws InvalidConfigurationFileException The file is bad created, or an error has ocurred-
     */
    public void readConfigurationFile(String filename) throws InvalidConfigurationFileException
    {
        this.values = null;
        BufferedReader br;
        HashMap<String, String> aux = new HashMap<>();
        try
        {
            // Reading the configuration file
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

            String text;
            while((text = br.readLine()) != null)
            {
                String[] str = text.split("\t");
                if(str.length != 2)
                {
                    throw(new InvalidConfigurationFileException("Invalid configuration file"));
                }
                aux.put(str[0], str[1]);
            }

            br.close();
            this.values = aux;
        }
        catch(IOException ioe)
        {
            throw(new InvalidConfigurationFileException(ioe));
        }
    }
    
    /**
     * Retrieves a String value for a given field name.
     * @param fieldname Name of the field to retrieve.
     * @return The String contained in the field if exists, null if not.
     * @throws ConfigurationFileNotReadyException The file has not been previously read.
     */
    public String getString(String fieldname) throws ConfigurationFileNotReadyException
    {
        if(values == null)
        {
            throw new ConfigurationFileNotReadyException("File not read");
        }
        
        return values.get(fieldname);
    }
    
    /**
     * Retrieves a Double value for a given field name.
     * @param fieldname Name of the field to retrieve.
     * @return The Double contained in the field if exists, null if not.
     * @throws ConfigurationFileNotReadyException The file has not been previously read.
     */    
    public Double getDouble(String fieldname) throws ConfigurationFileNotReadyException
    {
        if(values == null)
        {
            throw new ConfigurationFileNotReadyException("File not read");
        }
        
        String value = values.get(fieldname);
        
        if(value != null)
            return Double.parseDouble(values.get(fieldname));
        
        return null;
    }

    /**
     * Retrieves an Integer value for a given field name.
     * @param fieldname Name of the field to retrieve.
     * @return The Integer contained in the field if exists, null if not.
     * @throws ConfigurationFileNotReadyException The file has not been previously read.
     */    
    public Integer getInteger(String fieldname) throws ConfigurationFileNotReadyException
    {
        if(values == null)
        {
            throw new ConfigurationFileNotReadyException("File not read");
        }
        
        String value = values.get(fieldname);
        
        if(value != null)
            return Integer.parseInt(value);
        return null;
    }

   /**
     * Retrieves an Long value for a given field name.
     * @param fieldname Name of the field to retrieve.
     * @return The Long contained in the field if exists, null if not.
     * @throws ConfigurationFileNotReadyException The file has not been previously read.
     */
    public Long getLong(String fieldname) throws ConfigurationFileNotReadyException
    {
        if(values == null)
        {
            throw new ConfigurationFileNotReadyException("File not read");
        }
        
        String value = values.get(fieldname);
        
        if(value != null)
            return Long.parseLong(value);
        else
            return null;
    }

     /**
     * Retrieves an Float value for a given field name.
     * @param fieldname Name of the field to retrieve.
     * @return The Float contained in the field if exists, null if not.
     * @throws ConfigurationFileNotReadyException The file has not been previously read.
     */
    public Float getFloat(String fieldname) throws ConfigurationFileNotReadyException
    {
        if(values == null)
        {
            throw new ConfigurationFileNotReadyException("File not read");
        }
        String value = values.get(fieldname);
        
        if(value != null)
            return Float.parseFloat(value);
        return null;
    }

     /**
     * Retrieves a Boolean value for a given field name.
     * @param fieldname Name of the field to retrieve.
     * @return The Boolean contained in the field if exists, null if not.
     * @throws ConfigurationFileNotReadyException The file has not been previously read.
     */
    public Boolean getBoolean(String fieldname) throws ConfigurationFileNotReadyException
    {
        if(values == null)
        {
            throw new ConfigurationFileNotReadyException("File not read");
        }
        String value = values.get(fieldname);
        
        if(value != null)
        {
            if(value.equalsIgnoreCase(TRUE))
            {
                return true;
            }
            else if(value.equalsIgnoreCase(FALSE))
            {
                return false;
            }
        }
        return null;
    }
    
    /**
     * Retrieves a Date value for a given field name.
     * @param fieldname Name of the field to retrieve.
     * @param format Date format to use.
     * @return the Date contained in the field if exists, null if not, or a parsing problem exists.
     * @throws ConfigurationFileNotReadyException The file has not been previously read.
     * @see java.text.SimpleDateFormat
     */
    public Date getDate(String fieldname, String format) throws ConfigurationFileNotReadyException
    {
        if(values == null)
        {
            throw new ConfigurationFileNotReadyException("File not read");
        }
        
        String value = values.get(fieldname);
        
        if(value != null)
        {
            try {
                DateFormat df = new SimpleDateFormat(format);
                Date d = df.parse(value);
                return d;
            } catch (ParseException ex) 
            {
                return null;
            }
        }
        return null;
    }
}