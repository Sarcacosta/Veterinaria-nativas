const API = '/veterinaria/api';

// Manejador del formulario de login
document.getElementById('loginForm')?.addEventListener('submit', e => {
  e.preventDefault();

  const username = document.getElementById('username').value;
  const password = document.getElementById('password').value;

  fetch(`${API}/login`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({ username, password })
  })
  .then(r => {
    if (!r.ok) throw 'Credenciales inválidas'; // Si la respuesta no es exitosa, mostramos el error
    return r.json();
  })
  .then(() => window.location.href = 'dashboard.html') // Redirigimos al dashboard
  .catch(err => document.getElementById('error').innerText = err); // Mostramos error si ocurre
});

// Función de logout
function logout() {
  fetch(`${API}/logout`, { method: 'POST' })
    .then(() => window.location.href = 'index.html') // Redirigimos a la página de login
    .catch(err => console.error('Error al cerrar sesión:', err)); // Mostramos error si ocurre
}
