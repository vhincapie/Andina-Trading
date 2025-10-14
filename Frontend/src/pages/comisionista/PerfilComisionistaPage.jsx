import { useEffect, useMemo, useState } from "react";
import { getMiPerfilComisionista } from "../../api/comisionistaService";
import { listarPaises, listarCiudades } from "../../api/catalogosService";
import ErrorAlert from "../../components/alerts/ErrorAlert";

export default function PerfilComisionistaPage() {
  const [perfil, setPerfil] = useState(null);
  const [paises, setPaises] = useState([]);
  const [ciudades, setCiudades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const paisNombreById = useMemo(() => {
    const m = {};
    for (const p of paises) m[String(p.id)] = p.nombre;
    return m;
  }, [paises]);

  const ciudadNombreById = useMemo(() => {
    const m = {};
    for (const c of ciudades) m[String(c.id)] = c.nombre;
    return m;
  }, [ciudades]);

  useEffect(() => {
    (async () => {
      setError("");
      setLoading(true);
      try {
        const [raw, ps, cs] = await Promise.all([
          getMiPerfilComisionista(),
          listarPaises(),
          listarCiudades(),
        ]);
        setPerfil(raw);
        setPaises(ps || []);
        setCiudades(cs || []);
      } catch (e) {
        console.error(e);
        const msg =
          e?.response?.data?.message ||
          (e?.response?.status === 401 && "Sesión inválida o expirada.") ||
          "No se pudo cargar el perfil del comisionista.";
        setError(msg);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  if (loading)
    return <p className="max-w-3xl mx-auto px-4 py-6">Cargando...</p>;

  const paisNombre = paisNombreById[String(perfil?.paisId)] ?? "—";
  const ciudadNombre = ciudadNombreById[String(perfil?.ciudadId)] ?? "—";

  return (
    <div className="max-w-3xl mx-auto px-4 py-6 space-y-4">
      <h2 className="text-2xl font-semibold">Mi perfil de Comisionista</h2>
      <ErrorAlert message={error} onClose={() => setError("")} />

      <div className="bg-white border rounded p-4 space-y-1">
        <p>
          <b>Correo:</b> {perfil?.correo || "—"}
        </p>
        <p>
          <b>Nombre:</b> {perfil?.nombre || "—"}
        </p>
        <p>
          <b>Apellido:</b> {perfil?.apellido || "—"}
        </p>
        <p>
          <b>Fecha de nacimiento:</b> {perfil?.fechaNacimiento || "—"}
        </p>
        <p>
          <b>Tipo de documento:</b> {perfil?.tipoDocumento || "—"}
        </p>
        <p>
          <b>Número de documento:</b> {perfil?.numeroDocumento || "—"}
        </p>
        <p>
          <b>País:</b> {paisNombre}
        </p>
        <p>
          <b>Ciudad:</b> {ciudadNombre}
        </p>
        <p>
          <b>Años de experiencia:</b> {perfil?.aniosExperiencia ?? "—"}
        </p>
        <p>
          <b>Rol:</b> COMISIONISTA
        </p>
      </div>
    </div>
  );
}
