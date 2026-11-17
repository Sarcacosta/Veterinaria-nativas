package com.ucompensar.veterinaria.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "horario_atencion", uniqueConstraints = @UniqueConstraint(columnNames = {"veterinario_id", "fecha"}))
public class HorarioAtencion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "veterinario_id", nullable = false)
    private Long veterinarioId;
    @Column(nullable = false, length = 10)
    private String fecha;
    @Column(nullable = false, length = 255)
    private String horas;

    protected HorarioAtencion() {}
    public HorarioAtencion(Long veterinarioId, String fecha) {
        this.veterinarioId = veterinarioId;
        this.fecha = fecha;
    }
    public String getHoras() { return horas; }
    public void setHoras(String horas) { this.horas = horas; }
}
