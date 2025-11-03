import { useEffect, useMemo, useState } from "react";
import {
  searchInstruments,
  crearOrden,
  getMarketStatus,
} from "../../api/serviceOrdenes";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

function useDebounced(value, delay = 400) {
  const [v, setV] = useState(value);
  useEffect(() => {
    const t = setTimeout(() => setV(value), delay);
    return () => clearTimeout(t);
  }, [value, delay]);
  return v;
}

export default function BuscarYCrearOrden() {
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState([]);
  const [selected, setSelected] = useState(null);

  const [qty, setQty] = useState("");
  const [side, setSide] = useState("buy");
  const [type, setType] = useState("market");
  const [timeInForce, setTimeInForce] = useState("day");
  const [limitPrice, setLimitPrice] = useState("");
  const [stopPrice, setStopPrice] = useState("");

  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");

  const [marketOpen, setMarketOpen] = useState(null);

  const debouncedQuery = useDebounced(query, 450);
  const needsLimit = useMemo(
    () => type === "limit" || type === "stop_limit",
    [type]
  );
  const needsStop = useMemo(
    () => type === "stop" || type === "stop_limit",
    [type]
  );

  useEffect(() => {
    if (!debouncedQuery || debouncedQuery.trim().length < 2) {
      setResults([]);
      return;
    }
    let cancelled = false;
    setLoading(true);
    searchInstruments(debouncedQuery.trim())
      .then((list) => {
        if (!cancelled) setResults(Array.isArray(list) ? list : []);
      })
      .catch((e) => setErr(e?.message || "No se pudo buscar instrumentos."))
      .finally(() => !cancelled && setLoading(false));
    return () => {
      cancelled = true;
    };
  }, [debouncedQuery]);

  useEffect(() => {
    getMarketStatus()
      .then((s) => setMarketOpen(!!s?.open))
      .catch(() => {});
  }, []);

  useEffect(() => {
    if (err) {
      const t = setTimeout(() => setErr(""), 7000);
      return () => clearTimeout(t);
    }
    if (ok) {
      const t = setTimeout(() => setOk(""), 7000);
      return () => clearTimeout(t);
    }
  }, [err, ok]);

  const resetForm = () => {
    setQty("");
    setSide("buy");
    setType("market");
    setTimeInForce("day");
    setLimitPrice("");
    setStopPrice("");
  };

  const onSelect = (stk) => {
    setSelected(stk);
    setErr("");
    setOk("");
    resetForm();
  };

  const onCreate = async (e) => {
    e.preventDefault();
    setErr("");
    setOk("");

    if (!selected?.symbol) {
      setErr("Selecciona una acción primero.");
      return;
    }
    const q = Number(qty);
    if (!Number.isFinite(q) || q <= 0) {
      setErr("Cantidad inválida.");
      return;
    }
    if (needsLimit && !String(limitPrice).trim()) {
      setErr("Debes ingresar 'limitPrice'.");
      return;
    }
    if (needsStop && !String(stopPrice).trim()) {
      setErr("Debes ingresar 'stopPrice'.");
      return;
    }

    try {
      const payload = {
        symbol: selected.symbol,
        qty: String(qty).trim(),
        side,
        type,
        timeInForce,
        limitPrice: needsLimit ? String(limitPrice).trim() : undefined,
        stopPrice: needsStop ? String(stopPrice).trim() : undefined,
      };

      const res = await crearOrden(payload);
      setOk(
        `Orden creada y enviada a tu comisionista. Estado: ${
          res?.status || "PENDIENTE_AUTORIZACION"
        }`
      );
      resetForm();
    } catch (e) {
      const msg = e?.message || "No se pudo crear la orden.";
      setErr(msg);
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
    "bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-2.5 rounded-lg font-medium transition-colors disabled:opacity-60";
  const badgeBase =
    "text-xs px-2.5 py-1 rounded-full border backdrop-blur ring-1 ring-white/10";

  return (
    <div className={shell}>
      <div className={wrap}>
        <header className="flex items-end justify-between mb-4">
          <div>
            <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
              Órdenes — Inversionista
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

        <section className={`${panel} space-y-4 mt-4`}>
          <div>
            <label className={label}>Buscar (símbolo o nombre)</label>
            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="AAPL, Tesla, Nasdaq…"
              className={input}
            />
          </div>

          <div className="max-h-80 overflow-auto rounded-xl border border-slate-800">
            {loading ? (
              <div className="p-3 text-sm text-slate-300">Buscando…</div>
            ) : results.length === 0 ? (
              <div className="p-3 text-sm text-slate-400">Sin resultados.</div>
            ) : (
              <table className="min-w-full text-sm">
                <thead className="bg-slate-900/60 sticky top-0">
                  <tr className="text-slate-300">
                    <th className="text-left p-3 font-medium">Símbolo</th>
                    <th className="text-left p-3 font-medium">Nombre</th>
                    <th className="text-right p-3 font-medium">Precio</th>
                    <th className="p-3"></th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800">
                  {results.map((r) => (
                    <tr key={r.symbol} className="hover:bg-slate-900/40">
                      <td className="p-3 font-mono text-slate-100">
                        {r.symbol}
                      </td>
                      <td className="p-3">{r.description}</td>
                      <td className="p-3 text-right">
                        {Number(r.currentPrice || 0).toFixed(2)}
                      </td>
                      <td className="p-3 text-right">
                        <button
                          className="px-3 py-1.5 text-xs bg-slate-800 hover:bg-slate-700 text-slate-100 rounded-lg border border-slate-700"
                          onClick={() => onSelect(r)}
                        >
                          Seleccionar
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </section>

        <section className={`${panel} mt-6`}>
          <h3 className="font-semibold mb-4 text-slate-200">Nueva orden</h3>

          {selected ? (
            <div className="mb-4 text-sm text-slate-300">
              <span className="font-mono font-semibold text-slate-100">
                {selected.symbol}
              </span>{" "}
              — {selected.description} — Último:{" "}
              <span className="font-semibold text-emerald-300">
                {Number(selected.currentPrice || 0).toFixed(2)}
              </span>
            </div>
          ) : (
            <div className="mb-4 text-sm text-slate-400">
              Selecciona primero un instrumento.
            </div>
          )}

          <form onSubmit={onCreate} className="grid gap-5 md:grid-cols-2">
            <div>
              <label className={label}>Cantidad</label>
              <input
                className={input}
                inputMode="numeric"
                value={qty}
                onChange={(e) => setQty(e.target.value)}
                disabled={!selected}
                required
              />
            </div>

            <div>
              <label className={label}>Lado</label>
              <select
                className={input}
                value={side}
                onChange={(e) => setSide(e.target.value)}
                disabled={!selected}
              >
                <option value="buy">Comprar</option>
              </select>
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
                Crear orden
              </button>
            </div>
          </form>
        </section>
      </div>
    </div>
  );
}
