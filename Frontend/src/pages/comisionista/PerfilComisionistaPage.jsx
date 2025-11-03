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
    return (
      <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
        <div className="max-w-5xl mx-auto px-4 md:px-6 py-8">
          <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            Mi perfil
          </h2>
          <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded"></div>
          <p className="text-slate-400 mt-4">Cargando...</p>
        </div>
      </div>
    );

  const paisNombre = paisNombreById[String(perfil?.paisId)] ?? "—";
  const ciudadNombre = ciudadNombreById[String(perfil?.ciudadId)] ?? "—";

  const panel =
    "bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur p-6 md:p-7";
  const labelClass = "text-xs uppercase tracking-wide text-slate-400";
  const valueClass = "text-slate-200";

  return (
    <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-7xl mx-auto px-4 md:px-6 py-8 space-y-6">
        <div>
          <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            Mi perfil
          </h2>
          <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded"></div>
        </div>

        <div className="max-w-5xl">
          <ErrorAlert message={error} onClose={() => setError("")} />
        </div>

        <div className="grid grid-cols-1 xl:grid-cols-2 gap-8">
          <section className={panel}>
            <dl className="divide-y divide-slate-800/70">
              <div className="grid grid-cols-3 gap-4 py-3">
                <dt className={labelClass}>Correo</dt>
                <dd className={`col-span-2 ${valueClass}`}>
                  {perfil?.correo || "—"}
                </dd>
              </div>
              <div className="grid grid-cols-3 gap-4 py-3">
                <dt className={labelClass}>Nombre</dt>
                <dd className={`col-span-2 ${valueClass}`}>
                  {perfil?.nombre || "—"}
                </dd>
              </div>
              <div className="grid grid-cols-3 gap-4 py-3">
                <dt className={labelClass}>Apellido</dt>
                <dd className={`col-span-2 ${valueClass}`}>
                  {perfil?.apellido || "—"}
                </dd>
              </div>
              <div className="grid grid-cols-3 gap-4 py-3">
                <dt className={labelClass}>Fecha de nacimiento</dt>
                <dd className={`col-span-2 ${valueClass}`}>
                  {perfil?.fechaNacimiento || "—"}
                </dd>
              </div>
              <div className="grid grid-cols-3 gap-4 py-3">
                <dt className={labelClass}>Tipo de documento</dt>
                <dd className={`col-span-2 ${valueClass}`}>
                  {perfil?.tipoDocumento || "—"}
                </dd>
              </div>
              <div className="grid grid-cols-3 gap-4 py-3">
                <dt className={labelClass}>Número de documento</dt>
                <dd className={`col-span-2 ${valueClass}`}>
                  {perfil?.numeroDocumento || "—"}
                </dd>
              </div>
              <div className="grid grid-cols-3 gap-4 py-3">
                <dt className={labelClass}>País</dt>
                <dd className={`col-span-2 ${valueClass}`}>{paisNombre}</dd>
              </div>
              <div className="grid grid-cols-3 gap-4 py-3">
                <dt className={labelClass}>Ciudad</dt>
                <dd className={`col-span-2 ${valueClass}`}>{ciudadNombre}</dd>
              </div>
              <div className="grid grid-cols-3 gap-4 py-3">
                <dt className={labelClass}>Rol</dt>
                <dd className={`col-span-2 ${valueClass}`}>COMISIONISTA</dd>
              </div>
            </dl>
          </section>

          <section className={panel}>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-4">
                <div className="text-xs text-slate-400">
                  Años de experiencia
                </div>
                <div className="text-2xl font-semibold text-slate-100">
                  {perfil?.aniosExperiencia ?? "—"}
                </div>
              </div>
              <div className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-4">
                <div className="text-xs text-slate-400">Estado</div>
                <div className="text-2xl font-semibold text-emerald-300">
                  Activo
                </div>
              </div>
              <div className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-4">
                <div className="text-xs text-slate-400">Moneda preferida</div>
                <div className="text-2xl font-semibold text-slate-100">
                  {perfil?.moneda || "—"}
                </div>
              </div>
            </div>

            <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-4">
                <div className={labelClass}>Ubicación</div>
                <div className="text-sm text-slate-200 mt-1">
                  {ciudadNombre !== "—" || paisNombre !== "—"
                    ? `${ciudadNombre}, ${paisNombre}`
                    : "—"}
                </div>
              </div>
              <div className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-4">
                <div className={labelClass}>Identificación</div>
                <div className="text-sm text-slate-200 mt-1">
                  {perfil?.tipoDocumento || "—"} {perfil?.numeroDocumento || ""}
                </div>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}
