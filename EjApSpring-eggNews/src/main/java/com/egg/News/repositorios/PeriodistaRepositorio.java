
package com.egg.News.repositorios;

import com.egg.News.entidades.Periodista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodistaRepositorio extends JpaRepository<Periodista, String>{
    
    @Query("SELECT p FROM Periodista p WHERE p.nombre = :nombre")
    public Periodista buscarPorNombre(@Param("nombre") String nombre);
    
}
