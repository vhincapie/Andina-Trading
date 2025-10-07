import { useEffect, useState } from "react";
import { getMe } from "../api/authService";
import Navbar from "../components/Navbar";

export default function HomePage() {
  const [me, setMe] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const data = await getMe();
        setMe(data); 
      } catch (e) {
        console.error(e);
        setError("No se pudo cargar la información del usuario.");
      }
    };
    load();
  }, []);

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar usuario={me} />
      <main className="max-w-5xl mx-auto px-4 py-8">
        <h2 className="text-2xl font-semibold mb-4">Inicio</h2>

        {error && <p className="text-red-600">{error}</p>}

        {!me ? (
          <p className="text-gray-600">Cargando...</p>
        ) : (
          <div className="grid gap-4 md:grid-cols-2">
            <div className="bg-white border rounded p-4">
              <h3 className="font-semibold mb-2">Mi perfil</h3>
              <p>
                <span className="font-medium">Correo:</span> {me.correo}
              </p>
              <p>
                <span className="font-medium">Rol:</span> {me.rol}
              </p>
            </div>

            <div className="bg-white border rounded p-4">
              <h3 className="font-semibold mb-2">Estado</h3>
              <p className="text-gray-700">
                Estás autenticado con un access token en memoria (localStorage).
              </p>
              <p className="text-gray-700">
                El refresh token está en una cookie httpOnly y se usa solo para
                renovar el access token cuando expire.
              </p>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
