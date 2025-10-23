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

  return (
    <div className="max-w-5xl mx-auto space-y-5">
      <header className="flex items-center justify-between">
        <h2 className="text-2xl font-semibold">Órdenes — Inversionista</h2>
        <span
          className={`text-sm px-2 py-1 rounded ${
            marketOpen === null
              ? "bg-gray-200 text-gray-700"
              : marketOpen
              ? "bg-green-100 text-green-700"
              : "bg-yellow-100 text-yellow-800"
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

      {/* Buscador */}
      <section className="bg-white border rounded p-4 space-y-3">
        <label className="block text-sm mb-1">Buscar (símbolo o nombre)</label>
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="AAPL, Tesla, Nasdaq…"
          className="border p-2 rounded w-full"
        />

        {/* Resultados */}
        <div className="max-h-72 overflow-auto border rounded">
          {loading ? (
            <div className="p-3 text-sm text-gray-600">Buscando…</div>
          ) : results.length === 0 ? (
            <div className="p-3 text-sm text-gray-500">Sin resultados.</div>
          ) : (
            <table className="min-w-full text-sm">
              <thead className="bg-gray-50">
                <tr>
                  <th className="text-left p-2">Símbolo</th>
                  <th className="text-left p-2">Nombre</th>
                  <th className="text-right p-2">Precio</th>
                  <th className="p-2"></th>
                </tr>
              </thead>
              <tbody>
                {results.map((r) => (
                  <tr key={r.symbol} className="border-t">
                    <td className="p-2 font-mono">{r.symbol}</td>
                    <td className="p-2">{r.description}</td>
                    <td className="p-2 text-right">
                      {Number(r.currentPrice || 0).toFixed(2)}
                    </td>
                    <td className="p-2 text-right">
                      <button
                        className="px-3 py-1 text-sm bg-blue-600 text-white rounded"
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

      {/* Formulario de orden */}
      <section className="bg-white border rounded p-4">
        <h3 className="font-semibold mb-2">Nueva orden</h3>

        {selected ? (
          <div className="mb-3 text-sm">
            <span className="font-mono font-semibold">{selected.symbol}</span>{" "}
            — {selected.description} — Último:{" "}
            <span className="font-semibold">
              {Number(selected.currentPrice || 0).toFixed(2)}
            </span>
          </div>
        ) : (
          <div className="mb-3 text-sm text-gray-600">
            Selecciona primero un instrumento.
          </div>
        )}

        <form onSubmit={onCreate} className="grid gap-3 md:grid-cols-2">
          <div>
            <label className="block text-sm mb-1">Cantidad</label>
            <input
              className="border p-2 rounded w-full"
              inputMode="numeric"
              value={qty}
              onChange={(e) => setQty(e.target.value)}
              disabled={!selected}
              required
            />
          </div>

          <div>
            <label className="block text-sm mb-1">Lado</label>
            <select
              className="border p-2 rounded w-full"
              value={side}
              onChange={(e) => setSide(e.target.value)}
              disabled={!selected}
            >
              <option value="buy">Comprar</option>
            </select>
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
              Crear orden (enviar a comisionista)
            </button>
          </div>
        </form>
      </section>
    </div>
  );
}
