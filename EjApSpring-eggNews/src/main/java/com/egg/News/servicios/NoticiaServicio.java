
package com.egg.News.servicios;

import com.egg.News.entidades.Imagen;
import com.egg.News.entidades.Noticia;
import com.egg.News.entidades.Periodista;
import com.egg.News.excepciones.MiException;
import com.egg.News.repositorios.NoticiaRepositorio;
import com.egg.News.repositorios.PeriodistaRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NoticiaServicio {
    
    @Autowired
    private NoticiaRepositorio noticiaRepositorio;
    
    @Autowired
    private PeriodistaRepositorio periodistaRepositorio;
    
    @Autowired
    private ImagenServicio imagenServicio;
    
    @Transactional
    public void crearNoticia(MultipartFile archivo, String titulo, String cuerpo, String idPeriodista) throws MiException {
        
        validar(titulo, cuerpo);
        
        Periodista periodista = periodistaRepositorio.findById(idPeriodista).get();
        
        Noticia noticia = new Noticia();
        
        noticia.setTitulo(titulo);
        noticia.setCuerpo(cuerpo);
        noticia.setFecha(new Date());
        
        Imagen imagen = imagenServicio.guardar(archivo);

        noticia.setImagen(imagen);
        
        noticia.setCreador(periodista);
        
        noticiaRepositorio.save(noticia);
        
    }
    
    public List<Noticia> listarNoticias() {
        
        List<Noticia> noticias = new ArrayList();
        
        noticias = noticiaRepositorio.findAll();
        
        return noticias;
        
    }
    
    public List<Noticia> listarNoticiasPorPeriodista(String idPeriodista) {
        
        List<Noticia> noticias = new ArrayList();
        
        noticias = noticiaRepositorio.buscarPorPeriodista(idPeriodista);
        
        return noticias;
        
    }
    
    @Transactional
    public void modificarNoticia(MultipartFile archivo, String id, String titulo, String cuerpo) throws MiException {
        
        if (id.isEmpty() || id == null) {
            throw new MiException("El Id de la Noticia no puede ser nulo o estar vacio");
        }
        
        validar(titulo, cuerpo);
        
        Optional<Noticia> respuesta = noticiaRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            
            Noticia noticia = respuesta.get();
            
            noticia.setTitulo(titulo);
            noticia.setCuerpo(cuerpo);
            
            String idImagen = null;

            if (noticia.getImagen() != null) {
                idImagen = noticia.getImagen().getId();
            }

            Imagen imagen = imagenServicio.actualizar(archivo, idImagen);

            noticia.setImagen(imagen);
            
            noticiaRepositorio.save(noticia);

        }
    }
    
    @Transactional
    public void eliminarNoticia(String id) throws MiException {
        
        if (id.isEmpty() || id == null) {
            throw new MiException("El ID de la noticia no puede ser nulo o estar vacio");
        }
        Optional<Noticia> respuesta = noticiaRepositorio.findById(id);

        if (respuesta.isPresent()) {
            noticiaRepositorio.delete(respuesta.get());
        }
    }
    
    public Noticia getOne(String id) {
        return noticiaRepositorio.getOne(id);
    }
    
    
    private void validar(String titulo, String cuerpo) throws MiException{
        
        if (titulo.isEmpty() || titulo == null) {
            throw new MiException("El titulo de la Noticia no puede ser nulo o estar vacio");
        }
        
        if (cuerpo.isEmpty() || cuerpo == null) {
            throw new MiException("El cuerpo de la Noticia no puede ser nulo o estar vacio");
        }
        
    }
    
    
    
}
