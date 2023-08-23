
package com.egg.News.servicios;

import com.egg.News.entidades.Periodista;
import com.egg.News.entidades.Usuario;
import com.egg.News.enumeraciones.Rol;
import com.egg.News.excepciones.MiException;
import com.egg.News.repositorios.PeriodistaRepositorio;
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

@Service
public class PeriodistaServicio implements UserDetailsService{
    
    @Autowired
    private PeriodistaRepositorio periodistaRepositorio;
    

    @Transactional
    public void registrar(String nombre, String password, String password2) throws MiException {

        validar(nombre, password, password2);

        Periodista periodista = new Periodista();

        periodista.setNombre(nombre);
        
        //usuario.setPassword(password);
        
        periodista.setPassword(new BCryptPasswordEncoder().encode(password));

        periodista.setRol(Rol.PERIODISTA);
        
        periodista.setFecha(new Date());
        
        periodista.setActivo(Boolean.TRUE);
        
        periodistaRepositorio.save(periodista);
    }
    
    @Transactional
    public void actualizar(String idPeriodista, String nombre, String password, String password2) throws MiException {

        validar(nombre, password, password2);

        Optional<Periodista> respuesta = periodistaRepositorio.findById(idPeriodista);
        if (respuesta.isPresent()) {

            Periodista periodista = respuesta.get();
            
            periodista.setNombre(nombre);
            
            periodista.setPassword(new BCryptPasswordEncoder().encode(password));

            periodista.setRol(periodista.getRol());//Ver esto mas tarde
            
            periodista.setFecha(periodista.getFecha());
        
            periodista.setActivo(periodista.getActivo());
            
            periodistaRepositorio.save(periodista);
        }

    }
    
    public List<Periodista> listarPeriodista() {

        List<Periodista> periodistas = new ArrayList();

        periodistas = periodistaRepositorio.findAll();

        return periodistas;

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

        Periodista periodista = periodistaRepositorio.buscarPorNombre(nombre);

        if (periodista != null) {

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + periodista.getRol().toString()); //ROLE_USER por ejemplo

            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", periodista);

            return new User(periodista.getNombre(), periodista.getPassword(), permisos);
        } else {
            return null;
        }
    }
    
}
