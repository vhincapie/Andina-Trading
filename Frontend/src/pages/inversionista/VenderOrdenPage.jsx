import { useEffect, useMemo, useState } from "react";
import {
  getMisPosiciones,
  crearOrden,
  getMarketStatus,
} from "../../api/serviceOrdenes";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

const fmtQty = (q) => {
  const n = Number(q);
  return Number.isFinite(n) ? n.toFixed(6) : q;
};

const normalizePosition = (p = {}) => {
  const symbol = p.symbol ?? p.ticker ?? "";
  const qty = Number(p.qty ?? p.quantity ?? 0);
  const avgEntryPriceRaw =
    p.avgEntryPrice ?? p.avg_entry_price ?? p.averagePrice;
  const marketValueRaw = p.marketValue ?? p.market_value ?? p.marketVal;
  return {
    symbol,
    qty,
    avgEntryPrice: avgEntryPriceRaw != null ? Number(avgEntryPriceRaw) : null,
    marketValue: marketValueRaw != null ? Number(marketValueRaw) : null,
  };
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

  const [marketOpen, setMarketOpen] = useState(null);

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
      const list = Array.isArray(data) ? data.map(normalizePosition) : [];
      setPos(list);
    } catch (e) {
      setErr(e?.message || "No se pudieron cargar tus posiciones.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    getMarketStatus()
      .then((s) => setMarketOpen(!!s?.open))
      .catch(() => setMarketOpen(null));
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
      setErr(
        `No puedes vender más de ${fmtQty(selected.qty)} ${selected.symbol}.`
      );
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
      type,
      time_in_force: (timeInForce || "day").toLowerCase(),
      ...(needsLimit ? { limit_price: String(limitPrice) } : {}),
      ...(needsStop ? { stop_price: String(stopPrice) } : {}),
    };

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

  const shell = "min-h-[100dvh] bg-slate-950 text-slate-100";
  const wrap = "max-w-7xl mx-auto px-4 md:px-6 py-6 md:py-8";
  const panel =
    "bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur p-6 md:p-7";
  const label = "block text-xs uppercase tracking-wide text-slate-400 mb-1.5";
  const input =
    "w-full bg-slate-900/60 border border-slate-700/70 rounded-lg px-3 py-2 text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-400/60 focus:border-emerald-400/40";
  const btnPrimary =
    "bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-2.5 rounded-lg transition-colors disabled:opacity-60";
  const btnGhost =
    "px-3 py-1.5 text-xs bg-slate-800 hover:bg-slate-700 text-slate-100 rounded-lg border border-slate-700";
  const badgeBase =
    "text-xs px-2.5 py-1 rounded-full border backdrop-blur ring-1 ring-white/10";

  return (
    <div className={shell}>
      <div className={wrap}>
        <header className="flex items-end justify-between mb-4">
          <div>
            <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
              Vender — Posiciones
            </h2>
            <div className="mt-2 h-0.5 w-24 bg-emerald-400/80 rounded"></div>
          </div>

          <span
            className={`${badgeBase} ${
              marketOpen === null
                ? "bg-slate-800/60 border-slate-700 text-slate-300"
                : marketOpen
                ? "bg-emerald-400/10 border-emerald-500/30 text-emerald-300"
                : "bg-amber-400/10 border-amber-500/30 text-amber-300"
            }`}
          >
            {marketOpen === null
              ? "Consultando mercado…"
              : marketOpen
              ? "Mercado abierto"
              : "Mercado cerrado"}
          </span>
        </header>

        <ErrorAlert message={err} onClose={() => setErr("")} />
        <SuccessAlert message={ok} onClose={() => setOk("")} />

        <section className={`${panel} mb-6`}>
          <h3 className="font-semibold mb-3 text-slate-200">Tus posiciones</h3>
          {loading ? (
            <div className="text-sm text-slate-300">Cargando…</div>
          ) : pos.length === 0 ? (
            <div className="text-sm text-slate-300">
              No tienes posiciones abiertas.
            </div>
          ) : (
            <div className="overflow-auto rounded-xl border border-slate-800">
              <table className="min-w-full text-sm">
                <thead className="bg-slate-900/60 sticky top-0">
                  <tr className="text-slate-300">
                    <th className="p-3 text-left font-medium">Símbolo</th>
                    <th className="p-3 text-right font-medium">Qty</th>
                    <th className="p-3 text-right font-medium">Precio Prom.</th>
                    <th className="p-3 text-right font-medium">
                      Valor Mercado
                    </th>
                    <th className="p-3"></th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {pos.map((p) => (
                    <tr key={p.symbol} className="hover:bg-slate-900/40">
                      <td className="p-3 font-mono text-slate-100">
                        {p.symbol}
                      </td>
                      <td className="p-3 text-right">{fmtQty(p.qty)}</td>
                      <td className="p-3 text-right">
                        {p.avgEntryPrice != null
                          ? Number(p.avgEntryPrice).toFixed(2)
                          : "—"}
                      </td>
                      <td className="p-3 text-right">
                        {p.marketValue != null
                          ? Number(p.marketValue).toFixed(2)
                          : "—"}
                      </td>
                      <td className="p-3 text-right">
                        <button
                          className={btnGhost}
                          onClick={() => onSelect(p)}
                        >
                          Vender
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <section className={panel}>
          <h3 className="font-semibold mb-3 text-slate-200">
            Nueva orden de venta
          </h3>

          {selected ? (
            <div className="mb-4 text-sm text-slate-300">
              <span className="font-mono font-semibold text-slate-100">
                {selected.symbol}
              </span>{" "}
              — Qty disponible:{" "}
              <strong className="text-slate-100">{fmtQty(selected.qty)}</strong>{" "}
              — Precio prom:{" "}
              <strong className="text-slate-100">
                {selected.avgEntryPrice != null
                  ? Number(selected.avgEntryPrice).toFixed(2)
                  : "—"}
              </strong>
            </div>
          ) : (
            <div className="mb-4 text-sm text-slate-400">
              Selecciona una posición de la lista para vender.
            </div>
          )}

          <form onSubmit={onSell} className="grid gap-5 md:grid-cols-2">
            <div>
              <label className={label}>Cantidad a vender</label>
              <input
                className={input}
                inputMode="decimal"
                value={qty}
                onChange={(e) => setQty(e.target.value)}
                disabled={!selected}
                required
              />
            </div>

            <div>
              <label className={label}>Tipo</label>
              <select
                className={input}
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
              <label className={label}>Time in Force</label>
              <select
                className={input}
                value={timeInForce}
                onChange={(e) => setTimeInForce(e.target.value)}
                disabled={!selected}
              >
                <option value="day">DAY</option>
              </select>
            </div>

            {needsLimit && (
              <div>
                <label className={label}>Limit Price</label>
                <input
                  className={input}
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
                <label className={label}>Stop Price</label>
                <input
                  className={input}
                  inputMode="decimal"
                  value={stopPrice}
                  onChange={(e) => setStopPrice(e.target.value)}
                  disabled={!selected}
                  required
                />
              </div>
            )}

            <div className="md:col-span-2 pt-2">
              <button type="submit" className={btnPrimary} disabled={!selected}>
                Enviar orden de venta
              </button>
            </div>
          </form>
        </section>
      </div>
    </div>
  );
}
