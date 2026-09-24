# Concert Tracker - Backend

API REST para descubrir conciertos por artista, ciudad o fecha, registrar la asistencia personal y compartir reseñas. Hecha con Spring Boot, Spring Security (JWT) y PostgreSQL.

## Funcionalidades Implementadas

### Descubrimiento (Artist, Concert, Venue)

Los `GET` son públicos. Crear, actualizar y eliminar requiere un token de un usuario `ADMIN`.

- **Venues:** CRUD en `/api/v1/venues` y `GET /api/v1/venues/{id}/concerts` (conciertos del venue, ordenados por fecha).
- **Artists:** CRUD en `/api/v1/artists` (nombre único) y `GET /api/v1/artists/{id}/concerts`.
- **Concerts:** CRUD en `/api/v1/concerts`. El detalle incluye el artista y el venue embebidos. Eliminar un venue o un artista con conciertos responde 409.

**Búsqueda** con `GET /api/v1/concerts/search`, usando Specifications. Los filtros son opcionales y combinables: `artist` (texto parcial), `country`, `city`, `date` (`yyyy-MM-dd`) y `upcoming` (`true` próximos, `false` pasados).

Ejemplo: `/api/v1/concerts/search?artist=bad&city=Lima&upcoming=true`

### Interacción del usuario (Attendance, Review, Follow)

Todos requieren token. El usuario se obtiene del JWT (`SecurityUtils.getCurrentUserId()`), así que nadie puede actuar en nombre de otro.

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/user-interactions/attendance` | Marca o cambia la asistencia (`VOY_A_IR` o `YA_FUI`) |
| GET | `/api/v1/user-interactions/attendance/history` | Historial, con filtro opcional `?status=` |
| POST | `/api/v1/user-interactions/reviews` | Crea una reseña (`rating` de 1 a 5) |
| POST | `/api/v1/user-interactions/follow` | Sigue a un artista |

**Reglas de negocio** (en el service):

- Solo se puede reseñar si la asistencia es `YA_FUI`; si no, responde 409.
- No se puede marcar `YA_FUI` en un concierto futuro.
- Solo se permite una reseña por concierto y un follow por artista; los duplicados responden 409.
- Si el concierto o el artista no existe, responde 404. Los datos inválidos responden 400 (Bean Validation).

### Manejo de errores

`GlobalExceptionHandler` devuelve siempre `{timestamp, status, error, message, path}`. Los códigos son: 400 (validación), 401 (sin token o token inválido), 403 (rol insuficiente), 404 (no existe), 409 (duplicado o conflicto) y 500 (error interno).

## Seguridad Implementada

La autenticación usa JWT y no guarda sesiones en el servidor. Las rutas protegidas llevan `Authorization: Bearer <token>`.

- `POST /api/v1/auth/register`: crea la cuenta y devuelve los tokens.
- `POST /api/v1/auth/login`: valida las credenciales y devuelve los tokens.
- `POST /api/v1/auth/refresh`: entrega un access token nuevo.

Las contraseñas se guardan con **BCrypt**. El **access token** dura 15 minutos y el **refresh token** 7 días; cada uno lleva un claim `type` para que no se puedan intercambiar. `JwtAuthenticationFilter` valida el token en cada request.

**Roles:** todo usuario empieza como `USER`, y el rol `ADMIN` se asigna manualmente. Las escrituras de catálogo usan `@PreAuthorize("hasRole('ADMIN')")`. Sin token la API responde 401; con un rol insuficiente, 403.

**Genres:** CRUD en `/api/v1/genres`, con lectura pública y escritura solo para ADMIN.

## Eventos y Asincronía

Las tareas secundarias, como enviar un correo, no hacen esperar al usuario. El service publica un evento con `ApplicationEventPublisher` y `EmailNotificationListener` lo procesa en otro hilo.

| Evento | Se publica al | Acción del listener |
|---|---|---|
| `AttendanceCreatedEvent` | Marcar o cambiar la asistencia | Confirmación de asistencia |
| `ReviewCreatedEvent` | Crear una reseña | Confirmación de reseña |
| `ConcertCreatedEvent` | Crear un concierto | Avisa a los seguidores del artista que viven en la misma ciudad y país del venue |

- Los eventos son `record` inmutables, así que el service no depende de quién los escucha.
- `@EnableAsync` activa la ejecución asíncrona, y cada listener usa `@EventListener` + `@Async`. El endpoint responde de inmediato aunque el envío tarde o falle.
- **Limitación:** el correo se simula con un log. Para enviarlo de verdad hay que agregar `spring-boot-starter-mail` y un servidor SMTP.

## Modelo de Entidades

| Entidad | Campos principales |
|---|---|
| **User** | `name`, `email` (único), `password` (BCrypt), `role`, `country`, `city`, `createdAt` |
| **Genre** | `name` (único) |
| **Venue** | `name`, `city`, `country`, `address`, `capacity` |
| **Artist** | `name` (único), `country`, `description`, `imageUrl` |
| **Concert** | `title`, `date`, `ticketPrice`, `imageUrl`, `artist`, `venue` |
| **Attendance** | `userId`, `concertId`, `status` (`VOY_A_IR`/`YA_FUI`), fechas |
| **Review** | `userId`, `concertId`, `rating` (1-5), `comment` |
| **Follow** | `userId`, `artistId`, `createdAt` |

**Relaciones:**

- Venue → Concert y Artist → Concert, de uno a muchos.
- Genre ↔ Artist, de muchos a muchos (tabla `artist_genres`).
- Attendance y Review son intermedias entre User y Concert, y Follow entre User y Artist, con restricción única por par.

## Deployment

El backend está desplegado en **AWS** (Learner Lab), con la aplicación y la base de datos en la misma plataforma.

```
Cliente ──HTTP :8080──► EC2 (Amazon Linux 2023, Java 21, servicio systemd)
                              │ JDBC :5432 (red privada)
                              ▼
                        RDS PostgreSQL (sin acceso público)
```

- **EC2** (`t3.small`) clona este repositorio, compila con `./mvnw` y corre el `.jar` como servicio de systemd, que se reinicia solo.
- **RDS PostgreSQL** (`db.t4g.micro`) contiene la base `concert_tracker`. Hibernate crea las tablas.
- **Security Groups:** EC2 abre los puertos 22 y 8080; RDS solo acepta el puerto 5432 desde EC2.

**Variables de entorno** (en `/etc/concert-tracker.env`, fuera del repositorio):

| Variable | Uso |
|---|---|
| `DB_URL` | `jdbc:postgresql://<endpoint-rds>:5432/concert_tracker` |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciales de RDS |
| `JWT_SECRET` | Clave de firma, de 32 caracteres o más |
| `PORT` | Opcional, 8080 por defecto |

**Redespliegue desde GitHub:** después de fusionar en `main`, conectarse con EC2 Instance Connect y ejecutar `~/deploy.sh`. El script hace `git pull`, compila y reinicia el servicio. Los logs se revisan con `journalctl -u backend -f`.

**Consideraciones:** las sesiones del Learner Lab duran 4 horas y la IP pública cambia al reiniciar, así que hay que actualizar `base_url` en Postman. A diferencia de Railway, el despliegue no es automático al hacer push y la API se sirve por HTTP.
