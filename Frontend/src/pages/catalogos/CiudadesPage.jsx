import { useEffect, useState } from "react";
import {
  crearCiudad,
  listarCiudades,
  listarPaises,
} from "../../api/catalogosService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

export default function CiudadesPage() {
  const [items, setItems] = useState([]);
  const [paises, setPaises] = useState([]);
  const [form, setForm] = useState({ nombre: "", paisId: "" });

  const [error, setError] = useState("");
  const [ok, setOk] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!error && !ok) return;
    const t = setTimeout(() => {
      setError("");
      setOk("");
    }, 7000);
    return () => clearTimeout(t);
  }, [error, ok]);

  const load = async () => {
    setError("");
    setLoading(true);
    try {
      const [ciudades, paisesResp] = await Promise.all([
        listarCiudades(),
        listarPaises(),
      ]);
      setItems(ciudades);
      setPaises(paisesResp);
    } catch (e) {
      console.error(e);
      setError(
        e?.response?.data?.message || "No se pudieron cargar ciudades o países."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");
    setSubmitting(true);
    try {
      await crearCiudad({
        nombre: form.nombre.trim(),
        estado: "ACTIVO",
        paisDTO: { id: Number(form.paisId) },
      });
      setOk("Ciudad creada correctamente.");
      setForm({ nombre: "", paisId: "" });
      await load();
    } catch (e) {
      console.error(e);
      const status = e?.response?.status;
      const msg =
        e?.response?.data?.message ||
        (status === 409 &&
          "Ya existe una ciudad con ese nombre para ese país.") ||
        (status === 400 && "Datos inválidos.") ||
        "No se pudo crear la ciudad.";
      setError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-5xl mx-auto px-4 md:px-6 py-8">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Ciudades
        </h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4"></div>

        <form
          onSubmit={onSubmit}
          className="rounded-2xl border border-slate-800 bg-slate-900/50 ring-1 ring-white/5 backdrop-blur p-5 space-y-4"
        >
          <h3 className="text-lg font-semibold text-slate-100">Crear ciudad</h3>

          <ErrorAlert message={error} onClose={() => setError("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />

          <div className="grid gap-4 md:grid-cols-3">
            <input
              className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
              placeholder="Nombre"
              value={form.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
              required
            />
            <select
              className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
              value={form.paisId}
              onChange={(e) => setForm({ ...form, paisId: e.target.value })}
              required
            >
              <option value="">Selecciona país</option>
              {paises.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nombre}
                </option>
              ))}
            </select>
          </div>

          <button
            className="inline-flex items-center justify-center rounded-lg px-5 py-2.5 bg-emerald-500/90 hover:bg-emerald-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
            disabled={submitting}
          >
            {submitting ? "Creando..." : "Crear"}
          </button>
        </form>

        <div className="mt-6 rounded-2xl border border-slate-800 bg-slate-900/50 ring-1 ring-white/5 backdrop-blur p-5">
          <h3 className="text-lg font-semibold text-slate-100 mb-3">Listado</h3>
          {loading ? (
            <p className="text-slate-400">Cargando...</p>
          ) : (
            <ul className="space-y-2">
              {items.map((c) => (
                <li
                  key={c.id}
                  className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-3 hover:bg-slate-900/40 transition"
                >
                  <div className="font-medium text-slate-100">
                    {c.nombre} · {c.estado}
                  </div>
                  <div className="text-sm text-slate-400">
                    País: {c.paisDTO?.nombre}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
