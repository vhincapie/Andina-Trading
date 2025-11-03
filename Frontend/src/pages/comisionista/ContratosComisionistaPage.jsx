import { useEffect, useMemo, useState } from "react";
import { listarMisContratosComisionista } from "../../api/contratoService";
import ErrorAlert from "../../components/alerts/ErrorAlert";

const TABS = [
  { key: "ACTIVOS", label: "Activos" },
  { key: "FINALIZADOS", label: "Finalizados" },
  { key: "TODOS", label: "Todos" },
];

export default function ContratosComisionistaPage() {
  const [items, setItems] = useState([]);
  const [tab, setTab] = useState("ACTIVOS");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = async () => {
    setError("");
    setLoading(true);
    try {
      const data = await listarMisContratosComisionista();
      setItems(Array.isArray(data) ? data : []);
    } catch (e) {
      const msg =
        e?.response?.data?.message ||
        (e?.response?.status === 401 && "Sesión inválida o expirada.") ||
        "No fue posible cargar tus contratos.";
      setItems([]);
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const activos = useMemo(
    () => items.filter((c) => String(c.estado).toUpperCase() === "ACTIVO"),
    [items]
  );

  const finalizados = useMemo(
    () => items.filter((c) => String(c.estado).toUpperCase() !== "ACTIVO"),
    [items]
  );

  const currentList = useMemo(() => {
    if (tab === "ACTIVOS") return activos;
    if (tab === "FINALIZADOS") return finalizados;
    return items;
  }, [tab, activos, finalizados, items]);

  const renderLista = (list) => (
    <ul className="space-y-2">
      {list.map((c) => (
        <li
          key={c.id}
          className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-4 hover:bg-slate-900/50 transition"
        >
          <div className="font-medium text-slate-100">
            Inversionista: {c.inversionistaNombreCompleto || "—"}
            {c.inversionistaDocumento ? ` (${c.inversionistaDocumento})` : ""}
          </div>
          <div className="text-sm text-slate-300 space-y-1 mt-2">
            <p className="flex items-center gap-2">
              <b className="text-slate-200">Estado:</b>
              <span
                className={`px-2 py-0.5 rounded-full text-xs font-semibold ${
                  String(c.estado).toUpperCase() === "ACTIVO"
                    ? "bg-emerald-500/10 text-emerald-300"
                    : "bg-rose-500/10 text-rose-300"
                }`}
              >
                {c.estado}
              </span>
            </p>
            <p>
              <b className="text-slate-200">Moneda:</b> {c.moneda || "—"}
            </p>
            <p>
              <b className="text-slate-200">Porcentaje cobro:</b>{" "}
              {c.porcentajeCobroAplicado ?? "—"}%
            </p>
            <p>
              <b className="text-slate-200">Fecha inicio:</b>{" "}
              {c.fechaInicio ? new Date(c.fechaInicio).toLocaleString() : "—"}
            </p>
            {c.fechaFin && (
              <p>
                <b className="text-slate-200">Fecha fin:</b>{" "}
                {new Date(c.fechaFin).toLocaleString()}
              </p>
            )}
          </div>
        </li>
      ))}
    </ul>
  );

  return (
    <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-5xl mx-auto px-4 md:px-6 py-8 space-y-4">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Mis contratos con inversionistas
        </h2>
        <div className="mt-1 h-0.5 w-24 bg-emerald-400/80 rounded mb-2"></div>

        <ErrorAlert message={error} onClose={() => setError("")} />

        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-3">
          <div className="inline-flex rounded-xl ring-1 ring-slate-800 bg-slate-900/50 p-1">
            {TABS.map((t, idx) => {
              const isActive = tab === t.key;
              const count =
                t.key === "ACTIVOS"
                  ? activos.length
                  : t.key === "FINALIZADOS"
                  ? finalizados.length
                  : items.length;
              return (
                <button
                  key={t.key}
                  onClick={() => setTab(t.key)}
                  className={`px-4 py-2 text-sm rounded-lg transition ${
                    isActive
                      ? "bg-emerald-500/20 text-emerald-200"
                      : "text-slate-300 hover:bg-slate-800/60"
                  } ${idx > 0 ? "ml-1" : ""}`}
                >
                  {t.label} ({count})
                </button>
              );
            })}
          </div>

          <button
            onClick={load}
            className="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700 ring-1 ring-slate-700 disabled:opacity-60 transition"
            disabled={loading}
          >
            {loading ? "Actualizando..." : "Actualizar"}
          </button>
        </div>

        <div className="rounded-2xl border border-slate-800 ring-1 ring-white/5 bg-slate-900/50 backdrop-blur p-5">
          {loading ? (
            <p className="text-slate-400">Cargando contratos...</p>
          ) : currentList.length === 0 ? (
            <p className="text-slate-400">
              {tab === "ACTIVOS"
                ? "No tienes contratos activos."
                : tab === "FINALIZADOS"
                ? "No tienes contratos finalizados/cancelados."
                : "No hay contratos para mostrar."}
            </p>
          ) : (
            renderLista(currentList)
          )}
        </div>
      </div>
    </div>
  );
}
