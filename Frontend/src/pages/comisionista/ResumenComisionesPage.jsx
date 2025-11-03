import { useEffect, useMemo, useState } from "react";
import { getResumenComisiones } from "../../api/serviceOrdenes";
import ErrorAlert from "../../components/alerts/ErrorAlert";

const fmt = (d) => d.toISOString().slice(0, 10);
const firstDayOfMonth = () => {
  const d = new Date();
  return fmt(new Date(d.getFullYear(), d.getMonth(), 1));
};
const today = () => fmt(new Date());

export default function ComisionesResumenPage() {
  const [from, setFrom] = useState(firstDayOfMonth());
  const [to, setTo] = useState(today());

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [total, setTotal] = useState(0);
  const [cantidad, setCantidad] = useState(0);

  const periodoLabel = useMemo(() => {
    const a = from || "— — —";
    const b = to || "— — —";
    return `${a} — ${b}`;
  }, [from, to]);

  const fetchData = async (opts = {}) => {
    setErr("");
    setLoading(true);
    try {
      const res = await getResumenComisiones(opts);
      setTotal(Number(res?.total || 0));
      setCantidad(Number(res?.cantidadOrdenes || 0));
    } catch (e) {
      setErr(e?.message || "No se pudo obtener el resumen de comisiones.");
      setTotal(0);
      setCantidad(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData({ from, to });
  }, []);

  const onFiltrar = (e) => {
    e.preventDefault();
    const params = {};
    if (from) params.from = from;
    if (to) params.to = to;
    fetchData(params);
  };

  return (
    <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-5xl mx-auto px-4 md:px-6 py-8 space-y-5">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Resumen de comisiones
        </h2>
        <div className="mt-1 h-0.5 w-24 bg-emerald-400/80 rounded"></div>

        <form
          onSubmit={onFiltrar}
          className="rounded-2xl border border-slate-800 ring-1 ring-white/5 bg-slate-900/50 backdrop-blur grid gap-4 md:grid-cols-[1fr_1fr_auto] p-4 md:p-6"
        >
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">
              Desde
            </label>
            <input
              type="date"
              value={from}
              onChange={(e) => setFrom(e.target.value)}
              className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
            />
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">
              Hasta
            </label>
            <input
              type="date"
              value={to}
              onChange={(e) => setTo(e.target.value)}
              className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
            />
          </div>

          <div className="flex items-end">
            <button
              type="submit"
              className="inline-flex items-center justify-center rounded-lg px-4 py-2.5 bg-emerald-500/90 hover:bg-emerald-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
              disabled={loading}
            >
              {loading ? "Cargando..." : "Filtrar"}
            </button>
          </div>
        </form>

        <ErrorAlert message={err} onClose={() => setErr("")} />

        <section className="rounded-2xl border border-slate-800 ring-1 ring-white/5 bg-slate-900/50 backdrop-blur p-6">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-4">
              <div className="text-xs text-slate-400">Total de órdenes</div>
              <div className="text-2xl font-semibold text-slate-100">
                {cantidad}
              </div>
            </div>
            <div className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-4">
              <div className="text-xs text-slate-400">Comisión acumulada</div>
              <div className="text-2xl font-semibold text-slate-100">
                ${total.toFixed(2)}
              </div>
            </div>
            <div className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-4">
              <div className="text-xs text-slate-400">Período</div>
              <div className="text-sm font-medium text-slate-200">
                {periodoLabel}
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
