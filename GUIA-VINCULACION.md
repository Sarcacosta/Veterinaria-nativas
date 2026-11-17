# Huella con paneles por rol

Inicio reproduce la estructura del pantallazo del equipo con un saludo, una tarjeta degradada, dos cifras y botones lavanda.

- Propietario. Solicitar cita, Mis citas, Ver veterinarios, Mascotas y Mi perfil. Para agendar, debe tener una mascota registrada. El servidor verifica la propiedad de la mascota.
- Veterinario. Mis citas, Mascotas, Propietarios y Mi perfil. La agenda muestra únicamente las citas que corresponden a su ficha de veterinario. Desde Mi perfil puede editar el nombre y correo de su cuenta de acceso.
- Administración y Recepción. Acceso a la agenda general y gestión de citas de la clínica.

El ZIP de la compañera incluía el backend de citas. El proyecto conserva byte por byte sus cinco archivos Java de citas y reutiliza sus rutas. Se añadió una ruta de lectura de agenda personal que obtiene el identificador del veterinario a partir de la sesión y de su ficha vinculada.

## Desplegar

1. Conserva tu base veterinaria. Si es una instalación nueva, importa veterinaria.sql y aplica 01-registro-propietario.sql.
2. Abre veterinaria en VS Code. Revisa persistence.xml, ejecuta mvn clean package, despliega el WAR como veterinaria.war y reinicia Tomcat.
3. Abre HuellaAcceso en Android Studio y ejecuta el emulador con http://10.0.2.2:8080/veterinaria.
4. Abre la app e inicia sesión para que Hibernate inicialice el esquema y agregue usuario.id_veterinario. El usuario de MySQL del backend debe poder actualizar el esquema.
5. Vincula una sola vez la cuenta veterinaria ya autorizada con su ficha del directorio. Este paso hace posible distinguir sus citas de las de otros veterinarios.

## Vincular una cuenta veterinaria

En phpMyAdmin selecciona veterinaria y abre SQL. Usa esta consulta para ver los identificadores de fichas del directorio.

```sql
SELECT idVeterinario, nombres, apellidos FROM veterinario;
```

Confirma el nombre del veterinario antes de vincularlo. En el ejemplo, reemplaza 1 con el ID correcto y jjj con el usuario correcto. La consulta solo vincula una cuenta activa con rol Veterinario.

```sql
UPDATE usuario u
JOIN usuario_rol ur ON ur.id_usuario = u.idUsuario
JOIN rol r ON r.idRol = ur.id_rol
JOIN veterinario v ON v.idVeterinario = 1
SET u.id_veterinario = v.idVeterinario
WHERE u.username = 'jjj'
  AND r.nombre = 'Veterinario'
  AND u.estado = 1
  AND u.id_veterinario IS NULL;
```

Cada ficha puede vincularse a una cuenta. Al pulsar Mis citas, una cuenta sin ficha vinculada mostrará un mensaje que explica cómo pedir la vinculación.

## Prueba de los dos roles

1. Entra como Propietario. Registra una mascota si hace falta y guarda una cita para ella desde Solicitar cita. Confirma que aparece en Mis citas.
2. Entra como Veterinario con la ficha vinculada que seleccionaste al agendar. Confirma que la cita aparece. Si tienes otra ficha y cuenta, verifica que esa cuenta no vea la cita.
3. Abre Mascotas y Propietarios. En Mi perfil cambia el nombre o correo y vuelve a abrirlo para comprobar el guardado.
