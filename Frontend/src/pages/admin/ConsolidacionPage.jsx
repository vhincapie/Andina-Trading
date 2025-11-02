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
    listarPaises().then(setPaises).catch(() => setPaises([]));
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
    <div className="max-w-3xl mx-auto space-y-6">
      <header className="space-y-1">
        <h2 className="text-2xl font-semibold">Consolidación</h2>
        <p className="text-gray-600 text-sm">
          Descarga CSV de consolidación regional, segmentación y comisiones.
        </p>
      </header>

      <ErrorAlert message={err} onClose={() => setErr("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      <section className="bg-white border rounded p-4 space-y-3">
        <h3 className="font-medium">Consolidación regional</h3>
        <button
          className="px-4 py-2 rounded bg-blue-600 text-white disabled:opacity-60"
          disabled={!!downloading}
          onClick={() => onRun("REG", descargarRegionalCsv)}
        >
          {downloading === "REG" ? "Descargando…" : "Regional CSV"}
        </button>
      </section>

  
      <section className="bg-white border rounded p-4 space-y-4">
        <h3 className="font-medium">Segmentación de inversionistas</h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <label className="flex flex-col text-sm">
            <span className="text-gray-700 mb-1">Criterio</span>
            <select
              className="border rounded px-2 py-1"
              value={criterio}
              onChange={(e) => setCriterio(e.target.value)}
            >
              <option value="PAIS">Por país</option>
              <option value="MONTO">Por monto transado</option>
            </select>
          </label>

          <label className="flex flex-col text-sm">
            <span className="text-gray-700 mb-1">País (opcional)</span>
            <select
              className="border rounded px-2 py-1"
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
              <label className="flex flex-col text-sm">
                <span className="text-gray-700 mb-1">Monto mínimo</span>
                <input
                  type="number"
                  step="0.01"
                  className="border rounded px-2 py-1"
                  value={minMonto}
                  onChange={(e) => setMinMonto(e.target.value)}
                  placeholder="Ej: 1000"
                />
              </label>
              <label className="flex flex-col text-sm">
                <span className="text-gray-700 mb-1">Monto máximo</span>
                <input
                  type="number"
                  step="0.01"
                  className="border rounded px-2 py-1"
                  value={maxMonto}
                  onChange={(e) => setMaxMonto(e.target.value)}
                  placeholder="Ej: 5000"
                />
              </label>
            </>
          )}
        </div>

        <button
          className="px-4 py-2 rounded bg-emerald-600 text-white disabled:opacity-60"
          disabled={!!downloading}
          onClick={() => onRun("SEG", () => descargarSegmentacionCsv(segmentacionParams))}
        >
          {downloading === "SEG" ? "Descargando…" : "Segmentación CSV"}
        </button>
      </section>

      <section className="bg-white border rounded p-4 space-y-3">
        <h3 className="font-medium">Comisiones por comisionista</h3>
        <button
          className="px-4 py-2 rounded bg-indigo-600 text-white disabled:opacity-60"
          disabled={!!downloading}
          onClick={() => onRun("COMI", descargarComisionesCsv)}
        >
          {downloading === "COMI" ? "Descargando…" : "Comisiones CSV"}
        </button>
      </section>
    </div>
  );
}
