import { useEffect, useMemo, useState } from "react";
import { buscarLogs, exportCsvBackend } from "../../api/auditoriaService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

const parseInstant = (v) => {
  if (v === null || v === undefined || v === "") return null;
  if (typeof v === "number") {
    const ms = v > 1e12 ? v : v * 1000;
    const d = new Date(ms);
    return isNaN(d.getTime()) ? null : d;
  }
  if (typeof v === "string") {
    const s = v.trim();
    if (/^\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}:\d{2}$/.test(s)) {
      const d = new Date(s.replace(" ", "T"));
      return isNaN(d.getTime()) ? null : d;
    }
    const d = new Date(s);
    return isNaN(d.getTime()) ? null : d;
  }
  return null;
};

const fmtDate = (v) => {
  const d = parseInstant(v);
  if (!d) return "—";
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(
    d.getHours()
  )}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
};

const getFechaRaw = (r) =>
  r.createdAt ??
  r.created_at ??
  r.created ??
  r.createdAtUtc ??
  r.created_at_utc ??
  r.createdAtIso ??
  r.created_at_iso ??
  r.createdAtMs ??
  r.created_at_ms ??
  r.createdAtMillis ??
  r.created_at_millis ??
  null;

const getDetalleRaw = (r) =>
  r.details ?? r.detailsJson ?? r.details_json ?? null;

const stringifyDetalle = (val) => {
  if (val === null || val === undefined) return "{}";
  if (typeof val === "string") return val;
  try {
    return JSON.stringify(val);
  } catch {
    return String(val);
  }
};

const toLocalInputValue = (date) => {
  const d = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return d.toISOString().slice(0, 16);
};

export default function AuditoriaLogsPage() {
  const [eventCode, setEventCode] = useState("");
  const [userId, setUserId] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [rows, setRows] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");
  const [sort, setSort] = useState({ key: "createdAt", dir: "desc" });

  const nowMax = useMemo(() => toLocalInputValue(new Date()), []);
  const toMin = from || undefined;

  const buildParams = () => {
    const params = { page, size };
    if (eventCode.trim()) params.eventCode = eventCode.trim();
    if (userId.trim()) params.userId = userId.trim();
    if (from) params.from = new Date(from).toISOString();
    if (to) params.to = new Date(to).toISOString();
    return params;
  };

  const load = async (p = page) => {
    setLoading(true);
    setErr("");
    try {
      const data = await buscarLogs({ ...buildParams(), page: p });
      const content = data?.content ?? [];
      setRows(content);
      setTotalPages(data?.totalPages ?? 0);
      setTotalElements(data?.totalElements ?? 0);
      setPage(data?.number ?? p);
    } catch (e) {
      setErr(
        e?.response?.data?.message ||
          "No se pudieron cargar los logs de auditoría."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [page, size]);

  useEffect(() => {
    if (!err && !ok) return;
    const t = setTimeout(() => {
      setErr("");
      setOk("");
    }, 6000);
    return () => clearTimeout(t);
  }, [err, ok]);

  const onBuscar = async (e) => {
    e?.preventDefault?.();
    setPage(0);
    await load(0);
  };

  const onExport = async () => {
    if (!from || !to) {
      setErr("Debes seleccionar 'Desde' y 'Hasta' para exportar el CSV.");
      return;
    }
    if (new Date(from) > new Date(to)) {
      setErr(
        "El rango de fechas es inválido: 'Desde' no puede ser mayor que 'Hasta'."
      );
      return;
    }
    if (new Date(from) > new Date() || new Date(to) > new Date()) {
      setErr("No puedes seleccionar fechas futuras.");
      return;
    }
    try {
      await exportCsvBackend({ eventCode, userId, from, to });
    } catch (e) {
      setErr(e?.response?.data?.message || "No se pudo exportar el CSV.");
    }
  };

  const sortedRows = useMemo(() => {
    const key = sort.key;
    const dir = sort.dir === "asc" ? 1 : -1;
    const getVal = (r) => {
      if (key === "createdAt") {
        const v = getFechaRaw(r);
        const d = parseInstant(v);
        return d ? d.getTime() : 0;
      }
      const snake = key.replace(/([A-Z])/g, "_$1").toLowerCase();
      return String(r[key] ?? r[snake] ?? "").toUpperCase();
    };
    return [...rows].sort((a, b) => {
      const va = getVal(a);
      const vb = getVal(b);
      if (va < vb) return -1 * dir;
      if (va > vb) return 1 * dir;
      return 0;
    });
  }, [rows, sort]);

  const toggleSort = (key) => {
    setSort((s) =>
      s.key === key
        ? { key, dir: s.dir === "asc" ? "desc" : "asc" }
        : { key, dir: "asc" }
    );
  };

  const goFirst = async () => {
    if (page === 0) return;
    setPage(0);
    await load(0);
  };

  const goPrev = async () => {
    const p = Math.max(0, page - 1);
    if (p === page) return;
    setPage(p);
    await load(p);
  };

  const goNext = async () => {
    const p = Math.min(Math.max(0, totalPages - 1), page + 1);
    if (p === page) return;
    setPage(p);
    await load(p);
  };

  const goLast = async () => {
    const last = Math.max(0, (totalPages || 1) - 1);
    if (page === last) return;
    setPage(last);
    await load(last);
  };

  return (
    <div className="min-h[100dvh] min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-5xl mx-auto px-4 md:px-6 py-8">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Auditoría · Logs
        </h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4"></div>

        <form
          onSubmit={onBuscar}
          className="bg-slate-900/50 border border-slate-800 rounded-2xl p-6 shadow-xl ring-1 ring-white/5 backdrop-blur"
        >
          <div className="grid gap-4 md:gap-6 md:grid-cols-6">
            <div className="md:col-span-1">
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Event Code
              </label>
              <input
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                value={eventCode}
                onChange={(e) => setEventCode(e.target.value)}
                placeholder="AUTH_LOGIN_SUCCESS"
              />
            </div>
            <div className="md:col-span-1">
              <label className="block text-xs font-medium text-slate-300 mb-1">
                User ID
              </label>
              <input
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                value={userId}
                onChange={(e) => setUserId(e.target.value)}
                placeholder="correo o id"
              />
            </div>
            <div className="md:col-span-2">
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Desde
              </label>
              <input
                type="datetime-local"
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                value={from}
                max={nowMax}
                onChange={(e) => {
                  const v = e.target.value;
                  setFrom(v);
                  if (to && new Date(v) > new Date(to)) setTo(v);
                }}
              />
            </div>
            <div className="md:col-span-2">
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Hasta
              </label>
              <input
                type="datetime-local"
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                value={to}
                min={toMin}
                max={nowMax}
                onChange={(e) => setTo(e.target.value)}
              />
            </div>
            <div className="md:col-span-6 flex flex-col md:flex-row gap-3 md:items-end">
              <div className="flex gap-2">
                <button
                  type="submit"
                  className="inline-flex items-center justify-center rounded-lg px-4 py-2.5 bg-emerald-500/90 hover:bg-emerald-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
                  disabled={loading}
                >
                  {loading ? "Buscando..." : "Buscar"}
                </button>
                <button
                  type="button"
                  className="inline-flex items-center justify-center rounded-lg px-4 py-2.5 bg-cyan-500/90 hover:bg-cyan-500 text-white font-medium shadow-sm disabled:opacity-60 transition"
                  onClick={onExport}
                  disabled={loading}
                >
                  Exportar CSV
                </button>
              </div>
              <div className="md:ml-auto flex items-center gap-3">
                <label className="text-xs text-slate-300">Tamaño página</label>
                <select
                  className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-2.5 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                  value={size}
                  onChange={(e) => {
                    setSize(Number(e.target.value));
                    setPage(0);
                  }}
                >
                  {[10, 20, 50, 100].map((n) => (
                    <option key={n} value={n}>
                      {n}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>
        </form>

        <div className="max-w-5xl">
          <ErrorAlert message={err} onClose={() => setErr("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />
        </div>

        <div className="mt-4 bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm">
              <thead className="bg-slate-900/70 sticky top-0 z-10">
                <tr>
                  {[
                    { key: "createdAt", label: "Fecha" },
                    { key: "eventCode", label: "Event Code" },
                    { key: "userId", label: "User ID" },
                    { key: "username", label: "Username" },
                    { key: "ipAddress", label: "IP" },
                    { key: "resource", label: "Recurso" },
                    { key: "action", label: "Acción" },
                    { key: "details", label: "Detalle" },
                  ].map((col) => (
                    <th
                      key={col.key}
                      className="text-left px-4 py-3 text-slate-300 font-semibold uppercase tracking-wider text-[11px] cursor-pointer select-none"
                      onClick={() => toggleSort(col.key)}
                      title="Ordenar"
                    >
                      <div className="inline-flex items-center gap-2">
                        <span>{col.label}</span>
                        {sort.key === col.key && (
                          <span className="text-slate-400">
                            {sort.dir === "asc" ? "▲" : "▼"}
                          </span>
                        )}
                      </div>
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/70">
                {loading ? (
                  <tr>
                    <td className="px-4 py-6 text-slate-400" colSpan={8}>
                      Cargando...
                    </td>
                  </tr>
                ) : sortedRows.length === 0 ? (
                  <tr>
                    <td className="px-4 py-6 text-slate-400" colSpan={8}>
                      Sin resultados.
                    </td>
                  </tr>
                ) : (
                  sortedRows.map((r, i) => {
                    const fechaRaw = getFechaRaw(r);
                    const detalleStr = stringifyDetalle(getDetalleRaw(r));
                    return (
                      <tr
                        key={(r.id ?? r._id ?? i) + "-" + (fechaRaw ?? i)}
                        className="hover:bg-slate-800/40 transition"
                      >
                        <td className="px-4 py-3 whitespace-nowrap font-mono text-slate-200">
                          {fmtDate(fechaRaw)}
                        </td>
                        <td className="px-4 py-3 text-slate-200">
                          {r.eventCode ?? r.event_code ?? ""}
                        </td>
                        <td className="px-4 py-3 text-slate-200">
                          {r.userId ?? r.user_id ?? ""}
                        </td>
                        <td className="px-4 py-3 text-slate-200">
                          {r.username ?? ""}
                        </td>
                        <td className="px-4 py-3 text-slate-200">
                          {r.ipAddress ?? r.ip ?? ""}
                        </td>
                        <td className="px-4 py-3 text-slate-200">
                          {r.resource ?? ""}
                        </td>
                        <td className="px-4 py-3 text-slate-200">
                          {r.action ?? ""}
                        </td>
                        <td
                          className="px-4 py-3 max-w-[520px] truncate text-slate-300"
                          title={detalleStr}
                        >
                          {detalleStr}
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-3 p-4 text-sm">
            <div className="text-slate-300">
              Página <strong className="text-slate-100">{page + 1}</strong> de{" "}
              <strong className="text-slate-100">{totalPages}</strong> ·{" "}
              <span className="text-slate-400">{totalElements} registros</span>
            </div>
            <div className="flex gap-2">
              <button
                onClick={goFirst}
                className="px-3 py-1.5 rounded-lg ring-1 ring-slate-800 text-slate-200 hover:bg-slate-800/60 disabled:opacity-50 transition"
                disabled={page <= 0 || loading}
              >
                « Primero
              </button>
              <button
                onClick={goPrev}
                className="px-3 py-1.5 rounded-lg ring-1 ring-slate-800 text-slate-200 hover:bg-slate-800/60 disabled:opacity-50 transition"
                disabled={page <= 0 || loading}
              >
                ‹ Anterior
              </button>
              <button
                onClick={goNext}
                className="px-3 py-1.5 rounded-lg ring-1 ring-slate-800 text-slate-200 hover:bg-slate-800/60 disabled:opacity-50 transition"
                disabled={page >= totalPages - 1 || loading}
              >
                Siguiente ›
              </button>
              <button
                onClick={goLast}
                className="px-3 py-1.5 rounded-lg ring-1 ring-slate-800 text-slate-200 hover:bg-slate-800/60 disabled:opacity-50 transition"
                disabled={page >= totalPages - 1 || loading}
              >
                Último »
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
