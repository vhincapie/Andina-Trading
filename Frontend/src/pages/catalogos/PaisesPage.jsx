import { useEffect, useState } from "react";
import {
  crearPais,
  listarPaises,
  listarSituaciones,
} from "../../api/catalogosService";
import { getApiErrorMessage } from "../../utils/errorMessage";

export default function PaisesPage() {
  const [nombre, setNombre] = useState("");
  const [codigoIso3, setCodigoIso3] = useState("");
  const [situacionId, setSituacionId] = useState("");

  const [loadingList, setLoadingList] = useState(false);
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState(null);
  const [okMsg, setOkMsg] = useState(null);

  const [paises, setPaises] = useState([]);
  const [situaciones, setSituaciones] = useState([]);

  useEffect(() => {
    if (!error && !okMsg) return;
    const t = setTimeout(() => {
      setError(null);
      setOkMsg(null);
    }, 7000);
    return () => clearTimeout(t);
  }, [error, okMsg]);

  const resetAlerts = () => {
    setError(null);
    setOkMsg(null);
  };

  const validate = () => {
    const n = (nombre || "").trim();
    const c = (codigoIso3 || "").trim().toUpperCase();
    if (!n) return "El nombre es obligatorio.";
    if (!c) return "El código ISO3 es obligatorio.";
    if (c.length !== 3)
      return "El código ISO3 debe tener exactamente 3 letras.";
    if (!/^[A-Z]{3}$/.test(c))
      return "El código ISO3 solo debe contener letras A-Z.";
    if (!situacionId) return "La situación económica es obligatoria.";
    return null;
  };

  const loadData = async () => {
    setLoadingList(true);
    setError(null);
    try {
      const [paisesData, situacionesData] = await Promise.all([
        listarPaises(),
        listarSituaciones(),
      ]);
      setPaises(Array.isArray(paisesData) ? paisesData : []);
      setSituaciones(Array.isArray(situacionesData) ? situacionesData : []);
    } catch (e) {
      console.error(e);
      const status = e?.response?.status;
      const parsed = getApiErrorMessage(e);
      const msg =
        parsed ||
        (status === 401
          ? "Sesión inválida o expirada."
          : "No se pudieron cargar países o situaciones económicas.");
      setError(msg);
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    resetAlerts();

    const validation = validate();
    if (validation) {
      setError(validation);
      return;
    }

    const payload = {
      nombre: nombre.trim(),
      codigoIso3: codigoIso3.trim().toUpperCase(),
      situacionEconomicaDTO: { id: Number(situacionId) },
    };

    setCreating(true);
    try {
      await crearPais(payload);
      setOkMsg("País creado correctamente.");
      setNombre("");
      setCodigoIso3("");
      setSituacionId("");
      await loadData();
    } catch (e) {
      console.error(e);
      const status = e?.response?.status;
      const parsed = getApiErrorMessage(e);
      const msg =
        parsed ||
        (status === 409 && "Ya existe un país con ese código ISO3 o nombre.") ||
        (status === 400 && "Datos inválidos.") ||
        (status === 401 && "Sesión inválida o expirada.") ||
        "No se pudo crear el país.";
      setError(msg);
    } finally {
      setCreating(false);
    }
  };

  return (
    <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-5xl mx-auto px-4 md:px-6 py-8">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Países
        </h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4"></div>

        <div className="rounded-2xl border border-slate-800 bg-slate-900/50 ring-1 ring-white/5 backdrop-blur p-5 space-y-4">
          <h4 className="text-lg font-semibold text-slate-100">Crear país</h4>

          {error && (
            <div className="bg-red-500/10 border border-red-500/30 text-red-200 px-3 py-2 rounded-lg text-sm">
              <span className="font-semibold">Error:</span> {error}
            </div>
          )}

          {okMsg && (
            <div className="bg-emerald-500/10 border border-emerald-500/30 text-emerald-200 px-3 py-2 rounded-lg text-sm">
              {okMsg}
            </div>
          )}

          <form onSubmit={onSubmit} className="space-y-4">
            <div className="grid gap-4 md:grid-cols-3">
              <div className="md:col-span-2">
                <label className="block text-xs font-medium text-slate-300 mb-1">
                  Nombre
                </label>
                <input
                  type="text"
                  value={nombre}
                  onChange={(e) => setNombre(e.target.value)}
                  placeholder="Ecuador"
                  className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-300 mb-1">
                  Código ISO3
                </label>
                <input
                  type="text"
                  value={codigoIso3}
                  onChange={(e) => setCodigoIso3(e.target.value.toUpperCase())}
                  placeholder="ECU"
                  maxLength={3}
                  className="w-full uppercase tracking-wider bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                  required
                />
              </div>

              <div className="md:col-span-3">
                <label className="block text-xs font-medium text-slate-300 mb-1">
                  Situación Económica
                </label>
                <select
                  value={situacionId}
                  onChange={(e) => setSituacionId(e.target.value)}
                  required
                  className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                >
                  <option value="">— Seleccionar —</option>
                  {situaciones.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.nombre}
                    </option>
                  ))}
                </select>
                {situacionId && (
                  <small className="block mt-2 text-slate-400">
                    {
                      situaciones.find(
                        (x) => String(x.id) === String(situacionId)
                      )?.descripcion
                    }
                  </small>
                )}
              </div>
            </div>

            <button
              type="submit"
              disabled={creating}
              className="inline-flex items-center justify-center rounded-lg px-5 py-2.5 bg-emerald-500/90 hover:bg-emerald-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
            >
              {creating ? "Creando..." : "Crear"}
            </button>
          </form>
        </div>

        <div className="mt-6 rounded-2xl border border-slate-800 bg-slate-900/50 ring-1 ring-white/5 backdrop-blur p-5">
          <h4 className="text-lg font-semibold text-slate-100 mb-3">Listado</h4>

          {loadingList ? (
            <p className="text-slate-400">Cargando países...</p>
          ) : paises.length === 0 ? (
            <p className="text-slate-400">No hay países registrados.</p>
          ) : (
            <ul className="space-y-2">
              {paises.map((p) => (
                <li
                  key={p.id ?? `${p.nombre}-${p.codigoIso3}`}
                  className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-3 hover:bg-slate-900/40 transition"
                >
                  <div className="font-medium text-slate-100">
                    {p.nombre} · {p.codigoIso3?.toUpperCase()}
                  </div>
                  {p.situacionEconomicaDTO?.nombre && (
                    <div className="text-sm text-slate-400 mt-1">
                      Situación: {p.situacionEconomicaDTO.nombre}
                      {p.situacionEconomicaDTO.descripcion
                        ? ` — ${p.situacionEconomicaDTO.descripcion}`
                        : ""}
                    </div>
                  )}
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
