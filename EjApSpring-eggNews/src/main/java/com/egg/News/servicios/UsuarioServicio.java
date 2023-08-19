
package com.egg.News.servicios;

import com.egg.News.entidades.Usuario;
import com.egg.News.enumeraciones.Rol;
import com.egg.News.excepciones.MiException;
import com.egg.News.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService{
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    

    @Transactional
    public void registrar(String nombre, String password, String password2) throws MiException {

        validar(nombre, password, password2);

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        
        //usuario.setPassword(password);
        
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));

        usuario.setRol(Rol.USER);
        
        usuario.setFecha(new Date());
        
        usuario.setActivo(Boolean.TRUE);
        
        usuarioRepositorio.save(usuario);
    }
    
    @Transactional
    public void actualizar(String idUsuario, String nombre, String password, String password2) throws MiException {

        validar(nombre, password, password2);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            
            usuario.setNombre(nombre);
            
            usuario.setPassword(new BCryptPasswordEncoder().encode(password));

            usuario.setRol(usuario.getRol());//Ver esto mas tarde
            
            usuario.setFecha(usuario.getFecha());
        
            usuario.setActivo(usuario.getActivo());
            
            usuarioRepositorio.save(usuario);
        }

    }
    
    @Transactional
    public void eliminarUsuario(String id) throws MiException {

        if (id.isEmpty() || id == null) {
            throw new MiException("El Id del Usuario no puede ser nulo o estar vacio");
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            usuarioRepositorio.delete(respuesta.get());
        }
    }
    
    
    public Usuario getOne(String id) {
        return usuarioRepositorio.getOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {

        List<Usuario> usuarios = new ArrayList();

        usuarios = usuarioRepositorio.findAll();

        return usuarios;
    }
    
    @Transactional
    public void cambiarRol(String id) {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();

            if (usuario.getRol().equals(Rol.USER)) {

                usuario.setRol(Rol.ADMIN);

            } else if (usuario.getRol().equals(Rol.ADMIN)) {
                usuario.setRol(Rol.USER);
            }
        }
    }
    
    @Transactional
    public void cambiarActivo(String id) {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();

            if (usuario.getActivo().equals(Boolean.TRUE)) {

                usuario.setActivo(Boolean.FALSE);

            } else if (usuario.getActivo().equals(Boolean.FALSE)) {
                usuario.setActivo(Boolean.TRUE);
            }
        }
    }
    
    
    private void validar(String nombre, String password, String password2) throws MiException {

        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("El nombre no puede ser nulo o estar vacío");
        }
        
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MiException("La contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }

        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }

    }

     @Override
    public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorNombre(nombre);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString()); //ROLE_USER por ejemplo

            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getNombre(), usuario.getPassword(), permisos);
        } else {
            return null;
        }
    }
    
}
