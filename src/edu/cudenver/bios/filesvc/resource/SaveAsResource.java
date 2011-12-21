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

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

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
public class SaveAsResource extends ServerResource
{

	/**
	 * Handle POST requests for file save
	 * @param entity entity body information (form encoded)
	 */
	@Post 
	public Representation saveAs(Representation entity) throws ResourceException
	{
		try
		{
			// build the response xml
			Form form = new Form(entity);
			String filename = form.getFirstValue("filename");
			if (filename == null || filename.isEmpty()) filename = "out.xml";
			String format = form.getFirstValue("format");
			String data = form.getFirstValue("data");
			if (data == null)
			{
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "No data specified");
			}
			// TODO: format to pdf, word, ppt?
			Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");  
			if (responseHeaders == null)  
			{  
				responseHeaders = new Form();  
				getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
			}  
			responseHeaders.add("Content-type", "application/force-download");
			responseHeaders.add("Content-disposition", "attachment; filename=" + filename);
			return new StringRepresentation(data);
		}
		catch (IllegalArgumentException iae)
		{
			FileLogger.getInstance().error(iae.getMessage());
			try { getResponse().setEntity(new ErrorXMLRepresentation(iae.getMessage())); }
			catch (IOException e) {}
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, iae.getMessage());
		}
		//return entity;

	}

}
