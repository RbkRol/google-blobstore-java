package ejemplo;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

// Se utiliza para traer los headers que contienen los detalles del archivo
import com.google.appengine.api.blobstore.FileInfo;

// Información del blob
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;

@WebServlet("/Upload")
public class Upload extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();   
    

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
    	// URL destino, por 5 partes
    	String nombre_bucket_Storage = "admin-login-548f4.appspot.com";
    	
    	// Obtener nombre del archivo por medio del header
    	String nombredelArchivo = req.getHeader("filename");
    	System.out.println("Nombre del archivo: " +  nombredelArchivo);
    	    	
    	// Obtener informacion del obj request. PRUEBA
    	System.out.println("REQ INFO:" + blobstoreService.getBlobInfos(req));
    	
    	// Traer el blob key del objeto cargado
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
        List<BlobKey> blobKeys = blobs.get("myFile");
        
        // Obtener información del archivo cargado. Se declara como para blobKey, está arriba. PRUEBA
        Map<String, List<FileInfo>> info_headers_cargado = blobstoreService.getFileInfos(req);
        
        // Hacer set en los encabezados necesarios en la carga del archivo. PRUEBA
        res.setHeader("Content-Type", "text/csv");
        res.setHeader("Content-Type", "application/octet-stream");
        res.setHeader("Content-Disposition: attachment", "filename=/"+ nombredelArchivo + "/");
        
        // Traer el valor de los encabezados. PRUEBA
        System.out.println(info_headers_cargado.values());
        
        // Traer el nombre del archivo cargado después de obtener su blob Key. PRUEBA
    	BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
    	BlobKey get_blobKey = new BlobKey(req.getParameter("blob-key"));
        blobstoreService.serve(get_blobKey, res);
    	BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(get_blobKey);
    	
    	
    	System.out.println(res.getHeader(nombredelArchivo));

        if (blobKeys == null || blobKeys.isEmpty()) {
        	//Si está vacío el campo archivo, redirige a la misma pagina
            //res.sendRedirect("/");
            
            // Si está vacio el campo archivo, manda mensaje de error
            res.sendRedirect("/?error=" + URLEncoder.encode("El campo archivo está vacío", "UTF-8"));
        } else {
        	// Enviar el blob al Storage!!
        	System.out.println("Enviando a cloud storage!...");
        	BlobKey blobKey = blobstoreService.createGsBlobKey(
        	    "/gs/" + nombre_bucket_Storage + "/gmm/" + res.getHeader(nombredelArchivo));
        	blobstoreService.serve(blobKey, res);
            res.sendRedirect("/gs/admin-login-548f4.appspot.com/gmm/upload/serve?blob-key=" + blobKeys.get(0).getKeyString());
        }
        
        System.out.println(blobKeys);
        
    }
}