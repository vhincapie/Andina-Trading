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
    <div className="max-w-6xl mx-auto space-y-4">
      <header className="flex items-center justify-between">
        <h2 className="text-2xl font-semibold">Órdenes — Comisionista</h2>
        <select
          className="border p-2 rounded"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
        >
          {options.map((o) => (
            <option key={o.v} value={o.v}>
              {o.t}
            </option>
          ))}
        </select>
      </header>

      <ErrorAlert message={err} onClose={() => setErr("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      <div className="bg-white border rounded overflow-auto">
        {loading ? (
          <div className="p-4 text-sm text-gray-600">Cargando…</div>
        ) : rows.length === 0 ? (
          <div className="p-4 text-sm text-gray-600">Sin órdenes.</div>
        ) : (
          <table className="min-w-full text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="p-2 text-left">ID</th>
                <th className="p-2 text-left">Inversionista</th>
                <th className="p-2 text-left">Símbolo</th>
                <th className="p-2 text-right">Cantidad</th>
                <th className="p-2 text-left">Tipo</th>
                <th className="p-2 text-left">Lado</th>
                <th className="p-2 text-left">Estado</th>
                <th className="p-2 text-center">Acciones</th>
              </tr>
            </thead>
            <tbody>
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
                  <tr key={id ?? `${r.symbol}-${Math.random()}`} className="border-t">
                    <td className="p-2">{id ?? "—"}</td>
                    <td className="p-2">
                      <div className="leading-tight">
                        <span className="font-medium">{invNombre}</span>
                        <span className="text-gray-500">{invCorreo}</span>
                      </div>
                    </td>
                    <td className="p-2">{r.symbol}</td>
                    <td className="p-2 text-right">{r.qty}</td>
                    <td className="p-2">{r.orderType}</td>
                    <td className="p-2">{r.side}</td>
                    <td className="p-2">{r.status}</td>
                    <td className="p-2 text-center">
                      {canAct ? (
                        <div className="inline-flex gap-2">
                          <button
                            className="px-3 py-1 rounded bg-green-600 text-white"
                            onClick={() => aprobar(r)}
                          >
                            Aprobar
                          </button>
                          <button
                            className="px-3 py-1 rounded bg-red-600 text-white"
                            onClick={() => rechazar(r)}
                          >
                            Rechazar
                          </button>
                        </div>
                      ) : (
                        <span className="text-gray-400">—</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
