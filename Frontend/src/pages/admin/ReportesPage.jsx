import { useState, useEffect } from "react";
import {
  descargarReporteInversionistas,
  descargarReporteComisionistas,
  descargarReporteOrdenes,
} from "../../api/serviceReportes";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

export default function ReportesPage() {
  const [downloading, setDownloading] = useState("");
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");

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

  const run = async (fn, label) => {
    setErr("");
    setOk("");
    setDownloading(label);
    try {
      const res = await fn();
      setOk(`Descargado: ${res?.filename || "reporte.csv"}`);
    } catch (e) {
      setErr(e.message || "No se pudo descargar el reporte.");
    } finally {
      setDownloading("");
    }
  };

  return (
    <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-5xl mx-auto px-4 md:px-6 py-8">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Reportes
        </h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4"></div>

        <div className="mb-4 space-y-2">
          <ErrorAlert message={err} onClose={() => setErr("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          <section className="lg:col-span-2 bg-slate-900/50 border border-slate-800 rounded-2xl p-5 shadow-xl ring-1 ring-white/5 backdrop-blur">
            <div className="mb-4">
              <h3 className="text-lg font-semibold text-slate-100">
                Inversionistas · Comisionistas
              </h3>
              <p className="text-xs text-slate-400">
                Descarga listados consolidados en CSV.
              </p>
            </div>
            <div className="flex flex-col sm:flex-row gap-3">
              <button
                className="inline-flex items-center justify-center rounded-lg px-5 py-2.5 bg-emerald-500/90 hover:bg-emerald-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
                disabled={!!downloading}
                onClick={() => run(descargarReporteInversionistas, "INV")}
              >
                {downloading === "INV" ? "Descargando…" : "Inversionistas CSV"}
              </button>

              <button
                className="inline-flex items-center justify-center rounded-lg px-5 py-2.5 bg-cyan-500/90 hover:bg-cyan-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
                disabled={!!downloading}
                onClick={() => run(descargarReporteComisionistas, "COMI")}
              >
                {downloading === "COMI" ? "Descargando…" : "Comisionistas CSV"}
              </button>
            </div>
          </section>

          <section className="bg-slate-900/50 border border-slate-800 rounded-2xl p-5 shadow-xl ring-1 ring-white/5 backdrop-blur">
            <div className="mb-4">
              <h3 className="text-lg font-semibold text-slate-100">Órdenes</h3>
              <p className="text-xs text-slate-400">
                Movimientos y estados de órdenes.
              </p>
            </div>
            <div>
              <button
                className="w-full inline-flex items-center justify-center rounded-lg px-5 py-2.5 bg-indigo-500/90 hover:bg-indigo-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
                disabled={!!downloading}
                onClick={() => run(descargarReporteOrdenes, "ORD")}
              >
                {downloading === "ORD" ? "Descargando…" : "Órdenes CSV"}
              </button>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}
