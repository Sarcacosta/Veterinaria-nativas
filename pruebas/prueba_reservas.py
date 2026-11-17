import argparse
import concurrent.futures
import getpass
import http.cookiejar
import json
import threading
import urllib.error
import urllib.request

def call(client, base, method, path, data=None):
    body = None if data is None else json.dumps(data).encode()
    request = urllib.request.Request(base + path, data=body, method=method,
                                     headers={"Content-Type": "application/json"})
    try:
        with client.open(request, timeout=30) as response:
            payload = response.read()
            return response.status, json.loads(payload) if payload else None
    except urllib.error.HTTPError as error:
        return error.code, None

def login(base, username):
    client = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
    code, user = call(client, base, "POST", "/api/login",
                      {"username": username, "password": getpass.getpass(f"Contraseña de {username}: ")})
    assert code == 200 and "Propietario" in user["roles"], (username, code)
    return client

def main():
    cli = argparse.ArgumentParser(description="Prueba de reservas para una base de prueba. Usa dos cuentas con mascotas distintas sin citas activas.")
    cli.add_argument("--base", default="http://localhost:8080/veterinaria")
    cli.add_argument("--users", nargs=2, required=True)
    cli.add_argument("--pets", type=int, nargs=2, required=True)
    cli.add_argument("--vet", type=int, required=True)
    cli.add_argument("--date", required=True)
    cli.add_argument("--slot", required=True)
    cli.add_argument("--alternative", required=True)
    p = cli.parse_args()
    assert p.users[0] != p.users[1] and p.pets[0] != p.pets[1] and p.slot != p.alternative
    base = p.base.rstrip("/")
    clients = [login(base, user) for user in p.users]
    barrier = threading.Barrier(2)
    def payload(index):
        return {"mascotaId": p.pets[index], "veterinarioId": p.vet, "fecha": p.date,
                "hora": p.slot, "motivo": "Prueba de integración"}
    code, _ = call(clients[0], base, "POST", "/api/mis-citas", payload(1))
    assert code == 403, ("mascota ajena", code)
    code, _ = call(clients[0], base, "POST", "/api/mis-citas", {**payload(0), "hora": "formato incorrecto"})
    assert code == 400, ("hora inválida", code)

    def book(index):
        barrier.wait(timeout=10)
        return call(clients[index], base, "POST", "/api/mis-citas", payload(index))

    with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
        results = list(pool.map(book, range(2)))
    try:
        codes = [code for code, _ in results]
        assert sorted(codes) == [201, 409], codes
        winner = codes.index(201)
        code, second = call(clients[1-winner], base, "POST", "/api/mis-citas",
                            {**payload(1-winner), "hora": p.alternative})
        assert code == 201, code
        results.append((1-winner, second))
        appointment_id = results[winner][1]["id"]
        code, _ = call(clients[winner], base, "PUT", "/api/mis-citas",
                       {"id": appointment_id, "fecha": p.date, "hora": p.alternative})
        assert code == 409, ("reprogramación a horario ocupado", code)
        code, _ = call(clients[1-winner], base, "DELETE", f"/api/mis-citas?id={appointment_id}")
        assert code == 403, ("cancelación ajena", code)
        code, _ = call(clients[winner], base, "DELETE", f"/api/mis-citas?id={appointment_id}")
        assert code == 204, ("cancelación propia", code)
        code, appointments = call(clients[winner], base, "GET", "/api/mis-citas")
        assert code == 200 and any(row["id"] == appointment_id and row["estado"] == "CANCELADA"
                                   for row in appointments), "Historial no conserva la cancelación"
        print("OK: permisos, validación, concurrencia, conflicto al reprogramar y cancelación con historial.")
    finally:
        for index, (code, appointment) in enumerate(results):
            if appointment:
                client_index = index if index < 2 else code
                call(clients[client_index], base, "DELETE", f"/api/mis-citas?id={appointment['id']}")

if __name__ == "__main__":
    main()
