# Andina Trading

**Andina Trading** es una plataforma empresarial para la **gestión y simulación de inversiones bursátiles**, desarrollada bajo una **arquitectura de microservicios** con **Spring Boot, Java 17, React y MySQL**.  
El sistema permite la interacción entre **inversionistas y comisionistas**, gestionando órdenes bursátiles, cuentas, portafolios y reportes financieros en un entorno seguro, escalable y modular.

---

## Descripción del proyecto

Este proyecto implementa una arquitectura distribuida basada en microservicios independientes, cada uno encargado de una función específica del dominio financiero.  
La comunicación entre servicios se realiza mediante **Feign Clients**, el control de acceso se maneja con **Spring Security + JWT**, y la persistencia se gestiona con **MySQL** y **Flyway** para control de versiones en la base de datos.  

El despliegue y la orquestación de todos los servicios se realiza mediante **Docker Compose**, asegurando portabilidad y consistencia entre entornos.

---

## Funcionalidades principales

- **Autenticación y autorización** de usuarios mediante JWT.
- **Gestión de inversionistas y comisionistas**.
- **Manejo de cuentas, contratos y portafolios de inversión.**
- **Creación, ejecución y cancelación de órdenes bursátiles** (Market, Limit, Stop, Limit Stop).
- **Reportes y auditoría de operaciones.**
- **Sistema de notificaciones** para estados y alertas de órdenes.
- **Consolidación de información financiera entre servicios.**
- **Catálogos dinámicos y parametrización del sistema.**
- **Módulo de respaldo y recuperación de información.**

---

## Tecnologías utilizadas

**Backend:**
- Java 17 · Spring Boot · Spring Security · Feign Client · JWT  
- ModelMapper · Flyway · Maven · Log4j  
- Docker Compose · API Gateway  

**Frontend:**
- React · Tailwind CSS · Axios  

**Base de datos:**
- MySQL (entorno principal)
- Compatible con Oracle SQL, SQL Server y MongoDB

---

## Arquitectura de microservicios

Estructura del proyecto backend:
  Backend/
  │
  ├── api-gateway
  ├── servicio-autenticacion
  ├── servicio-auditoria
  ├── servicio-catalogos
  ├── servicio-comisionista
  ├── servicio-consolidacion
  ├── servicio-contratos
  ├── servicio-cuentas
  ├── servicio-inversionista
  ├── servicio-notificaciones
  ├── servicio-ordenes
  ├── servicio-portafolio
  ├── servicio-reportes
  ├── servicio-respaldo
  ├── servicio-respaldo
  └── docker-compose.yml


Cada servicio implementa su propio modelo de dominio, capa de persistencia y lógica de negocio, favoreciendo la **independencia, escalabilidad y mantenibilidad** del sistema.

---

## Vista previa

![61130579-538f-4052-b3e1-9497329d8e09](https://github.com/user-attachments/assets/e00cacc9-e181-4f6e-b6f1-1b9b526f95e8)

![0ebc77c3-9d49-4eab-ace4-6a531a188cb8](https://github.com/user-attachments/assets/ccb75b52-e8f9-4247-a8d3-f4d48bb1fbf6)

![282e4e8b-749d-40c5-90c4-d31d24d7254b](https://github.com/user-attachments/assets/8de2211e-f118-44e8-bcd4-9781ab54a64c)

![0c80b802-e568-44fa-9bd0-9b1886ec0226](https://github.com/user-attachments/assets/8a1bb891-838e-4274-bb71-4602d462da19)

![aaf9eb4b-938b-4257-a667-7c86746c2151](https://github.com/user-attachments/assets/2babf622-2919-4ecb-8cf0-6c4aef28287f)

![37a45022-0c3a-4f57-80c4-c153c1a0c54d](https://github.com/user-attachments/assets/bb6122ce-bcf4-42a9-b391-bb198f290108)

![c403112d-c5ca-4ee4-8e84-9be24cbc1bd1](https://github.com/user-attachments/assets/4d840b7d-e78c-45cf-87ad-d9afdb9efacd)

![9483887c-8b4b-427d-9430-63423c789eaf](https://github.com/user-attachments/assets/cdd17a09-ed06-4a07-8f5f-e5d73796f2fa)

