# Concert Tracker - Backend

API REST de Concert Tracker, una aplicación para descubrir conciertos por artista, ciudad o fecha, llevar un registro personal de asistencia y compartir reseñas.

## Funcionalidades Implementadas

### Descubrimiento (Artist, Concert, Venue)

Todos los endpoints `GET` son públicos. Crear, actualizar y eliminar requiere un token JWT de un usuario con rol `ADMIN` (`Authorization: Bearer <token>`).

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/v1/venues` | Público | Lista los venues |
| GET | `/api/v1/venues/{id}` | Público | Detalle de un venue |
| GET | `/api/v1/venues/{id}/concerts` | Público | Conciertos de un venue, ordenados por fecha |
| POST | `/api/v1/venues` | ADMIN | Crea un venue (`name`, `city` y `country` obligatorios) |
| PUT | `/api/v1/venues/{id}` | ADMIN | Actualiza un venue |
| DELETE | `/api/v1/venues/{id}` | ADMIN | Elimina un venue (409 si tiene conciertos) |
| GET | `/api/v1/artists` | Público | Lista los artistas |
| GET | `/api/v1/artists/{id}` | Público | Detalle de un artista |
| GET | `/api/v1/artists/{id}/concerts` | Público | Otros conciertos de un artista, ordenados por fecha |
| POST | `/api/v1/artists` | ADMIN | Crea un artista (nombre único) |
| PUT | `/api/v1/artists/{id}` | ADMIN | Actualiza un artista |
| DELETE | `/api/v1/artists/{id}` | ADMIN | Elimina un artista (409 si tiene conciertos) |
| GET | `/api/v1/concerts` | Público | Lista los conciertos, ordenados por fecha |
| GET | `/api/v1/concerts/{id}` | Público | Detalle del concierto con artista y venue embebidos |
| GET | `/api/v1/concerts/search` | Público | Búsqueda con filtros (ver abajo) |
| POST | `/api/v1/concerts` | ADMIN | Crea un concierto y publica `ConcertCreatedEvent` |
| PUT | `/api/v1/concerts/{id}` | ADMIN | Actualiza un concierto |
| DELETE | `/api/v1/concerts/{id}` | ADMIN | Elimina un concierto |

**Búsqueda de conciertos** (`GET /api/v1/concerts/search`). Todos los parámetros son opcionales y se pueden combinar:

| Parámetro | Ejemplo | Efecto |
|---|---|---|
| `artist` | `artist=bad` | Nombre del artista que contiene el texto (sin distinguir mayúsculas) |
| `country` | `country=Perú` | País del venue |
| `city` | `city=Lima` | Ciudad del venue |
| `date` | `date=2026-10-12` | Conciertos de ese día (formato `yyyy-MM-dd`) |
| `upcoming` | `upcoming=true` | `true` próximos, `false` pasados |

Ejemplo: `GET /api/v1/concerts/search?artist=bad&country=Perú&city=Lima&upcoming=true`

**Aviso al crear un concierto.** Al crear un concierto se publica un `ConcertCreatedEvent` con el id del concierto, el id del artista y el país y la ciudad del venue. Está pensado para que el módulo de interacción lo escuche de forma asíncrona y avise a los usuarios que siguen al artista en esa misma ubicación, sin bloquear la creación.

### Interacción del usuario (Attendance, Review, Follow)

Todos los endpoints requieren token JWT (`Authorization: Bearer <token>`), con rol `USER` o `ADMIN`. El usuario se identifica con el id que viene en el token (`SecurityUtils.getCurrentUserId()`), así que el cliente **no** envía `userId` y nadie puede actuar en nombre de otro.

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/v1/user-interactions/attendance` | Autenticado | Marca o cambia la asistencia a un concierto (`VOY_A_IR` o `YA_FUI`). Publica `AttendanceCreatedEvent` |
| GET | `/api/v1/user-interactions/attendance/history` | Autenticado | Historial personal. Filtro opcional `?status=VOY_A_IR` (próximos) o `?status=YA_FUI` (pasados) |
| POST | `/api/v1/user-interactions/reviews` | Autenticado | Crea una reseña (`rating` de 1 a 5, `comment` opcional). Publica `ReviewCreatedEvent` |
| POST | `/api/v1/user-interactions/follow` | Autenticado | Sigue a un artista |

**Reglas de negocio** (validadas en `UserInteractionServiceImpl`, no en el controller):

- Solo se puede dejar una reseña si la asistencia del usuario a ese concierto es `YA_FUI`. Si no registró asistencia, o sigue en `VOY_A_IR`, responde 409.
- No se puede marcar `YA_FUI` en un concierto cuya fecha todavía no llega (409).
- Una sola asistencia, una sola reseña por usuario y concierto, y un solo follow por usuario y artista. Si el usuario vuelve a marcar asistencia, se actualiza el estado en lugar de crear otra fila; una reseña o un follow repetido responde 409.
- El concierto o el artista tienen que existir (404 si no).
- Los DTOs se validan con Bean Validation (`@NotNull`, `@Min(1)`, `@Max(5)`, `@Size`); un campo faltante o un rating fuera de rango responde 400.

Ejemplo de reseña:

```json
POST /api/v1/user-interactions/reviews
{
  "concertId": 2,
  "rating": 5,
  "comment": "Increíble show, el sonido estuvo perfecto."
}
```

### Manejo de errores

Todos los errores devuelven el mismo formato JSON, generado por `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-09-20T17:03:39.168217",
  "status": 404,
  "error": "Not Found",
  "message": "Venue no encontrado con id: 999",
  "path": "/api/v1/venues/999"
}
```

| Código | Cuándo ocurre |
|---|---|
| 400 | Validación de campos, JSON mal formado o parámetro con formato inválido |
| 401 | Credenciales incorrectas, token no válido o ruta protegida sin token |
| 403 | El usuario no tiene el rol necesario (por ejemplo, `USER` intentando crear un venue) o la regla de negocio no le permite la operación |
| 404 | El recurso no existe |
| 409 | Recurso duplicado o eliminación que dejaría datos huérfanos |
| 500 | Error interno no controlado |

## Seguridad Implementada

Autenticación con JWT y sin sesiones en el servidor (`SessionCreationPolicy.STATELESS`): cada request a una ruta protegida debe llevar `Authorization: Bearer <token>`.

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Público | Crea la cuenta (`name`, `email`, `password` mín. 8 caracteres, `country`/`city` opcionales) y devuelve de una el par de tokens, sin pedir login aparte |
| POST | `/api/v1/auth/login` | Público | Valida email y contraseña, devuelve un par de tokens nuevo |
| POST | `/api/v1/auth/refresh` | Público | Cambia un refresh token vigente por un access token nuevo, sin pedir contraseña |

Las contraseñas se guardan hasheadas con BCrypt, nunca en texto plano.

**Tokens.** Hay dos para no forzar un login a cada rato pero tampoco dejar un token robado activo para siempre: el access token dura 15 minutos y es el que va en cada request protegido; el refresh token dura 7 días y solo sirve para pedir un access token nuevo en `/auth/refresh`. Cada uno lleva un claim `type` (`access`/`refresh`) para que no se puedan usar al revés.

**Roles.** Todo usuario nace `USER`; el rol `ADMIN` se asigna manualmente en la base de datos. Los `GET` de genres, artistas, conciertos y venues son públicos; todo lo demás pide token, y crear, actualizar o eliminar esos recursos pide además `ADMIN` (`@PreAuthorize("hasRole('ADMIN')")` en cada controller). Sin token en una ruta protegida el backend responde 401; con un rol insuficiente responde 403.

### Genre

CRUD con el mismo patrón que los demás recursos: lectura pública, escritura solo ADMIN.

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/v1/genres` | Público | Lista los géneros |
| GET | `/api/v1/genres/{id}` | Público | Detalle de un género |
| POST | `/api/v1/genres` | ADMIN | Crea un género (nombre único) |
| PUT | `/api/v1/genres/{id}` | ADMIN | Actualiza un género |
| DELETE | `/api/v1/genres/{id}` | ADMIN | Elimina un género |

## Eventos y Asincronía

Algunas acciones disparan tareas secundarias (como enviar un correo) que no deberían hacer esperar al usuario. Para eso se usan los eventos de Spring: el service publica un evento con `ApplicationEventPublisher` y un listener lo procesa en otro hilo.

```
POST /attendance ──► UserInteractionServiceImpl ──► guarda Attendance
                              │
                              └─ publishEvent(AttendanceCreatedEvent) ──► EmailNotificationListener (@Async)
                                                                          se ejecuta en otro hilo
◄── 201 Created (responde sin esperar al listener)
```

| Evento | Se publica en | Datos | Listener |
|---|---|---|---|
| `AttendanceCreatedEvent` | `setOrUpdateAttendance` (crear o cambiar asistencia) | `userId`, `concertId`, `status` | `EmailNotificationListener.handleAttendanceEvent`: confirmación de asistencia |
| `ReviewCreatedEvent` | `createReview` | `userId`, `concertId`, `rating` | `EmailNotificationListener.handleReviewEvent`: confirmación de reseña |
| `ConcertCreatedEvent` | `ConcertServiceImpl.create` (módulo de descubrimiento) | `concertId`, `artistId`, `venueCountry`, `venueCity` | Todavía sin listener (ver pendientes) |

**Cómo funciona:**

- Los eventos son `record` inmutables en el paquete `event`, así que el service no depende de quién los escuche.
- `@EnableAsync` en `BackendApplication` activa la ejecución asíncrona. Sin esta anotación, `@Async` se ignora y el listener correría en el mismo hilo del request.
- Cada método del listener lleva `@EventListener` (se suscribe al tipo de evento) y `@Async` (corre en el pool de hilos de Spring, `task-*`). Así, si el envío del correo tarda o falla, el endpoint igual responde 201 y los datos quedan guardados.

**Pendientes / limitaciones:**

- Por ahora el "envío de correo" se simula con un log en consola (`[ASYNC EVENT] Enviando email...`). Para enviarlo de verdad hay que agregar `spring-boot-starter-mail` y configurar un servidor SMTP.
- Falta el listener de `ConcertCreatedEvent` que avise a los seguidores del artista en la misma ciudad.
- Los listeners usan `@EventListener`, que se ejecuta aunque la transacción luego haga rollback. Con `@TransactionalEventListener(phase = AFTER_COMMIT)` solo se notificaría cuando el dato ya esté guardado.

## Modelo de Entidades

### Venue

Recinto donde se realizan los conciertos.

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | Clave primaria |
| `name` | String | Obligatorio, máximo 150 caracteres |
| `city` | String | Obligatorio, máximo 100 caracteres |
| `country` | String | Obligatorio al crear, máximo 100 caracteres |
| `address` | String | Opcional |
| `capacity` | Integer | Opcional, positivo |

### Artist

Artista o banda.

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | Clave primaria |
| `name` | String | Obligatorio y único, máximo 150 caracteres |
| `country` | String | Opcional |
| `description` | String | Opcional, máximo 1000 caracteres |
| `imageUrl` | String | Opcional, URL de la imagen (Cloudinary) |

### Concert

Evento de un artista en un venue.

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | Clave primaria |
| `title` | String | Opcional |
| `date` | LocalDateTime | Obligatorio |
| `ticketPrice` | BigDecimal | Opcional, no negativo |
| `imageUrl` | String | Opcional, URL del póster |
| `artist` | Artist | Obligatorio, relación `ManyToOne` |
| `venue` | Venue | Obligatorio, relación `ManyToOne` |

### User

Cuenta de la aplicación.

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | Clave primaria |
| `name` | String | Obligatorio, máximo 100 caracteres |
| `email` | String | Obligatorio y único, formato válido |
| `password` | String | Obligatorio, se guarda hasheado con BCrypt |
| `role` | Enum (`USER`, `ADMIN`) | `USER` por defecto al registrarse |
| `country` / `city` | String | Opcionales |
| `createdAt` | LocalDateTime | Se asigna sola al crear el usuario |

### Genre

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | Clave primaria |
| `name` | String | Obligatorio y único, máximo 50 caracteres |

### Attendance

Asistencia de un usuario a un concierto. Tabla `attendances`, con restricción única `(user_id, concert_id)`.

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | Clave primaria |
| `userId` | Long | Obligatorio, id del usuario (sale del JWT) |
| `concertId` | Long | Obligatorio, el concierto tiene que existir |
| `status` | Enum (`VOY_A_IR`, `YA_FUI`) | Obligatorio |
| `createdAt` / `updatedAt` | LocalDateTime | Se asignan solas al crear y al actualizar |

### Review

Reseña de un concierto. Tabla `reviews`, con restricción única `(user_id, concert_id)`.

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | Clave primaria |
| `userId` | Long | Obligatorio |
| `concertId` | Long | Obligatorio, requiere asistencia `YA_FUI` |
| `rating` | Integer | Obligatorio, de 1 a 5 |
| `comment` | String | Opcional, máximo 1000 caracteres |
| `createdAt` | LocalDateTime | Se asigna sola al crear |

### Follow

Un usuario sigue a un artista. Tabla `follows`, con restricción única `(user_id, artist_id)`.

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | Clave primaria |
| `userId` | Long | Obligatorio |
| `artistId` | Long | Obligatorio, el artista tiene que existir |
| `createdAt` | LocalDateTime | Se asigna sola al crear |

### Relaciones

- **Venue → Concert (uno a muchos):** un venue puede albergar muchos conciertos, y cada concierto ocurre en un solo venue.
- **Artist → Concert (uno a muchos):** un artista puede dar múltiples conciertos, y cada concierto tiene un artista principal.
- **Genre ↔ Artist (muchos a muchos):** un artista puede tener varios géneros y un género puede estar en varios artistas, mediante la tabla `artist_genres`. Se asignan con `genreIds` al crear o actualizar un artista, y se devuelven como `genres` en la respuesta.
- **User ↔ Concert mediante Attendance y Review:** son entidades intermedias. Un usuario puede asistir a muchos conciertos y un concierto tiene muchos asistentes; lo mismo con las reseñas. Guardan `userId` y `concertId` como columnas, y la existencia del concierto se valida en el service.
- **User ↔ Artist mediante Follow:** un usuario puede seguir a muchos artistas y un artista puede tener muchos seguidores.
