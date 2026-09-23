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

### Relaciones

- **Venue → Concert (uno a muchos):** un venue puede albergar muchos conciertos, y cada concierto ocurre en un solo venue.
- **Artist → Concert (uno a muchos):** un artista puede dar múltiples conciertos, y cada concierto tiene un artista principal.
- **Genre ↔ Artist (muchos a muchos):** un artista puede tener varios géneros y un género puede estar en varios artistas, mediante la tabla `artist_genres`. Se asignan con `genreIds` al crear o actualizar un artista, y se devuelven como `genres` en la respuesta.
