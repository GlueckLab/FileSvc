/*
 * File Service for the GLIMMPSE Software System.  Manages
 * upload/save requests.
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.filesvc.resource;

import java.io.IOException;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import edu.cudenver.bios.filesvc.application.FileLogger;
import edu.cudenver.bios.filesvc.representation.ErrorXMLRepresentation;

/**
 * Resource which returns form data with an application/download
 * header to force the Save As dialog in the browser.
 * (Could not find a browser independent method for this on the
 * client-side)
 * 
 * @author Sarah Kreidler
 *
 */
public class SaveAsResource extends Resource
{
	/**
	 * Constructor and entry point for save as requests
	 * @param context servlet context
	 * @param request HTTP request information
	 * @param response HTTP response information
	 */
    public SaveAsResource(Context context, Request request, Response response) 
    {
        super(context, request, response);
        getVariants().add(new Variant(MediaType.APPLICATION_XML));
    }

    /**
     * Disallow get requests to this resource
     */
    @Override
    public boolean allowGet()
    {
        return false;
    }

    /**
     * Disallow put requests to this resource
     */
    @Override
    public boolean allowPut()
    {
        return false;
    }

    /**
     * Allow post requests to this resource
     */
    @Override
    public boolean allowPost() 
    {
        return  true;
    }

    /**
     * Handle POST requests for file save
     * @param entity entity body information (form encoded)
     */
    @Override 
    public void acceptRepresentation(Representation entity)
    {
        try
        {
            // build the response xml
        	Form form = new Form(entity);
        	String filename = form.getFirstValue("filename");
        	if (filename == null || filename.isEmpty()) filename = "out.xml";
        	String format = form.getFirstValue("format");
        	String data = form.getFirstValue("data");
        	// TODO: format to pdf, word, ppt?
        	if (data != null)
        	{
        	    if (format != null)
        	    {
        	        
        	    }
        	    else
        	    {
                    FileLogger.getInstance().warn("No format specified, returning data as xml");
        	        getResponse().setEntity(new StringRepresentation(data));
        	    }
        	}
        	else
        	{
        		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "No data specified");
        	}
            Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");  
            if (responseHeaders == null)  
            {  
            	responseHeaders = new Form();  
            	getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
            }  
            responseHeaders.add("Content-type", "application/force-download");
            responseHeaders.add("Content-disposition", "attachment; filename=" + filename);
            
            getResponse().setStatus(Status.SUCCESS_CREATED);
        }
        catch (IllegalArgumentException iae)
        {
            FileLogger.getInstance().error(iae.getMessage());
            try { getResponse().setEntity(new ErrorXMLRepresentation(iae.getMessage())); }
            catch (IOException e) {}
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
        catch (ResourceException re)
        {
            FileLogger.getInstance().error(re.getMessage());
            try { getResponse().setEntity(new ErrorXMLRepresentation(re.getMessage())); }
            catch (IOException e) {}
            getResponse().setStatus(re.getStatus());
        }

    }

}
