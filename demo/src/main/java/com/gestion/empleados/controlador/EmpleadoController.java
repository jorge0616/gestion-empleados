package com.gestion.empleados.controlador;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gestion.empleados.entidades.Empleado;
import com.gestion.empleados.servicio.EmpleadoService;
import com.gestion.empleados.util.paginacion.PageRender;

@Controller
public class EmpleadoController {

    @Autowired(required = false)
    private EmpleadoService empleadoService;

    @GetMapping("/ver/{id}")
    public String verDetallesDelEmpleado(@PathVariable(value = "id") Long id, Map<String,Object> modelo, RedirectAttributes flash){
        Empleado empleado = empleadoService.findOne(id);
        if(empleado == null){
            flash.addFlashAttribute("error", "El empleado no existe en la base de datos");
            return "redirect:/listar";
        }

        modelo.put("empleado", empleado);
        modelo.put("titulo", "Detalles del empleado " + empleado.getNombre());
        return "listar";
    }

    @GetMapping("/form")
    public String mostrarFormularioDeRegistrarCliente(Map<String, Object> modelo){
        Empleado empleado = new Empleado();
        modelo.put("empleado", empleado);
        modelo.put("titulo", "Registro de empleados");
        return "form";
    }

    @PostMapping("/form")
    public String guardarEmpleado(@Valid Empleado empleado, BindingResult result, Model modelo, RedirectAttributes flash, SessionStatus status){
        if(result.hasErrors()){
            modelo.addAttribute("titulo", "Registro de cliente");
            return "form";
        }

        String mensaje = (empleado.getId() != null) ? "El empleado ha sido actulizado con exito" : "Empleado registrado con exito";
        
        empleadoService.save(empleado);
        status.setComplete();
        flash.addFlashAttribute("success", mensaje);
        return "redirect:listar";
    }

    @GetMapping("/form/{id}")
    public String editarEmpleado(@PathVariable(value = "id") Long id, Map<String, Object> modelo, RedirectAttributes flash){
        Empleado empleado = null;
        if(id > 0){
            empleado = empleadoService.findOne(id);
            if(empleado == null){
                flash.addFlashAttribute("error", "El ID del empleado no existe en la base de datos");
                return "redirect:/listar";
            }
        }else{
            flash.addFlashAttribute("error", "El ID del empleado no puede ser 0");
            return "redirect:/listar";
        }
        modelo.put("empleado", empleado);
        modelo.put("titulo", "Editar empleado");
        return "form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable(value = "id") Long id, RedirectAttributes flash){
        if(id > 0){
            empleadoService.delete(id);
            flash.addFlashAttribute("success", "Empleado eliminado con exito");
        }
        return "redirect:/listar";
    }

    @GetMapping({"/","/listar",""})
    public String listarEmpleados(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo){
        Pageable pageRequest = PageRequest.of(page, 4);
        Page<Empleado> empleados = empleadoService.findAll(pageRequest);
        PageRender<Empleado> pageRender = new PageRender<>("/listar", empleados);

        modelo.addAttribute("titulo", "Listado de empleados");
        modelo.addAttribute("empleados", empleados);
        modelo.addAttribute("page", pageRender);

        return "Listar";
    }
    
}
