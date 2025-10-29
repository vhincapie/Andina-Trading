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
    <div className="bg-white border rounded p-2">
      <div className="flex items-center justify-between px-2 py-1 gap-2">
        <div className="flex items-center gap-2">
          <h3 className="font-semibold">Mercado</h3>
          {parsedSymbols.length > 1 && (
            <select
              className="border rounded px-2 py-1 text-sm"
              value={selected}
              onChange={(e) => setSelected(e.target.value)}
            >
              {parsedSymbols.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          )}
        </div>
      </div>
      <ErrorAlert message={err} onClose={() => setErr("")} />
      <CandlesChart dataBySymbol={singleData} height={height} />
    </div>
  );
}
