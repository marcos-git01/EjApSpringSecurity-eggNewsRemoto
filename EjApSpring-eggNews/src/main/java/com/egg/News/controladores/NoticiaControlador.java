
package com.egg.News.controladores;

import com.egg.News.entidades.Noticia;
import com.egg.News.excepciones.MiException;
import com.egg.News.servicios.NoticiaServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/noticia") //localhost:8080/noticia
public class NoticiaControlador {
    
    @Autowired
    private NoticiaServicio noticiaServicio;
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_PERIODISTA')")
    @GetMapping("/registrar") //localhost:8080/noticia/registrar
    public String registrar(){
        return "noticia_form.html";
    }
    
    @PostMapping("/registro")
    public String registro(@RequestParam String titulo, @RequestParam String cuerpo, ModelMap modelo, @RequestParam MultipartFile archivo) {
        
        try {
            noticiaServicio.crearNoticia(archivo, titulo, cuerpo);
            
            modelo.put("exito", "La Noticia fue registrada correctamente!");
        } catch (MiException ex) {
            
            modelo.put("error", ex.getMessage());
            return "noticia_form.html"; 
        }
        
        return "noticia_form.html";
        //return "index.html"; 
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_PERIODISTA')")
    @GetMapping("/lista") //localhost:8080/noticia/lista
    public String listar(ModelMap modelo){
        
        List<Noticia> noticias = noticiaServicio.listarNoticias();
        
        modelo.addAttribute("noticias", noticias);
        
        return "noticia_list.html";
    }
    
    @GetMapping("/modificar/{id}") //localhost:8080/noticia/modificar
    public String modificar(@PathVariable String id, ModelMap modelo){
        
        modelo.put("noticia", noticiaServicio.getOne(id));
      
        return "noticia_modificar.html";
    }
    
    
    @PostMapping("/modificar/{id}")
    public String modificar(@RequestParam MultipartFile archivo, @PathVariable String id, @RequestParam String titulo, @RequestParam String cuerpo, ModelMap modelo) {
        
        try {
            noticiaServicio.modificarNoticia(archivo, id, titulo, cuerpo);
            
            //Ver esta linea si funciona? No funciona porque redirecciona a lista
            modelo.put("exito", "La noticia fue modificada correctamente!");
 
            return "redirect:../lista";
            
        } catch (MiException ex) {
            
            modelo.put("error", ex.getMessage());
            
            return "noticia_modificar.html"; 
        }
                       
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, ModelMap modelo) throws MiException {

        modelo.put("noticia", noticiaServicio.getOne(id));
        try {

            noticiaServicio.eliminarNoticia(id);

            return "redirect:../lista";

        } catch (MiException ex) {

            modelo.put("error", ex.getMessage());

            //return "noticia_eliminar.html";
            return "redirect:../lista";
        }

    }
    
    @GetMapping("/mostrar/{id}") //localhost:8080/noticia/mostrar
    public String mostrar(@PathVariable String id, ModelMap modelo){
        
        modelo.put("noticia", noticiaServicio.getOne(id));
      
        return "noticia_mostrar.html";
    }


}
