package edu.cudenver.bios.filesvc.application;

import org.apache.log4j.Logger;

public class FileLogger
{
    private static Logger instance = null;

    private FileLogger() 
    {
    }

    public static Logger getInstance() 
    {
        if (instance == null) 
        {
            instance = Logger.getLogger("edu.cudenver.bios.filesvc.File");
        }

        return instance;
    }
}

