import { useEffect, useState } from "react";
import { getMisOrdenes } from "../../api/serviceOrdenes";
import ErrorAlert from "../../components/alerts/ErrorAlert";

export default function MisOrdenesPage() {
  const [items, setItems] = useState([]);
  const [err, setErr] = useState("");

  const getRowKey = (o, idx) =>
    o?.dbId ??
    o?.id ??
    o?.alpacaOrderId ??
    `${o?.symbol || "?"}-${o?.creadoEn || idx}-${idx}`;

  useEffect(() => {
    getMisOrdenes()
      .then(setItems)
      .catch((e) => setErr(e?.message || "No se pudieron cargar las órdenes."));
  }, []);

  const shell = "min-h-[100dvh] bg-slate-950 text-slate-100";
  const wrap = "max-w-7xl mx-auto px-4 md:px-6 py-6 md:py-8";
  const panel =
    "bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur overflow-hidden";
  const headCell = "px-3 py-2 text-left font-medium text-slate-300";
  const cell = "px-3 py-2";
  const pill = (status = "") => {
    const s = String(status || "").toUpperCase();
    if (s === "PENDIENTE_AUTORIZACION")
      return "bg-yellow-500/15 text-yellow-300";
    if (s === "RECHAZADA") return "bg-red-500/15 text-red-300";
    return "bg-emerald-500/15 text-emerald-300";
  };

  return (
    <div className={shell}>
      <div className={wrap}>
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Mis órdenes
        </h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4"></div>

        <div className="max-w-5xl">
          <ErrorAlert message={err} onClose={() => setErr("")} />
        </div>

        <div className={`${panel} max-w-5xl`}>
          <div className="overflow-auto">
            <table className="min-w-full text-sm">
              <thead className="bg-slate-900/70 sticky top-0">
                <tr>
                  <th className={headCell}>Fecha</th>
                  <th className={headCell}>Símbolo</th>
                  <th className={`${headCell} text-right`}>Qty</th>
                  <th className={headCell}>Tipo</th>
                  <th className={headCell}>Side</th>
                  <th className={headCell}>TIF</th>
                  <th className={headCell}>Estado</th>
                  <th className={`${headCell} text-right`}>Neto</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800">
                {items.length === 0 ? (
                  <tr>
                    <td className="px-4 py-6 text-slate-400" colSpan="8">
                      Aún no tienes órdenes.
                    </td>
                  </tr>
                ) : (
                  items.map((o, idx) => (
                    <tr
                      key={getRowKey(o, idx)}
                      className="hover:bg-slate-900/40 transition-colors"
                    >
                      <td className={cell}>
                        {o.creadoEn
                          ? new Date(o.creadoEn).toLocaleString()
                          : "—"}
                      </td>
                      <td className={`${cell} font-mono text-slate-100`}>
                        {o.symbol}
                      </td>
                      <td className={`${cell} text-right`}>{o.qty}</td>
                      <td className={cell}>{o.orderType}</td>
                      <td className={cell}>{o.side}</td>
                      <td className={cell}>{o.timeInForce}</td>
                      <td className={cell}>
                        <span
                          className={`px-2 py-0.5 rounded-full text-xs font-medium ${pill(
                            o.status
                          )}`}
                        >
                          {o.status}
                        </span>
                      </td>
                      <td className={`${cell} text-right`}>
                        {o.netAmount ? Number(o.netAmount).toFixed(2) : "—"}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
