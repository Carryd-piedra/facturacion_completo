package com.sistemalp.facturacion.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemalp.facturacion.Entidades.Empresa;

//interfaz que sirve para definir el repositorio de la empresa
public interface EmpresaRepositorio extends JpaRepository<Empresa,Long>{
    boolean existsByRuc(String ruc); //metodo que permite 
    // verificar si existe una empresa con el ruc
}
