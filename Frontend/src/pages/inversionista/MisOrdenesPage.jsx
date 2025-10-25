import { useEffect, useState } from "react";
import { getMisOrdenes } from "../../api/serviceOrdenes";
import ErrorAlert from "../../components/alerts/ErrorAlert";

export default function MisOrdenesPage() {
  const [items, setItems] = useState([]);
  const [err, setErr] = useState("");

  useEffect(() => {
    getMisOrdenes()
      .then(setItems)
      .catch((e) => setErr(e?.message || "No se pudieron cargar las órdenes."));
  }, []);

  return (
    <div className="max-w-5xl mx-auto space-y-4">
      <h2 className="text-2xl font-semibold">Mis órdenes</h2>
      <ErrorAlert message={err} onClose={() => setErr("")} />

      <div className="bg-white border rounded overflow-auto">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="text-left p-2">Fecha</th>
              <th className="text-left p-2">Símbolo</th>
              <th className="text-right p-2">Qty</th>
              <th className="text-left p-2">Tipo</th>
              <th className="text-left p-2">Side</th>
              <th className="text-left p-2">TIF</th>
              <th className="text-left p-2">Estado</th>
              <th className="text-right p-2">Neto</th>
            </tr>
          </thead>
          <tbody>
            {items.length === 0 ? (
              <tr>
                <td className="p-3 text-gray-500" colSpan="8">
                  Aún no tienes órdenes.
                </td>
              </tr>
            ) : (
              items.map((o) => (
                <tr key={o.id} className="border-t">
                  <td className="p-2">
                    {o.creadoEn
                      ? new Date(o.creadoEn).toLocaleString()
                      : "—"}
                  </td>
                  <td className="p-2 font-mono">{o.symbol}</td>
                  <td className="p-2 text-right">{o.qty}</td>
                  <td className="p-2">{o.orderType}</td>
                  <td className="p-2">{o.side}</td>
                  <td className="p-2">{o.timeInForce}</td>
                  <td className="p-2">
                    <span
                      className={`px-2 py-0.5 rounded text-xs ${
                        o.status?.toUpperCase() === "PENDIENTE_AUTORIZACION"
                          ? "bg-yellow-100 text-yellow-800"
                          : o.status?.toUpperCase() === "RECHAZADA"
                          ? "bg-red-100 text-red-700"
                          : "bg-green-100 text-green-700"
                      }`}
                    >
                      {o.status}
                    </span>
                  </td>
                  <td className="p-2 text-right">
                    {o.netAmount ? Number(o.netAmount).toFixed(2) : "—"}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
