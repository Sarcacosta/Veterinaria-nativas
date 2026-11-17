const API = '/veterinaria/api';
let datosAuditoria = [];

// Logout funcion
function logout() {
  fetch('/veterinaria/api/logout', {
    method: 'POST'
  })
  .then(response => {
    if (response.status === 200) {
      window.location.href = 'index.html';  // Redirige al inicio
    }
  })
  .catch(error => {
    console.error('Error durante el cierre de sesión:', error);
  });
}

// USUARIOS
// Cargar la lista de Usuarios
function cargarUsuarios() {
  document.getElementById('titulo').innerText = 'Usuarios';

  fetch(`/veterinaria/api/usuarios`)
    .then(r => r.json())
    .then(data => {

      let html = `
        <button class="btn btn-success mb-3" onclick="mostrarFormularioUsuario()">Nuevo Usuario</button>

        <table class="table table-striped table-bordered">
          <thead>
            <tr>
              <th>Username</th>
              <th>Nombre</th>
              <th>Correo</th>
              <th>Estado</th>
              <th>Roles</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>`;

      data.forEach(u => {
        html += `
          <tr>
            <td>${u.username}</td>
            <td>${u.nombre}</td>
            <td>${u.email}</td>
            <td>${u.estado ? 'Activo' : 'Inactivo'}</td>
            <td>${u.roles.join(', ')}</td>
            <td>
              <button class="btn btn-sm btn-warning" onclick='editarUsuario(${JSON.stringify(u)})'>Editar</button>
            </td>
          </tr>`;
      });

      html += `</tbody></table>`;
      document.getElementById('contenido').innerHTML = html;
    });
}

//Mostrar el formmulario de usuarios
function mostrarFormularioUsuario() {
  const template = document.getElementById('formUsuarioTemplate');
  const clone = template.content.cloneNode(true);
  document.getElementById('contenido').innerHTML = '';
  document.getElementById('contenido').appendChild(clone);
}

//Guardar o crear un Usuario
function guardarUsuario(e) {
  e.preventDefault();

  const usuario = {
    username: document.getElementById('username').value,
    nombre: document.getElementById('nombre').value,
    email: document.getElementById('email').value,
    estado: document.getElementById('estado').value === 'true',
    passwordHash: document.getElementById('password').value
  };

  fetch(`${API}/usuarios`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(usuario)
  }).then(() => cargarUsuarios());
}

//Editar un usuario
function editarUsuario(u) {
  mostrarFormularioUsuario();

  document.getElementById('idUsuario').value = u.idUsuario;
  document.getElementById('username').value = u.username;
  document.getElementById('nombre').value = u.nombre;
  document.getElementById('email').value = u.email;
  document.getElementById('estado').value = u.estado;
}

// PERFIL O ROLES
// Cargar la lista de Perfiles
function cargarPerfiles() {
  document.getElementById('titulo').innerText = 'Perfiles';

  fetch(`${API}/roles`)
    .then(r => r.json())
    .then(data => {

      let html = `
        <button class="btn btn-success mb-3" onclick="mostrarFormularioPerfil()">Nuevo Perfil</button>

        <table class="table table-striped table-bordered">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>`;

      data.forEach(r => {
        html += `
          <tr>
            <td>${r.nombre}</td>
            <td>${r.descripcion}</td>
            <td>
              <button class="btn btn-sm btn-warning" onclick='editarPerfil(${JSON.stringify(r)})'>Editar</button>
            </td>
          </tr>`;
      });

      html += `</tbody></table>`;
      document.getElementById('contenido').innerHTML = html;
    });
}

// Mostrar el formulario de perfiles (para crear o editar)
function mostrarFormularioPerfil() {
  const template = document.getElementById('formRolTemplate');
  const clone = template.content.cloneNode(true);
  document.getElementById('contenido').innerHTML = '';
  document.getElementById('contenido').appendChild(clone);
}

// Guardar o crear un rol o perfil
function guardarRol(e) {
  e.preventDefault();

  const rol = {
    nombre: document.getElementById('nombreRol').value,
    descripcion: document.getElementById('descripcionRol').value
  };

  fetch(`${API}/roles`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(rol)
  }).then(() => cargarPerfiles());
}

// Editar un rol o perfil
function editarPerfil(r) {
  mostrarFormularioPerfil();

  document.getElementById('idRol').value = r.idRol;
  document.getElementById('nombreRol').value = r.nombre;
  document.getElementById('descripcionRol').value = r.descripcion;
}

// MASCOTAS
// Cargar la lista de Mascotas
function cargarMascotas() {
  document.getElementById('titulo').innerText = 'Mascotas';

  fetch(`${API}/mascotas`)
    .then(r => {
      if (r.status === 401) window.location.href = 'index.html';
      return r.json();
    })
    .then(data => {

      let html = `
      <button class="btn btn-success mb-3" onclick="mostrarFormularioMascota()">Nueva Mascota</button>

      <table class="table table-striped table-bordered">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Especie</th>
            <th>Raza</th>
            <th>Propietario</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>`;

      data.forEach(m => {
        html += `
        <tr>
          <td>${m.nombre}</td>
          <td>${m.especie}</td>
          <td>${m.raza}</td>
          <td>${m.propietario}</td>
          <td>
            <button class="btn btn-sm btn-warning" onclick='editarMascota(${JSON.stringify(m)})'>Editar</button>
            <button class="btn btn-sm btn-danger" onclick='eliminarMascota(${m.id})'>Eliminar</button>
          </td>
        </tr>`;
      });

      html += `</tbody></table>`;
      document.getElementById('contenido').innerHTML = html;
    });
}

// Mostrar el formulario de mascotas (para crear o editar)
function mostrarFormularioMascota() {
  const template = document.getElementById('formMascotaTemplate');

  if (!template) {
    console.error('Template formMascotaTemplate no encontrado');
    return;
  }

  const clone = template.content.cloneNode(true);

  const contenido = document.getElementById('contenido');
  contenido.innerHTML = '';
  contenido.appendChild(clone);

  cargarSelectPropietarios();
}

// Cargar select de propietarios
function cargarSelectPropietarios() {

  fetch('/veterinaria/api/propietarios')
    .then(r => r.json())
    .then(data => {

      const select = document.getElementById('idPropietario');

      if (!select) {
        console.error('Select idPropietario no existe en el DOM');
        return;
      }

      select.innerHTML = '<option value="">Seleccione propietario</option>';

      data.forEach(p => {
        const option = document.createElement('option');
        option.value = p.id;   
        option.textContent = `${p.nombres} ${p.apellidos}`;
        select.appendChild(option);
      });
    })
    .catch(err => console.error('Error cargando propietarios', err));
}


// Guardar o crear una mascota
function guardarMascota(e) {
    e.preventDefault();

    const idMascota = document.getElementById('idMascota').value;
    const idProp = document.getElementById('idPropietario').value;

    if (!idProp) {
        alert('Debe seleccionar un propietario');
        return;
    }

    const mascota = {
        idMascota: idMascota ? Number(idMascota) : null,

        nombre: document.getElementById('nombre').value,
        especie: document.getElementById('especie').value,
        raza: document.getElementById('raza').value,
        sexo: document.getElementById('sexo').value,
        fechaNacimiento: document.getElementById('fechaNacimiento').value,
        color: document.getElementById('color').value,

        pesoActual: document.getElementById('pesoActual').value
            ? Number(document.getElementById('pesoActual').value)
            : null,

        propietario: {
            idPropietario: Number(idProp)
        }
    };

    const metodo = idMascota ? 'PUT' : 'POST';

    fetch('/veterinaria/api/mascotas', {
        method: metodo,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(mascota)
    })
    .then(response => {

        if (!response.ok) {
            throw new Error('Error HTTP: ' + response.status);
        }

        return response;
    })
    .then(() => {

        alert(
            idMascota
                ? 'Mascota actualizada correctamente'
                : 'Mascota registrada correctamente'
        );

        cargarMascotas();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Ocurrió un error al guardar la mascota');
    });
}

// Editar una mascota
function editarMascota(m) {

    mostrarFormularioMascota();

    document.getElementById('idMascota').value = m.id;
    document.getElementById('nombre').value = m.nombre || '';
    document.getElementById('especie').value = m.especie || '';
    document.getElementById('raza').value = m.raza || '';
    document.getElementById('sexo').value = m.sexo || '';
    document.getElementById('fechaNacimiento').value = m.fechaNacimiento || '';
    document.getElementById('color').value = m.color || '';
    document.getElementById('pesoActual').value = m.pesoActual || '';

    setTimeout(() => {

        const selectPropietario =
            document.getElementById('idPropietario');

        const idPropietario =
            m.idPropietario ||
            (m.propietario ? m.propietario.idPropietario : null);

        console.log('ID propietario:', idPropietario);

        if (idPropietario) {
            selectPropietario.value = idPropietario;
        }

    }, 300);
}

// Eliminar una mascota
function eliminarMascota(id) {
  if (!confirm('¿Está seguro de que desea eliminar esta mascota?')) {
    return;
  }

  fetch(`/veterinaria/api/mascotas?id=${id}`, {
    method: 'DELETE'
  }).then(() => cargarMascotas());
}



// PROPIETARIOS
// Cargar la lista de propietarios
function cargarPropietarios() {
  document.getElementById('titulo').innerText = 'Propietarios';

  fetch(`${API}/propietarios`)
    .then(r => {
      if (r.status === 401) window.location.href = 'index.html';
      return r.json();
    })
    .then(data => {
      let html = `
      <button class="btn btn-success mb-3" onclick="mostrarFormularioPropietario()">Nuevo Propietario</button>
      <table class="table table-striped table-bordered">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Documento</th>
            <th>Teléfono</th>
            <th>Correo</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>`;

      data.forEach(p => {
        html += `
        <tr>
          <td>${p.nombres} ${p.apellidos}</td>
          <td>${p.numeroDocumento}</td>
          <td>${p.telefono}</td>
          <td>${p.email}</td>
          <td>
            <button class="btn btn-sm btn-warning" onclick='editarPropietario(${JSON.stringify(p)})'>Editar</button>
            <button class="btn btn-sm btn-danger" onclick='eliminarPropietario(${p.id})'>Eliminar</button>
          </td>
        </tr>`;
      });

      html += `</tbody></table>`;
      document.getElementById('contenido').innerHTML = html;
    });
}

// Mostrar el formulario de propietario (para crear o editar)
function mostrarFormularioPropietario() {
  const template = document.getElementById('formPropietarioTemplate');

  if (!template) {
    console.error('Template formPropietarioTemplate no encontrado');
    return;
  }

  const clone = template.content.cloneNode(true);
  const contenido = document.getElementById('contenido');
  contenido.innerHTML = '';
  contenido.appendChild(clone);
}

// Guardar o crear un propietario
function guardarPropietario(e) {
  e.preventDefault();

  const idPropietario = document.getElementById('idPropietario').value;

  const propietario = {
    idPropietario: idPropietario ? Number(idPropietario) : null,
    tipoDocumento: document.getElementById('tipoDocumento').value,
    numeroDocumento: document.getElementById('numeroDocumento').value,
    nombres: document.getElementById('nombres').value,
    apellidos: document.getElementById('apellidos').value,
    telefono: document.getElementById('telefono').value,
    email: document.getElementById('email').value,
    direccion: document.getElementById('direccion').value
  };

  const metodo = idPropietario ? 'PUT' : 'POST';

  fetch('/veterinaria/api/propietarios', {
    method: metodo,
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(propietario)
  })
  .then(response => {
    if (!response.ok) {
      throw new Error(`Error HTTP: ${response.status}`);
    }

    return cargarPropietarios();
  })
  .catch(error => {
    console.error('Error al guardar propietario:', error);
  });
}


// Editar un propietario
function editarPropietario(p) {

  console.log('Propietario recibido para editar:', p);
  console.log('ID recibido:', p.id);

  mostrarFormularioPropietario();

  // El DTO utiliza "id", la entidad utiliza "idPropietario"
  document.getElementById('idPropietario').value = p.id;

  document.getElementById('tipoDocumento').value = p.tipoDocumento;
  document.getElementById('numeroDocumento').value = p.numeroDocumento;
  document.getElementById('nombres').value = p.nombres;
  document.getElementById('apellidos').value = p.apellidos;
  document.getElementById('telefono').value = p.telefono;
  document.getElementById('email').value = p.email;
  document.getElementById('direccion').value = p.direccion;
}



// Eliminar un propietario
function eliminarPropietario(id) {
  if (!confirm('¿Está seguro de que desea eliminar este propietario?')) {
    return;
  }

  fetch(`/veterinaria/api/propietarios?id=${id}`, {
    method: 'DELETE'
  }).then(() => cargarPropietarios());
}


// Cargar la lista de Veterinarios
function cargarVeterinarios() {
  document.getElementById('titulo').innerText = 'Veterinarios';

  fetch(`${API}/veterinarios`)
    .then(r => {
      if (r.status === 401) window.location.href = 'index.html';
      return r.json();
    })
    .then(data => {
      let html = `
      <button class="btn btn-success mb-3" onclick="mostrarFormularioVeterinario()">Nuevo Veterinario</button>
      <table class="table table-striped table-bordered">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Especialidad</th>
            <th>Documento</th>
            <th>Teléfono</th>
            <th>Correo</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>`;

      data.forEach(v => {
        html += `
        <tr>
          <td>${v.nombres} ${v.apellidos}</td>
          <td>${v.especialidad}</td>
          <td>${v.numeroDocumento}</td>
          <td>${v.telefono}</td>
          <td>${v.email}</td>
          <td>${v.estado}</td>
          <td>
            <button class="btn btn-sm btn-warning" onclick='editarVeterinario(${JSON.stringify(v)})'>Editar</button>
            <button class="btn btn-sm btn-danger" onclick='eliminarVeterinario(${v.id})'>Eliminar</button>
          </td>
        </tr>`;
      });

      html += `</tbody></table>`;
      document.getElementById('contenido').innerHTML = html;
    });
}

// Mostrar el formulario de Veterinario (para crear o editar)
function mostrarFormularioVeterinario() {
  const template = document.getElementById('formVeterinarioTemplate');

  if (!template) {
    console.error('Template formVeterinarioTemplate no encontrado');
    return;
  }

  const clone = template.content.cloneNode(true);
  const contenido = document.getElementById('contenido');
  contenido.innerHTML = '';
  contenido.appendChild(clone);
}

// Guardar o crear un Veterinario
function guardarVeterinario(e) {
  e.preventDefault();

  const idVeterinario = document.getElementById('idVeterinario').value;

  const veterinario = {
    idVeterinario: idVeterinario ? Number(idVeterinario) : null,

    tipoDocumento: document.getElementById('tipoDocumento').value,
    numeroDocumento: document.getElementById('numeroDocumento').value,
    nombres: document.getElementById('nombres').value,
    apellidos: document.getElementById('apellidos').value,
    especialidad: document.getElementById('especialidad').value,
    telefono: document.getElementById('telefono').value,
    email: document.getElementById('email').value,
    estado: document.getElementById('estado').value
  };

  const metodo = idVeterinario ? 'PUT' : 'POST';

  fetch('/veterinaria/api/veterinarios', {
    method: metodo,
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(veterinario)
  })
  .then(response => {
    if (!response.ok) {
      throw new Error(`Error HTTP: ${response.status}`);
    }

    return cargarVeterinarios();
  })
  .catch(error => {
    console.error("Error al guardar veterinario:", error);
  });
}

// Editar un Veterinario
function editarVeterinario(v) {
  mostrarFormularioVeterinario();

  document.getElementById('idVeterinario').value = v.id;
  document.getElementById('tipoDocumento').value = v.tipoDocumento;
  document.getElementById('numeroDocumento').value = v.numeroDocumento;
  document.getElementById('nombres').value = v.nombres;
  document.getElementById('apellidos').value = v.apellidos;
  document.getElementById('especialidad').value = v.especialidad;
  document.getElementById('telefono').value = v.telefono;
  document.getElementById('email').value = v.email;
  document.getElementById('estado').value = v.estado;
}


// Eliminar un Veterinario
function eliminarVeterinario(id) {
  if (!confirm('¿Está seguro de que desea eliminar este veterinario?')) {
    return;
  }

  fetch(`/veterinaria/api/veterinarios?id=${id}`, {
    method: 'DELETE'
  }).then(() => cargarVeterinarios());
}

// Función para cargar los datos estadísticos
function cargarEstadisticas() {
  fetch('/veterinaria/api/estadisticas')
    .then(r => r.json())
    .then(data => {
      document.getElementById('mascotas-count').textContent = data.mascotas;
      document.getElementById('propietarios-count').textContent = data.propietarios;
      document.getElementById('veterinarios-count').textContent = data.veterinarios;
    })
    .catch(err => {
      console.error('Error al cargar las estadísticas:', err);
    });
}

// Copia de seguridad
function backupBD() {
    if (!confirm("¿Desea generar la copia de seguridad de la base de datos?")) {
        return;
    }

    window.location.href = API + "/backup";
}

function exportarMascotasExcel() {
    window.open(API + "/export/mascotas/excel", "_blank");
}

function exportarMascotasPDF() {
    window.open(API + "/export/mascotas/pdf", "_blank");
}

function exportarPropietariosExcel() {
    window.open(API + "/export/propietarios/excel", "_blank");
}

function exportarPropietariosPDF() {
    window.open(API + "/export/propietarios/pdf", "_blank");
}

function exportarVeterinariosExcel() {
    window.open(API + "/export/veterinarios/excel", "_blank");
}

function exportarVeterinariosPDF() {
    window.open(API + "/export/veterinarios/pdf", "_blank");
}

function cargarAuditoria() {

  document.getElementById('titulo').innerText = 'Auditoria';

  fetch(`${API}/auditoria`)
    .then(response => {

      if (response.status === 401) {
        window.location.href = 'index.html';
        return null;
      }

      if (!response.ok) {
        throw new Error('Error al consultar la auditoría');
      }

      return response.json();
    })
    .then(data => {

      if (!data) return;

      datosAuditoria = data;

      const contenido = document.getElementById('contenido');

      // Obtener valores únicos para los filtros
      const usuarios = [...new Set(
        data.map(a => a.username)
      )].sort();

      const acciones = [...new Set(
        data.map(a => a.accion)
      )].sort();

      const entidades = [...new Set(
        data.map(a => a.entidad)
      )].sort();

      contenido.innerHTML = `

        <h4 class="mb-3">Auditoría del sistema</h4>

        <!-- Filtros -->
        <div class="card mb-4">

          <div class="card-body">

            <div class="row g-3">

              <!-- Usuario -->
              <div class="col-md-2">
                <label for="filtroUsuario" class="form-label">
                  Usuario
                </label>

                <select id="filtroUsuario" class="form-select">
                  <option value="">Todos</option>

                  ${usuarios.map(usuario => `
                    <option value="${usuario}">
                      ${usuario}
                    </option>
                  `).join('')}

                </select>
              </div>

              <!-- Acción -->
              <div class="col-md-2">
                <label for="filtroAccion" class="form-label">
                  Acción
                </label>

                <select id="filtroAccion" class="form-select">
                  <option value="">Todas</option>

                  ${acciones.map(accion => `
                    <option value="${accion}">
                      ${accion}
                    </option>
                  `).join('')}

                </select>
              </div>

              <!-- Entidad -->
              <div class="col-md-2">
                <label for="filtroEntidad" class="form-label">
                  Entidad
                </label>

                <select id="filtroEntidad" class="form-select">
                  <option value="">Todas</option>

                  ${entidades.map(entidad => `
                    <option value="${entidad}">
                      ${entidad}
                    </option>
                  `).join('')}

                </select>
              </div>
              
              <!-- Fecha -->
              <div class="col-md-3">
                    <label for="filtroFechaDesde" class="form-label">
                        Fecha desde
                    </label>
                    <input
                        type="date"
                        id="filtroFechaDesde"
                        class="form-control">
              </div>

              <div class="col-md-3">
                    <label for="filtroFechaHasta" class="form-label">
                        Fecha hasta
                    </label>
                    <input
                        type="date"
                        id="filtroFechaHasta"
                        class="form-control">
              </div>

            </div>

            <div class="mt-3">

              <button
                type="button"
                class="btn btn-primary"
                onclick="filtrarAuditoria()">
                Filtrar
              </button>

              <button
                type="button"
                class="btn btn-secondary"
                onclick="limpiarFiltrosAuditoria()">
                Limpiar filtros
              </button>

            </div>

          </div>

        </div>

        <!-- Tabla -->
        <div class="table-responsive">

          <table class="table table-striped table-hover table-bordered">

            <thead class="table-dark">
              <tr>
                <th>ID</th>
                <th>Usuario</th>
                <th>Acción</th>
                <th>Entidad</th>
                <th>ID Registro</th>
                <th>Fecha y hora</th>
                <th>Descripción</th>
              </tr>
            </thead>

            <tbody id="tablaAuditoria">

              ${generarFilasAuditoria(data)}

            </tbody>

          </table>

        </div>
      `;
    })
    .catch(error => {

      console.error(
        'Error al cargar auditoría:',
        error
      );

      document.getElementById('contenido').innerHTML = `
        <div class="alert alert-danger">
          No fue posible cargar la información de auditoría.
        </div>
      `;
    });
}

function generarFilasAuditoria(data) {

  if (data.length === 0) {

    return `
      <tr>
        <td colspan="7" class="text-center">
          No existen registros de auditoría.
        </td>
      </tr>
    `;
  }

  return data.map(a => `
    <tr>
      <td>${a.idAuditoria}</td>
      <td>${a.username}</td>
      <td>${a.accion}</td>
      <td>${a.entidad}</td>
      <td>${a.idRegistro ?? ''}</td>
      <td>${new Date(a.fechaHora).toLocaleString('es-CO')}</td>
      <td>${a.descripcion ?? ''}</td>
    </tr>
  `).join('');
}

function filtrarAuditoria() {

  const filtroUsuario =
    document.getElementById('filtroUsuario').value;

  const filtroAccion =
    document.getElementById('filtroAccion').value;

  const filtroEntidad =
    document.getElementById('filtroEntidad').value;

  const filtroFechaDesde =
    document.getElementById('filtroFechaDesde').value;

  const filtroFechaHasta =
    document.getElementById('filtroFechaHasta').value;


  // Validar que la fecha inicial no sea posterior a la final
  if (
    filtroFechaDesde &&
    filtroFechaHasta &&
    filtroFechaDesde > filtroFechaHasta
  ) {

    document.getElementById('tablaAuditoria').innerHTML = `
      <tr>
        <td colspan="7" class="text-center text-danger">
          La fecha inicial no puede ser posterior a la fecha final.
        </td>
      </tr>
    `;

    return;
  }


  const filtrados = datosAuditoria.filter(a => {

    const coincideUsuario =
      !filtroUsuario ||
      a.username === filtroUsuario;

    const coincideAccion =
      !filtroAccion ||
      a.accion === filtroAccion;

    const coincideEntidad =
      !filtroEntidad ||
      a.entidad === filtroEntidad;


    // Tomamos únicamente YYYY-MM-DD
    // para evitar problemas de zona horaria
    const fechaRegistro =
      a.fechaHora.substring(0, 10);

    const coincideFechaDesde =
      !filtroFechaDesde ||
      fechaRegistro >= filtroFechaDesde;

    const coincideFechaHasta =
      !filtroFechaHasta ||
      fechaRegistro <= filtroFechaHasta;


    return (
      coincideUsuario &&
      coincideAccion &&
      coincideEntidad &&
      coincideFechaDesde &&
      coincideFechaHasta
    );
  });


  document.getElementById('tablaAuditoria').innerHTML =
    generarFilasAuditoria(filtrados);
}

function limpiarFiltrosAuditoria() {

    document.getElementById('filtroUsuario').value = '';
    document.getElementById('filtroAccion').value = '';
    document.getElementById('filtroEntidad').value = '';
    document.getElementById('filtroFechaDesde').value = '';
    document.getElementById('filtroFechaHasta').value = '';

    document.getElementById('tablaAuditoria').innerHTML =
        generarFilasAuditoria(datosAuditoria);
}

// Llamar a la función al cargar la página del dashboard
document.addEventListener('DOMContentLoaded', cargarEstadisticas);