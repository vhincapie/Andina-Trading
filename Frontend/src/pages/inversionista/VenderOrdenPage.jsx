import { useEffect, useMemo, useState } from "react";
import { getMisPosiciones, crearOrden } from "../../api/serviceOrdenes";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

const fmtQty = (q) => {
  const n = Number(q);
  return Number.isFinite(n) ? n.toFixed(6) : q;
};

export default function VenderOrdenPage() {
  const [pos, setPos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");

  const [selected, setSelected] = useState(null);
  const [qty, setQty] = useState("");
  const [type, setType] = useState("market");
  const [timeInForce, setTimeInForce] = useState("day");
  const [limitPrice, setLimitPrice] = useState("");
  const [stopPrice, setStopPrice] = useState("");

  const needsLimit = useMemo(
    () => type === "limit" || type === "stop_limit",
    [type]
  );
  const needsStop = useMemo(
    () => type === "stop" || type === "stop_limit",
    [type]
  );

  const load = async () => {
    setErr("");
    setLoading(true);
    try {
      const data = await getMisPosiciones();
      setPos(Array.isArray(data) ? data : []);
    } catch (e) {
      setErr(e?.message || "No se pudieron cargar tus posiciones.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  useEffect(() => {
    if (err) {
      const t = setTimeout(() => setErr(""), 6000);
      return () => clearTimeout(t);
    }
    if (ok) {
      const t = setTimeout(() => setOk(""), 6000);
      return () => clearTimeout(t);
    }
  }, [err, ok]);

  const onSelect = (p) => {
    setSelected(p);
    setQty("");
    setType("market");
    setTimeInForce("day");
    setLimitPrice("");
    setStopPrice("");
    setOk("");
    setErr("");
  };

  const onSell = async (e) => {
  e.preventDefault();
  setErr("");
  setOk("");

  if (!selected?.symbol) {
    setErr("Selecciona primero una posición.");
    return;
  }

  const maxQty = Number(selected?.qty || 0);
  const q = Number(qty);
  if (!Number.isFinite(q) || q <= 0) {
    setErr("Cantidad inválida.");
    return;
  }
  if (q > maxQty) {
    setErr(`No puedes vender más de ${fmtQty(selected.qty)} ${selected.symbol}.`);
    return;
  }
  if (needsLimit && !limitPrice) {
    setErr("Debes ingresar 'limit price'.");
    return;
  }
  if (needsStop && !stopPrice) {
    setErr("Debes ingresar 'stop price'.");
    return;
  }

  const payload = {
    symbol: selected.symbol,
    qty: String(q),
    side: "sell",
    type: type,
    time_in_force: (timeInForce || "day").toLowerCase(),
    ...(needsLimit ? { limit_price: String(limitPrice) } : {}),
    ...(needsStop ? { stop_price: String(stopPrice) } : {}),
  };

  console.log("Payload venta ->", payload);

  try {
    await crearOrden(payload);
    setOk(`Orden de venta enviada (símbolo ${selected.symbol}).`);
    setSelected(null);
    setQty("");
    setLimitPrice("");
    setStopPrice("");

  } catch (e) {
    setErr(e?.message || "No se pudo crear la orden de venta.");
  }
};


  return (
    <div className="max-w-5xl mx-auto space-y-5">
      <h2 className="text-2xl font-semibold">Vender — Posiciones</h2>

      <ErrorAlert message={err} onClose={() => setErr("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      {/* Lista de posiciones */}
      <section className="bg-white border rounded p-4">
        <h3 className="font-semibold mb-2">Tus posiciones</h3>
        {loading ? (
          <div className="text-sm text-gray-600">Cargando…</div>
        ) : pos.length === 0 ? (
          <div className="text-sm text-gray-600">No tienes posiciones abiertas.</div>
        ) : (
          <table className="min-w-full text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="p-2 text-left">Símbolo</th>
                <th className="p-2 text-right">Qty</th>
                <th className="p-2 text-right">Precio Prom.</th>
                <th className="p-2 text-right">Valor Mercado</th>
                <th className="p-2"></th>
              </tr>
            </thead>
            <tbody>
              {pos.map((p) => (
                <tr key={p.symbol} className="border-t">
                  <td className="p-2 font-mono">{p.symbol}</td>
                  <td className="p-2 text-right">{fmtQty(p.qty)}</td>
                  <td className="p-2 text-right">
                    {p.avgEntryPrice != null ? Number(p.avgEntryPrice).toFixed(2) : "—"}
                  </td>
                  <td className="p-2 text-right">
                    {p.marketValue != null ? Number(p.marketValue).toFixed(2) : "—"}
                  </td>
                  <td className="p-2 text-right">
                    <button
                      className="px-3 py-1 text-sm bg-blue-600 text-white rounded"
                      onClick={() => onSelect(p)}
                    >
                      Vender
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      {/* Formulario venta */}
      <section className="bg-white border rounded p-4">
        <h3 className="font-semibold mb-2">Nueva orden de venta</h3>

        {selected ? (
          <div className="mb-3 text-sm">
            <span className="font-mono font-semibold">{selected.symbol}</span>{" "}
            — Qty disponible:{" "}
            <strong>{fmtQty(selected.qty)}</strong> — Precio prom:{" "}
            <strong>
              {selected.avgEntryPrice != null
                ? Number(selected.avgEntryPrice).toFixed(2)
                : "—"}
            </strong>
          </div>
        ) : (
          <div className="mb-3 text-sm text-gray-600">
            Selecciona una posición de la lista para vender.
          </div>
        )}

        <form onSubmit={onSell} className="grid gap-3 md:grid-cols-2">
          <div>
            <label className="block text-sm mb-1">Cantidad a vender</label>
            <input
              className="border p-2 rounded w-full"
              inputMode="decimal"
              value={qty}
              onChange={(e) => setQty(e.target.value)}
              disabled={!selected}
              required
            />
          </div>

          <div>
            <label className="block text-sm mb-1">Tipo</label>
            <select
              className="border p-2 rounded w-full"
              value={type}
              onChange={(e) => setType(e.target.value)}
              disabled={!selected}
            >
              <option value="market">Market</option>
              <option value="limit">Limit</option>
              <option value="stop">Stop</option>
              <option value="stop_limit">Stop Limit</option>
            </select>
          </div>

          <div>
            <label className="block text-sm mb-1">Time in Force</label>
            <select
              className="border p-2 rounded w-full"
              value={timeInForce}
              onChange={(e) => setTimeInForce(e.target.value)}
              disabled={!selected}
            >
              <option value="day">DAY</option>
            </select>
          </div>

          {needsLimit && (
            <div>
              <label className="block text-sm mb-1">Limit Price</label>
              <input
                className="border p-2 rounded w-full"
                inputMode="decimal"
                value={limitPrice}
                onChange={(e) => setLimitPrice(e.target.value)}
                disabled={!selected}
                required
              />
            </div>
          )}

          {needsStop && (
            <div>
              <label className="block text-sm mb-1">Stop Price</label>
              <input
                className="border p-2 rounded w-full"
                inputMode="decimal"
                value={stopPrice}
                onChange={(e) => setStopPrice(e.target.value)}
                disabled={!selected}
                required
              />
            </div>
          )}

          <div className="md:col-span-2">
            <button
              type="submit"
              className="bg-green-600 text-white px-4 py-2 rounded disabled:opacity-60"
              disabled={!selected}
            >
              Enviar orden de venta (a comisionista)
            </button>
          </div>
        </form>
      </section>
    </div>
  );
}
