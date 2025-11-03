import { useEffect, useMemo, useState } from "react";
import {
  listarRespaldoArchivos,
  ejecutarBackupDb,
  ejecutarBackupTodo,
  crearZip,
  descargarArchivo,
} from "../../api/backupService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

function nowTs() {
  const d = new Date();
  const p = (n) => String(n).padStart(2, "0");
  return (
    d.getFullYear() +
    p(d.getMonth() + 1) +
    p(d.getDate()) +
    "-" +
    p(d.getHours()) +
    p(d.getMinutes()) +
    p(d.getSeconds())
  );
}

export default function RespaldoPage() {
  const [archivos, setArchivos] = useState([]);
  const [sel, setSel] = useState({});
  const [scope, setScope] = useState("ALL");
  const [dbFull, setDbFull] = useState("");
  const [zipName, setZipName] = useState("respaldo-" + nowTs());
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");

  const dbItems = useMemo(() => {
    const env = (import.meta.env.VITE_BACKUP_DBS || "")
      .split(",")
      .map((s) => s.trim())
      .filter(Boolean);
    const fallback = [
      "andinatrading_auth",
      "andinatrading_catalogos",
      "andinatrading_inversionistas",
      "andinatrading_comisionistas",
      "andinatrading_contratos",
      "andinatrading_cuentas",
      "andinatrading_ordenes",
      "andinatrading_auditoria",
    ];
    const list = env.length > 0 ? env : fallback;
    return list.map((full) => ({
      full,
      short: full.replace(/^andinatrading_/, ""),
    }));
  }, []);

  const load = async () => {
    setLoading(true);
    setErr("");
    try {
      const files = await listarRespaldoArchivos();
      const onlyAllowed = files.filter(
        (f) => f.endsWith(".sql") || f.endsWith(".zip")
      );
      onlyAllowed.sort((a, b) => b.localeCompare(a));
      setArchivos(onlyAllowed);
      const m = {};
      for (const f of onlyAllowed) m[f] = !!sel[f];
      setSel(m);
    } catch (e) {
      setErr(
        e?.response?.data?.message || "No se pudo cargar la lista de archivos."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  useEffect(() => {
    if (!err && !ok) return;
    const t = setTimeout(() => {
      setErr("");
      setOk("");
    }, 6000);
    return () => clearTimeout(t);
  }, [err, ok]);

  const toggleAll = (v) => {
    const m = {};
    for (const f of archivos) m[f] = v;
    setSel(m);
  };

  const onRun = async () => {
    setLoading(true);
    setErr("");
    try {
      if (scope === "ALL") {
        const list = await ejecutarBackupTodo();
        setOk("Backups generados: " + list.length);
      } else {
        if (!dbFull.trim()) {
          setErr("Selecciona la base de datos.");
          setLoading(false);
          return;
        }
        const res = await ejecutarBackupDb(dbFull.trim());
        const created = res?.data || "";
        setOk(created ? "Backup generado: " + created : "Backup generado.");
      }
      await load();
    } catch (e) {
      const msgAll = "No se pudieron crear los backups.";
      const msgOne = "No se pudo crear el backup.";
      setErr(e?.response?.data?.message || (scope === "ALL" ? msgAll : msgOne));
    } finally {
      setLoading(false);
    }
  };

  const onZip = async () => {
    const files = Object.entries(sel)
      .filter(([, v]) => v)
      .map(([k]) => k);
    if (files.length === 0) {
      setErr("Selecciona al menos un archivo para comprimir.");
      return;
    }
    setLoading(true);
    setErr("");
    try {
      const name =
        zipName && zipName.trim().length > 0
          ? zipName.trim()
          : "respaldo-" + nowTs();
      const res = await crearZip(files);
      const created = res?.data || name + ".zip";
      setOk("ZIP creado: " + created);
      await load();
    } catch (e) {
      setErr(e?.response?.data?.message || "No se pudo crear el ZIP.");
    } finally {
      setLoading(false);
    }
  };

  const onDownload = async (filename) => {
    setLoading(true);
    setErr("");
    try {
      const { data } = await descargarArchivo(filename);
      const url = window.URL.createObjectURL(new Blob([data]));
      const a = document.createElement("a");
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (e) {
      setErr(e?.response?.data?.message || "No se pudo descargar el archivo.");
    } finally {
      setLoading(false);
    }
  };

  const allChecked = useMemo(() => {
    if (archivos.length === 0) return false;
    return archivos.every((f) => sel[f]);
  }, [archivos, sel]);

  return (
    <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-5xl mx-auto px-4 md:px-6 py-8">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Respaldo
        </h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4"></div>

        <div className="mb-4 space-y-2">
          <ErrorAlert message={err} onClose={() => setErr("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
          <section className="lg:col-span-3 bg-slate-900/50 border border-slate-800 rounded-2xl p-5 shadow-xl ring-1 ring-white/5 backdrop-blur">
            <div className="grid gap-4 md:grid-cols-3">
              <div className="md:col-span-2">
                <span className="block text-xs font-medium text-slate-300 mb-2">
                  Alcance
                </span>
                <div className="flex items-center gap-6 text-sm">
                  <label className="inline-flex items-center gap-2">
                    <input
                      type="radio"
                      name="scope"
                      value="ALL"
                      checked={scope === "ALL"}
                      onChange={(e) => setScope(e.target.value)}
                    />
                    <span className="text-slate-200">Todas las bases</span>
                  </label>
                  <label className="inline-flex items-center gap-2">
                    <input
                      type="radio"
                      name="scope"
                      value="ONE"
                      checked={scope === "ONE"}
                      onChange={(e) => setScope(e.target.value)}
                    />
                    <span className="text-slate-200">Una específica</span>
                  </label>
                </div>
              </div>

              <div className="md:col-span-1">
                <label className="block text-xs font-medium text-slate-300 mb-2">
                  Base de datos
                </label>
                <select
                  className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition disabled:opacity-60"
                  value={dbFull}
                  onChange={(e) => setDbFull(e.target.value)}
                  disabled={scope !== "ONE"}
                >
                  <option value="">
                    {scope === "ONE" ? "Selecciona…" : "No aplica"}
                  </option>
                  {dbItems.map((d) => (
                    <option key={d.full} value={d.full}>
                      {d.short}
                    </option>
                  ))}
                </select>
              </div>

              <div className="md:col-span-3 flex flex-col sm:flex-row gap-3">
                <button
                  onClick={onRun}
                  disabled={loading || (scope === "ONE" && !dbFull)}
                  className="inline-flex items-center justify-center rounded-lg px-5 py-2.5 bg-emerald-500/90 hover:bg-emerald-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
                >
                  Ejecutar
                </button>
                <button
                  onClick={load}
                  disabled={loading}
                  className="inline-flex items-center justify-center rounded-lg px-5 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-100 font-medium ring-1 ring-slate-700 disabled:opacity-60 transition"
                >
                  Recargar lista
                </button>
              </div>
            </div>
          </section>

          <section className="lg:col-span-3 bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur overflow-hidden">
            <div className="p-5 grid gap-4 md:grid-cols-3 items-end">
              <div className="md:col-span-2">
                <label className="block text-xs font-medium text-slate-300 mb-2">
                  Nombre ZIP
                </label>
                <input
                  className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                  value={zipName}
                  onChange={(e) => setZipName(e.target.value)}
                  placeholder="respaldo-YYYYMMDD-HHmmss"
                />
              </div>
              <div className="md:col-span-1">
                <button
                  onClick={onZip}
                  disabled={loading}
                  className="w-full inline-flex items-center justify-center rounded-lg px-5 py-2.5 bg-cyan-500/90 hover:bg-cyan-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
                >
                  Crear ZIP
                </button>
              </div>
            </div>

            <div className="overflow-x-auto">
              <table className="min-w-full text-sm">
                <thead className="bg-slate-900/70 sticky top-0 z-10">
                  <tr>
                    <th className="px-4 py-3">
                      <input
                        type="checkbox"
                        checked={allChecked}
                        onChange={(e) => toggleAll(e.target.checked)}
                      />
                    </th>
                    <th className="text-left px-4 py-3 text-slate-300 font-semibold uppercase tracking-wider text-[11px]">
                      Archivo
                    </th>
                    <th className="text-left px-4 py-3 text-slate-300 font-semibold uppercase tracking-wider text-[11px]">
                      Acciones
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/70">
                  {loading ? (
                    <tr>
                      <td className="px-4 py-6 text-slate-400" colSpan={3}>
                        Cargando...
                      </td>
                    </tr>
                  ) : archivos.length === 0 ? (
                    <tr>
                      <td className="px-4 py-6 text-slate-400" colSpan={3}>
                        Sin archivos.
                      </td>
                    </tr>
                  ) : (
                    archivos.map((f) => (
                      <tr key={f} className="hover:bg-slate-800/40 transition">
                        <td className="px-4 py-3">
                          <input
                            type="checkbox"
                            checked={!!sel[f]}
                            onChange={(e) =>
                              setSel((m) => ({ ...m, [f]: e.target.checked }))
                            }
                          />
                        </td>
                        <td className="px-4 py-3 text-slate-200">{f}</td>
                        <td className="px-4 py-3">
                          <button
                            onClick={() => onDownload(f)}
                            disabled={loading}
                            className="inline-flex items-center justify-center rounded-lg px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-100 ring-1 ring-slate-700 disabled:opacity-60 transition"
                          >
                            Descargar
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}
