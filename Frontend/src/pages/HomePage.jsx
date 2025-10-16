import { useState, useEffect } from "react";
import { getMe } from "../api/authService";
import SaldoCard from "../components/inversionista/SaldoCard";
import { useAuth } from "../context/AuthContext";

export default function HomePage() {
  const [me, setMe] = useState(null);
  const [error, setError] = useState("");
  const { user } = useAuth();

  useEffect(() => {
    const load = async () => {
      try {
        const data = await getMe();
        setMe(data);
      } catch {
        setError("No se pudo cargar la información del usuario.");
      }
    };
    load();
  }, []);

  const role = String(user?.rol || me?.rol || "").toUpperCase();
  const isInvestor = ["INVESTOR", "INVERSIONISTA"].includes(role);

  return (
    <main>
      <h2 className="text-2xl font-semibold mb-4">Inicio</h2>
      {error && <p className="text-red-600">{error}</p>}
      <div className="grid gap-4 md:grid-cols-2">
        <div className="bg-white border rounded p-4">
          <h3 className="font-semibold mb-2">Mi perfil</h3>
          {!me ? (
            <p className="text-gray-600">Cargando...</p>
          ) : (
            <>
              <p>
                <span className="font-medium">Correo:</span> {me.correo}
              </p>
              <p>
                <span className="font-medium">Rol:</span> {me.rol}
              </p>
            </>
          )}
        </div>
        {isInvestor && <SaldoCard />}
      </div>
    </main>
  );
}
