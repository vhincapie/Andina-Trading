import { useEffect, useMemo, useState } from "react";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";
import {
  descargarRegionalCsv,
  descargarSegmentacionCsv,
  descargarComisionesCsv,
  listarPaises,
} from "../../api/consolidacionService";

export default function ConsolidacionPage() {
  const [downloading, setDownloading] = useState("");
  const [ok, setOk] = useState("");
  const [err, setErr] = useState("");

  const [criterio, setCriterio] = useState("PAIS");
  const [paisId, setPaisId] = useState("");
  const [minMonto, setMinMonto] = useState("");
  const [maxMonto, setMaxMonto] = useState("");

  const [paises, setPaises] = useState([]);

  useEffect(() => {
    listarPaises()
      .then(setPaises)
      .catch(() => setPaises([]));
  }, []);

  useEffect(() => {
    if (err) {
      const t = setTimeout(() => setErr(""), 6000);
      return () => clearTimeout(t);
    }
    if (ok) {
      const t = setTimeout(() => setOk(""), 4000);
      return () => clearTimeout(t);
    }
  }, [err, ok]);

  const onRun = async (label, fn) => {
    setErr("");
    setOk("");
    setDownloading(label);
    try {
      const res = await fn();
      setOk(`Descargado: ${res?.filename || "reporte.csv"}`);
    } catch (e) {
      setErr(e?.message || "No se pudo descargar.");
    } finally {
      setDownloading("");
    }
  };

  const segmentacionParams = useMemo(() => {
    const p = { criterio };
    if (paisId) p.paisId = Number(paisId);
    if (criterio === "MONTO") {
      if (minMonto !== "") p.minMonto = Number(minMonto);
      if (maxMonto !== "") p.maxMonto = Number(maxMonto);
    }
    return p;
  }, [criterio, paisId, minMonto, maxMonto]);

  return (
    <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-5xl mx-auto px-4 md:px-6 py-8">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Consolidación
        </h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4"></div>

        <div className="mb-4 space-y-2">
          <ErrorAlert message={err} onClose={() => setErr("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          <section className="bg-slate-900/50 border border-slate-800 rounded-2xl p-5 shadow-xl ring-1 ring-white/5 backdrop-blur flex flex-col">
            <div className="mb-3">
              <h3 className="text-lg font-semibold text-slate-100">
                Consolidación regional
              </h3>
              <p className="text-xs text-slate-400">
                Resumen por regiones en CSV.
              </p>
            </div>
            <div className="mt-auto">
              <button
                className="w-full inline-flex items-center justify-center rounded-lg px-4 py-2.5 bg-emerald-500/90 hover:bg-emerald-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
                disabled={!!downloading}
                onClick={() => onRun("REG", descargarRegionalCsv)}
              >
                {downloading === "REG" ? "Descargando…" : "Regional CSV"}
              </button>
            </div>
          </section>

          <section className="lg:col-span-2 bg-slate-900/50 border border-slate-800 rounded-2xl p-5 shadow-xl ring-1 ring-white/5 backdrop-blur">
            <div className="mb-4">
              <h3 className="text-lg font-semibold text-slate-100">
                Segmentación de inversionistas
              </h3>
              <p className="text-xs text-slate-400">
                Filtra por país o por rangos de monto transado.
              </p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <label className="flex flex-col">
                <span className="text-xs font-medium text-slate-300 mb-1">
                  Criterio
                </span>
                <select
                  className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                  value={criterio}
                  onChange={(e) => setCriterio(e.target.value)}
                >
                  <option value="PAIS">Por país</option>
                  <option value="MONTO">Por monto transado</option>
                </select>
              </label>

              <label className="flex flex-col">
                <span className="text-xs font-medium text-slate-300 mb-1">
                  País (opcional)
                </span>
                <select
                  className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                  value={paisId}
                  onChange={(e) => setPaisId(e.target.value)}
                >
                  <option value="">— Todos —</option>
                  {paises.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.nombre}
                    </option>
                  ))}
                </select>
              </label>

              {criterio === "MONTO" && (
                <>
                  <label className="flex flex-col">
                    <span className="text-xs font-medium text-slate-300 mb-1">
                      Monto mínimo
                    </span>
                    <input
                      type="number"
                      step="0.01"
                      className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                      value={minMonto}
                      onChange={(e) => setMinMonto(e.target.value)}
                      placeholder="Ej: 1000"
                    />
                  </label>
                  <label className="flex flex-col">
                    <span className="text-xs font-medium text-slate-300 mb-1">
                      Monto máximo
                    </span>
                    <input
                      type="number"
                      step="0.01"
                      className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                      value={maxMonto}
                      onChange={(e) => setMaxMonto(e.target.value)}
                      placeholder="Ej: 5000"
                    />
                  </label>
                </>
              )}
            </div>

            <div className="mt-5 flex items-center justify-between gap-3">
              <div className="text-xs text-slate-400">
                {criterio === "PAIS"
                  ? "Filtro opcional por país"
                  : "Rango de monto transado"}
              </div>
              <button
                className="inline-flex items-center justify-center rounded-lg px-5 py-2.5 bg-cyan-500/90 hover:bg-cyan-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
                disabled={!!downloading}
                onClick={() =>
                  onRun("SEG", () =>
                    descargarSegmentacionCsv(segmentacionParams)
                  )
                }
              >
                {downloading === "SEG" ? "Descargando…" : "Segmentación CSV"}
              </button>
            </div>
          </section>

          <section className="lg:col-span-3 bg-slate-900/50 border border-slate-800 rounded-2xl p-5 shadow-xl ring-1 ring-white/5 backdrop-blur">
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
              <div>
                <h3 className="text-lg font-semibold text-slate-100">
                  Comisiones por comisionista
                </h3>
                <p className="text-xs text-slate-400">
                  Detalle consolidado de comisiones en CSV.
                </p>
              </div>
              <button
                className="inline-flex items-center justify-center rounded-lg px-5 py-2.5 bg-indigo-500/90 hover:bg-indigo-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
                disabled={!!downloading}
                onClick={() => onRun("COMI", descargarComisionesCsv)}
              >
                {downloading === "COMI" ? "Descargando…" : "Comisiones CSV"}
              </button>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}
