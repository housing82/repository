package com.universal.runner.srd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextFileReader {

	private BufferedReader bReader = null;
	 
    // Create BufferedReader : System encode
    public TextFileReader(String strFilename) throws FileNotFoundException
    {
        this.bReader = new BufferedReader( new FileReader(strFilename) );
    }
     
    // Create BufferedReader : user encode
    public TextFileReader(String strFilename, String strCharset) throws IOException
    {
        this.bReader = new BufferedReader(
                            new InputStreamReader(
                                new FileInputStream( new File(strFilename) ),
                                strCharset
                            )
                        );
        removeBOM(strCharset) ;
    }
 
    // remove BOM
    private void removeBOM(String strCharset) throws IOException
    {
        if( strCharset.compareTo("UTF-8"   ) == 0 ||
            strCharset.compareTo("UTF-16LE") == 0 ||
            strCharset.compareTo("UTF-16BE") == 0 )
        {
            // delete BOM
            char[] cBuffer = new char[1];
            this.bReader.read(cBuffer, 0, 1);
        }
    }
     
    // read file data
    public String readLine() throws IOException
    {
        return bReader.readLine();
    }
     
    // close file
    public void close() throws IOException
    {
        if( this.bReader != null )
        {
            this.bReader.close();
            this.bReader = null;
        }
    }
}
