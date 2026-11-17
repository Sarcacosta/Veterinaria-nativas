# Huella con gestión por rol

## Qué puede hacer cada cuenta

| Rol | Operaciones |
|---|---|
| Propietario | Registrarse, editar su perfil, registrar y editar sus mascotas, consultar disponibilidad, agendar, reprogramar o cancelar citas futuras propias y consultar el historial. |
| Veterinario | Consultar citas asignadas e historial, configurar su jornada por fecha, consultar mascotas y propietarios y editar su perfil. Su cuenta debe estar vinculada a su ficha. |
| Recepción | Consultar la agenda general, gestionar citas y fichas y configurar horarios de atención seleccionando el veterinario. |
| Administrador | Gestión general y autorización de cuentas veterinarias. La activación y vinculación se hacen desde la administración de la base. |

En Mascotas, toca la tarjeta del animal para editar sus datos. En Mis citas puedes alternar Próximas citas e Historial. Los turnos cancelados se conservan en el historial. En Inicio, Horarios de atención permite cargar y guardar la jornada de una fecha. Una jornada vacía cierra ese día si no hay citas activas. La configuración predeterminada se usa cuando una fecha no tiene jornada personalizada.

## Despliegue

1. Inicia MySQL. Conserva la base veterinaria. Para una base nueva, importa veterinaria.sql y ejecuta 01-registro-propietario.sql.
2. Revisa persistence.xml. Hibernate crea horario_atencion y agrega usuario.id_veterinario cuando inicializa la unidad de persistencia. El usuario JDBC debe tener permisos para actualizar el esquema.
3. En veterinaria ejecuta mvn clean package. Detén Tomcat y despliega target/veterinaria-1.0-SNAPSHOT.war como veterinaria.war. Reinicia Tomcat.
4. Abre HuellaAcceso en Android Studio y ejecuta en el emulador con http://10.0.2.2:8080/veterinaria.
5. Para una cuenta Veterinario, consulta GUIA-VINCULACION.md y verifica una vez la ficha vinculada. Comprueba que la cuenta esté activa en la tabla usuario. La app conserva el mensaje explicativo si todavía falta la vinculación.

## Comprobaciones

- Propietario: registra y edita una mascota propia. Prueba un peso inválido y abre Mis citas después de reservar, reprogramar y cancelar.
- Veterinario: elige una fecha futura en Horarios de atención, guarda una hora y confirma que aparece en la disponibilidad del propietario. Comprueba que solo se muestran las citas de su ficha.
- Recepción o Administrador: abre la agenda general y verifica reprogramación a un horario libre y rechazo de un horario ocupado.
- Con dos cuentas y dos mascotas distintas sin citas activas, usa la prueba automatizada de pruebas/prueba_reservas.py contra una base de prueba. Dale un veterinario, una fecha futura y dos horas libres. La prueba guarda citas de prueba y las cancela, conservando los registros en el historial. Solicita las contraseñas de forma interactiva.

```powershell
python .\pruebas\prueba_reservas.py --users prueba1 prueba2 --pets 10 11 --vet 1 --date AAAA-MM-DD --slot 08:00 --alternative 09:00
```

La prueba espera 201 y 409 en las dos solicitudes concurrentes al mismo turno, 403 para mascotas o citas ajenas, 400 para una hora inválida y 204 al cancelar una cita propia. También comprueba que el historial conserve la cita cancelada.

Los cinco archivos Java del módulo de citas de la compañera se conservan byte por byte. El filtro dirige las escrituras y la consulta de disponibilidad al módulo transaccional compartido ReservasController y ReservasService. Se utilizan bloqueos de las filas de mascota y veterinario para coordinar reservas y cambios de jornada. El archivo de prueba automatizada requiere Tomcat y MySQL en ejecución.
