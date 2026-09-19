package com.concerttracker.backend.entity;

/**
 * Roles del sistema. USER es el rol por defecto al registrarse.
 * ADMIN se asigna manualmente (o vía un endpoint protegido, ya vemos si lo agregamos).
 */
public enum Role {
    USER,
    ADMIN
}