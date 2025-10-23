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
    <div className="max-w-4xl mx-auto space-y-5">
      <h2 className="text-2xl font-semibold">Resumen de comisiones</h2>

      <form
        onSubmit={onFiltrar}
        className="bg-white border rounded p-4 grid gap-3 md:grid-cols-[1fr_1fr_auto]"
      >
        <div>
          <label className="block text-sm mb-1">Desde</label>
          <input
            type="date"
            value={from}
            onChange={(e) => setFrom(e.target.value)}
            className="border p-2 rounded w-full"
          />
        </div>

        <div>
          <label className="block text-sm mb-1">Hasta</label>
          <input
            type="date"
            value={to}
            onChange={(e) => setTo(e.target.value)}
            className="border p-2 rounded w-full"
          />
        </div>

        <div className="flex items-end">
          <button
            type="submit"
            className="bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-60"
            disabled={loading}
          >
            {loading ? "Cargando..." : "Filtrar"}
          </button>
        </div>
      </form>

      <ErrorAlert message={err} onClose={() => setErr("")} />

      <section className="bg-white border rounded p-4">
        <p className="text-lg">
          <span className="font-semibold">Total de órdenes:</span>{" "}
          {cantidad}
        </p>
        <p className="text-lg">
          <span className="font-semibold">Comisión acumulada:</span>{" "}
          ${total.toFixed(2)}
        </p>
        <p className="text-sm text-gray-600">
          <span className="font-semibold">Período:</span> {periodoLabel}
        </p>
      </section>
    </div>
  );
}
