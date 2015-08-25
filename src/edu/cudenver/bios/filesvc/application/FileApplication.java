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
package edu.cudenver.bios.filesvc.application;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import edu.cudenver.bios.filesvc.resource.DefaultResource;
import edu.cudenver.bios.filesvc.resource.SaveAsResource;
import edu.cudenver.bios.filesvc.resource.UploadResource;

/**
 * Restlet Application for basic file upload/save
 *
 * @author Sarah Kreidler
 *
 */
public class FileApplication extends Application
{
    /**
     * Constructor.
     * @param parentContext servlet context information
     */
    public FileApplication(Context parentContext) throws Exception
    {
        super(parentContext);

        FileLogger.getInstance().info("file service starting.");
    }

    /**
     * Define URI mappings.  Admittedly, this interface is really not very
     * RESTful, but it does the job
     */
    @Override
    public Restlet createInboundRoot()
    {
        // Create a router Restlet that routes each call to a new instance of Resource.
        Router router = new Router(getContext());
        // Defines only one default route, self-identifies server
        router.attachDefault(DefaultResource.class);
        router.attach("/file", DefaultResource.class);

        /* file save-as resource - echos the contents of a file with appropriate
         * headers to initiate a save ad dialog in the client browser
         */
        router.attach("/saveas", SaveAsResource.class);

        /* file upload resource - echos the contents of a file as text/html */
        router.attach("/upload", UploadResource.class);

        return router;
    }
}

