package ejemplo;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@WebServlet("/Serve")
public class Serve extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws IOException {
    	res.getHeader(getServletInfo().toString());
    	
            BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
            
            //Añadir la extensión del archivo adjunto
            res.addHeader("Content-Disposition", "attachment");

            blobstoreService.serve(blobKey, res);
        }
}