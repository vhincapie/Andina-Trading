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
    <div className="max-w-3xl mx-auto space-y-5">
      <header className="space-y-1">
        <h2 className="text-2xl font-semibold">Reportes</h2>
        <p className="text-gray-600 text-sm">
          Genera y descarga reportes en formato CSV.
        </p>
      </header>

      <ErrorAlert message={err} onClose={() => setErr("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      <section className="bg-white border rounded p-4 space-y-4">
        <h3 className="font-medium">Inversionistas - Comisionistas</h3>
        <div className="flex flex-col sm:flex-row gap-3">
          <button
            className="px-4 py-2 rounded bg-blue-600 text-white disabled:opacity-60"
            disabled={!!downloading}
            onClick={() => run(descargarReporteInversionistas, "INV")}
          >
            {downloading === "INV" ? "Descargando…" : "Inversionistas CSV"}
          </button>

          <button
            className="px-4 py-2 rounded bg-blue-600 text-white disabled:opacity-60"
            disabled={!!downloading}
            onClick={() => run(descargarReporteComisionistas, "COMI")}
          >
            {downloading === "COMI" ? "Descargando…" : "Comisionistas CSV"}
          </button>
        </div>
      </section>

      <section className="bg-white border rounded p-4 space-y-4">
        <h3 className="font-medium">Ordenes</h3>
        <div className="flex flex-col sm:flex-row gap-3">
          <button
            className="px-4 py-2 rounded bg-emerald-600 text-white disabled:opacity-60"
            disabled={!!downloading}
            onClick={() => run(descargarReporteOrdenes, "ORD")}
          >
            {downloading === "ORD" ? "Descargando…" : "Órdenes CSV"}
          </button>
        </div>

      </section>
    </div>
  );
}
