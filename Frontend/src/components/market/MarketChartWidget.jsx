import { useEffect, useMemo, useState } from "react";
import { getCandles } from "../../api/marketService";
import CandlesChart from "./CandlesChart";
import ErrorAlert from "../alerts/ErrorAlert";

export default function MarketChartWidget({
  symbols = "AAPL,MSFT",
  timeframe = "1Day",
  height = 360,
}) {
  const parsedSymbols = useMemo(
    () =>
      String(symbols)
        .split(",")
        .map((s) => s.trim())
        .filter(Boolean),
    [symbols]
  );
  const [selected, setSelected] = useState(parsedSymbols[0] || "");
  const [dataBySymbol, setDataBySymbol] = useState({});
  const [err, setErr] = useState("");

  const load = async () => {
    setErr("");
    try {
      const end = new Date().toISOString();
      const data = await getCandles({
        symbols: parsedSymbols.join(","),
        timeframe,
        end,
        lastDays: 120,
      });
      const norm = {};
      for (const k of Object.keys(data || {})) {
        norm[k] = (data[k] || []).map((b) => ({
          time: b.time || b.t,
          o: b.o ?? b.open,
          h: b.h ?? b.high,
          l: b.l ?? b.low,
          c: b.c ?? b.close,
          v: b.v ?? b.volume,
        }));
      }
      setDataBySymbol(norm);
    } catch (e) {
      const m =
        e?.response?.data?.message ||
        e?.message ||
        "No se pudo cargar el mercado.";
      setErr(m);
    }
  };

  useEffect(() => {
    setSelected(parsedSymbols[0] || "");
  }, [parsedSymbols]);

  useEffect(() => {
    load();
    const t = setInterval(load, 60000);
    return () => clearInterval(t);
  }, [timeframe, parsedSymbols.join(",")]);

  const singleData = useMemo(() => {
    if (!selected) return {};
    return { [selected]: dataBySymbol[selected] || [] };
  }, [selected, dataBySymbol]);

  return (
    <div className="bg-slate-900/50 border border-slate-800 rounded-2xl ring-1 ring-white/5 shadow-xl p-3 sm:p-4 text-slate-100">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 px-1 py-1">
        <div className="flex items-center gap-3">
          <h3 className="font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            Chart de Trading
          </h3>
          {parsedSymbols.length > 1 && (
            <select
              className="border border-slate-700 rounded-lg bg-slate-900/70 px-2.5 py-1.5 text-sm text-slate-100 focus:outline-none focus:ring-2 focus:ring-emerald-400/60 focus:border-emerald-400/40"
              value={selected}
              onChange={(e) => setSelected(e.target.value)}
            >
              {parsedSymbols.map((s) => (
                <option
                  key={s}
                  value={s}
                  className="bg-slate-900 text-slate-100"
                >
                  {s}
                </option>
              ))}
            </select>
          )}
        </div>
      </div>

      <ErrorAlert message={err} onClose={() => setErr("")} />

      <div className="mt-2">
        <CandlesChart dataBySymbol={singleData} height={height} />
      </div>
    </div>
  );
}
