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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.restlet.resource.XmlRepresentation;

import edu.cudenver.bios.filesvc.application.FileLogger;
import edu.cudenver.bios.filesvc.representation.ErrorXMLRepresentation;

/**
 * Resource which performs a file upload and returns the 
 * data to the calling application.
 * @author Sarah Kreidler
 *
 */
public class UploadResource extends Resource
{
    private static final String FORM_TAG_FILE = "file";
    
	/**
	 * Constructor and entry point for file upload requests
	 * @param context servlet context
	 * @param request HTTP request information
	 * @param response HTTP response information
	 */
    public UploadResource(Context context, Request request, Response response) 
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
     * Handle POST requests for file upload
     * @param entity entity body information (multi-part form encoded)
     */
    @Override
    public void acceptRepresentation(Representation entity) 
    {
        if (entity != null) 
        {
            if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
                    true)) 
            {

                // The Apache FileUpload project parses HTTP requests which
                // conform to RFC 1867, "Form-based File Upload in HTML". That
                // is, if an HTTP request is submitted using the POST method,
                // and with a content type of "multipart/form-data", then
                // FileUpload can parse that request, and get all uploaded files
                // as FileItem.

                // 1/ Create a factory for disk-based file items
                DiskFileItemFactory factory = new DiskFileItemFactory();
                factory.setSizeThreshold(1000240);

                // 2 Create a new file upload handler based on the Restlet
                // FileUpload extension that will parse Restlet requests and
                // generates FileItems.
                RestletFileUpload upload = new RestletFileUpload(factory);
                List<FileItem> items;
                try 
                {
                    // 3. Request is parsed by the handler which generates a
                    // list of FileItems
                    items = upload.parseRequest(getRequest());

                    // Process only the uploaded item called "fileToUpload" and
                    // save it on disk
                    boolean found = false;
                    FileItem fi = null;
                    for (final Iterator<FileItem> it = items.iterator(); it.hasNext() && !found;) 
                    {
                        fi = (FileItem) it.next();
                        if (fi.getFieldName().equals(FORM_TAG_FILE)) 
                        {
                        	found = true;
                        	break;
                        }
                    }
                    // Once handled, the content of the uploaded file is sent
                    // back to the client.
                    Representation rep = null;
                    if (found) 
                    {
                        // Create a new representation based on disk file.
                        // The content is arbitrarily sent as plain text.
                        rep = new StringRepresentation(fi.getString(),
                                MediaType.TEXT_HTML);
                        getResponse().setEntity(rep);
                        getResponse().setStatus(Status.SUCCESS_OK);
                    } 
                    else 
                    {
                        rep = new StringRepresentation("No file data found",
                                MediaType.TEXT_HTML);
                        getResponse().setEntity(rep);
                        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    }
                } 
                catch (Exception e) 
                {
                    // The message of all thrown exception is sent back to
                    // client as simple plain text
                    getResponse().setEntity(
                            new StringRepresentation("Upload failed: " + e.getMessage(),
                                    MediaType.TEXT_HTML));
                    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                }
            }
        } 
        else 
        {
            // POST request with no entity.
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
    }
}


