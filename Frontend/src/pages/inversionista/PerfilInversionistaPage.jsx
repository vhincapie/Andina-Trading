import { useEffect, useMemo, useState } from "react";
import {
  getMiPerfil,
  actualizarMiPerfil,
} from "../../api/inversionistaService";
import { listarPaises, listarCiudades } from "../../api/catalogosService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";
import { useAuth } from "../../context/AuthContext";

function normalizePerfil(raw = {}, fallbackCorreo = "", fallbackRol = "") {
  const get = (obj, ...keys) =>
    keys.reduce(
      (v, k) => (v !== undefined && v !== null ? v : obj?.[k]),
      undefined
    );

  const correo = get(raw, "correo", "email", "username") ?? fallbackCorreo;
  const nombre =
    get(raw, "nombre", "nombres", "firstName", "primerNombre") ?? "";
  const apellido =
    get(raw, "apellido", "apellidos", "lastName", "primerApellido") ?? "";
  const fechaNacimiento = get(raw, "fechaNacimiento", "birthDate") ?? "";
  const tipoDocumento = get(raw, "tipoDocumento", "documentType") ?? "";
  const numeroDocumento =
    get(raw, "numeroDocumento", "documentNumber", "documento") ?? "";

  const paisId =
    get(raw, "paisId") ?? get(raw, "paisDTO", "pais", "country")?.id ?? "";
  const ciudadId =
    get(raw, "ciudadId") ?? get(raw, "ciudadDTO", "ciudad", "city")?.id ?? "";

  const paisNombre =
    get(raw, "paisDTO", "pais", "country")?.nombre ??
    get(raw, "paisNombre", "countryName");
  const ciudadNombre =
    get(raw, "ciudadDTO", "ciudad", "city")?.nombre ??
    get(raw, "ciudadNombre", "cityName");

  const rolRaw = get(raw, "rol", "role") ?? fallbackRol;
  const rol = String(rolRaw || "").toUpperCase();

  return {
    correo,
    nombre,
    apellido,
    fechaNacimiento,
    tipoDocumento,
    numeroDocumento,
    paisId: paisId || "",
    ciudadId: ciudadId || "",
    paisNombre,
    ciudadNombre,
    rol,
  };
}

export default function PerfilInversionistaPage() {
  const { user } = useAuth();
  const [perfil, setPerfil] = useState(null);
  const [paises, setPaises] = useState([]);
  const [ciudades, setCiudades] = useState([]);
  const [form, setForm] = useState({
    nombre: "",
    apellido: "",
    fechaNacimiento: "",
    tipoDocumento: "",
    numeroDocumento: "",
    paisId: "",
    ciudadId: "",
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [ok, setOk] = useState("");
  const [error, setError] = useState("");

  const ciudadesFiltradas = useMemo(() => {
    if (!form.paisId) return [];
    return ciudades.filter(
      (c) => String(c.paisDTO?.id) === String(form.paisId)
    );
  }, [ciudades, form.paisId]);

  useEffect(() => {
    (async () => {
      try {
        const [raw, ps, cs] = await Promise.all([
          getMiPerfil(),
          listarPaises(),
          listarCiudades(),
        ]);
        const norm = normalizePerfil(raw, user?.correo, user?.rol);
        setPerfil(norm);
        setPaises(ps || []);
        setCiudades(cs || []);
        setForm({
          nombre: norm.nombre,
          apellido: norm.apellido,
          fechaNacimiento: norm.fechaNacimiento || "",
          tipoDocumento: norm.tipoDocumento,
          numeroDocumento: norm.numeroDocumento,
          paisId: norm.paisId || "",
          ciudadId: norm.ciudadId || "",
        });
      } catch (e) {
        console.error(e);
        setError("No se pudo cargar la información del perfil.");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  useEffect(() => {
    if (!error && !ok) return;
    const t = setTimeout(() => {
      setError("");
      setOk("");
    }, 6000);
    return () => clearTimeout(t);
  }, [error, ok]);

  const onChange = (e) => {
    const { name, value } = e.target;
    if (name === "paisId")
      setForm((f) => ({ ...f, paisId: value, ciudadId: "" }));
    else setForm((f) => ({ ...f, [name]: value }));
  };

  const validate = () => {
    if (!form.paisId) return "El país es obligatorio.";
    if (!form.ciudadId) return "La ciudad es obligatoria.";
    return null;
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");
    const v = validate();
    if (v) return setError(v);

    setSaving(true);
    try {
      const updatedRaw = await actualizarMiPerfil({
        paisId: Number(form.paisId),
        ciudadId: Number(form.ciudadId),
      });
      const normUpdated = normalizePerfil(
        updatedRaw,
        perfil?.correo,
        perfil?.rol
      );
      setPerfil(normUpdated);
      setOk("Ubicación actualizada correctamente.");
    } catch (e) {
      console.error(e);
      setError("No se pudo actualizar la ubicación.");
    } finally {
      setSaving(false);
    }
  };

  if (loading)
    return (
      <p className="max-w-sm mx-auto mt-20 text-center text-slate-200">
        Cargando...
      </p>
    );

  const paisNombre =
    perfil?.paisNombre ||
    paises.find((p) => String(p.id) === String(form.paisId))?.nombre ||
    "—";
  const ciudadNombre =
    perfil?.ciudadNombre ||
    ciudades.find((c) => String(c.id) === String(form.ciudadId))?.nombre ||
    "—";

  const panel =
    "bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur p-6 md:p-7";
  const titleBar =
    "text-sm font-semibold text-slate-200 flex items-center gap-2 mb-4";
  const bullet = "h-1.5 w-1.5 rounded-full bg-emerald-400 inline-block";

  const labelClass = "text-xs uppercase tracking-wide text-slate-400";
  const valueClass = "text-slate-200";
  const inputBase =
    "w-full bg-slate-900/60 border border-slate-700/70 rounded-lg px-3 py-2 text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-400/60 focus:border-emerald-400/40";
  const inputReadOnly =
    "bg-slate-900/40 border-slate-800 text-slate-300 cursor-not-allowed";

  return (
    <div className="relative min-h-[100dvh] px-4 md:px-6 py-6 md:py-8">
      <div className="max-w-7xl mx-auto">
        <div className="mb-6">
          <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            Mi perfil
          </h2>
          <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded"></div>
        </div>

        <div className="max-w-5xl mb-4">
          <ErrorAlert message={error} onClose={() => setError("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />
        </div>
        <div className="grid grid-cols-1 xl:grid-cols-2 gap-8">
          <section className={panel}>
            <div className={titleBar}>
              <span className={bullet} />
              <span>Información registrada</span>
            </div>

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
                <dd className={`col-span-2 ${valueClass}`}>
                  {perfil?.rol || String(user?.rol || "").toUpperCase() || "—"}
                </dd>
              </div>
            </dl>
          </section>

          <form onSubmit={onSubmit} className={panel}>
            <div className={titleBar}>
              <span className={bullet} />
              <span>Actualizar ubicación</span>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
              <div>
                <label className={labelClass}>Nombre</label>
                <input
                  className={`${inputBase} ${inputReadOnly}`}
                  name="nombre"
                  value={form.nombre}
                  readOnly
                />
              </div>

              <div>
                <label className={labelClass}>Apellido</label>
                <input
                  className={`${inputBase} ${inputReadOnly}`}
                  name="apellido"
                  value={form.apellido}
                  readOnly
                />
              </div>

              <div>
                <label className={labelClass}>Tipo de documento</label>
                <select
                  className={`${inputBase} ${inputReadOnly}`}
                  name="tipoDocumento"
                  value={form.tipoDocumento}
                  disabled
                >
                  <option value="">
                    {form.tipoDocumento || "Tipo de documento"}
                  </option>
                  <option value="CC">Cédula de Ciudadanía</option>
                  <option value="CE">Cédula de Extranjería</option>
                  <option value="PAS">Pasaporte</option>
                </select>
              </div>

              <div>
                <label className={labelClass}>Número de documento</label>
                <input
                  className={`${inputBase} ${inputReadOnly}`}
                  name="numeroDocumento"
                  value={form.numeroDocumento}
                  readOnly
                />
              </div>

              <div>
                <label className={labelClass}>Fecha de nacimiento</label>
                <input
                  type="date"
                  className={`${inputBase} ${inputReadOnly}`}
                  name="fechaNacimiento"
                  value={form.fechaNacimiento || ""}
                  readOnly
                />
              </div>

              <div>
                <label className={labelClass}>País</label>
                <select
                  className={inputBase}
                  name="paisId"
                  value={form.paisId}
                  onChange={onChange}
                  required
                >
                  <option value="">Selecciona un país…</option>
                  {paises.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.nombre}
                    </option>
                  ))}
                </select>
              </div>

              <div className="sm:col-span-2">
                <label className={labelClass}>Ciudad</label>
                <select
                  className={`${inputBase} ${!form.paisId ? "opacity-60" : ""}`}
                  name="ciudadId"
                  value={form.ciudadId}
                  onChange={onChange}
                  required
                  disabled={!form.paisId}
                >
                  <option value="">
                    {form.paisId
                      ? "Selecciona una ciudad…"
                      : "Selecciona primero el país"}
                  </option>
                  {ciudadesFiltradas.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.nombre}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="mt-6">
              <button
                type="submit"
                disabled={saving}
                className="w-full sm:w-auto px-6 py-3 rounded-lg font-medium text-white
                           bg-emerald-600 hover:bg-emerald-500 transition-colors
                           focus:outline-none focus:ring-2 focus:ring-emerald-400/60
                           disabled:opacity-60"
              >
                {saving ? "Guardando..." : "Actualizar ubicación"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
