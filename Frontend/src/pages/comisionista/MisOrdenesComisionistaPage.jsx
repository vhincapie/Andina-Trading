import { useEffect, useMemo, useState } from "react";
import {
  comisionistaMisOrdenes,
  comisionistaAprobarOrden,
  comisionistaRechazarOrden,
} from "../../api/serviceOrdenes";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

const getRowId = (o) => o?.dbId ?? null;

function prettyBackendError(e, id) {
  const raw =
    e?.message ||
    e?.response?.data?.message ||
    e?.response?.data ||
    `No se pudo procesar la orden #${id}.`;
  if (typeof raw === "string" && /wash trade|opposite side/i.test(raw)) {
    return (
      "Alpaca rechazó la operación: existe una orden abierta del lado opuesto " +
      "para este símbolo. Espera a que finalice (regla anti-wash trade)."
    );
  }
  return raw;
}

export default function ComiOrdenesPage() {
  const [statusFilter, setStatusFilter] = useState("PENDIENTE_AUTORIZACION");
  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState([]);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");

  const load = async () => {
    setErr("");
    setLoading(true);
    try {
      const data = await comisionistaMisOrdenes(
        statusFilter ? { status: statusFilter } : {}
      );
      setRows(Array.isArray(data) ? data : []);
    } catch (e) {
      setRows([]);
      setErr(e?.message || "No se pudieron cargar las órdenes.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [statusFilter]);

  useEffect(() => {
    if (err) {
      const t = setTimeout(() => setErr(""), 6000);
      return () => clearTimeout(t);
    }
    if (ok) {
      const t = setTimeout(() => setOk(""), 5000);
      return () => clearTimeout(t);
    }
  }, [err, ok]);

  const aprobar = async (row) => {
    setErr("");
    const id = getRowId(row);
    if (id == null) {
      setErr("No se reconoce el ID interno de la orden.");
      return;
    }
    try {
      await comisionistaAprobarOrden(id);
      setOk(`Orden #${id} aprobada y enviada a Alpaca.`);
      await load();
    } catch (e) {
      setErr(prettyBackendError(e, id));
    }
  };

  const rechazar = async (row) => {
    setErr("");
    const id = getRowId(row);
    if (id == null) {
      setErr("No se reconoce el ID interno de la orden.");
      return;
    }
    const motivo = window.prompt("Motivo del rechazo:");
    if (!motivo) return;
    try {
      await comisionistaRechazarOrden(id, { motivo });
      setOk(`Orden #${id} rechazada.`);
      await load();
    } catch (e) {
      setErr(prettyBackendError(e, id));
    }
  };

  const options = useMemo(
    () => [
      { v: "", t: "Todas" },
      { v: "PENDIENTE_AUTORIZACION", t: "Pendientes" },
      { v: "RECHAZADA", t: "Rechazadas" },
      { v: "PENDING_NEW", t: "Pending New" },
      { v: "ACCEPTED", t: "Accepted" },
      { v: "FILLED", t: "Filled" },
      { v: "CANCELED", t: "Canceled" },
      { v: "EXPIRED", t: "Expired" },
      { v: "DONE_FOR_DAY", t: "Done for day" },
    ],
    []
  );

  return (
    <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-6xl mx-auto px-4 md:px-6 py-8 space-y-4">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Órdenes — Comisionista
        </h2>
        <div className="mt-1 h-0.5 w-24 bg-emerald-400/80 rounded mb-1"></div>

        <div className="flex items-center justify-between gap-3">
          <div className="text-sm text-slate-400"></div>
          <select
            className="bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-2.5 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            {options.map((o) => (
              <option key={o.v} value={o.v}>
                {o.t}
              </option>
            ))}
          </select>
        </div>

        <ErrorAlert message={err} onClose={() => setErr("")} />
        <SuccessAlert message={ok} onClose={() => setOk("")} />

        <div className="rounded-2xl border border-slate-800 ring-1 ring-white/5 bg-slate-900/50 backdrop-blur overflow-hidden">
          {loading ? (
            <div className="p-4 text-sm text-slate-400">Cargando…</div>
          ) : rows.length === 0 ? (
            <div className="p-4 text-sm text-slate-400">Sin órdenes.</div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm">
                <thead className="bg-slate-900/70 sticky top-0 z-10">
                  <tr>
                    <th className="px-4 py-3 text-left text-slate-300 font-semibold uppercase tracking-wider text-[11px]">
                      ID
                    </th>
                    <th className="px-4 py-3 text-left text-slate-300 font-semibold uppercase tracking-wider text-[11px]">
                      Inversionista
                    </th>
                    <th className="px-4 py-3 text-left text-slate-300 font-semibold uppercase tracking-wider text-[11px]">
                      Símbolo
                    </th>
                    <th className="px-4 py-3 text-right text-slate-300 font-semibold uppercase tracking-wider text-[11px]">
                      Cantidad
                    </th>
                    <th className="px-4 py-3 text-left text-slate-300 font-semibold uppercase tracking-wider text-[11px]">
                      Tipo
                    </th>
                    <th className="px-4 py-3 text-left text-slate-300 font-semibold uppercase tracking-wider text-[11px]">
                      Lado
                    </th>
                    <th className="px-4 py-3 text-left text-slate-300 font-semibold uppercase tracking-wider text-[11px]">
                      Estado
                    </th>
                    <th className="px-4 py-3 text-center text-slate-300 font-semibold uppercase tracking-wider text-[11px]">
                      Acciones
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/70">
                  {rows.map((r) => {
                    const id = getRowId(r);
                    const canAct =
                      String(r?.status || "").toUpperCase() ===
                      "PENDIENTE_AUTORIZACION";
                    const invNombre =
                      r?.inversionistaNombre || r?.inversionistaCorreo || "—";
                    const invCorreo = r?.inversionistaCorreo
                      ? ` (${r.inversionistaCorreo})`
                      : "";
                    return (
                      <tr
                        key={id ?? `${r.symbol}-${Math.random()}`}
                        className="hover:bg-slate-800/40 transition"
                      >
                        <td className="px-4 py-3 text-slate-200">
                          {id ?? "—"}
                        </td>
                        <td className="px-4 py-3 text-slate-200">
                          <div className="leading-tight">
                            <span className="font-medium text-slate-100">
                              {invNombre}
                            </span>
                            <span className="text-slate-400">{invCorreo}</span>
                          </div>
                        </td>
                        <td className="px-4 py-3 text-slate-200">{r.symbol}</td>
                        <td className="px-4 py-3 text-right text-slate-200">
                          {r.qty}
                        </td>
                        <td className="px-4 py-3 text-slate-200">
                          {r.orderType}
                        </td>
                        <td className="px-4 py-3 text-slate-200">{r.side}</td>
                        <td className="px-4 py-3">
                          <span className="px-2 py-0.5 rounded-full text-xs font-medium bg-slate-700/40 text-slate-200">
                            {r.status}
                          </span>
                        </td>
                        <td className="px-4 py-3 text-center">
                          {canAct ? (
                            <div className="inline-flex gap-2">
                              <button
                                className="px-3 py-1.5 rounded-lg bg-emerald-500/90 hover:bg-emerald-500 text-white font-medium shadow-sm transition"
                                onClick={() => aprobar(r)}
                              >
                                Aprobar
                              </button>
                              <button
                                className="px-3 py-1.5 rounded-lg bg-rose-500/90 hover:bg-rose-500 text-white font-medium shadow-sm transition"
                                onClick={() => rechazar(r)}
                              >
                                Rechazar
                              </button>
                            </div>
                          ) : (
                            <span className="text-slate-400">—</span>
                          )}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
