import { useEffect, useRef } from "react";
import { createChart } from "lightweight-charts";

export default function CandlesChart({ dataBySymbol = {}, height = 420 }) {
  const containerRef = useRef(null);
  const chartRef = useRef(null);
  const candleRef = useRef(null);

  useEffect(() => {
    const el = containerRef.current;
    if (!el) return;

    const chart = createChart(el, {
      height,
      layout: {
        background: { type: "Solid", color: "#0b1220" },
        textColor: "#D1D5DB",
      },
      grid: {
        vertLines: { color: "rgba(203,213,225,0.08)" },
        horzLines: { color: "rgba(203,213,225,0.08)" },
      },
      crosshair: { mode: 1 },
      rightPriceScale: { borderVisible: false },
      timeScale: { rightOffset: 6, barSpacing: 12, borderVisible: false },
    });
    chartRef.current = chart;

    candleRef.current = chart.addCandlestickSeries({
      upColor: "#10b981",
      downColor: "#ef4444",
      borderUpColor: "#10b981",
      borderDownColor: "#ef4444",
      wickUpColor: "#10b981",
      wickDownColor: "#ef4444",
    });

    const ro = new ResizeObserver(() =>
      chart.applyOptions({ width: el.clientWidth })
    );
    ro.observe(el);

    return () => {
      ro.disconnect();
      chart.remove();
      chartRef.current = null;
      candleRef.current = null;
    };
  }, [height]);

  useEffect(() => {
    const chart = chartRef.current;
    const candle = candleRef.current;
    if (!chart || !candle) return;

    const symbols = Object.keys(dataBySymbol || {});
    const sym = symbols[0];
    const rows = sym ? dataBySymbol[sym] || [] : [];

    const toTs = (iso) => Math.floor(new Date(iso).getTime() / 1000);

    const candles = rows
      .map((b) => {
        const o = Number(b.o ?? b.open);
        const h = Number(b.h ?? b.high);
        const l = Number(b.l ?? b.low);
        const c = Number(b.c ?? b.close);
        return {
          time: toTs(b.time || b.t),
          open: o,
          high: h,
          low: l,
          close: c,
        };
      })
      .sort((a, b) => a.time - b.time);

    const todayTs = Math.floor(
      new Date(new Date().toISOString().slice(0, 10) + "T00:00:00Z").getTime() /
        1000
    );
    const lastTs = candles.length ? candles[candles.length - 1].time : 0;
    if (todayTs > lastTs) {
      candles.push({ time: todayTs });
    }

    candle.setData(candles);
    chart.timeScale().fitContent();
  }, [dataBySymbol]);

  return (
    <div
      ref={containerRef}
      className="w-full rounded-xl border border-slate-800 bg-slate-900/60 ring-1 ring-white/5 shadow-lg overflow-hidden"
      style={{ height, minHeight: height }}
    />
  );
}
