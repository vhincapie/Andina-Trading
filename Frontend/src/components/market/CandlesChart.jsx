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
        vertLines: { color: "rgba(197,203,206,0.1)" },
        horzLines: { color: "rgba(197,203,206,0.1)" },
      },
      crosshair: { mode: 1 },
      rightPriceScale: { borderVisible: false },
      timeScale: { rightOffset: 6, barSpacing: 12 },
    });
    chartRef.current = chart;

    candleRef.current = chart.addCandlestickSeries({
      upColor: "#26a69a",
      downColor: "#ef5350",
      borderUpColor: "#26a69a",
      borderDownColor: "#ef5350",
      wickUpColor: "#26a69a",
      wickDownColor: "#ef5350",
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
      className="w-full"
      style={{ height, minHeight: height }}
    />
  );
}
