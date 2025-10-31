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
    <div className="space-y-4">
      <h2 className="text-2xl font-semibold">Respaldo</h2>

      <ErrorAlert message={err} onClose={() => setErr("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      <div className="bg-white border rounded p-4 space-y-3">
        <div className="grid gap-3 md:grid-cols-3">
          <div className="md:col-span-2">
            <span className="block text-sm mb-1">Alcance</span>
            <div className="flex items-center gap-4">
              <label className="inline-flex items-center gap-2">
                <input
                  type="radio"
                  name="scope"
                  value="ALL"
                  checked={scope === "ALL"}
                  onChange={(e) => setScope(e.target.value)}
                />
                <span>Todas las bases</span>
              </label>
              <label className="inline-flex items-center gap-2">
                <input
                  type="radio"
                  name="scope"
                  value="ONE"
                  checked={scope === "ONE"}
                  onChange={(e) => setScope(e.target.value)}
                />
                <span>Una específica</span>
              </label>
            </div>
          </div>

          <div className="md:col-span-1">
            <label className="block text-sm mb-1">Base de datos</label>
            <select
              className="border rounded p-2 w-full"
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

          <div className="md:col-span-3 flex items-end gap-2">
            <button
              onClick={onRun}
              disabled={loading || (scope === "ONE" && !dbFull)}
              className="bg-blue-600 text-white px-3 py-2 rounded disabled:opacity-60"
            >
              Ejecutar
            </button>
            <button
              onClick={load}
              disabled={loading}
              className="bg-gray-200 text-gray-800 px-3 py-2 rounded disabled:opacity-60"
            >
              Recargar lista
            </button>
          </div>
        </div>
      </div>

      <div className="bg-white border rounded">
        <div className="p-3 flex gap-2 items-end">
          <div className="flex-1">
            <label className="block text-sm mb-1">Nombre ZIP</label>
            <input
              className="border rounded p-2 w-full"
              value={zipName}
              onChange={(e) => setZipName(e.target.value)}
              placeholder="respaldo-YYYYMMDD-HHmmss"
            />
          </div>
          <button
            onClick={onZip}
            disabled={loading}
            className="bg-emerald-600 text-white px-3 py-2 rounded disabled:opacity-60"
          >
            Crear ZIP
          </button>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-gray-100">
              <tr>
                <th className="px-3 py-2">
                  <input
                    type="checkbox"
                    checked={allChecked}
                    onChange={(e) => toggleAll(e.target.checked)}
                  />
                </th>
                <th className="text-left px-3 py-2">Archivo</th>
                <th className="text-left px-3 py-2">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td className="px-3 py-3 text-gray-600" colSpan={3}>
                    Cargando...
                  </td>
                </tr>
              ) : archivos.length === 0 ? (
                <tr>
                  <td className="px-3 py-3 text-gray-600" colSpan={3}>
                    Sin archivos.
                  </td>
                </tr>
              ) : (
                archivos.map((f) => (
                  <tr key={f} className="border-t">
                    <td className="px-3 py-2">
                      <input
                        type="checkbox"
                        checked={!!sel[f]}
                        onChange={(e) =>
                          setSel((m) => ({ ...m, [f]: e.target.checked }))
                        }
                      />
                    </td>
                    <td className="px-3 py-2">{f}</td>
                    <td className="px-3 py-2">
                      <button
                        onClick={() => onDownload(f)}
                        disabled={loading}
                        className="px-3 py-1.5 rounded bg-gray-200 hover:bg-gray-300 text-gray-800"
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
      </div>
    </div>
  );
}
