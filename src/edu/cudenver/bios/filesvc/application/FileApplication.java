package edu.cudenver.bios.filesvc.application;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Router;

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
     * @param parentContext
     */
    public FileApplication(Context parentContext) throws Exception
    {
        super(parentContext);

        FileLogger.getInstance().info("data feed service starting.");
    }

    @Override
    public Restlet createRoot() 
    {
        // Create a router Restlet that routes each call to a new instance of Resource.
        Router router = new Router(getContext());
        // Defines only one default route, self-identifies server
        router.attachDefault(DefaultResource.class);
        router.attach("/file", DefaultResource.class);

        /* file save-as resource - echos the contents of a file with appropriate
         * headers to initiate a save ad dialog in the client browser
         */
        router.attach("/file/saveas", SaveAsResource.class);
        
        /* file upload resource - echos the contents of a file as text/html */
        router.attach("/file/upload", UploadResource.class);

        return router;
    }
}

